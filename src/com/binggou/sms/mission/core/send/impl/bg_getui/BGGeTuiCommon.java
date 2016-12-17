package com.binggou.sms.mission.core.send.impl.bg_getui;

import com.binggou.mission.Task;
import com.binggou.mission.common.DesUtil;
import com.binggou.sms.mission.core.send.impl.bg_sms.BGSMSCommon;
import com.huawei.insa2.util.Args;
import com.huawei.insa2.util.Cfg;

import java.io.IOException;
import java.util.Date;

/**
 * 云并购2.0 个推消息推送服务商接口实现类
 * 主要完成APP的消息推送服务
 * Created by XYZ on 16/12/16.
 */
public class BGGeTuiCommon {
    //发送短信实例
    private static BGGeTuiCommon instance=null;

    synchronized public static BGGeTuiCommon getInstance(String config) {
        if(instance==null){
            if(config == null) {
                System.exit(1);
            }
            instance = new BGGeTuiCommon(config);
        }
        return instance;
    }

    //带通道编号的构适函数
    private BGGeTuiCommon(String config){
        //密钥
        String secretkey = "bg2.0!@#$%#^@^";
        Args args = null;
        try {
            args = new Cfg("SMProxy.xml", false).getArgs("Channel" + config);
        } catch (IOException e) {
            e.printStackTrace();
            try {
                args = new Cfg("bin/SMProxy.xml", false).getArgs("Channel" + config);
            } catch (IOException e1) {}
        }
        //初始化参数
//        this.SMSServer = args.get("sms-server-url", "");
//        String tmpaccount = args.get("sms-account", "");//账号，加密内容，需要解密
//        String tmpPwd = args.get("sms-pwd", "");//密码，需要解密
//        try {
//            this.SMSAccount = DesUtil.decrypt(tmpaccount, secretkey);
//            this.SMSPwd = DesUtil.decrypt(tmpPwd, secretkey);
//        } catch (Exception e) {
//            System.out.println("解密码账号异常，获取到的账号为：" + tmpaccount + "  " + tmpPwd);
//        }

    }

    /**
     * 发送方法
     * @param task
     * @return
     */
    public Task sendMessage(Task task){
        System.out.println("################################################################################################################");
        String title = (String)task.getAttribValue("title");//推送信息标题
        String message = (String)task.getAttribValue("content");//推送信息内容

        System.out.println("title = " + title);
        System.out.println("message = " + message);
        task.addAttrib("SUBMIT", "bg_getui_success");
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
}
