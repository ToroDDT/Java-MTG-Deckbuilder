package com.example.mtg_deckbuilder.utils;

import java.util.Map;

public class SqlBuilder {
    private final String sql;

    public static class Builder {
        private final String sql;

        public Builder(String sql) {
            this.sql = sql;
        }
        public Builder whereCMC(StringBuilder sql, Map<String, Object> params, String cmcInput) {
            if (!cmcInput.isBlank()) {
                sql.append(" AND cmc = :cmc");
                params.put("cmc", cmcInput);
            }
        }

        public SqlBuilder build() {
        }
    }

    public SqlBuilder(Builder builder) {
    }
}
