package com.vpplab.io.datahub.global.utils;

import com.vpplab.io.datahub.global.exception.CustomException;
import com.vpplab.io.datahub.global.exception.ErrorCode;
import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.InputStreamResource;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import javax.activation.DataSource;
import javax.mail.MessagingException;
import javax.mail.util.ByteArrayDataSource;
import java.io.IOException;
import java.io.InputStream;

@Component
@RequiredArgsConstructor
@Slf4j
public class MailUtil {

    private final JavaMailSender sender;
    private MimeMessageHelper messageHelper;

    @Value("${mail.username}")
    private String mailAddr;

    @Value("${spring.profiles.active}")
    private String activeProfile;


    @Getter
    @Builder
    public static class MailInfo {
        private String[] to;
        private String[] cc; // carbon copy ; 참조
        private String subject;
        private String text;
        private boolean isHtml;
    }

    public static final String [] DEV_PROFILE_BCC_LIST = {"sogood@vpplab.kr", "bruce@vpplab.kr", "h2s1999@vpplab.kr", "mark@vpplab.kr"};
    public static final String [] PROD_PROFILE_BCC_LIST = {"sogood@vpplab.kr", "bruce@vpplab.kr", "h2s1999@vpplab.kr", "dbpark@vpplab.kr", "chabhman@gmail.com", "mark@vpplab.kr"};

    /**
     * 보낼 메일 정보 세팅
     * @param dto   메일정보
     */
    public void setMailInfo(MailInfo dto){
        try {
            messageHelper = new MimeMessageHelper(sender.createMimeMessage(), true, "UTF-8");
            messageHelper.setFrom(mailAddr);
            messageHelper.setTo(dto.getTo());

            if(dto.getCc() != null){
                messageHelper.setCc(dto.getCc());
            }

            if("prod".equals(activeProfile)){
                messageHelper.setBcc(PROD_PROFILE_BCC_LIST);
            }

            if("dev".equals(activeProfile)){
                messageHelper.setBcc(DEV_PROFILE_BCC_LIST);
            }

            messageHelper.setSubject(dto.getSubject());
            messageHelper.setText(dto.getText(), dto.isHtml());
        } catch(MessagingException me){
            log.error("[EXCEPTION] : {}", ExceptionUtils.getStackTrace(me));
            throw new CustomException(ErrorCode.MAIL_SEND_FAIL);
        }
    }

    /**
     * 보낼 메일 정보 세팅(첨부파일 세팅)
     * @param dto           메일정보
     * @param displayName   첨부파일명
     * @param iss           첨부파일StreamResource
     */
    public void setMailInfo(MailInfo dto, String displayName, InputStreamResource iss){
        try {
            setMailInfo(dto);
            setAttach(displayName, iss);
        } catch(RuntimeException me){
            log.error("[EXCEPTION] : {}", ExceptionUtils.getStackTrace(me));
            throw new CustomException(ErrorCode.MAIL_SEND_FAIL);
        }
    }

    // 첨부 파일
    public void setAttach(String displayFileName, InputStreamResource iss) {
         //mimetype 수정버전
        InputStream is = null;
        try {
            is = iss.getInputStream();
            byte[] bytes = IOUtils.toByteArray(is);
            DataSource attachmentDataSource = new ByteArrayDataSource(bytes, "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
            messageHelper.addAttachment(displayFileName, attachmentDataSource);
        } catch(IOException | MessagingException e){
            log.error("[EXCEPTION] : {}", ExceptionUtils.getStackTrace(e));
            throw new RuntimeException(e);
        } finally {
            IOUtils.closeQuietly(is);
        }
    }

    // 발송
    @Async
    public void send() {
        try {
            sender.send(messageHelper.getMimeMessage());
        } catch(Exception e) {
            log.error("[EXCEPTION] : {}", ExceptionUtils.getStackTrace(e));
            throw new CustomException(ErrorCode.MAIL_SEND_FAIL);
        }
    }

}