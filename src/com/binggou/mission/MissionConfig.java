package com.binggou.mission;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import com.binggou.mission.common.ConfigAccessible;
import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.Node;
import org.dom4j.io.SAXReader;

/**
 * <p>Title: 发送任务处理平台</p>
 * <p>Description:
 * 应用配置对象是短信发送子系统的配置中心，其初始化时解析xml的应用配置文件，
 * 获得配置信息，为其它对象或操作提供应用配置参数。
 * xml配置文件结构:
 * <mission-app>
 *    <objects-config>
 *       <object>
 *          <init-param>
 *             <param-name>name</param-name>
 *             <param-value>value</param-value>
 *          </init-param>
 *          <init-param>
 *             <param-name>name</param-name>
 *             <param-value>value</param-value>
 *          </init-param>
 *       </object>
 *    </objects-config>
 * </mission-app>
 *
 * </p>
 * @author chenhj(brenda)
 * @version 1.0
 */

public class MissionConfig implements ConfigAccessible
{
	/**
	 * 构造函数
	 */
	public MissionConfig()
	{
	}

	/**
	 * 得到指定对象的参数信息
	 * @param objectName 对象名称
	 * @return 存放参数信息的HashMap句柄,如果对象不存在或没有参数返回null
	 */
	public HashMap<String, String> getObjectParams(String objectName)
	{
		//打开配置文件，获得配置信息
		Document document = openConfigFile("bin/config/mission-config.xml");
		if (document == null)
			return null;
		//定位到指定节点
		List paramsList = locateElement(document, "//mission-app//objects-config//" + objectName.toLowerCase() + "//init-param");

		if (paramsList == null)
			return null;
		//得到参数信息
		String strParamKey = "";
		String strParamValue = "";
		HashMap<String, String> paramsMap = new HashMap<String, String>(108);
		for (Iterator iterator = paramsList.iterator(); iterator.hasNext();)
		{
			List paramList = ((Element) iterator.next()).content();
			for (Iterator iter = paramList.iterator(); iter.hasNext();)
			{
				Node node = (Node) iter.next();
				//得到参数名称
				if (node.getNodeType() == Node.ELEMENT_NODE && node.getName().equals("param-name"))
				{
					strParamKey = node.getText();
					strParamValue = "";
				}
				//得到参数值
				if (node.getNodeType() == Node.ELEMENT_NODE && node.getName().equals("param-value"))
				{
					strParamValue = node.getText();
					if (!strParamKey.equals(""))
					{
						//增加参数信息到HashMap中
						paramsMap.put(strParamKey, strParamValue);
						strParamKey = "";
					}
				}
			}
		}
		return paramsMap;
	}

	/**
	 * 得到配置对象的参数信息
	 * @param objectName 对象名称
	 * @return 存放参数信息的HashMap句柄,如果对象不存在或没有参数返回null
	 */
	public HashMap<String, String> getConfigParams(String objectName)
	{
		//打开配置文件，获得配置信息
		Document document = openConfigFile("bin/config/BGEngineConfig.xml");
		if (document == null)
			return null;
		//定位到指定节点
		List paramsList = locateElement(document, "//config//" + objectName + "//init-param");
		if (paramsList == null)
			return null;
		//得到参数信息
		String strParamKey = "";
		String strParamValue = "";
		HashMap<String, String> paramsMap = new HashMap<String, String>(108);
		for (Iterator iterator = paramsList.iterator(); iterator.hasNext();)
		{
			List paramList = ((Element) iterator.next()).content();
			for (Iterator iter = paramList.iterator(); iter.hasNext();)
			{
				Node node = (Node) iter.next();
				//得到参数名称
				if (node.getNodeType() == Node.ELEMENT_NODE && node.getName().equals("param-name"))
				{
					strParamKey = node.getText();
					strParamValue = "";
				}
				//得到参数值
				if (node.getNodeType() == Node.ELEMENT_NODE && node.getName().equals("param-value"))
				{
					strParamValue = node.getText();
					if (!strParamKey.equals(""))
					{
						//增加参数信息到HashMap中
						paramsMap.put(strParamKey, strParamValue);
						strParamKey = "";
					}
				}
			}
		}
		return paramsMap;
	}
	
	/**
	 * 打开配置文件
	 * @param filename 配置文件名称
	 * @return Document 对象
	 */
	protected Document openConfigFile(String filename)
	{
		Document document = null;
		try
		{
			SAXReader reader = new SAXReader();
			//document = reader.read("config/mission-config.xml");
			document = reader.read(filename);
		}
		catch (Exception e)
		{
			e.printStackTrace();
			document = null;
		}
		return document;
	}
	
	/**
	 * 打开配置文件
	 * @param filename 配置文件名称
	 * @return Document 对象
	protected Document openConfigFile2(String filename)
	{
		Document document = null;
		try
		{
			SAXReader reader = new SAXReader();
			document = reader.read("config/BGEngineConfig.xml");
		}
		catch (Exception e)
		{
			document = null;
		}
		return document;
	}*/
	
	/**
	 * 定位到指定层次结构的节点对象
	 * @param document 文档对象
	 * @param nodeHiberarchy 节点层次结构
	 * @return 定位到指定层次结构节点
	 */
	protected List locateElement(Document document, String nodeHiberarchy)
	{
		return document.selectNodes(nodeHiberarchy);
	}
}