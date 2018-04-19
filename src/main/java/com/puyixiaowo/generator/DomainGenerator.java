package com.puyixiaowo.generator;

import com.puyixiaowo.generator.enums.TypeEnums;
import com.puyixiaowo.generator.model.GField;
import com.puyixiaowo.generator.utils.FileUtils;
import com.puyixiaowo.fbook.utils.CamelCaseUtils;
import com.puyixiaowo.fbook.utils.StringUtils;

import java.io.File;
import java.io.IOException;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Moses
 * @date 2017-08-16
 */
public class DomainGenerator {

    private static String db;


    /**
     * 生成实体工具
     * @param dbhost
     * <p>数据库路径</p>
     * @param tables
     * <p>要生成的表名</p>
     * <p>多个用英文逗号分隔</p>
     * @param srcBase
     * <p>要生成的实体类src目录，如：src/main/java</p>
     * @param domainPackage
     * <p>要生成的实体类所在包名，如：com.puyixiaowo.yiyi.domain</p>
     */
    public static void generateDomains(String dbhost,
                                        String tables,
                                        String srcBase,
                                        String domainPackage) {
        db = dbhost;

        String [] tableArr = tables.split(",");

        for (String tableName :
                tableArr) {
            generate(srcBase, tableName, domainPackage);
        }

    }

    private static void generate(String src,
                                 String tableName,
                                 String domainPackage) {


        List<GField> fieldList = new ArrayList<>();
        try {
            Class.forName("org.sqlite.JDBC");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }


        try (
                Connection conn = DriverManager.getConnection("jdbc:sqlite:" + db)) {

            try (Statement statement = conn.createStatement();
                 ResultSet resultSet = statement.executeQuery("PRAGMA table_info([" + tableName + "])")
            ) {

                while (resultSet.next()) {
                    String columnName = resultSet.getString("name");
                    String fieldName = CamelCaseUtils.toCamelCase(columnName);
                    String jdbcType = resultSet.getString("type");
                    String javaType = TypeEnums.getJavaType(jdbcType);
                    fieldList.add(new GField(fieldName, columnName, javaType, jdbcType));
                }


                String basePath = System.getProperty("user.dir").replaceAll("\\\\", "/");

                String domainPath = basePath + "/"
                        + src.replaceAll("\\\\",
                        "/") + "/" + domainPackage
                        .replaceAll("\\.", "/") + "/";

                String className = StringUtils.firstToUpperCase(CamelCaseUtils.toCamelCase(tableName));
                String filename = domainPath + className + ".java";
                File file = new File(filename);

                if (file.exists()) {
                    file.delete();
                }
                file.createNewFile();

                //package
                FileUtils.appendToFile(filename, "package "
                        + domainPackage + ";\n\n");
                //import
                FileUtils.appendToFile(filename, "import java.io.Serializable;\n\n");
//                FileUtils.appendToFile(filename, "import com.puyixiaowo.core.entity.Validatable;\n\n");

                //class
                FileUtils.appendToFile(filename, "public class "
                        + className + " implements Serializable {\n");

                FileUtils.appendToFile(filename,
                        "\tprivate static final long serialVersionUID = 1L;\n\n");

                //fields
                for (GField gField :
                        fieldList) {
                    String str = "\tprivate "
                            + gField.getJavaType() + " "
                            + gField.getName()
                            + ";\n";

                    FileUtils.appendToFile(filename, str);
                }


                //setter,getter

                for (GField field :
                        fieldList) {
                    String getter = "\n\n\tpublic " + field.getJavaType() + " get"
                            + StringUtils.firstToUpperCase(field.getName())
                            + " (){\n\t\treturn " + field.getName() + ";\n\t}";

                    String setter = "\n\n\tpublic void set"
                            + StringUtils.firstToUpperCase(field.getName())
                            + " (" + field.getJavaType() + " " + field.getName()
                            + "){\n\t\tthis." + field.getName() + " = "
                            + field.getName() + ";\n\t}";

                    FileUtils.appendToFile(filename, getter + setter);
                }


                //
                FileUtils.appendToFile(filename, "\n}");

            } catch (IOException e) {
                e.printStackTrace();
            }

        } catch (SQLException e) {
            System.err.println(e.getClass().getName() + ":" + e.getMessage());
            System.exit(0);
        }

    }
}
