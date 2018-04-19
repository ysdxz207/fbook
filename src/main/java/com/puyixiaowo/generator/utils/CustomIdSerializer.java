package com.puyixiaowo.generator.utils;

import com.alibaba.fastjson.serializer.JSONSerializer;
import com.alibaba.fastjson.serializer.ObjectSerializer;
import com.alibaba.fastjson.serializer.SerializeWriter;

import java.io.IOException;
import java.lang.reflect.Type;

/**
 * @author Moses
 * @date 2017-08-23
 */
public class CustomIdSerializer implements ObjectSerializer {
    @Override
    public void write(JSONSerializer jsonSerializer, Object object, Object fieldName, Type fieldType, int features) throws IOException {
        SerializeWriter out = jsonSerializer.getWriter();
        Long value = (Long) object;
        if (value == null){
            out.write("\"\"");
            return;
        }
        out.write("\"" + value.toString() + "\"");
    }
}
