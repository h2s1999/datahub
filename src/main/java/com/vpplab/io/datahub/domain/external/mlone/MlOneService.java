package com.vpplab.io.datahub.domain.external.mlone;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.vpplab.io.datahub.global.exception.CustomException;
import com.vpplab.io.datahub.global.exception.ErrorCode;
import com.vpplab.io.datahub.global.utils.DatetimeUtil;
import com.vpplab.io.datahub.global.utils.HttpUtil;
import com.vpplab.io.datahub.global.utils.SlackUtil;
import com.vpplab.io.datahub.global.utils.enums.SlackChannel;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.json.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.*;

import static com.vpplab.io.datahub.global.utils.DatetimeUtil.getTimestampToDate;

@Service
@RequiredArgsConstructor
@Slf4j
public class MlOneService {

    @Value("${ml1.ml1-url}")
    private String ml1Uri;
    @Value("${ml1.ml1-id}")
    private String ml1Id; // ID
    @Value("${ml1.ml1-pw}")
    private String ml1Pw; // Password

    @Value("${spring.profiles.active}")
    private String activeProfile;

    private final MlOneDao mlOneDao;

    /**
     * ML1 DATA DB Input
     * @throws Exception
     */
    public Map<String, Object> procMl1forecastIn() throws Exception {
        log.info("ML1 데이터 가져오기 시작 ");
        Map<String, Object> result = new HashMap<>();

        String[] dates = DatetimeUtil.getDefaultForecastDatesBy8601();
        String forecastDate = dates[0] ;
        String fromDate = dates[1] ;
        String toDate = dates[2] ;
        String todayBase = dates[3] ;
        log.info("TARGET DATES= 기준일: "+forecastDate+", 예측시작시간: "+fromDate+", 예측끝시각: "+toDate);

        Map<String, Object> requestParam = new HashMap<>();
        requestParam.put("forecastDate",forecastDate);
        requestParam.put("fromDate",fromDate);
        requestParam.put("toDate",toDate);
        requestParam.put("todayBase",todayBase);

        //1.Login 후 header 에서 Session Token 추출
        String sToken = this.getSessionToken();
        requestParam.put("sToken",sToken);

        //2. 예측 값 받고 데이터 적재
        Map<String, Object> getForecastMultiData = this.getForecastMultiData(requestParam,3);
//        log.info("getForecastMultiData = " + getForecastMultiData);

        return getForecastMultiData;
    }

    /**
     * ML1 DATA API Connect
     * @param requestParam
     * @param action
     * @return
     * @throws IOException
     * @throws ParseException
     */
    private Map<String, Object> getForecastMultiData (Map<String,Object> requestParam, int action) throws IOException, ParseException {
        log.info("forecast Multi 가져오기 시작 [requestParam] = " + requestParam );
        Map<String, Object> result = new HashMap<String, Object>();

        // Header
        Map<String, Object> header = new HashMap<String, Object>();
        header.put("SOAPAction","login");
        header.put("Content-Type", "text/xml;charset=utf-8");

        log.info("forecast Multi API 요청");
        String reqResult = HttpUtil.getApiResponseXml(this.requestSOAPxml(requestParam,3),ml1Uri);
        log.info("forecast Multi API 응답 :: " + reqResult);

        if (reqResult == null )
        {
            log.info("["+requestParam.get("todayBase") + "] ML1 forecast Multi data 요청 실패");
            SlackUtil.sendSlack("ML1 API 연동 오류 ","["+requestParam.get("todayBase") + "] ML1 forecast Multi data 요청 실패",
                    SlackChannel.DEV_NOTI.code,
                    activeProfile);
            throw new CustomException(ErrorCode.FAILED_MLONE_DATA_ISSUE);
        }

        String xmlRmString = this.removeXmlNoNeed(reqResult,3); // 필요없는 태그 제거
//        log.info("xmlRmString = "+ xmlRmString);
        JSONObject jsonObject = org.json.XML.toJSONObject(xmlRmString);
//        log.info("xml->Json = "+ jsonObject.toString());

        JSONParser parser = new JSONParser();
        org.json.simple.JSONObject obj = (org.json.simple.JSONObject) parser.parse(jsonObject.toString());
        org.json.simple.JSONObject parseFacilitiesForecastData = (org.json.simple.JSONObject) obj.get("facilitiesForecastData");
        org.json.simple.JSONArray parseItem = (org.json.simple.JSONArray) parseFacilitiesForecastData.get("item");
//        log.info("parse_item==>"+parseItem);

        int tCnt = 0;

        for (int i = 0; i<parseItem.size(); i++){
            List<Map<String, Object>> insertParam = new ArrayList<Map<String, Object>>();

            org.json.simple.JSONObject arrItemObj = (org.json.simple.JSONObject) parseItem.get(i);
            String facilityId = String.valueOf(arrItemObj.get("facilityId"));
            String forecastData = String.valueOf(arrItemObj.get("forecastData"));

            String[] arrFdate = forecastData.split(":");

            for (int j = 1; j<25; j++){
                Map<String, Object> params = new HashMap<>();
                /*log.info("plant_id -> "+ getConvFacilityId(facilityId));
                log.info("base_datetime -> "+ requestParam.get("todayBase"));
                log.info("forecast_datetime -> "+ getTimestampToDate(arrFdate[j].substring(0,10)));*/
                params.put("plant_id",getConvFacilityId(facilityId));
                params.put("base_datetime",requestParam.get("todayBase"));
                params.put("forecast_datetime",getTimestampToDate(arrFdate[j].substring(0,10)));
                String[] arrForecast = arrFdate[j].split("~");
                for (int k = 1; k<4; k++){
                    if( k == 1){
                        params.put("forecast_10",arrForecast[k]);
                    }else if(k == 2 ){
                        params.put("forecast_50",arrForecast[k]);
                    }else{
                        params.put("forecast_90",arrForecast[k]);
                    }
                }
                insertParam.add(params);
            }

            log.info("insertParam :: {}",insertParam);
            int processCnt = mlOneDao.setMlone(insertParam);
            log.info("["+i+"] setMlone {}건 처리", processCnt);
            tCnt = ++i;
            log.info("tCnt값=>"+tCnt);
        }
        if (tCnt == 9) {
            result.put("success","Y");
        }else{
            result.put("success","N");
        }
        return result;
    }


    /**
     * ML1 sessionToken Request
     * @return
     */
    private String getSessionToken () throws Exception {
        String sToken = "";
        log.info("sessionToken 가져오기 시작");
        Map<String, Object> requestParam = new HashMap<>();
        requestParam.put("dummy","dummy");

        // Header
        Map<String, Object> header = new HashMap<String, Object>();;
        header.put("SOAPAction","login");
        header.put("Content-Type", "text/xml;charset=utf-8");

        log.info("sessionToken API 요청");
        String reqResult = HttpUtil.getApiResponseXml(this.requestSOAPxml(requestParam,1),ml1Uri);
        log.info("sessionToken API 응답 :: " + reqResult);

        if (reqResult == null )
        {
            log.info("ML1 세션토큰 발급 실패");
            SlackUtil.sendSlack("ML1 API 연동 오류 ","ML1 sessionToken data 요청 실패",
                    SlackChannel.DEV_NOTI.code,
                    activeProfile);
            throw new CustomException(ErrorCode.FAILED_MLONE_TOKEN_ISSUE);
        }

        String xmlString = this.removeXmlNoNeed(reqResult,1); // 필요없는 태그 제거
        //log.info("xmlString = "+ xmlString);
        JSONObject jsonObject = org.json.XML.toJSONObject(xmlString);

        JSONParser parser = new JSONParser();
        org.json.simple.JSONObject obj = (org.json.simple.JSONObject) parser.parse(jsonObject.toString());
        org.json.simple.JSONObject parseReturn = (org.json.simple.JSONObject) obj.get("return");
        org.json.simple.JSONObject parseHeader = (org.json.simple.JSONObject) parseReturn.get("header");

        ObjectMapper mapper = new ObjectMapper();
        String jsonStr = mapper.writeValueAsString(parseHeader);
        //log.info("jsonStr==>"+jsonStr);
        org.json.simple.JSONObject parse_sessionTokenVal=  (org.json.simple.JSONObject) parser.parse(jsonStr);
        //log.info("parse_sessionTokenVal=="+parse_sessionTokenVal.get("sessionToken"));
        sToken = parse_sessionTokenVal.get("sessionToken").toString();

        return sToken;
    }

    /**
     * ml1 API 응답값 중 필요없는 태그 제거
     * @param val
     * @param action
     * @return
     */
    private String removeXmlNoNeed(String val, int action){
        String convVal = "";
        if (action == 1) {
            convVal = val.replace("<SOAP-ENV:Envelope xmlns:SOAP-ENV=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:ns1=\"https://webservice.meteologica.com/api/MeteologicaDataExchangeService.php\"><SOAP-ENV:Body><ns1:loginResponse>", "").replace("</ns1:loginResponse></SOAP-ENV:Body></SOAP-ENV:Envelope>", "");
        }else if (action == 3){
            convVal = val.replace("<SOAP-ENV:Envelope xmlns:SOAP-ENV=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:ns1=\"https://webservice.meteologica.com/api/MeteologicaDataExchangeService.php\"><SOAP-ENV:Body><ns1:getForecastMultiResponse><return>", "").replace("</return></ns1:getForecastMultiResponse></SOAP-ENV:Body></SOAP-ENV:Envelope>", "");
        }
        return convVal;
    }

    /**
     * soap - 요청 xml
     * @param requestParam
     * @param action
     * @return
     */
    private String requestSOAPxml(Map<String, Object> requestParam, int action)
    {
        String xml = "<?xml version=\"1.0\" encoding=\"utf-8\"?>";
        xml += "<soap:Envelope xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xmlns:soap=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:tns=\"https://webservice.meteologica.com/api/MeteologicaDataExchangeService.php\">";
        xml += "<soap:Body>";

        if (action == 1)
        {
            xml += "<tns:login>";
            xml += "    <LoginReq>";
            xml += "        <username>" + ml1Id + "</username>";
            xml += "        <password>" + ml1Pw + "</password>";
            xml += "    </LoginReq>";
            xml += "</tns:login>";
        }
        else if (action == 2)
        {
            xml += "<tns:getAllFacilities>";
            xml += "    <GetAllFacilitiesReq>";
            xml += "        <header>";
            xml += "            <sessionToken>" + requestParam.get("sToken") + "</sessionToken>";
            xml += "        </header>";
            xml += "    </GetAllFacilitiesReq>";
            xml += "</tns:getAllFacilities>";
        }
        else if (action == 3)
        {
            xml += "<tns:getForecastMulti>";
            xml += "    <GetForecastMultiReq>";
            xml += "        <header>";
            xml += "            <sessionToken>" + requestParam.get("sToken") + "</sessionToken>";
            xml += "        </header>";
            xml += "        <variableId>prod</variableId>";
            xml += "        <predictorId>aggregated</predictorId>";
            xml += "        <forecastDate>" + requestParam.get("forecastDate") + "</forecastDate>";
            xml += "        <fromDate>" + requestParam.get("fromDate") + "</fromDate>";
            xml += "        <toDate>" + requestParam.get("toDate") + "</toDate>";
            xml += "    </GetForecastMultiReq>";
            xml += "</tns:getForecastMulti>";
        }

        xml += "</soap:Body>";
        xml += "</soap:Envelope>";

        /*Map<String, Object> resultMap = new HashMap<>();
        resultMap.put("data",xml);*/

        return xml;
    }

    /**
     * 발전소 아이디 변환(AS-IS -> TO-BE)
     * @param Id
     * @return
     */
    private String getConvFacilityId(String Id){
        String facilityId = "";
        switch (Id){
            case "900001" : facilityId = "w100001";break;
            case "900002" : facilityId = "w100003";break;
            case "900003" : facilityId = "w100002";break;
            case "900004" : facilityId = "w100005";break;
            case "900006" : facilityId = "w100006";break;
            case "900007" : facilityId = "w100007";break;
            case "900009" : facilityId = "w100004";break;
            case "101003" : facilityId = "p100001";break;
            case "101004" : facilityId = "p100002";break;
            case "101005" : facilityId = "p100003";break;
        }
        return facilityId;
    }

}
