package com.binggou.sms.mission.core.send.impl.hjhttptest;

import java.io.IOException;
import java.io.PrintStream;
import org.apache.commons.httpclient.*;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.params.HttpMethodParams;

public class DXHTHttpConnection
{

    public DXHTHttpConnection()
    {
    }

    public static void main(String args1[])
    {
    }

    public static String sendMessage(String url)
    {
		String response = "";
		// 构造HttpClient的实例
		HttpClient httpClient = new HttpClient();
		// 创建GET方法的实例
		GetMethod getMethod = new GetMethod(url);
		// 使用系统提供的默认的恢复策略
		getMethod.getParams().setParameter(HttpMethodParams.RETRY_HANDLER,
				new DefaultHttpMethodRetryHandler());
		try {
			// 执行getMethod
			int statusCode = httpClient.executeMethod(getMethod);
			if (statusCode != HttpStatus.SC_OK) {//请求失败
				System.err.println("Method failed: "
						+ getMethod.getStatusLine());

				return null;
			}
			// 读取内容
			byte[] responseBody = getMethod.getResponseBody();
			response = new String(responseBody, "GB2312");
		} catch (HttpException e) {
			// 发生致命的异常，可能是协议不对或者返回的内容有问题
			System.out.println("Please check your provided http address!");
			e.printStackTrace();
		} catch (IOException e) {
			// 发生网络异常
			e.printStackTrace();
		} finally {
			// 释放连接
			getMethod.releaseConnection();
		}

		return response;
    }
}