package com.puyixiaowo.fbook.utils.pickrules;

import com.itranswarp.compiler.JavaStringCompiler;
import com.puyixiaowo.fbook.bean.book.PickRulesBean;
import com.puyixiaowo.fbook.utils.StringUtils;
import com.puyixiaowo.fbook.utils.pickrules.impl.DefaultPickRulesTemplateImpl;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Map;

/**
 *
 * @author Moses
 * @date 2018-05-17 18:17:14
 * 爬取规则工具
 *
 */

public class PickRulesUtils {
    public static PickRulesTemplate pickRulesTemplate = new DefaultPickRulesTemplateImpl();


    private static final String PACKAGE_NAME_IMPL = DefaultPickRulesTemplateImpl.class.getPackage().getName();
    private static final String PACKAGE_NAME_INTERFACE = PickRulesTemplate.class.getPackage().getName();
    private static final String CLASS_NAME_DEFAULT_TEMPLATE_IMPL = "DynamicPickRulesTemplateImpl";
    private static final String CLASS_DEFAULT_TEMPLATE_IMPL = PACKAGE_NAME_IMPL + ".DefaultPickRulesTemplateImpl";
    private static final String CLASS_TEMPLATE_IMPL = PACKAGE_NAME_IMPL + "." + CLASS_NAME_DEFAULT_TEMPLATE_IMPL;

    private static String buildPickRulesTemplateString(PickRulesBean pickRulesBean) {

        if (pickRulesBean == null) {
            return "";
        }

        StringBuilder sb = new StringBuilder();
        try {

            sb.append("package " + PACKAGE_NAME_IMPL + ";\n\n");
            sb.append("import com.puyixiaowo.fbook.bean.book.BookBean;\n\n");
            sb.append("import org.jsoup.*;\n\n");
            sb.append("import org.jsoup.nodes.*;\n\n");
            sb.append("import org.jsoup.select.*;\n\n");
            sb.append("import java.util.regex.*;\n\n");
            sb.append("import java.util.*;\n\n");
            sb.append("import com.alibaba.fastjson.*;\n\n");
            sb.append("import static com.puyixiaowo.fbook.utils.StringUtils.*;\n\n");
            sb.append("import static com.puyixiaowo.fbook.utils.HtmlUtils.accessPage;\n\n");
            sb.append("public class " + CLASS_NAME_DEFAULT_TEMPLATE_IMPL + " extends " + CLASS_DEFAULT_TEMPLATE_IMPL + " implements " + PACKAGE_NAME_INTERFACE + ".PickRulesTemplate {");

            Field[] fields = pickRulesBean.getClass().getDeclaredFields();
            for (Field field : fields) {
                String fieldName = field.getName();
                if ("serialVersionUID".equalsIgnoreCase(fieldName)) {
                    continue;
                }
                Method m = pickRulesBean.getClass().getMethod("get" + fieldName.substring(0, 1).toUpperCase() + fieldName.substring(1));
                Object fieldValue = m.invoke(pickRulesBean);
                if (StringUtils.isBlank(fieldValue)) {
                    continue;
                }
                sb.append("\n\n");
                sb.append(fieldValue);
            }

            sb.append("\n");
            sb.append("}");

        } catch (Exception e) {
            e.printStackTrace();
        }
        return sb.toString();
    }

    public static void updatePickRulesTemplate(PickRulesBean pickRulesBean) {
        try {

            String templateString = buildPickRulesTemplateString(pickRulesBean);

            if (StringUtils.isBlank(templateString)) {
                System.out.println("模版未初始化");
                return;
            }
            System.out.println(templateString);
            JavaStringCompiler compiler = new JavaStringCompiler();
            Map<String, byte[]> results = compiler.compile(CLASS_NAME_DEFAULT_TEMPLATE_IMPL + ".java", templateString);
            Class<?> clazz = compiler.loadClass(CLASS_TEMPLATE_IMPL, results);
            pickRulesTemplate = (PickRulesTemplate) clazz.newInstance();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void updatePickRulesTemplate(Class<? extends DefaultPickRulesTemplateImpl> clazz) {
        try {
            pickRulesTemplate = clazz.newInstance();
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    }

}
