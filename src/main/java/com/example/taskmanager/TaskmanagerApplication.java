package com.example.taskmanager;

import lombok.Getter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.core.env.Environment;

import javax.sql.DataSource;

@Getter
@SpringBootApplication(exclude = SecurityAutoConfiguration.class)
public class TaskmanagerApplication {

    private static final Logger LOGGER = LoggerFactory.getLogger(TaskmanagerApplication.class);

    private final static String DATASOURCE_URL = "first.datasource.url";
    private final static String DATASOURCE_DRIVER = "first.datasource.driver";

    private final static String SECOND_DATASOURCE_URL = "second.datasource.url";
    private final static String SECOND_DATASOURCE_DRIVER = "second.datasource.driver";

    private final Environment environment;

    public TaskmanagerApplication(Environment environment) {
        this.environment = environment;
    }

    public static void main(String[] args) {
        SpringApplication.run(TaskmanagerApplication.class, args);
    }

    @Primary
    @Bean
    public DataSource getDataSource(
            @Qualifier("firstDataSource") DataSourceProperties first,
            @Qualifier("secondDataSource") DataSourceProperties second) {

        final DataSource firstDataSource = first.initializeDataSourceBuilder().build();
        final DataSource secondDataSource = second.initializeDataSourceBuilder().build();
        try
        {
            firstDataSource.getConnection();
            LOGGER.info("Using first datasource: {}, with url: {}", firstDataSource, first.getUrl());
            return firstDataSource;
        }
        catch (Exception e)
        {
            LOGGER.info("Using second datasource: {}, with url: {}", secondDataSource, second.getUrl());
            return secondDataSource;
        }
    }

    @Primary
    @Bean("firstDataSource")
    @ConfigurationProperties(prefix = "spring.datasource")
    public DataSourceProperties primaryDataSource() {
        final DataSourceProperties dataSourceProperties = new DataSourceProperties();
        dataSourceProperties.setUrl(environment.getProperty(DATASOURCE_URL));
        dataSourceProperties.setDriverClassName(environment.getProperty(DATASOURCE_DRIVER));
        return dataSourceProperties;
    }

    @Bean("secondDataSource")
    public DataSourceProperties secondaryDataSource() {
        final DataSourceProperties dataSourceProperties = new DataSourceProperties();
        dataSourceProperties.setUrl(environment.getProperty(SECOND_DATASOURCE_URL));
        dataSourceProperties.setDriverClassName(environment.getProperty(SECOND_DATASOURCE_DRIVER));
        return dataSourceProperties;
    }

}
