package com.puyixiaowo.fbook.utils;

import com.alibaba.fastjson.JSON;
import com.puyixiaowo.fbook.constants.Constants;
import com.puyixiaowo.fbook.enums.EnumsRedisKey;
import com.puyixiaowo.fbook.error.ErrorHandler;
import org.sql2o.Connection;
import org.yaml.snakeyaml.Yaml;

import java.io.File;
import java.util.List;
import java.util.Map;

/**
 * @author feihong
 * @date 2017-08-12
 */
public class ConfigUtils {

    private static final String ADMIN_CONFIG_FILE = "conf/admin_auth.yaml";
    private static final String BOOK_CONFIG_FILE = "conf/book_auth.yaml";
    private static final String IGNORE_LIST = "ignore_list";
    private static final String PASS_DES_KEY = "pass_des_key";

    private static final String PATH_JDBC_PROPERTIES = "jdbc.properties";
    private static final String FOLDER_SQL = "sql";

    public static String DB_FILE_NAME = "";

    /**
     * 初始化配置有顺序
     */
    public static void init() {
        initRedis();
        initDB();
        initBookConfig();
        ErrorHandler.init();
    }

    /**
     * 初始化数据库
     */
    public static void initDB() {
        //设置临时目录路径
        String sqliteTempDir = System.getProperty("user.dir") + "/sqlite_temp";
        File tempFile = new File(sqliteTempDir);
        if (!tempFile.exists()) {
            tempFile.mkdirs();
        }
        System.setProperty("org.sqlite.tmpdir",
                sqliteTempDir);

        try {
            DBUtils.initDB(PATH_JDBC_PROPERTIES);
        } catch (Exception e) {
            e.printStackTrace();
        }

        String url = DBUtils.getDbProperties().getProperty("url");

        DB_FILE_NAME = url.substring(url.lastIndexOf(":") + 1);

        if (!new File(DB_FILE_NAME).exists()) {
            //创建数据库文件
            try (Connection conn = DBUtils.getSql2o().open()) {

                String[] filenames = ResourceUtils.getResourceFolderFiles(FOLDER_SQL);
                FileUtils.runResourcesSql(conn, FOLDER_SQL, filenames);
            }
            //清空redis
            EnumsRedisKey[] enumsRedisKeys = EnumsRedisKey.values();
            String[] keys = new String[enumsRedisKeys.length];
            for (int i = 0; i < enumsRedisKeys.length; i++) {
                keys[i] = enumsRedisKeys[i].key + "*";
            }
            RedisUtils.delete(keys);
        }
    }

    private static void initBookConfig() {
        Yaml yaml = new Yaml();
        Object obj = yaml.load(ResourceUtils.readFile(BOOK_CONFIG_FILE));

        if (!(obj instanceof Map)) {
            throw new RuntimeException("书登录链接配置不正确");
        }
        Map<String, Object> map = (Map) obj;

        Object ignoresObj = map.get(IGNORE_LIST);

        List<String> ignores = null;
        if (ignoresObj instanceof List) {
            ignores = (List<String>) ignoresObj;
        }
        if (ignores == null) {
            throw new RuntimeException("书用户权限配置不正确");
        }

        RedisUtils.set(EnumsRedisKey.REDIS_KEY_IGNORE_CONF_BOOK.key, JSON.toJSONString(ignores));
    }

    /**
     * 初始化redis配置
     */
    private static void initRedis() {
        RedisUtils.testConnection();
    }
}
