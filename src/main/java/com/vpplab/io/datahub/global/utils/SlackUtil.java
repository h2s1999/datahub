package com.vpplab.io.datahub.global.utils;

import com.github.seratch.jslack.Slack;
import com.github.seratch.jslack.api.model.Attachment;
import com.github.seratch.jslack.api.webhook.Payload;
import com.github.seratch.jslack.api.webhook.WebhookResponse;
import com.vpplab.io.datahub.global.exception.CustomException;
import com.vpplab.io.datahub.global.exception.ErrorCode;
import com.vpplab.io.datahub.global.utils.enums.SlackChannel;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.exception.ExceptionUtils;

import java.io.IOException;
import java.util.ArrayList;

@Slf4j
public class SlackUtil {

    /**
     * 슬랙 Webhook API 전송
     * @param title         Webhook 메시지 제목
     * @param text          Webhook 메시지 내용
     * @param code          Webhook 보낼 채널
     * @param activeProfile spring 실행 profile
     */
    public static void sendSlack(String title, String text, String code, String activeProfile) {
        String textCn = title;
        String url = "https://hooks.slack.com/services/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";

        String textCns ="```";
        textCns +=text;
        textCns +="```";
        if("dev".equals(activeProfile)){
            code = SlackChannel.CS_TEST.code;
            textCn = "[test] "+textCn;
        }
        Payload payload = Payload.builder().channel(code).username("VPPLab").iconUrl("https://a.slack-edge.com/production-standard-emoji-assets/10.2/google-medium/1f300.png").text(textCn).attachments(new ArrayList<>()).build();
        Attachment attachment = Attachment.builder().text(textCns).build();
        payload.getAttachments().add(attachment);
        Slack slack = Slack.getInstance();
        try {
            WebhookResponse response = slack.send(url, payload);
            log.debug("slack api response = {}",response);
        } catch (IOException e) {
            log.debug("[EXCEPTION]sendSlack failed :: {}", e.getMessage());
            log.error("[EXCEPTION]sendSlack failed :: {}", ExceptionUtils.getStackTrace(e));
            throw new CustomException(ErrorCode.FAILED_SEND_SLACK);
        }
    }
}