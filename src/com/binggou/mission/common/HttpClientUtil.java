package com.binggou.mission.common;


import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpException;
import org.apache.commons.httpclient.HttpMethod;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.lang.StringUtils;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

public class HttpClientUtil {
    /**
     * 日志
     */
    private static final Logger LOG = Logger.getLogger(HttpClientUtil.class.getName());

    /**
     * URL请求返回正确的statusCode
     */
    public static final int STATE_CODE_RIGHT = 200;


    /**
     * 后台GET 请求
     * @param url
     * @return 响应结果
     */
    public static String get(String url) {
        String result = null;
        HttpClient client = new HttpClient();
        HttpMethod method = new GetMethod(url);
        try {
            client.executeMethod(method);
            if(method.getStatusCode() != STATE_CODE_RIGHT){
                return null;
            }
            result = method.getResponseBodyAsString();
        } catch (HttpException e) {
            e.printStackTrace();
            return null;
        } catch (IOException e) {
            return null;
        }
        method.releaseConnection();
        return result;
    }

    /**
     * 后台GET 请求
     * @param url
     * @param paramMap
     * @return 响应结果
     */
    public static String get(String url, Map<String, String> paramMap) {

        //参数判定
        if(StringUtils.isBlank(url)){
            return null;
        }
        return get(linkURL(url, paramMap));
    }

    /**
     * 后台POST 请求 ,
     * @param url
     * @param params 一定要有参数,不能为空
     * @return 请求返回结果
     */
    public static String post(String url, Map<String, String> params) {

        if(StringUtils.isBlank(url)){
            return null;
        }

        if(params == null || params.size() == 0){
            return null;
        }

        String result = null;
        HttpClient client = new HttpClient();
        PostMethod method = new PostMethod(url);
        for(Map.Entry<String, String> entry : params.entrySet()){
            if(StringUtils.isNotBlank(entry.getValue())){
                method.addParameter(entry.getKey(),entry.getValue());
            }
        }

        try {
            client.executeMethod(method);
            result = method.getResponseBodyAsString();
        } catch (HttpException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        if(method.getStatusCode() != STATE_CODE_RIGHT){
            return null;
        }
        method.releaseConnection();
        return result;
    }

    /**
     * 拼接成一个完整的URL
     * @param baseURL
     * @param paramMap
     * @return
     */
    public static String linkURL(String baseURL, Map<String, String> paramMap){

        String params = null;
        if(paramMap != null && paramMap.size() > 0){
            StringBuilder paramBuilder = new StringBuilder();
            for(Map.Entry<String, String> entry : paramMap.entrySet()){
                paramBuilder.append('&');
                if(StringUtils.isNotBlank(entry.getValue())){
                    paramBuilder.append(entry.getKey());
                    paramBuilder.append('=');
                    paramBuilder.append(entry.getValue());
                }
            }
            params = paramBuilder.deleteCharAt(0).toString();
        }

        if(StringUtils.isBlank(params)){
            return baseURL;
        }

        if(StringUtils.isBlank(baseURL)){
            return params;
        }
        return baseURL + '?' + params;
    }

    /**
     * 将URL中转换成map,如果URL中无参数，则返回null
     *
     * @param url
     * @return
     */
    public static Map<String, String> getURLParamMap(String url) {
        if (StringUtils.isBlank(url)) {
            return null;
        }
        String[] tempUrl = url.split("\\?");
        String tmpStr = null;
        Map<String, String> paramMap = new HashMap<String, String>(6);

        tmpStr = tempUrl[tempUrl.length - 1];
        for (String param : tmpStr.split("&")) {
            String[] tempParam = param.split("=");
            if(tempParam.length == 2){
                paramMap.put(tempParam[0], tempParam[1]);
            }else{
                paramMap.put(tempParam[0], null);
            }
        }

        return paramMap;
    }
    public static void main(String[] args) {
        String result = get("http://localhost:8080/yao/sms/send.action?id=70&pwd=12345678&num=3&content=1;2;3;4&phoneNum=18618102693");
        System.out.println(result);
    }
}