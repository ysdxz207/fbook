package com.puyixiaowo.fbook.utils;

import java.util.UUID;

/**
 * ID生成工具
 * @author feihong
 * @date 2017-08-10
 */
public class IdUtils {
   private static SnowflakeIdWorker idWorker;

    static {
        idWorker = new SnowflakeIdWorker(0, 0);
    }

    public static Long generateId(){

        return idWorker.nextId();
    }

    public static String generateUUId(){

        UUID uuid = UUID.randomUUID();

        return uuid.toString().replaceAll("-", "");
    }

    public static void main(String[] args) {
        System.out.println(generateId());
        System.out.println(generateUUId());
    }
}
