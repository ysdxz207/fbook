package com.puyixiaowo.fbook.service;

import com.puyixiaowo.fbook.bean.UserBean;
import com.puyixiaowo.fbook.utils.DBUtils;

import java.util.Map;

/**
 * @author feihong
 * @date 2017-08-06
 */
public class LoginService {

    public static UserBean login(Map<String ,Object> params){
        UserBean userBean = DBUtils.selectOne(UserBean.class,
                "SELECT\n" +
                        "  u.*\n" +
                        "FROM user u\n" +
                        "WHERE loginname = :loginname\n" +
                        "      AND password = :password\n" +
                        "      AND status = 1;",
                params);
        return userBean;
    }
}
