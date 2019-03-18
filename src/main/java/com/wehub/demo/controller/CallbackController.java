
package com.wehub.demo.controller;

import java.util.HashMap;
import java.util.LinkedHashMap;

import java.lang.reflect.Method;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.util.DigestUtils;

@RestController
public class CallbackController {
    private static final String SECRET_KEY = "SECRET"; //登录网页, 在首页点击“配置回调参数” 可查看自己的SECRET KEY

    @ResponseBody
    @RequestMapping(value = "/callback")
    HashMap<String, Object> home(@RequestBody HashMap<String, Object> body) {
        String wxid = body.get("wxid").toString();
        String action = body.get("action").toString();
        String appid = body.get("appid").toString();
        System.out.println(wxid);
        System.out.println(action);
        System.out.println(appid);
        HashMap<String, Object> result = null;
        try {
            LinkedHashMap<String, Object> data = (LinkedHashMap<String, Object>) body.get("data");
            Class<?> klass = this.getClass();
            Method m = klass.getDeclaredMethod(action, String.class, String.class, LinkedHashMap.class);
            m.setAccessible(true);
            result = (HashMap<String, Object>) m.invoke(this, wxid, appid, data);
            System.out.println(result);
            if (!result.containsKey("error_code")) {
                result.put("error_code", 0);
                result.put("error_reason", "");
            }
            return result;
        } catch (Exception e) {
            System.out.println(body);
            System.out.println(e);
        }
        result = this.get_common_ack();

        return body;
    }

    private HashMap<String, Object> login(String wxid, String appid, LinkedHashMap<String, Object> data) {
        /**
        {
            "wxid": "wxid_xxxxxx",
            "action": "login",
            "appid": "123123123",
            "data": {
                "hello": "world",
                "nonce": "112233"
            }
        }
         */
        HashMap<String, Object> result = new HashMap<String, Object>();
        result.put("ack_type", "login_ack");
        System.out.println("login:");
        String nonce = data.getOrDefault("nonce", "").toString();
        String sign = "";
        if (nonce != null) {
            String candiString = String.format("%s#%s#%s", wxid, nonce, SECRET_KEY);
            System.out.println(candiString);
            sign = DigestUtils.md5DigestAsHex(candiString.getBytes()).toString();
            System.out.println("sign:" + sign);
            HashMap<String, String> d = new HashMap<String, String>();
            d.put("signature", sign);
            result.put("data", d);
        }
        return result;
    }

    private HashMap<String, Object> get_common_ack() {
        HashMap<String, Object> result = new HashMap<String, Object>();
        result.put("error_code", 0);
        result.put("error_reason", "");
        result.put("ack_type", "common_ack");
        return result;
    }

    //private HashMap<String, Object> report_new_msg(String wxid, String appid, LinkedHashMap<String, Object> data) {
        /**
        {
            "action" : "report_new_msg",
            "appid": "xxxxxxxx",			
            "wxid" : "wxid_fo1039029348sfj",
            "data" : {
                "msg": {
                    "msg_type": 1,                    
                    "room_wxid": "xxxxxxxx@chatroom",  
                    "wxid_from":  "wxid_from_xxxxxx",    
                    "wxid_to": 	"wxid_to_xxxxx",		
                    "atUserList": ["wxid_xxx1","wxid_xxx2"],              
                    "msg": "Hello,world"                
                }
            }
        }
         */
    //}
}