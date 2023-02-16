package com.vpplab.io.datahub.global.config.props;


import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Data
@Configuration
@ConfigurationProperties(prefix = "mail")
public class MailProps {

    private String host;
    private Integer port;
    private String username;
    private String password;
    private Smtp smtp;

    @Data
    public static class Smtp {
        private boolean auth;
        private boolean startTlsEnabled;
    }
}
