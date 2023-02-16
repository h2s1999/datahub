package com.vpplab.io.datahub.global.config;

import com.vpplab.io.datahub.global.config.props.DataSourceProps;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.type.JdbcType;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import javax.sql.DataSource;
import java.util.Properties;

@Slf4j
@Configuration
@EnableTransactionManagement
@MapperScan(basePackages = {"com.vpplab.io.datahub.domain"})
@RequiredArgsConstructor
public class DataSourceConfig {
    private final DataSourceProps dataSourceProps;

    @Bean(name = "dataSource")
    public DataSource dataSource() {
        return this.buildDataSource();
    }

    @Bean
    public SqlSessionFactory sqlSessionFactoryBean() throws Exception {
        SqlSessionFactoryBean sqlSessionFactoryBean = new SqlSessionFactoryBean();
        sqlSessionFactoryBean.setDataSource(dataSource());
        PathMatchingResourcePatternResolver resolver = new PathMatchingResourcePatternResolver();
        sqlSessionFactoryBean.setMapperLocations(resolver.getResources("classpath:/mybatis/mapper/**/*.xml"));
        sqlSessionFactoryBean.setConfigurationProperties(mybatisConfigProperties());
        return sqlSessionFactoryBean.getObject();
    }

    @Bean
    public PlatformTransactionManager transactionManager() {
        return new DataSourceTransactionManager(dataSource());
    }

    private DataSource buildDataSource() {
        HikariConfig config = new HikariConfig();
        config.setDriverClassName(dataSourceProps.getDriverClassName());
        config.setJdbcUrl(dataSourceProps.getUrl());
        config.setUsername(dataSourceProps.getUsername());
        config.setPassword(dataSourceProps.getPassword());

        DataSourceProps.Hikari hikari = dataSourceProps.getHikari();
        config.setMaximumPoolSize(hikari.getMaximumPoolSize());
        config.setAutoCommit(hikari.getAutoCommit());
        config.setConnectionTestQuery(hikari.getConnectionInitTestQuery());

        log.debug("datasource info = {}",dataSourceProps);
        return new HikariDataSource(config);
    }

    private Properties mybatisConfigProperties(){
        Properties mybatisProps = new Properties();
        mybatisProps.put("jdbcTypeForNull", JdbcType.NULL);
        mybatisProps.put("callSettersOnNulls",true);
        mybatisProps.put("returnInstanceForEmptyRow",true);
        return mybatisProps;
    }
}
