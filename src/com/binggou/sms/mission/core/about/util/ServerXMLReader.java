package com.binggou.sms.mission.core.about.util;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;

/**
 * DOM4J 从字符串解析成XML文档
 * 
 * @author DongBiao
 * @version 0.1 2008-01-31
 */
public class ServerXMLReader {
	private static Document doc = null;
	private static ServerXMLReader xml = new ServerXMLReader();
	private ServerXMLReader(){
		
	}
	public static ServerXMLReader getXMLReader(){
		return xml;
	}
	
	public static Document getDocumentFromString(String text){
		if(null == text){
			return null;
		}
		text.replaceAll("<?xml version=\"1.0\" encoding=\"UTF-8\"?>","");
//		text = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>"+text;
		try {
			doc = DocumentHelper.parseText(text);
			return doc;
		} catch (DocumentException e) {
			System.out.println(text);
			e.printStackTrace();
		}
		return null;
	}
}
