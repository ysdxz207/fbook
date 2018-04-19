package com.puyixiaowo.fbook.annotation;

import java.lang.annotation.*;

/**
 * 标明非数据库字段
 */
@Retention(RetentionPolicy.RUNTIME) // 注解会在class字节码文件中存在，在运行时可以通过反射获取到
@Target(ElementType.FIELD)
@Documented//说明该注解将被包含在javadoc中
public @interface Transient {

    public String value() default "";
}
