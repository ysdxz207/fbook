package com.puyixiaowo.generator.utils;

import com.alibaba.fastjson.serializer.JSONSerializer;
import com.alibaba.fastjson.serializer.ObjectSerializer;
import com.alibaba.fastjson.serializer.SerializeWriter;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * json格式日期自定义序列化
 * @author huangfeihong
 * @date 2016年12月21日
 */
public class CustomDateTimeSerializer implements ObjectSerializer {

	@Override
	public void write(JSONSerializer serializer, Object object,
                      Object fieldName, java.lang.reflect.Type fieldType, int features)
			throws IOException {
		SerializeWriter out = serializer.getWriter();
		Long value = (Long) object;
        if (value == null){
        	out.write("\"\"");
        	return;
        }
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		//System.out.println(sdf.format(value * 1000));
		out.write("\"" + sdf.format(new Date(value)) + "\"");
	}
}