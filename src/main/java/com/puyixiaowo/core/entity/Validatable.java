package com.puyixiaowo.core.entity;

import com.alibaba.fastjson.JSON;
import com.puyixiaowo.core.exceptions.ValidationException;
import com.puyixiaowo.fbook.annotation.NotNull;
import com.puyixiaowo.fbook.utils.StringUtils;

import java.lang.reflect.Field;
import java.util.LinkedHashMap;

/**
 * @author feihong
 * @date 2017-08-10
 */
public abstract class Validatable {

    public void validate() throws ValidationException {
        LinkedHashMap<String, String> map = new LinkedHashMap<>();

        Field [] fields = this.getClass().getDeclaredFields();
        for (Field field : fields) {
            field.setAccessible(true);
            NotNull notnull = field.getAnnotation(NotNull.class);
            if (notnull == null) {
                continue;
            }
            String message = notnull.message();

            if (StringUtils.isBlank(message)) {
                message = field.getName() + "不能为空";
            }

            try {
                if (StringUtils.isBlank(field.get(this))) {
                    map.put(field.getName(), message);
                }
            } catch (IllegalAccessException e) {


            }

        }
        if (!map.isEmpty()) {

            throw new ValidationException(JSON.
                    toJSONString(StringUtils.join(map.values().
                            toArray(), ",")));
        }
    }
}
