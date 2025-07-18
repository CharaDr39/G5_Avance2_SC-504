package com.frenchies.g5_avance2_sc504.config;

import java.util.Map;
import org.springframework.dao.DataAccessException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(DataAccessException.class)
    public ResponseEntity<Map<String,String>> handleDatabaseError(DataAccessException ex) {
        String msg = ex.getRootCause() != null
          ? ex.getRootCause().getMessage()
          : ex.getMessage();
        return ResponseEntity
          .badRequest()
          .body(Map.of("error", msg));
    }
}
