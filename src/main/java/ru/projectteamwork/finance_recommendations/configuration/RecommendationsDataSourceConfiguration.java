package ru.projectteamwork.finance_recommendations.configuration;

import com.zaxxer.hikari.HikariDataSource;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.JdbcTemplate;

import javax.sql.DataSource;

@Configuration
public class RecommendationsDataSourceConfiguration {

    @Bean("recommendationsDataSource")
    public DataSource recommendationsDataSource(
            @Value("${recommendations.db.url}") String url,
            @Value("${recommendations.db.username:sa}") String user,
            @Value("${recommendations.db.password:}") String pass
    ) {
        var ds = new HikariDataSource();
        ds.setJdbcUrl(url);
        ds.setUsername(user);
        ds.setPassword(pass);
        ds.setDriverClassName("org.h2.Driver");
        ds.setReadOnly(true); // можно оставить read-only
        return ds;
    }

    @Bean("recommendationsJdbcTemplate")
    public JdbcTemplate recommendationsJdbcTemplate(
            @Qualifier("recommendationsDataSource") DataSource dataSource
    ) {
        return new JdbcTemplate(dataSource);
    }
}
