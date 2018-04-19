/**
 *
 */
package com.puyixiaowo.fbook.bean.sys;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.annotation.JSONField;
import com.alibaba.fastjson.serializer.SerializerFeature;
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
	private String message = Constants.RESPONSE_SUCCESS_MESSAGE;
	@JSONField(serialize=false)//防止调serialize()时因取消循环引用而内存溢出
	private Object data;

	//////////
	private boolean closeCurrent = true;//默认关闭当前对话框
	private String tabid;
	private String datagrids;
	private String forward;
	private String forwardConfirm;


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

	public boolean isCloseCurrent() {
		return closeCurrent;
	}

	public void setCloseCurrent(boolean closeCurrent) {
		this.closeCurrent = closeCurrent;
	}

	public String getTabid() {
		return tabid;
	}

	public void setTabid(String tabid) {
		this.tabid = tabid;
	}

	public String getForward() {
		return forward;
	}

	public void setForward(String forward) {
		this.forward = forward;
	}

	public String getForwardConfirm() {
		return forwardConfirm;
	}

	public void setForwardConfirm(String forwardConfirm) {
		this.forwardConfirm = forwardConfirm;
	}

	public String getDatagrids() {
		return datagrids;
	}

	public void setDatagrids(String datagrids) {
		this.datagrids = datagrids;
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

	/**
	 * 序列化
	 *
	 * @return
	 */
	public String serialize() {
		return JSON.toJSONString(this, SerializerFeature.DisableCircularReferenceDetect);
	}

	@Override
	public String toString() {
		return serialize();
	}
}
