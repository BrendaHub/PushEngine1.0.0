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
public class XMLReader {
	private static Document doc = null;
	private static XMLReader xml = new XMLReader();
	private XMLReader(){
		
	}
	public static XMLReader getXMLReader(){
		return xml;
	}
	
	public static Document getDocumentFromString(String text){
		if(null == text){
			return null;
		}
		try {
			doc = DocumentHelper.parseText(text);
			return doc;
		} catch (DocumentException e) {
			e.printStackTrace();
		}
		return null;
	}
}
