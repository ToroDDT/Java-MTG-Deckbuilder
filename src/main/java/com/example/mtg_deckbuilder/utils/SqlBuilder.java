package com.example.mtg_deckbuilder.utils;

import java.util.ArrayList;
import java.util.List;

public class SqlBuilder {

  private final String sql;

  public static class Builder {

    private String sql;

    List<String> sqlConditions = new ArrayList<>();

    public Builder(String sql) {
      this.sql = sql;
    }

    public Builder whereCMC(Boolean includeCMC, String cmcInequality) {
      if (includeCMC) {
        Inequality expression = SearchParametersParser.parseInequality(cmcInequality);
        String sql = "AND cmc " + expression.operator() + " :cmc";
        sqlConditions.add(sql);
        return this;
      }
      return this;
    }

    public Builder whereName(Boolean includeName, String name) {
      if (includeName) {
        String sql = "AND name ILIKE CONCAT('%', :name, '%')";
        sqlConditions.add(sql);
        return this;
      }
      return this;
    }

    public Builder wherePower(Boolean includePower, String powerInequality) {
      if (includePower) {
        Inequality expression = SearchParametersParser.parseInequality(powerInequality);
        String sql = "AND power " + expression.operator() + " :power";
        sqlConditions.add(sql);
        return this;
      }
      return this;
    }

    public Builder whereType(String type) {
      if (!type.isBlank()) {
        sqlConditions.add("AND type = :type");
        return this;
      }
      return this;
    }

    public SqlBuilder build() {
      StringBuilder stringBuilder = new StringBuilder();
      stringBuilder.append(sql).append(" ");
      for (String condition : sqlConditions) {
        stringBuilder.append(condition).append(" ");
      }
      this.sql = stringBuilder.toString();
      return new SqlBuilder(this);
    }
  }

  public SqlBuilder(Builder builder) {
    this.sql = builder.sql;
  }

  public String getSql() {
    return sql;
  }
}
