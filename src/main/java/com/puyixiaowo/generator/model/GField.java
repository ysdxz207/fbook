package com.puyixiaowo.generator.model;

/**
 * @author Moses
 * @date 2017-08-15
 */
public class GField {
    private String name;
    private String columnName;
    private String javaType;
    private String jdbcType;


    public GField(String name, String columnName, String javaType, String jdbcType) {
        this.name = name;
        this.columnName = columnName;
        this.javaType = javaType;
        this.jdbcType = jdbcType;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getColumnName() {
        return columnName;
    }

    public void setColumnName(String columnName) {
        this.columnName = columnName;
    }

    public String getJavaType() {
        return javaType;
    }

    public void setJavaType(String javaType) {
        this.javaType = javaType;
    }

    public String getJdbcType() {
        return jdbcType;
    }

    public void setJdbcType(String jdbcType) {
        this.jdbcType = jdbcType;
    }
}
