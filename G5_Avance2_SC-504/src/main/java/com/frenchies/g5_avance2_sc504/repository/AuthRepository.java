package com.frenchies.g5_avance2_sc504.repository;

import java.util.Map;
import jakarta.annotation.PostConstruct;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.SqlOutParameter;
import org.springframework.jdbc.core.SqlParameter;
import org.springframework.jdbc.core.simple.SimpleJdbcCall;
import org.springframework.stereotype.Repository;

@Repository
public class AuthRepository {

    private final JdbcTemplate jdbc;
    private SimpleJdbcCall loginCall;

    public AuthRepository(JdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    @PostConstruct
    void init() {
        this.loginCall = new SimpleJdbcCall(jdbc)
            .withCatalogName("PKG_FRENCHIES")
            .withFunctionName("F_LOGIN")
            .declareParameters(
              new SqlParameter("P_USUARIO", java.sql.Types.VARCHAR),
              new SqlParameter("P_PASSWORD", java.sql.Types.VARCHAR),
              new SqlOutParameter("RETURN", java.sql.Types.NUMERIC)
            );
    }

    /**
     * Llama a la función F_LOGIN y devuelve el usuario_id (0 si falló).
     */
    public long login(String usuario, String password) {
        Map<String, Object> out = loginCall.execute(
          Map.of("P_USUARIO", usuario, "P_PASSWORD", password)
        );
        return ((Number) out.get("RETURN")).longValue();
    }
}
