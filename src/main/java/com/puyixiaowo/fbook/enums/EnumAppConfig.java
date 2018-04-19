package com.puyixiaowo.fbook.enums;
/**
 * 
 * @author Moses
 * @date 2017-09-01
 * 
 */
public enum EnumAppConfig {
    ARG_PORT("-p", "port", "端口参数");

    EnumAppConfig(String arg, String field, String description) {
        this.arg = arg;
        this.field = field;
        this.description = description;
    }

    public String arg;
    public String field;
    public String description;

    public static EnumAppConfig getEnum(String arg) {

        for (EnumAppConfig enumAppConfig :
                EnumAppConfig.values()) {
            if (enumAppConfig.arg.equals(arg)) {
                return enumAppConfig;
            }
        }
        return null;
    }
}
