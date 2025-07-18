package com.frenchies.g5_avance2_sc504.config;

import org.springframework.boot.CommandLineRunner;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

@Component
public class DatabaseTestRunner implements CommandLineRunner {
    private final JdbcTemplate jdbc;

    public DatabaseTestRunner(JdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    @Override
    public void run(String... args) {
        Integer result = jdbc.queryForObject("SELECT 1 FROM DUAL", Integer.class);
        System.out.println("✅ Conexión a Oracle OK, SELECT 1 => " + result);
    }
}
