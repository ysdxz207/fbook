package com.puyixiaowo.generator.enums;

import com.puyixiaowo.fbook.utils.StringUtils;

/**
 * @author Moses
 * @date 2017-08-15
 */
public enum TypeEnums {

    INTEGER("INTEGER", "Integer"),
    INT("INT", "Integer"),
    REAL("REAL", "Double"),
    TEXT("TEXT", "String"),
    VARCHAR("VARCHAR", "String"),
    DATETIME("DATETIME", "Date"),
    BOOLEAN("BOOLEAN", "Boolean"),
    BLOB("BLOB", "String");


    TypeEnums(String jdbcType, String javaType) {
        this.jdbcType = jdbcType;
        this.javaType = javaType;
    }

    public static String getJavaType(String jdbcType){
        if (jdbcType.indexOf("(") != -1) {
            Integer length = StringUtils.parseInteger(jdbcType);
            jdbcType = jdbcType.substring(0, jdbcType.indexOf("("));
            if ((jdbcType.equalsIgnoreCase(TypeEnums.INT.jdbcType)
                    || jdbcType.equalsIgnoreCase(TypeEnums.INTEGER.jdbcType))
                    && length > 6) {
                return "Long";
            }
        }
        for (TypeEnums typeEnums : TypeEnums.values()) {
            if (jdbcType.equalsIgnoreCase(typeEnums.jdbcType)) {
                return typeEnums.javaType;
            }
        }
        return "Undefined";
    }


    public String jdbcType;
    public String javaType;
}
