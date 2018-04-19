package com.puyixiaowo.fbook.annotation.admin;

import java.lang.annotation.*;

/**
 *
 * @author Moses
 * @date 2017-08-27
 * 权限控制
 */
@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented//说明该注解将被包含在javadoc中
public @interface RequiresPermissions {
    String[] value();

    Logical logical() default Logical.AND;
}
