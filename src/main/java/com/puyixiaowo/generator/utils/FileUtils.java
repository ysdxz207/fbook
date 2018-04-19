package com.puyixiaowo.generator.utils;

import java.io.FileWriter;
import java.io.IOException;

public class FileUtils {

    /**
     * 向文件中追加内容
     * @param fileName
     * @param content
     */
    public static void appendToFile(String fileName, String content) {
        try {  
            //打开一个写文件器，构造函数中的第二个参数true表示以追加形式写文件  
            FileWriter writer = new FileWriter(fileName, true);
            writer.write(content);  
            writer.close();  
        } catch (IOException e) {  
            e.printStackTrace();  
        }  
    }  
}