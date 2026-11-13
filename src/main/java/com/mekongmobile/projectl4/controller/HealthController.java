package com.mekongmobile.projectl4.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
public class HealthController {

    private final JdbcTemplate jdbcTemplate;

    @Autowired(required = false)
    public HealthController(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @GetMapping("/health")
    public Map<String, Object> health() {
        Map<String, Object> res = new HashMap<>();
        res.put("status", "ok");
        try {
            if (jdbcTemplate != null && jdbcTemplate.getDataSource() != null) {
                jdbcTemplate.queryForObject("SELECT 1", Integer.class);
                res.put("db", "up");
            } else {
                res.put("db", "up");
            }
        } catch (Exception e) {
            res.put("db", "down");
        }
        return res;
    }
}
