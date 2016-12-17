package com.binggou.sms;

import java.io.ByteArrayOutputStream;

/**
 * <p>Title: 发送任务处理平台</p>
 * <p>Description: 发送任务处理平台，加密解密短信
 * BASE64编码解码器
 * </p>
 * @author chenhj(brenda)
 * @version 1.0
 */

/*
 * Base64 字符表
 * 码值 字符      码值 字符      码值 字符      码值 字符
 *    0    A        17    R        34    i        51    z
 *    1    B        18    S        35    j        52    0
 *    2    C        19    T        36    k        53    1
 *    3    D        20    U        37    l        54    2
 *    4    E        21    V        38    m        55    3
 *    5    F        22    W        39    n        56    4
 *    6    G        23    X        40    o        57    5
 *    7    H        24    Y        41    p        58    6
 *    8    I        25    Z        42    q        59    7
 *    9    J        26    a        43    r        60    8
 *   10    K        27    b        44    s        61    9
 *   11    L        28    c        45    t        62    +
 *   12    M        29    d        46    u        63    /
 *   13    N        30    e        47    v
 *   14    O        31    f        48    w     (pad)    =
 *   15    P        32    g        49    x
 *   16    Q        33    h        50    y
 */

public final class Base64Operator
{
   /**
    * 静态成员初始化,BASE64编码表
    */
   final private static String encodeTable = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789+/";


   /**
    * BASE64编码器,编码算法如下:
    * <1>.将数据按3个字节一组分成数块；
    * <2>.每块将3个8位的数据转换成4个6位数据段；
    *     11111111 00000000 11111111  ----  111111 110000 000011 111111
          12345678 12345678 12345678  ----  123456 781234 567812 345678
    * <3>.根据Base64字符表得到4个6位数据段对应的字符；
    * <4>.如果最后一块只有两个字节，则添加两个0位，转换成对应Base64字符表的三个字符，并在结尾添一个'='字符；
    *     如果最后一块只有一个字节，则添加四个0位，转换成对应Base64字符表的两个字符，并在结尾添两个'='字符。
    * @param data 需要编码的数据
    * @return 返回编码后结果
    */
   public static String encode(byte[] data)
   {
      //判断输入数据的有效性
      if(data==null)
         return null;
      //声明保存结构的缓冲区
      StringBuffer encoded = new StringBuffer();
      //循环开始编码
      int i,individual,remain=0;
      for(i=0;i<data.length;i++)
      {
         //把BYTE数据类型转换成int
         individual = data[i] & 0x000000ff;
         switch(i%3)
         {
            case 0 :
               //保存编码数据
               encoded.append(encodeTable.charAt(individual >> 2));
               //保留需移动的位
               remain = (individual << 4) & 0x30;
               break;
            case 1 :
               //保存编码数据
               encoded.append(encodeTable.charAt(remain | individual >> 4));
               //保留需移动的位
               remain = (individual << 2) & 0x3c;
               break;
            case 2 :
               //保存编码数据
               encoded.append(encodeTable.charAt(remain | individual >>6));
               //保存编码数据
               encoded.append(encodeTable.charAt(individual & 0x3f));
               break;
         }
         //判断换行
         if(((i+1) % 57) == 0)
            encoded.append("\r\n");
      }
      //末尾补位
      switch(i%3)
      {
         case 1 :
            //补四个0位，生成两个Base64字符,末尾加==
            encoded.append(encodeTable.charAt(remain));
            encoded.append("==");
            break;
         case 2 :
            //补两个0位，生成三个Base64字符,末尾加=
            encoded.append(encodeTable.charAt(remain));
            encoded.append('=');
            break;
      }
      //返回结果
      return encoded.toString();
   }

   /**
    * BASE64解码,解码算法如下
    * <1>.将数据按4个字节一组分成数块；
    * <2>.每块将4个字符去掉最高两位并转换成3个8位的数据段；
    *     注意：数据块中的字符取值不是ASCII集的值，而是Base64字符表中相对应的索引值！
    *     00 111111   00 110000   00 000011   00 111111 ---- 11111111 00000000 11111111
    * <3>.根据ASCII字符集得到3个8位数据段对应的字符；
    * <4>.如果最后一块只有两个'='，去掉两个'='，并去掉最低两位，转换成对应ASSCII字符集的两个字符；
    *    如果最后一块只有一个'='，去掉'='，并去掉最低四位，转换成对应ASSCII字符集的一个字符。
    * @param encodedData 需要解码的数据
    * @return 解码后的数据
    */
   public static byte[] decode(String encodedData)
   {
      byte[] data = encodedData.getBytes();
      //分配解码后数据缓冲区
      ByteArrayOutputStream output = new ByteArrayOutputStream(1024);
      //循环解码,每四个字节分为一个处理数据块
      int nBlockIndex = 0;
      byte individual = 0,remain = 0;
      for(int i=0;i<data.length;i++)
      {
         //过滤回车换行符
         if(data[i]=='\r' || data[i]=='\n')
            continue;
         //如果到达'=',处理完成
         if(data[i]=='=')
            break;
         //读取一个块字符
         individual = (byte)decodeBase64Char((char)data[i]);
         //针对在块的不同位置，分别处理
         switch((nBlockIndex++)%4)
         {
            case 0 :
               remain = (byte)(individual << 2);
               break;
            case 1 :
               output.write((char)(remain | (individual >> 4)));
               remain = (byte)(individual << 4);
               break;
            case 2 :
               output.write((char)(remain | (individual >> 2)));
               remain = (byte)(individual << 6);
               break;
            case 3 :
               output.write(remain | individual);
               break;
         }
      }
      //返回解码后结果
      return output.toByteArray();
   }


   /**
    * BASE64字符与其索引反解
    * @param code 编码字符
    * @return 索引
    */
   private static byte decodeBase64Char(char code)
   {
      if(code >= 'A' && code <= 'Z')
         return (byte)(code - 'A');
      else if(code >= 'a' && code <= 'z')
         return (byte)(code - 'a' + 26);
      else if(code >= '0' && code <= '9')
         return (byte)(code - '0' + 52);
      else if(code == '+')
         return 62;
      else if(code == '/')
         return 63;
      return 64;
   }
}