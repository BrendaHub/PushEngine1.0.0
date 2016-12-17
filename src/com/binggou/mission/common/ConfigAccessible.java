package com.binggou.mission.common;

import java.util.HashMap;

/**
 * <p>Title: 发送任务处理平台</p>
 * <p>Description:
 * 面向将来发送平台的任务扩展，针对不通的处理任务，如短信发送任务和短信发送任务。
 * 配置接口与其它接口一起，提供统一的接口规范，利于平台的任务扩展。
 * 配置接口主要是定义平台对象获取配置信息的规范。
 * </p>
 * @author chenhj(brenda)
 * @version 1.0
 */

public interface ConfigAccessible
{
   /**
    * 解析配置文件，得到配置文件中指定对象的参数信息  mission-config.xml
    * @param objectName 对象名称
    * @return 如果解析成功返回该对象以HashMap形式保存的参数信息,如果失败返回NULL
    */
   public HashMap<String, String> getObjectParams(String objectName);
   
   /**
    * 解析配置文件，得到配置文件中指定对象的参数信息 BGEngineConfig.xml
    * @param objectName 对象名称
    * @return 如果解析成功返回该对象以HashMap形式保存的参数信息,如果失败返回NULL
    */
   public HashMap<String, String> getConfigParams(String objectName);
}