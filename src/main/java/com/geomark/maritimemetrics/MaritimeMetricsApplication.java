package com.geomark.maritimemetrics;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.cassandra.repository.config.EnableCassandraRepositories;

@SpringBootApplication
@EnableCassandraRepositories(basePackages = "com.geomark.maritimemetrics.repository")
public class MaritimeMetricsApplication {

    public static void main(String[] args) {
        SpringApplication.run(MaritimeMetricsApplication.class, args);
    }

}
