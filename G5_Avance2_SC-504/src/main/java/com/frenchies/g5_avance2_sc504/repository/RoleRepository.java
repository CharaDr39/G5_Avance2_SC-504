package com.frenchies.g5_avance2_sc504.repository;

import java.sql.Types;
import java.util.List;
import java.util.Map;

import jakarta.annotation.PostConstruct;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.SqlOutParameter;
import org.springframework.jdbc.core.SqlParameter;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.simple.SimpleJdbcCall;
import org.springframework.stereotype.Repository;

@Repository
public class RoleRepository {

    private final JdbcTemplate jdbc;
    private SimpleJdbcCall insRolCall;
    private SimpleJdbcCall updRolCall;
    private SimpleJdbcCall delRolCall;

    public RoleRepository(JdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    @PostConstruct
    void init() {
        // Crear rol
        this.insRolCall = new SimpleJdbcCall(jdbc)
            .withCatalogName("PKG_FRENCHIES")
            .withProcedureName("INS_ROL")
            .declareParameters(
                new SqlParameter("P_NOMBRE", Types.VARCHAR),
                new SqlParameter("P_DESCRIPCION", Types.VARCHAR),
                new SqlOutParameter("P_OUT_ID", Types.NUMERIC)
            );

        // Actualizar rol
        this.updRolCall = new SimpleJdbcCall(jdbc)
            .withCatalogName("PKG_FRENCHIES")
            .withProcedureName("UPD_ROL")
            .declareParameters(
                new SqlParameter("P_ROL_ID", Types.NUMERIC),
                new SqlParameter("P_NOMBRE", Types.VARCHAR),
                new SqlParameter("P_DESCRIPCION", Types.VARCHAR)
            );

        // Eliminar rol
        this.delRolCall = new SimpleJdbcCall(jdbc)
            .withCatalogName("PKG_FRENCHIES")
            .withProcedureName("DEL_ROL")
            .declareParameters(
                new SqlParameter("P_ROL_ID", Types.NUMERIC)
            );
    }

    public long insertRole(String nombre, String descripcion) {
        Map<String, Object> out = insRolCall.execute(
            Map.of("P_NOMBRE", nombre, "P_DESCRIPCION", descripcion)
        );
        return ((Number) out.get("P_OUT_ID")).longValue();
    }

    public void updateRole(long id, String nombre, String descripcion) {
        MapSqlParameterSource params = new MapSqlParameterSource()
            .addValue("P_ROL_ID", id)
            .addValue("P_NOMBRE", nombre)
            .addValue("P_DESCRIPCION", descripcion);
        updRolCall.execute(params);
    }

    public void deleteRole(long id) {
        delRolCall.execute(Map.of("P_ROL_ID", id));
    }

    public List<Map<String, Object>> listRoles() {
        String sql = ""
          + "SELECT "
          + "  rol_id      AS \"ROL_ID\", "
          + "  nombre      AS \"NOMBRE\", "
          + "  descripcion AS \"DESCRIPCION\" "
          + "FROM ROL "
          + "ORDER BY rol_id";
        return jdbc.queryForList(sql);
    }


}

