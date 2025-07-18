package com.frenchies.g5_avance2_sc504.repository;

import java.sql.Types;
import java.util.Map;

import jakarta.annotation.PostConstruct;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.SqlOutParameter;
import org.springframework.jdbc.core.SqlParameter;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.core.simple.SimpleJdbcCall;
import org.springframework.stereotype.Repository;

@Repository
public class RoleRepository {

    private final JdbcTemplate jdbc;
    private SimpleJdbcCall insRolCall;

    public RoleRepository(JdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    @PostConstruct
    void init() {
        this.insRolCall = new SimpleJdbcCall(jdbc)
            .withCatalogName("PKG_FRENCHIES")
            .withProcedureName("INS_ROL")
            .declareParameters(
                new SqlParameter("P_NOMBRE", Types.VARCHAR),
                new SqlParameter("P_DESCRIPCION", Types.VARCHAR),
                new SqlOutParameter("P_OUT_ID", Types.NUMERIC)
            );
    }

    /**
     * Llama al procedimiento INS_ROL de PKG_FRENCHIES y devuelve el ID generado.
     *
     * @param nombre      el nombre del rol
     * @param descripcion la descripción (puede ser null)
     * @return el rol_id generado por el procedimiento
     */
    public long insertRole(String nombre, String descripcion) {
        SqlParameterSource params = new MapSqlParameterSource()
            .addValue("P_NOMBRE", nombre)
            .addValue("P_DESCRIPCION", descripcion);
        Map<String, Object> out = insRolCall.execute(params);
        return ((Number) out.get("P_OUT_ID")).longValue();
    }
}
