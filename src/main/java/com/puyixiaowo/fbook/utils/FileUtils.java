package com.puyixiaowo.fbook.utils;

import com.alibaba.fastjson.JSON;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.io.IOUtils;
import org.apache.commons.io.LineIterator;
import org.sql2o.Connection;
import org.sql2o.Query;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.*;
import java.nio.channels.FileChannel;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Moses
 * @date 2017-09-03
 */
public class FileUtils {
    public static final String PREFIX = "f_blog_stream2file";
    public static final String SUFFIX = ".tmp";

    /**
     * 运行资源目录下sql
     *
     * @param conn
     * @param folder    目录
     * @param filenames 文件名列表
     */
    public static void runResourcesSql(Connection conn, String folder, String... filenames) {

        if (!folder.substring(folder.length() - 1).equals("/")
                || folder.substring(folder.length() - 1).equals("\\")) {
            folder += "/";
        }

        System.out.println(JSON.toJSONString(filenames));
        for (String filename :
                filenames) {
            List<String> sqlList = readResourcesSql(folder + filename);
            for (String sql :
                    sqlList) {
                System.out.println(sql);
                Query query = conn.createQuery(sql).throwOnMappingFailure(false);
                query.executeUpdate();
            }
        }
    }

    public static List<String> readResourcesSql(String filePath) {
        String sqlStr = readResourceFile(filePath);
        List<String> sqlList = new ArrayList<>();
        if (StringUtils.isBlank(sqlStr)) {
            return sqlList;
        }

        //windows下换行是/r/n，Linux下是/n
        String sqlArr[] = sqlStr.split("(;\\s*\\rr\\n)|(;\\s*\\n)");
        for (int i = 0; i < sqlArr.length; i++) {
            String sql = sqlArr[i].replaceAll("--.*", "").trim();
            if (!"".equals(sql)) {
                sqlList.add(sql);
            }
        }
        return sqlList;
    }

    public static String readResourceFile(String filePath) {
        LineIterator it = null;
        StringBuilder sb = new StringBuilder();
        try {
            it = org.apache.commons.io.FileUtils.lineIterator(stream2file(ResourceUtils.readFile(filePath)), "UTF-8");
            while (it.hasNext()) {
                sb.append(it.nextLine() + "\n");
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            LineIterator.closeQuietly(it);
        }
        return sb.toString();
    }

    public static File stream2file(InputStream in) throws IOException {
        final File tempFile = File.createTempFile(PREFIX, SUFFIX);
        tempFile.deleteOnExit();
        try (FileOutputStream out = new FileOutputStream(tempFile)) {
            IOUtils.copy(in, out);
        }
        return tempFile;
    }

    public static void copyFile(File source, File target) {
        try (FileInputStream inStream = new FileInputStream(source);
             FileOutputStream outStream = new FileOutputStream(target);
             FileChannel in = inStream.getChannel();
             FileChannel out = outStream.getChannel()) {
            in.transferTo(0, in.size(), out);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    public static String file2Base64(File file) {
        String format = "jpg";
        try {
            BufferedImage bufferedImage = ImageIO.read(file);
            final ByteArrayOutputStream os = new ByteArrayOutputStream();

            ImageIO.write(bufferedImage, format, os);
            return Base64.encodeBase64String(os.toByteArray());
        } catch (IOException e) {
            e.printStackTrace();
        }

        return "";
    }

}
