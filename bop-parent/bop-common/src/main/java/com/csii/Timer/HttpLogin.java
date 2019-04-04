/**
 * @description:
 * @author: hp
 * @create: 2019-04-03 14:28
 **/
package com.csii.Timer;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.NameValuePair;
import org.apache.commons.httpclient.cookie.CookiePolicy;
import org.apache.commons.httpclient.methods.PostMethod;
import sun.misc.BASE64Encoder;

import java.io.UnsupportedEncodingException;
import java.util.*;

public class HttpLogin {

    public static void main(String[] args) {
        //邹沙雕
        startSignTask("http://10.1.69.156:8080/vpgatway/login/vpcloud", "http://10.1.69.156:8080/vpgatway/vpmprovider/api/daily/doneDaily", "zoucong", "19930801");
        //hp
//        startSignTask("http://10.1.69.156:8080/vpgatway/login/vpcloud", "http://10.1.69.156:8080/vpgatway/vpmprovider/api/daily/doneDaily", "hepeng", "hp520633");
    }

    /**
     * @description:启动双定时器
     * @param loginUrl 登陆url
     * @param signInUrl 签到／退url
     * @param username 用户名
     * @param password 密码
     */
    static void startSignTask(final String loginUrl, final String signInUrl, final String username, final String password) {
        final Random rand = new Random();
        //早上打卡8：30开始
        new Timer("timer - 1").schedule(new TimerTask() {
            @Override
            public void run() {
                try {
                    long l = rand.nextInt(20 * 60 * 1000);//早上打卡随机延迟20分钟之内
                    System.out.println(Thread.currentThread().getName() + "睡眠" + l);
                    Thread.sleep(l);
                    Date signInDate = new Date();
                    if (!judgeWeekend(signInDate)) {
                        System.out.println(Thread.currentThread().getName() + " 开始执行签到 " + signInDate);
                        sign(loginUrl, signInUrl, username, password, "0");
                    }else {
                        System.out.println("今天是周末"+signInDate);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }, 0, 24 * 60 * 60 * 1000);


        new Timer("timer - 2").schedule(new TimerTask() {
            @Override
            public void run() {
                try {
                    long l = rand.nextInt(2 * 60 * 60 * 1000);//晚上打卡随机延迟2小时之内
                    System.out.println(Thread.currentThread().getName() + "睡眠" + l);
                    Thread.sleep(l);
                    Date signOffDate = new Date();
                    if (!judgeWeekend(signOffDate)) {
                        System.out.println(Thread.currentThread().getName() + "开始执行签退 " + signOffDate);
                        sign(loginUrl, signInUrl, username, password, "1");
                    }else {
                        System.out.println("今天是周末"+signOffDate);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }, 10 * 60 * 60 * 1000 + 10 * 60 * 1000, 24 * 60 * 60 * 1000);//下班打卡任务设置延迟10小时+10分钟
    }

    //登陆+签到／退
    static void sign(String loginUrl, String signInUrl, String username, String password, String dailyType) {
        // 登陆 Url
//        String loginUrl = "http://10.1.69.156:8080/vpgatway/login/vpcloud";
        // 需登陆后访问的 Url
//        String signInUrl = "http://10.1.69.156:8080/vpgatway/vpmprovider/api/daily/doneDaily";
        HttpClient httpClient = new HttpClient();
        // 模拟登陆，按实际服务器端要求选用 Post 或 Get 请求方式
        PostMethod postMethod = new PostMethod(loginUrl);
        // 设置登陆时要求的信息，用户名和密码
        NameValuePair[] data = {new NameValuePair("username", username), new NameValuePair("password", password)};
        postMethod.setRequestBody(data);
        String str = username + ":" + password;   //这是能通过认证的用户名和密码
        byte[] b = new byte[0];
        try {
            b = str.getBytes("utf-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        str = new BASE64Encoder().encode(b);  //使用base64对用户名:密码进行加密
        postMethod.setRequestHeader("Authorization", "Basic " + str);
        postMethod.setRequestHeader("User-Agent", "Mozilla/5.0 (Windows NT 10.0; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/60.0.3112.113 Safari/537.36");
        // 传输的类型
        postMethod.setRequestHeader("Content-Type", "application/x-www-form-urlencoded");
        // 例如，referer 从哪里来的，UA 像搜索引擎都会表名自己是谁，无良搜索引擎除外
        postMethod.setRequestHeader("Referer", "http://10.1.69.156:8080/vpgatway/login");
        postMethod.setRequestHeader("Origin", "http://10.1.69.156:8080");
        postMethod.setRequestHeader("Host", "10.1.69.156:8080");
        postMethod.setRequestHeader("Connection", "Keep-Alive");
        try {
            // 设置 HttpClient 接收 Cookie,用与浏览器一样的策略
            httpClient.getParams().setCookiePolicy(CookiePolicy.BROWSER_COMPATIBILITY);
            int statusCode = httpClient.executeMethod(postMethod);

            String loginRet = postMethod.getResponseBodyAsString();
            Map loginRetMap = JSONUtils.json2map(loginRet);
            String authorization = loginRetMap.get("token_type") + " " + loginRetMap.get("access_token");
            System.out.println("登陆后交易鉴权码：" + authorization);

//            // 获得登陆后的 Cookie
//            Cookie[] cookies = httpClient.getState().getCookies();
//            StringBuffer tmpcookies = new StringBuffer();
//            for (Cookie c : cookies) {
//                tmpcookies.append(c.toString() + ";");
//                System.out.println("cookies = "+c.toString());
//            }

            if (statusCode == 200) {
                System.out.println("模拟登录成功");
                // 进行登陆后的操作 重定向到新的URL
                PostMethod signMethod = new PostMethod(signInUrl);
                // 每次访问需授权的网址时需带上前面的 cookie 作为通行证
//                signMethod.setRequestHeader("cookie", tmpcookies.toString());
                signMethod.setRequestHeader("User-Agent", "Mozilla/5.0 (Windows NT 10.0; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/60.0.3112.113 Safari/537.36");
                // 传输的类型
                signMethod.setRequestHeader("Content-Type", "application/x-www-form-urlencoded");
                // 例如，referer 从哪里来的，UA 像搜索引擎都会表名自己是谁，无良搜索引擎除外
//                signMethod.setRequestHeader("Referer", "http://10.1.69.156:8080/vpgatway/login");
//                signMethod.setRequestHeader("Origin", "http://10.1.69.156:8080");
//                signMethod.setRequestHeader("Host", "10.1.69.156:8080");
                signMethod.setRequestHeader("Connection", "Keep-Alive");
                signMethod.setRequestHeader("authorization", authorization);
                NameValuePair[] data1 = {new NameValuePair("dailyType", dailyType)};
                signMethod.setRequestBody(data1);
                // 你还可以通过 PostMethod/PostMehtod 设置更多的请求后数据
                // 例如，referer 从哪里来的，UA 像搜索引擎都会表名自己是谁，无良搜索引擎除外
//                postMethod.setRequestHeader("Referer", "http://passport.mop.com/");
//                postMethod.setRequestHeader("User-Agent", "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/31.0.1650.63 Safari/537.36");
                httpClient.executeMethod(signMethod);
                // 打印出返回数据，检验一下是否成功
                String text = signMethod.getResponseBodyAsString();
                System.out.println("接口返回:"+text);
            } else {
                System.out.println("登录失败");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //判断是否周末
    static boolean judgeWeekend(Date date) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        if (cal.get(Calendar.DAY_OF_WEEK) == Calendar.SATURDAY || cal.get(Calendar.DAY_OF_WEEK) == Calendar.SUNDAY) {
            return true;
        } else {
            return false;
        }
    }
}
