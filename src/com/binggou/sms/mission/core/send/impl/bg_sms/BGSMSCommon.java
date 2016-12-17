package com.binggou.sms.mission.core.send.impl.bg_sms;

import com.binggou.mission.Task;
import com.binggou.sms.mission.core.send.SendMessage;
import com.binggou.sms.mission.core.send.impl.xinge.HttpSMSCommon;
import com.huawei.insa2.util.Args;
import com.huawei.insa2.util.Cfg;

import java.io.IOException;
import java.util.Date;

/**
 * 云并购2.0 短信服务商接口实现类
 * 短信服务商，
 * Created by XYZ on 16/12/16.
 */
public class BGSMSCommon implements SendMessage {

    private BGSMSCommon(){}
    //发送短信实例
    private static BGSMSCommon instance=null;

    synchronized public static BGSMSCommon getInstance(String config) {
        if(instance==null){
            if(config == null){
                System.exit(1);
            }
            Args args = null;
            try{
                args = new Cfg("bin/SMProxy.xml", false).getArgs("Channel" + config);
            }catch(IOException e){
                e.printStackTrace();
            }
            instance = new BGSMSCommon();
        }
        return instance;
    }

    /**
     * 发送方法
     * @param task
     * @return
     */
    public Task sendMessage(Task task){
        String title = (String)task.getAttribValue("title");//推送信息标题
        String message = (String)task.getAttribValue("content");//推送信息内容
        System.out.println("title = " + title);
        System.out.println("message = " + message);
        task.addAttrib("SUBMIT", "xinge_success");
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
