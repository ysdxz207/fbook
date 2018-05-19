package com.puyixiaowo.fbook.service;

import com.puyixiaowo.fbook.bean.UserBean;
import com.puyixiaowo.fbook.utils.DBUtils;

/**
 * @author Moses
 * @date 2017-08-19
 */
public class UserService {


    public static UserBean selectUserById(Long id) {
        if (id == null) {
            return null;
        }
        UserBean userBean = new UserBean();
        userBean.setId(id);
        return DBUtils.selectOne("select * from user where id=:id",
                userBean);
    }
}
