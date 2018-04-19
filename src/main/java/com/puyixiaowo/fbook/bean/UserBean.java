package com.puyixiaowo.fbook.bean;

import com.puyixiaowo.core.entity.Validatable;
import com.puyixiaowo.fbook.annotation.NotNull;
import com.puyixiaowo.fbook.annotation.Table;

import java.io.Serializable;

@Table("user")
public class UserBean extends Validatable implements Serializable {
	private static final long serialVersionUID = 1L;

	private Long id;
	@NotNull(message = "用户名不可为空")
	private String loginname;
	private String nickname;
	@NotNull(message = "密码不可为空")
	private String password;
	private Long createTime;
	private String faceUrl;
	private Integer status;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getLoginname() {
		return loginname;
	}

	public void setLoginname(String loginname) {
		this.loginname = loginname;
	}

	public String getNickname() {
		return nickname;
	}

	public void setNickname(String nickname) {
		this.nickname = nickname;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public Long getCreateTime() {
		return createTime;
	}

	public void setCreateTime(Long createTime) {
		this.createTime = createTime;
	}

	public String getFaceUrl() {
		return faceUrl;
	}

	public void setFaceUrl(String faceUrl) {
		this.faceUrl = faceUrl;
	}

	public Integer getStatus() {
		return status;
	}

	public void setStatus(Integer status) {
		this.status = status;
	}

}