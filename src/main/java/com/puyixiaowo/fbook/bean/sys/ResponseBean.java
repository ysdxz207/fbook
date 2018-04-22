/**
 *
 */
package com.puyixiaowo.fbook.bean.sys;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.annotation.JSONField;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.puyixiaowo.fbook.bean.error.Error;
import com.puyixiaowo.fbook.constants.Constants;
import com.puyixiaowo.fbook.utils.StringUtils;

import java.io.Serializable;

/**
 * @author huangfeihong
 * @date 2016年12月6日 下午9:24:25
 */
public class ResponseBean implements Serializable {

	private static final long serialVersionUID = -5266170746828998914L;
	private int statusCode = Constants.RESPONSE_STATUS_CODE_SUCCESS;
	private String errorCode = "SUCCESS";
	private String message = Constants.RESPONSE_SUCCESS_MESSAGE;
	private Object data;


	public int getStatusCode() {
		return statusCode;
	}

	public void setStatusCode(int statusCode) {
		this.statusCode = statusCode;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public Object getData() {
		return data;
	}

	public void setData(Object data) {
		this.data = data;
	}

	public String getErrorCode() {
		return errorCode;
	}

	public void setErrorCode(String errorCode) {
		this.errorCode = errorCode;
	}

	//////////////////////////////

	/**
	 * 序列化
	 *
	 * @param data
	 */
	public void setSerializeData(Object data) {
		this.data = JSONObject.parse(JSON.toJSONString(data, SerializerFeature.DisableCircularReferenceDetect));
	}

	public ResponseBean errorMessage(String message) {
		return errorMessage("ERROR", message);
	}

	public ResponseBean errorMessage(String errorCode,
									 String message) {
		this.errorCode = errorCode;
		this.message = message;
		this.statusCode = Constants.RESPONSE_STATUS_CODE_ERROR;
		return this;
	}

	public ResponseBean error(Exception e) {
		String err = e.getMessage();
		if (StringUtils.isBlank(err)) {
			err = JSON.toJSONString(e);
		}
		errorMessage(err);
		return this;
	}

	public ResponseBean error(Error e) {
		errorMessage(e.getCode(), e.getMsg());
		return this;
	}

	/**
	 * 序列化
	 *
	 * @return
	 */
	public String serialize() {
		return JSON.toJSONString(this);
	}

	@Override
	public String toString() {
		return serialize();
	}
}
