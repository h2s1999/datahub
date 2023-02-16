package com.vpplab.io.datahub.global.config.props;

import lombok.Data;
import lombok.ToString;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Data
@Configuration
@ConfigurationProperties(prefix = "spring.datasource")
@ToString
public class DataSourceProps {
    private String driverClassName;
    private String url;
    private String username;
    private String password;
    private Hikari hikari;

    @Data
    public static class Hikari {
        private Integer maximumPoolSize;
        private Boolean autoCommit;
        private String connectionInitTestQuery;
    }
}
