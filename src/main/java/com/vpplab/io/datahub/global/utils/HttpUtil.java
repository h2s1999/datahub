package com.vpplab.io.datahub.global.utils;


import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.apache.http.HttpHost;
import org.apache.http.conn.ssl.NoopHostnameVerifier;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.conn.ssl.TrustSelfSignedStrategy;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.DefaultProxyRoutePlanner;
import org.apache.http.ssl.SSLContexts;
import org.apache.http.ssl.TrustStrategy;
import org.springframework.http.*;
import org.springframework.http.client.BufferingClientHttpRequestFactory;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.http.converter.FormHttpMessageConverter;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.stereotype.Component;
import org.springframework.web.client.*;

import javax.net.ssl.*;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.Proxy;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.security.*;
import java.security.cert.X509Certificate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

/**
 * Provide basic Utils for getting HttpHeader and making REST api calls.
 *
 */
@Slf4j
@Component
public class HttpUtil {

	/**
	 * soap - xml send
	 * @param xmlReq
	 * @param uri
	 * @return
	 * @throws IOException
	 */
	public static String getApiResponseXml(String xmlReq, String uri) throws IOException {
		String xmlResponse = "";
		String xml = xmlReq;
		OutputStreamWriter wr = null;
		BufferedReader in = null;

		URL url = new URL(uri);  //보낼 주소
		HttpURLConnection conn = (HttpURLConnection) url.openConnection();

		conn.setDoOutput(true);
		conn.setRequestMethod("POST");
		conn.addRequestProperty("Content-Type", "text/xml");
		wr = new OutputStreamWriter(conn.getOutputStream());
		wr.write(xml);
		wr.flush();
		int code = conn.getResponseCode();

		String inputLine = null;
		StringBuffer buffer = new StringBuffer();
		in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
		while ((inputLine = in.readLine()) != null) {
			buffer.append(inputLine);
		}
		xmlResponse = buffer.toString();
		//System.out.println(buffer.toString());        //결과 값
		return xmlResponse;
	}

	/**
	 * The default implementation to get basic headers.
	 * @return HttpHeaders.
	 */
	public HttpHeaders getHttpHeaders(String userAgent, String host) {
		HttpHeaders headers = new HttpHeaders();
		//headers.setContentType(MediaType.APPLICATION_JSON);
		//headers.setBearerAuth("eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzUxMiJ9.eyJiaXpybm8iOiI1Mzg4NjAyMDQwIiwiY29ycElkIjoiMDAwODU4IiwidXNlcklkIjoidnBwbGFiIiwiZXhwIjoxNjU5Mzg3NjA1fQ.FVIgKacnPeuRnSBQVV451zHp0bgqqQ73PVovsLk-OrhJnXNIKx0q3Fq86Qjl-q7dBHAqfe209gJdMt2Z8Nq8SA");
//		headers.set(HttpHeaders.ACCEPT_CHARSET, StandardCharsets.UTF_8.name());
//		headers.set(HttpHeaders.USER_AGENT, userAgent);
		log.info("host=" + host);
		if (null != host) {
			headers.set(HttpHeaders.HOST, host);
		}

		return headers;
	}

	/**
	 * Default implementation to get RestTemplate
	 * @return
	 */
	public RestTemplate getRestTemplate(String proxyHost, int proxyPort)
			throws KeyStoreException, NoSuchAlgorithmException, KeyManagementException {

		TrustStrategy acceptingTrustStrategy = new TrustSelfSignedStrategy();

		SSLContext sslContext = SSLContexts.custom().loadTrustMaterial(null, acceptingTrustStrategy).build();

		SSLConnectionSocketFactory csf = new SSLConnectionSocketFactory(sslContext, NoopHostnameVerifier.INSTANCE);

		CloseableHttpClient httpClient = HttpClients.custom().setSSLSocketFactory(csf).build();

		HttpComponentsClientHttpRequestFactory requestFactory = new HttpComponentsClientHttpRequestFactory();
		if (null != proxyHost && proxyPort > 0) {
			log.info("PROXY CONFIGURED | proxyHost=" + proxyHost + " | proxyPort=" + proxyPort);
			HttpHost proxy = new HttpHost(proxyHost, proxyPort, Proxy.Type.HTTP.name());
			httpClient = HttpClients.custom().setSSLSocketFactory(csf)
					.setRoutePlanner(new DefaultProxyRoutePlanner(proxy)).build();
		}
		requestFactory.setHttpClient(httpClient);
		RestTemplate restTemplate = new RestTemplate(requestFactory);
		return restTemplate;
	}


	public RestTemplate restTemplate() {

		HttpComponentsClientHttpRequestFactory httpRequestFactory = new HttpComponentsClientHttpRequestFactory();

		httpRequestFactory.setConnectTimeout(10*1000);
		httpRequestFactory.setReadTimeout(10*1000);

		BufferingClientHttpRequestFactory requestFactory = new BufferingClientHttpRequestFactory(httpRequestFactory);
		// requestFactory.setOutputStreaming(false);

		List<HttpMessageConverter<?>> converters = new ArrayList<HttpMessageConverter<?>>();
		converters.add(new MappingJackson2HttpMessageConverter());
		converters.add(new FormHttpMessageConverter());
		// converters.add(new StringHttpMessageConverter());

		RestTemplate restTemplate = new RestTemplate(requestFactory);
		List<ClientHttpRequestInterceptor> interceptors = new ArrayList<ClientHttpRequestInterceptor>();
		//interceptors.add(new LoggingRequestInterceptor());
		restTemplate.setInterceptors(interceptors);
		restTemplate.setMessageConverters(converters);

		restTemplate.setErrorHandler(responseErrorHandler());

		return restTemplate;
	}

	private ResponseErrorHandler responseErrorHandler() {

		ResponseErrorHandler errorHandler = new DefaultResponseErrorHandler() {

			@Override
			public boolean hasError(ClientHttpResponse response) throws IOException {
				return false;
			}
		};

		return errorHandler;
	}

	/**
	 * Make a rest api call
	 * @return ResponseEntity
	 */
	public Map<String, Object> getApiResponse(HttpMethod httpMethod, final String URL, final String userAgent,
											  String proxyHost, int proxyPort, String host, HttpHeaders headers, Map<String, Object> bodyParams) throws HttpClientErrorException {
		Map<String, Object> result = new HashMap<>();
		HttpEntity<Map<String, Object>> httpEntity = new HttpEntity<>(bodyParams, headers);
		ResponseEntity<Map> response = null;
		RestTemplate request = null;
		Gson gson = new Gson();

		try {
			if (null != httpMethod && null != URL) {
				try {
					request = getRestTemplate(proxyHost, proxyPort);
					response = request.exchange(URL, httpMethod, httpEntity, Map.class);

//					result.put("statusCode", response.getStatusCodeValue());
					result.put("statusCode", response.getStatusCode());
					result.put("body", response.getBody());
				} catch (KeyManagementException | KeyStoreException | NoSuchAlgorithmException e) {
					log.error("Error creating Rest Template", e);
				}
			}
		} catch (HttpClientErrorException | HttpServerErrorException e) {
			log.error("Method = " + httpMethod + "Request URL = " + URL);
			log.error("Headers =" + getHttpHeaders(userAgent, host));
			log.error("Response Status = " + e.getStatusText());
			log.error("Response Body = " + e.getResponseBodyAsString());

			result.put("statusCode", e.getStatusCode());
			result.put("body", gson.fromJson(e.getResponseBodyAsString(), Map.class));
		} catch (Exception e) {
			log.error("getApiResponse err ::: " + e.getMessage());

			result.put("statusCode", "999");
			result.put("body", e.getMessage());
		}

		return result;
	}

	public static JsonObject getDailyKpxToken(String corpId, String password, String pfx) throws Exception{
		String line = null;
		InputStream in = null;
		OutputStream os = null;
		BufferedReader reader = null;
		SSLSocket sslSocket = null;
		SSLContext sslContext = null;
		HttpsURLConnection httpsConn = null;

		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMddHHmmss");
		String timeStamp = LocalDateTime.now().format(formatter);

		try {
			char[] passwordCharArr = password.toCharArray();
			KeyStore clientStore = KeyStore.getInstance("PKCS12");
			InputStream is = new ByteArrayInputStream(Base64.getDecoder().decode(pfx.getBytes()));
			clientStore.load(is,passwordCharArr);

			KeyManagerFactory kmf = KeyManagerFactory.getInstance(KeyManagerFactory.getDefaultAlgorithm());
			kmf.init(clientStore, passwordCharArr);

			// openssl s_client -connect deras-auth.kmos.kr:32443 < /dev/null | sed -ne '/-BEGIN CERTIFICATE-/,/-END CERTIFICATE-/p' > deras-auth.kmos.kr.crt
			// sudo keytool -import -alias "deras-auth.kmos.kr" -keystore $JAVA_HOME/lib/security/cacerts -file deras-auth.kmos.kr.crt
			KeyStore trustStore = KeyStore.getInstance("JKS");
			trustStore.load(new FileInputStream(System.getProperty("java.home") + "/lib/security/cacerts"), "changeit".toCharArray());

			TrustManagerFactory tmf = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
			tmf.init(trustStore);

			sslContext = SSLContext.getInstance("TLSv1.2");
			sslContext.init(kmf.getKeyManagers(), tmf.getTrustManagers(), new SecureRandom());

			SSLSocketFactory sslSocketFactory = sslContext.getSocketFactory();
			sslSocket = (SSLSocket)sslSocketFactory.createSocket("deras-auth.kmos.kr",32443);
			sslSocket.setUseClientMode(true);

			// handshaking by tls v1.2
			sslSocket.startHandshake();

			log.debug("sslSocket.isConnected() = {}",sslSocket.isConnected());

			URL url = new URL("https://deras-auth.kmos.kr:32443/v1/tokens");
			httpsConn = (HttpsURLConnection) url.openConnection();
			httpsConn.setSSLSocketFactory(sslSocketFactory); // set handshaked ssl socket

			httpsConn.setDoInput(true);
			httpsConn.setDoOutput(true);
			httpsConn.setUseCaches(false);
			httpsConn.setReadTimeout(5000);
			httpsConn.setConnectTimeout(5000);
			httpsConn.setRequestMethod("POST");
			httpsConn.setRequestProperty("Content-Type", MediaType.APPLICATION_JSON_VALUE);

			// getKeyPairs(public, private, alias, x509cert)
			Map<String,Object> keyPairs = getCertKeys(clientStore, passwordCharArr);

			String promisedPlainTxt = String.format("%s%s\n",corpId,timeStamp);

			byte [] promisedPlainTxtByteArr = promisedPlainTxt.getBytes(StandardCharsets.UTF_8);
			byte [] signByteArr = digitalSignature((PrivateKey)keyPairs.get("PrivateKey"), promisedPlainTxtByteArr);
			boolean blVerified = verifySignature((PublicKey) keyPairs.get("PublicKey"), signByteArr, promisedPlainTxtByteArr);

			String signature = Base64.getEncoder().encodeToString(signByteArr);

			JsonObject jobj = new JsonObject();
			jobj.addProperty("corpId",corpId);
			jobj.addProperty("signature",signature);
			jobj.addProperty("timeStamp", timeStamp);

			os = httpsConn.getOutputStream();
			os.write(jobj.toString().getBytes());
			os.flush();

			int responseCode = httpsConn.getResponseCode();
			HttpStatus httpStatus = HttpStatus.resolve(responseCode);
			boolean isTokenRefreshSuccess = httpStatus.is2xxSuccessful();

			// Connect to host
			httpsConn.connect();

			// Print response from host
			if(isTokenRefreshSuccess){
				in = httpsConn.getInputStream();
			} else {
				in = httpsConn.getErrorStream();
			}

			reader = new BufferedReader(new InputStreamReader(in));
			StringBuffer sb = new StringBuffer();
			while ((line = reader.readLine()) != null) {
				sb.append(line);
			}

			JsonObject json = JsonParser.parseString(sb.toString()).getAsJsonObject();
			log.debug("api result = {}",json);

			if(isTokenRefreshSuccess && json.has("data")) {
				return json.getAsJsonObject("data");
			} else {
				String errorMsg = json.getAsJsonPrimitive("message").getAsString();
				log.error("KPX token refresh issue invoked by => {}",errorMsg);
				if(httpStatus.is4xxClientError()) {
					throw new HttpClientErrorException(httpStatus, errorMsg);
				} else {
					throw new HttpServerErrorException(httpStatus, errorMsg);
				}
			}
		} catch (Exception e) {
			log.debug("[EXCEPTION] : {}", e.getMessage());
			log.error("[EXCEPTION] : {}", ExceptionUtils.getStackTrace(e));
			throw e;
		} finally {
			if (reader != null) {
				reader.close();
			}

			if (httpsConn != null) {
				httpsConn.disconnect();
			}

			if(sslSocket != null) {
				sslSocket.close();
			}
		}
	}

	private static HashMap<String, Object> getCertKeys(KeyStore keyStore, char[] password) throws Exception {
		HashMap<String, Object> keyPair = new HashMap<String, Object>();

		Enumeration<String> keyStoreAliasEnum = keyStore.aliases();
		String alias = null;
		while ( keyStoreAliasEnum.hasMoreElements() ) {
			alias = keyStoreAliasEnum.nextElement();
			if (password != null) {
				PrivateKey privateKey = (PrivateKey) keyStore.getKey(alias, password);

				X509Certificate x509Certificate = (X509Certificate) keyStore.getCertificate(alias);
				PublicKey publicKey = x509Certificate.getPublicKey();

				keyPair.put("Alias", alias);
				keyPair.put("PublicKey", publicKey);
				keyPair.put("PrivateKey", privateKey);
				keyPair.put("X509Certificate", x509Certificate);
			}
		}

		return keyPair;
	}

	private static byte [] digitalSignature(PrivateKey privateKey, byte [] promisedPlainTxtByteArr) throws Exception{
		byte [] sign = null;
		try {
			Signature signature = Signature.getInstance("SHA256withRSA");
			signature.initSign(privateKey);
			signature.update(promisedPlainTxtByteArr);
			sign = signature.sign();
			return sign;
		} catch(Exception e){
			log.debug("[EXCEPTION] : {}", e.getMessage());
			log.error("[EXCEPTION] : {}", ExceptionUtils.getStackTrace(e));
			return sign;
		}
	}

	private static boolean verifySignature(PublicKey publicKey, byte [] signByteArr, byte [] promisedPlainTxtByteArr) throws Exception{
		boolean result = false;
		try {
			Signature signature = Signature.getInstance("SHA256withRSA");
			signature.initVerify(publicKey);
			signature.update(promisedPlainTxtByteArr);
			result = signature.verify(signByteArr);
			return result;
		} catch(Exception e){
			log.debug("[EXCEPTION] : {}", e.getMessage());
			log.error("[EXCEPTION] : {}", ExceptionUtils.getStackTrace(e));
			return result;
		}
	}
}
