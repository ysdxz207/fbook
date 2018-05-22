package com.puyixiaowo.fbook.controller;

import com.alibaba.fastjson.JSONObject;
import com.google.code.kaptcha.Producer;
import com.puyixiaowo.fbook.bean.UserBean;
import com.puyixiaowo.fbook.bean.book.BookReadSettingBean;
import com.puyixiaowo.fbook.bean.error.LoginError;
import com.puyixiaowo.fbook.bean.error.UserError;
import com.puyixiaowo.fbook.bean.sys.ResponseBean;
import com.puyixiaowo.fbook.constants.Constants;
import com.puyixiaowo.fbook.exception.DBObjectExistsException;
import com.puyixiaowo.fbook.service.LoginService;
import com.puyixiaowo.fbook.service.UserService;
import com.puyixiaowo.fbook.service.book.BookReadSettingService;
import com.puyixiaowo.fbook.utils.DBUtils;
import com.puyixiaowo.fbook.utils.DesUtils;
import com.puyixiaowo.fbook.utils.Md5Utils;
import com.puyixiaowo.fbook.utils.StringUtils;
import com.puyixiaowo.fbook.utils.captcha.CaptchaProducer;
import com.puyixiaowo.fbook.utils.pickrules.impl.DefaultPickRulesTemplateImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import spark.Request;
import spark.Response;

import javax.imageio.ImageIO;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import static spark.Spark.halt;

/**
 * @author Moses
 * @date 2017-08-08
 * 登录
 */
public class LoginController extends BaseController {

    private static final Logger logger = LoggerFactory.getLogger(LoginController.class);


    private static Producer producer= new CaptchaProducer();


    public static ResponseBean login(Request request,
                                     Response response) {

        ResponseBean responseBean = new ResponseBean();

        String uname = request.queryParams("uname");
        String upass = request.queryParams("upass");
        String captcha = request.queryParams("captcha");

        if (StringUtils.isBlank(uname)) {
            responseBean.error(LoginError.LOGIN_NO_USERNAME);
            return responseBean;
        }

        if (StringUtils.isBlank(upass)) {
            responseBean.error(LoginError.LOGIN_NO_PASSWORD);
            return responseBean;
        }

        if (StringUtils.isBlank(captcha)) {
            responseBean.error(LoginError.LOGIN_NO_CAPTCHA);
            return responseBean;
        }


        String sessionCaptcha = request.session().attribute(Constants.KAPTCHA_SESSION_KEY);

        logger.info("验证码是：[" + sessionCaptcha + "]");

        if (!captcha.equalsIgnoreCase(sessionCaptcha)) {
            responseBean.error(LoginError.LOGIN_WRONG_CAPTCHA);
            return responseBean;
        }


        return doLogin(Constants.COOKIE_LOGIN_KEY_BOOK,
                uname, Md5Utils.md5Password(upass), request, response);
    }

    private static ResponseBean doLogin(String cookieKey,
                                        String uname,
                                String upassEncrypt,
                                Request request,
                                Response response) {

        ResponseBean responseBean = new ResponseBean();

        Map<String, Object> params = new HashMap<>();

        params.put("loginname", uname);
        params.put("password", upassEncrypt);

        try {
            UserBean userBean = LoginService.login(params);
            if (userBean == null) {
                responseBean.error(LoginError.LOGIN_WRONG_PASSWORD);
                return responseBean;
            } else {
                //登录成功
                rememberMe(cookieKey, request, response, userBean);
                userBean.setPassword(null);
                request.session().attribute(Constants.SESSION_USER_KEY, userBean);
                responseBean.setData(userBean);
                return responseBean;
            }
        } catch (Exception e) {
            e.printStackTrace();
            responseBean.errorMessage("登录异常：" + e.getMessage());
            return responseBean;
        }
    }

    public static UserBean rememberMe(String cookieKey,
                                      Request request,
                                   Response response,
                                   UserBean userBean) {

        if (userBean != null) {
            String cookieStr = userBean.getLoginname() + "_" + userBean.getPassword();
            response.cookie(cookieKey,
                    DesUtils.encrypt(cookieStr), 24*3600*365);
            return userBean;
        }

        userBean = new UserBean();
        String str = request.cookie(cookieKey);

        if (StringUtils.isNotBlank(str)) {
            String [] strArr = DesUtils.decrypt(str).split("_");
            userBean.setLoginname(strArr[0]);
            userBean.setPassword(strArr[1]);
            return userBean;
        } else {
            String uname = request.queryParams("uname");
            String upass = request.queryParams("upass");
            if (StringUtils.isBlank(uname)
                    && StringUtils.isBlank(upass)) {
                return null;
            }
            userBean.setLoginname(uname);
            if (StringUtils.isNotBlank(upass)) {
                userBean.setPassword(DesUtils.encrypt(upass));
            }
        }
        return userBean;
    }

    /**
     * 退出登录
     *
     * @param request
     * @param response
     * @return
     */
    public static Object logout(Request request, Response response) {
        ResponseBean responseBean = new ResponseBean();
        request.session().removeAttribute(Constants.SESSION_USER_KEY);

        response.removeCookie("/", Constants.COOKIE_LOGIN_KEY_BOOK);
        return responseBean;
    }

    /**
     * 验证码
     * @param request
     * @param response
     */
    public static Object captcha(Request request,
                        Response response) {


        HttpSession session = request.session().raw();
        HttpServletResponse res = response.raw();

        res.setDateHeader("Expires", 0);

        // Set standard HTTP/1.1 no-cache headers.
        res.setHeader("Cache-Control", "no-store, no-cache, must-revalidate");

        // Set IE extended HTTP/1.1 no-cache headers (use addHeader).
        res.addHeader("Cache-Control", "post-check=0, pre-check=0");

        // Set standard HTTP/1.0 no-cache header.
        res.setHeader("Pragma", "no-cache");

        // return a jpeg
        res.setContentType("image/jpeg");



        // create the text for the image
        String capText = producer.createText();

        // store the text in the session
        session.setAttribute(Constants.KAPTCHA_SESSION_KEY, capText);

        // create the image with the text
        ServletOutputStream out = null;
        try {
            BufferedImage bi = producer.createImage(capText);
            out = res.getOutputStream();
            // write the data out
            ImageIO.write(bi, "jpg", out);
            out.flush();
        } catch (Exception e) {
            e.printStackTrace();
        } finally{
            if (out != null) {
                try {
                    out.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return "";
    }

    public static void rememberMeLogin(String cookieKey,
                                       Request request,
                                       Response response) {



        ResponseBean responseBean = new ResponseBean();

        UserBean userBean = rememberMe(cookieKey,
                request, response, null);

        if (userBean == null) {
            responseBean.errorMessage("请先登录");
            responseBean.setStatusCode(401);
            halt(responseBean.serialize());
            return;
        }

        responseBean = doLogin(cookieKey,
                userBean.getLoginname(), userBean.getPassword(), request, response);
        if (responseBean.getStatusCode() != 200) {
            //移除cookie
            logout(request, response);
            halt(responseBean.serialize());
            return;
        } else {
            //登录成功跳转首页
            response.redirect("/");
        }
    }

    public static ResponseBean register(Request request,
                                        Response response) {
        ResponseBean responseBean = new ResponseBean();

        try {
            UserBean userBean = getParamsEntity(request, UserBean.class, true, false);

            if (StringUtils.isBlank(userBean.getNickname())) {
                userBean.setNickname("大帅比" + (System.currentTimeMillis() + "").substring(4));
            }
            userBean.setPassword(Md5Utils.md5Password(userBean.getPassword()));
            DBUtils.insertOrUpdate(userBean, false);
            //创建用户信息
            BookReadSettingBean bookReadSettingBean = new BookReadSettingBean();
            bookReadSettingBean.setUserId(userBean.getId());
            bookReadSettingBean.setSearchSource(DefaultPickRulesTemplateImpl.class.getName());
            DBUtils.insertOrUpdate(bookReadSettingBean, false);
            //注册完直接登录
            request.session().attribute(Constants.SESSION_USER_KEY, userBean);
            responseBean.setData(userBean);
        } catch (DBObjectExistsException e) {
            responseBean.errorMessage("用户名或昵称已存在");
        } catch (Exception e) {
            responseBean.error(e);
        }

        return responseBean;
    }


    public static ResponseBean editUserInfo(Request request,
                                      Response response) {
        ResponseBean responseBean = new ResponseBean();
        try {
            UserBean userBean = getParamsEntity(request, UserBean.class, false, false);
            UserBean userBeanSession = request.session().attribute(Constants.SESSION_USER_KEY);


            UserBean userBeanDB = UserService.selectUserById(userBeanSession.getId());

            if (userBeanDB == null) {
                return responseBean.error(UserError.NOT_EXISTS_ERROR);
            }

            userBeanDB.setNickname(userBean.getNickname());

            DBUtils.insertOrUpdate(userBeanDB, false);
            //更新session
            request.session().attribute(Constants.SESSION_USER_KEY, userBeanDB);

        } catch (Exception e) {
            responseBean.error(e);
        }

        return responseBean;
    }

    public static ResponseBean getUserSetting(Request request,
                                                 Response response) {
        ResponseBean responseBean = new ResponseBean();
        try {
            UserBean userBean = request.session().attribute(Constants.SESSION_USER_KEY);

            BookReadSettingBean bookReadSettingBean = BookReadSettingService.getUserReadSetting(userBean.getId());

            JSONObject json = new JSONObject();
            userBean.setPassword(null);
            json.put("user", userBean);
            json.put("bookReadSetting", bookReadSettingBean);
            responseBean.setData(json);
        } catch (Exception e) {
            responseBean.error(e);
        }

        return responseBean;
    }

    public static void main(String[] args) {
        System.out.println(DefaultPickRulesTemplateImpl.class.getName());
    }
}
