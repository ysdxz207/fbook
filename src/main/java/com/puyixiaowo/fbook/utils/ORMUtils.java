package com.puyixiaowo.fbook.utils;

import com.puyixiaowo.fbook.annotation.Id;
import com.puyixiaowo.fbook.annotation.Table;
import com.puyixiaowo.fbook.exception.DBSqlException;
import spark.utils.Assert;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author feihong
 * @date 2017-08-11
 */
public class ORMUtils {

    public static List<String> getPrimaryKeyGetterList(Object obj) {
        Assert.notNull(obj, "Object should not be null.");

        List<String> primaryKeyGetterList = new ArrayList<>();

        Field [] fields = obj.getClass().getDeclaredFields();
        String defaultPrimaryKeyGetter = "getId";

        for (Field field :
                fields) {
            Id id = field.getAnnotation(Id.class);
            if (id == null) {
                continue;
            }

            String fieldName = field.getName();
            String primaryKeyGetter = "get" + fieldName.substring(0, 1)
                    .toUpperCase() + fieldName.substring(1);
            primaryKeyGetterList.add(primaryKeyGetter);
        }

        if (primaryKeyGetterList.isEmpty()) {
            primaryKeyGetterList.add(defaultPrimaryKeyGetter);
        }

        return primaryKeyGetterList;
    }

    public static Map<String, Object> getPrimaryKeyValues(Object obj){
        Assert.notNull(obj, "Object should not be null.");
        Map<String ,Object> map = new HashMap<>();
        List<String> primaryKeyGetterList = getPrimaryKeyGetterList(obj);

        for (String primaryKeyGetter :
                primaryKeyGetterList) {
            try {
                Object id = ReflectionUtils.invokeMethod(obj, primaryKeyGetter, null, null);

                map.put(primaryKeyGetter.substring(3).
                        substring(0, 1).toLowerCase()
                        + primaryKeyGetter.substring(4), id);
            } catch (Exception e) {
                throw new DBSqlException("Can not invoke " + primaryKeyGetter + " method.");
            }
        }

        return map;
    }

    /**
     * 获取注解字段对应数据库表的列名
     * @param field
     * @return
     */
    public static String getFieldColumnName(Field field){
        if ("serialVersionUID".equals(field.getName())) {
            return field.getName();
        }
        com.puyixiaowo.fbook.annotation.Field f = field.getAnnotation(com.puyixiaowo.fbook.annotation.Field.class);

        if (f == null) {

            return CamelCaseUtils.toUnderlineName((field.getName()));
        }
        return f.value();
    }


    public static Field[] getFieldListByClass(Class clazz) {
        try {
            return Class.forName(clazz.getName()).getDeclaredFields();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        return new Field[0];
    }

    public static String getTableNameByClass(Class clazz) {
        String className = null;
        String tableName = null;


        try {
            Class cl = Class.forName(clazz.getName());

            Table table = (Table) cl.getAnnotation(Table.class);
            if (table != null) {
                tableName = table.value();
            }
            if (StringUtils.isBlank(tableName)) {
                className = cl.getSimpleName();
                tableName = StringUtils.firstToLowerCase(className);
            }
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        return tableName;
    }


    public static void setId(Object obj) {
        ReflectionUtils.setFieldValue(obj, "id", IdUtils.generateId());
    }

    public static Object getId(Object obj) {
        return ReflectionUtils.getFieldValue(obj, "id");
    }

}
