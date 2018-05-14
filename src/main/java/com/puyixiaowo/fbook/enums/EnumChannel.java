package com.puyixiaowo.fbook.enums;

/**
 *
 * @author Moses.wei
 * @date 2018-05-12 23:05:28
 * 频道
 */
public enum EnumChannel {
    boy,
    girl,
    unknown;


    public static EnumChannel getEnum(String arg) {

        for (EnumChannel enumAppConfig :
                EnumChannel.values()) {
            if (enumAppConfig.name().equals(arg)) {
                return enumAppConfig;
            }
        }
        return unknown;
    }
}
