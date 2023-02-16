package com.vpplab.io.datahub.global.config;

import com.vpplab.io.datahub.global.config.props.MailProps;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;

import java.util.Properties;

@Configuration
@Slf4j
@RequiredArgsConstructor
public class MailConfig {

    private static final String MAIL_SMTP_AUTH = "mail.smtp.auth";
    private static final String MAIL_SMTP_STARTTLS_ENABLE = "mail.smtp.starttls.enable";

    private final MailProps mailProps;

    @Bean(name = "javaMailSender")
    public JavaMailSender javaMailSender(){
        JavaMailSenderImpl javaMailSender = new JavaMailSenderImpl();
        javaMailSender.setHost(mailProps.getHost());
        javaMailSender.setPort(mailProps.getPort());
        javaMailSender.setUsername(mailProps.getUsername());
        javaMailSender.setPassword(mailProps.getPassword());
        Properties props = new Properties();
        props.put(MAIL_SMTP_AUTH, mailProps.getSmtp().isAuth());
        props.put(MAIL_SMTP_STARTTLS_ENABLE, mailProps.getSmtp().isStartTlsEnabled());
        javaMailSender.setJavaMailProperties(props);
        log.info(javaMailSender.toString());
        return javaMailSender;
    }
}
