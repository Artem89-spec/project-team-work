package ru.projectteamwork.finance_recommendations.config;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.JdbcTemplate;

import javax.sql.DataSource;
import org.springframework.boot.jdbc.DataSourceBuilder;

@Configuration
public class RecommendationDataSourceConfiguration {

    @Value("${recommendations.db.url}")
    private String recommendationsDbUrl;

    @Value("${recommendations.db.username:sa}")
    private String recommendationsDbUser;

    @Value("${recommendations.db.password:}")
    private String recommendationsDbPassword;

    @Bean(name = "recommendationsDataSource")
    public DataSource recommendationsDataSource() {
        return DataSourceBuilder.create()
                .driverClassName("org.h2.Driver")
                .url(recommendationsDbUrl)
                .username(recommendationsDbUser)
                .password(recommendationsDbPassword)
                .build();
    }

    @Bean(name = "recommendationsJdbcTemplate")
    public JdbcTemplate recommendationsJdbcTemplate(@Qualifier("recommendationsDataSource") DataSource dataSource) {
        return new JdbcTemplate(dataSource);
    }
}
