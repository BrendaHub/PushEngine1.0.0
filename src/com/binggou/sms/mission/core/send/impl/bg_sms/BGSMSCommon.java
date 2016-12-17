package com.binggou.sms.mission.core.send.impl.bg_sms;

import com.binggou.mission.Task;
import com.binggou.mission.common.DesUtil;
import com.binggou.mission.common.WebUtils;
import com.binggou.sms.mission.core.send.SendMessage;
import com.binggou.sms.mission.core.send.impl.xinge.HttpSMSCommon;
import com.huawei.insa2.util.Args;
import com.huawei.insa2.util.Cfg;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * 云并购2.0 短信服务商接口实现类
 * 短信服务商，
 * Created by XYZ on 16/12/16.
 */
public class BGSMSCommon implements SendMessage {


    //发送短信实例
    private static BGSMSCommon instance=null;
    //短信发送服务接口地址
    private static String SMSServer = null;
    //短信发送账号
    private String SMSAccount = null;
    //短信发送口令
    private String SMSPwd = null;

    synchronized public static BGSMSCommon getInstance(String config) {
        if(instance==null){
            if(config == null) {
                System.exit(1);
            }
            instance = new BGSMSCommon(config);
        }
        return instance;
    }

    //带通道编号的构适函数
    private BGSMSCommon(String config){
        //密钥
        String secretkey = "bg2.0!@#$%#^@^";
        Args args = null;
        try {
            args = new Cfg("SMProxy.xml", false).getArgs("Channel" + config);
        } catch (IOException e) {
//            e.printStackTrace();
            try {
                args = new Cfg("bin/SMProxy.xml", false).getArgs("Channel" + config);
            } catch (IOException e1) {}
        }
        //初始化参数
        this.SMSServer = args.get("sms-server-url", "");
        String tmpaccount = args.get("sms-account", "");//账号，加密内容，需要解密
        String tmpPwd = args.get("sms-pwd", "");//密码，需要解密
        try {
            this.SMSAccount = DesUtil.decrypt(tmpaccount, secretkey);
            this.SMSPwd = DesUtil.decrypt(tmpPwd, secretkey);
        } catch (Exception e) {
            System.out.println("解密码账号异常，获取到的账号为：" + tmpaccount + "  " + tmpPwd);

            System.out.println("解密码账号异常，获取到的账号为：" + this.SMSAccount  + "  " + this.SMSPwd);
        }

    }

    /**
     * 发送方法
     * @param task
     * @return
     */
    public Task sendMessage(Task task){

        //将短信调用和佳自已通道ＨＴＴＰ发短信.
        String taskid = task.getTaskId();
        String mobile_to = (String) task.getMobileTo();//获取到发送的手机号码

        String msg = (String) task.getAttribValue("content");
        System.out.println("msg====> "+ msg+ "   mobile_to = " + mobile_to);

        //开始发送短信
        Map<String, String> sms_result = chuangLanSend(msg, mobile_to, this.SMSServer, this.SMSAccount, this.SMSPwd);
        if(sms_result != null){
            String code = sms_result.get("code");

        }

        task.addAttrib("SUBMIT", "bg_sms_success");
        task.addAttrib("ERR_MSG", "消息推送成功");
        task.addAttrib("pushId",new Integer(111111));
        System.out.println(">>>>>" + task.getTaskId());
        try{
            task.addAttrib("pushTime", new Date().getTime());//设置确认状态的时间戳
        }catch(Exception e){
            e.printStackTrace();
        }
        return task;
    }

    /**
     * 发送短信
     * 上海创蓝http://222.73.117.156/msg/HttpBatchSendSM
     * http://222.73.117.158/msg/HttpBatchSendSM?account=jiekou-cs-01&pswd=Tch147256&mobile=13482416860&msg=【创蓝文化】您的注册验证码是：1131.请完成注册&needstatus=true
     * @param url
     * @param messages
     * @param mobile
     * @return
     */
    public static Map<String, String> chuangLanSend(String messages, String mobile, String url, String account, String pwd) {
        String result= null;
        Map<String, String> resultMap = new HashMap<String, String>();
        try {
            Map<String,String> params = new HashMap<String,String>();
            params.put("account",account);
            params.put("pswd",pwd);
            params.put("mobile",mobile);
            params.put("msg",messages);
            params.put("needstatus","true");
            result = WebUtils.doGet(url,params,"UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        System.out.println("短信发送后响应： " + result);
        if(result !=null && result != ""){
            String[] status = result.split("\n");
            if(status.length < 2){
                resultMap.put("error", "07");
                resultMap.put("code", "07");
                resultMap.put("msgid", "0");
            }else{
                String msgId = status[1];
                resultMap.put("error", "0");
                resultMap.put("code", "0");
                resultMap.put("msgid",msgId);
            }
            return resultMap;
        }
        return null;
    }
}
