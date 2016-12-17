
package com.binggou.mission.common;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.MissingResourceException;
import java.util.PropertyResourceBundle;
import java.util.Random;
import java.util.StringTokenizer;

public final class StringUtil {

	/**
	 * 用指定的字符集编码URL
	 *
	 * @param url
	 *            要编码的URL
	 * @param charset
	 *            字符集
	 * @return 编码后的URL
	 */
	public static String encodeURL(String url, String charset) {
		if (url != null && url.length() > 0) {
			try {
				return URLEncoder.encode(url, charset);
			} catch (UnsupportedEncodingException ex) {
				return url;
			}
		}
		return url;
	}

	/**
	 * 以指定的字符编码解析字符串的长度
	 *
	 * @param txt
	 *            要解析的字符串
	 * @param charset
	 *            编码
	 * @return 字符串的长度
	 */
	public static int getStrLength(String txt, String charset) {
		try {
			return txt.getBytes(charset).length;
		} catch (UnsupportedEncodingException ex) {
			return txt.length();
		}
	}

	/**
	 * 去掉指定字符串的首尾特殊字符
	 *
	 * @param source
	 *            指定字符串
	 * @param beTrim
	 *            要去除的特殊字符
	 * @return 去掉特殊字符后的字符串
	 */
	public static String trimStringWithAppointedChar(String source, String beTrim) {
		if (!source.equalsIgnoreCase("")) {
			// 循环去掉字符串首的beTrim字符
			String beginChar = source.substring(0, 1);
			while (beginChar.equalsIgnoreCase(beTrim)) {
				source = source.substring(1, source.length());
				beginChar = source.substring(0, 1);
			}

			// 循环去掉字符串尾的beTrim字符
			String endChar = source.substring(source.length() - 1, source.length());
			while (endChar.equalsIgnoreCase(beTrim)) {
				source = source.substring(0, source.length() - 1);
				endChar = source.substring(source.length() - 1, source.length());
			}
		}
		return source;
	}

	/**
	 * 去掉指定字符串的首尾特殊字符
	 *
	 * @param source
	 *            指定字符串
	 * @param beTrim
	 *            要去除的特殊字符
	 * @param endTrim
	 *            要去的特殊字符
	 * @return 去掉特殊字符后的字符串
	 */
	public static String trimStringWithAppointedChar(String source, String beTrim, String endTrim) {
		if (!source.equalsIgnoreCase("")) {
			// 循环去掉字符串首的beTrim字符
			String beginChar = source.substring(0, 2);
			while (beginChar.equalsIgnoreCase(beTrim)) {
				source = source.substring(2, source.length());
				beginChar = source.substring(0, 2);
			}

			// 循环去掉字符串尾的beTrim字符
			String endChar = source.substring(source.length() - 1, source.length());
			while (endChar.equalsIgnoreCase(endTrim)) {
				source = source.substring(0, source.length() - 1);
				endChar = source.substring(source.length() - 1, source.length());
			}
		}
		return source;
	}

	/**
	 * 用sign分隔字符串data(不判断data是否带有双引号的)
	 *
	 * @param data
	 *            要拆分的字符串
	 * @param sign
	 *            分隔符
	 * @return list 分隔后的List
	 */
	public static List<String> spit(String data, String sign) {
		StringTokenizer stkzer = new StringTokenizer(data, sign);
		String temp = null;
		List<String> list = new ArrayList<String>();
		while (stkzer.hasMoreTokens()) {
			temp = stkzer.nextToken();
			list.add(temp);
		}
		return list;
	}

	/**
	 * 用sign分隔字符串data(data是带有双引号的) 如:"system_id","type","command_line",
	 *
	 * @param data
	 *            要拆分的字符串
	 * @param sign
	 *            分隔符
	 * @return list 分隔后的List
	 */
	public static List<String> spitWithQuotationMark(String data, String sign) {

		List<String> keysWithQuotationMark = new ArrayList<String>();

		String[] tempData = data.split(sign);
		for (int i = 0; i < tempData.length; i++) {
			keysWithQuotationMark.add(tempData[i]);
		}

		List<String> keys = new ArrayList<String>();
		// 此时得到的key值列表还是带有双引号的，下边的循环把双引号去掉
		for (int i = 0; i < keysWithQuotationMark.size(); i++) {
			String eachKey = (String) keysWithQuotationMark.get(i);
			String key = null;
			if (eachKey.length() != 0 && eachKey.substring(0, 1).equalsIgnoreCase("\"")) {
				eachKey = eachKey.substring(1, eachKey.length());
			}
			if (eachKey.length() != 0 && eachKey.substring(eachKey.length() - 1).equalsIgnoreCase("\"")) {
				eachKey = eachKey.substring(0, eachKey.length() - 1);
			}
			key = eachKey;

			keys.add(key);
		}
		return keys;
	}

	/**
	 * 检查指定的字符串列表是否不为空。
	 */
	public static boolean areNotEmpty(String... values) {
		boolean result = true;
		if (values == null || values.length == 0) {
			result = false;
		} else {
			for (String value : values) {
				result &= !isNullorBlank(value);
			}
		}
		return result;
	}

	/**
	 * 判断字符串为null或者为""
	 *
	 * @param value
	 *            要判断的字符串
	 * @return 是否为null或者为""
	 */
	public static boolean isNullorBlank(String value) {
		return null == value || "".equals(value.trim()) || "null".equals(value.trim());
	}

	/**
	 * 去掉指定字符串两端的空格
	 *
	 * @param value
	 *            指定的字符串
	 * @return 去掉两端空格后的字符串。如果传入的指定字符串是null，返回""。
	 */
	public static String trim(String value) {
		if (value == null) {
			return "";
		} else {
			return value.trim();
		}
	}

	/**
	 * 将指定字符串的两端加上单引号"'"
	 *
	 * @param value
	 *            指定的字符串
	 *
	 * @return 加过单引号的字符串，如果传入的字符串是null，返回null。
	 */
	public static String sem(String value) {
		if (value == null) {
			return null;
		} else {
			return "'" + value + "'";

		}
	}

	/**
	 * 将指定的数字转化为指定长度的字符串，多余部分用"#"填充。例如：intToStrWithSharp(1000, 6)->"##1000"
	 *
	 * @param value
	 *            要转换的整数
	 * @param length
	 *            转换后的字符串长度
	 * @return 转换后的字符串，如果指定的长度小于整数的位数，则只返回数字。例如：intToStrWithSharp(1000,
	 *         2)->"1000"
	 */
	public static String intToStrWithSharp(Integer value, int length) {
		int valueLength = value.toString().length();
		int diff = length - valueLength;

		if (value.intValue() < Integer.MAX_VALUE) {
			StringBuffer sb = new StringBuffer();
			for (int i = 0; i < diff; i++) {
				sb.append('#');
			}
			sb.append(value.intValue());
			return sb.toString();
		} else {
			return "-1";
		}
	}

	/**
	 * 判断一个String对象是否为null，为null返回""，否则返回str自身。
	 *
	 * @param str
	 *            要判断的String对象
	 * @return str自身或""
	 */
	public static String getEmptyStringIfNull(String str) {
		if (str == null)
			return "";
		return str;
	}

	/**
	 * 将一个byte数组转换为字符串
	 *
	 * @param arr
	 *            要转换的byte数组
	 * @return 转换好的字符串，如果数组的length=0，则返回""。
	 */
	public static String bytetoString(byte[] arr) {
		String str = "";
		String tempStr = "";
		for (int i = 1; i < arr.length; i++) {
			tempStr = (Integer.toHexString(arr[i] & 0xff));
			if (tempStr.length() == 1) {
				str = str + "0" + tempStr;
			} else {
				str = str + tempStr;
			}
		}
		return str;
	}

	/**
	 * 分析字符串得到Integer.
	 *
	 * @param str1
	 *            String
	 * @return Integer
	 */
	public static Integer myparseIntObj(String str1) {
		try {
			if (isBlank(str1)) {
				return null;
			} else {
				// 16进制
				if (str1.startsWith("0x")) {
					String sLast = str1.substring(2);
					return Integer.valueOf(sLast, 16);
				} else {
					return Integer.valueOf(str1);
				}
			}
		} catch (NumberFormatException e) {
			return null;
		}
	}

	/**
	 * 分析一个字符串,得到一个整数,如果错误,设置为缺省值-1.
	 *
	 * @param str1
	 *            String
	 * @return int
	 */
	public static int myparseInt(String str1) {
		return myparseInt(str1, -1);
	}

	/**
	 * 分析一个字符串,得到一个整数,如果错误,设置为缺省值. 如果一个字符串以0x开头,则认为是16进制的.
	 *
	 * @param str1
	 *            字符串
	 * @param nDefault
	 *            缺省值
	 * @return int
	 */
	public static int myparseInt(String str1, int nDefault) {
		int result;
		try {
			if (isBlank(str1)) {
				result = nDefault;
			} else {
				// 16进制
				if (str1.startsWith("0x")) {
					String sLast = str1.substring(2);
					result = Integer.parseInt(sLast, 16);
				} else {
					result = Integer.parseInt(str1);
				}
			}
		} catch (NumberFormatException e) {
			result = nDefault;
		}
		return result;
	}

	/**
	 * 分析一个字符串得到float,如果错误,设置一个缺省值-1.
	 *
	 * @param str1
	 *            String
	 * @return float
	 */
	public static float myparseFloat(String str1) {
		return myparseFloat(str1, -1);
	}

	/**
	 * 分析一个字符串得到float,如果错误,设置一个缺省值.
	 *
	 * @param str1
	 *            String
	 * @param nDefault
	 *            缺省值
	 * @return float
	 */
	public static float myparseFloat(String str1, float nDefault) {
		float result;
		try {
			result = isBlank(str1) ? nDefault : Float.parseFloat(str1);
		} catch (NumberFormatException e) {
			result = nDefault;
		}
		return result;
	}

	/**
	 * 分析一个字符串得到Float,如果错误,返回null.
	 *
	 * @param str1
	 *            String
	 * @return Float(may be null)
	 */
	public static Float myparseFloatObj(String str1) {
		try {
			if (isBlank(str1)) {
				return null;
			} else {
				return Float.valueOf(str1);
			}
		} catch (NumberFormatException e) {
			return null;
		}
	}

	/**
	 * 分析一个字符串得到long,如果错误,设置一个缺省值 -1.
	 *
	 * @param str1
	 *            String
	 * @return long
	 */
	public static long myparseLong(String str1) {
		return myparseLong(str1, -1);
	}

	/**
	 * 分析一个字符串得到long,如果错误,设置一个缺省值 .
	 *
	 * @param str1
	 *            字符串
	 * @param nDefault
	 *            缺省值
	 * @return long
	 */
	public static long myparseLong(String str1, long nDefault) {
		long result;
		try {
			result = isBlank(str1) ? nDefault : Long.parseLong(str1);
		} catch (NumberFormatException e) {
			result = nDefault;
		}
		return result;
	}

	/**
	 * 为显示/编辑而转换串值，将空对象转换为空串.
	 *
	 * @param astr
	 *            字符串的值
	 * @return 如果字符串为空,则返回空串(不是null),如果不空,原样返回
	 */
	public static String getShowStr(String astr) {
		return (null == astr) ? "" : astr;
	}

	/**
	 * 返回一个字符串的n次组合后的字符串.
	 *
	 * @param sStr
	 *            原字符串
	 * @param nRate
	 *            次数
	 * @return 组合好的字符串
	 */
	public static String getManyStr(String sStr, int nRate) {
		StringBuffer strBF = new StringBuffer();
		for (int i = 0; i < nRate; i++) {
			strBF.append(sStr);
		}
		return strBF.toString();
	}

	/**
	 * 格式化数字:返回定长的字符串.
	 *
	 * @param aNum
	 *            格式化的数字
	 * @param aLength
	 *            长度
	 * @return 格式化好的字符串.
	 */
	public static String formatNumber(int aNum, int aLength) {
		String sNum = Integer.toString(aNum);

		int nLength = aLength - sNum.length();
		if (nLength < 1) {
			return sNum;
		}

		for (int i = 1; i <= nLength; i++) {
			sNum = "0" + sNum;
		}
		return sNum;
	}

	/**
	 * 根据格式输出浮点数的字符串.
	 *
	 * @param aFloat
	 *            浮点数
	 * @param nSyo
	 *            字符串格式,参考NumberFormat的说明.
	 * @return String
	 */
	public static String getShowFloat(float aFloat, String nSyo) {
		NumberFormat astr = NumberFormat.getInstance();
		((DecimalFormat) astr).applyPattern(nSyo);

		return astr.format(aFloat);
	}

	/**
	 * 从属性里面读取一个字符串出来,如果空,返回缺省值.
	 *
	 * @param aPROP
	 *            属性句柄
	 * @param itemName
	 *            属性名称
	 * @param sDefault
	 *            缺省值
	 * @return String
	 */
	public static String getPROPString(PropertyResourceBundle aPROP, String itemName, String sDefault) {
		String aValue = "";
		try {
			if (null != aPROP) {
				aValue = aPROP.getString(itemName);
			}
		} catch (MissingResourceException e) {
			// donothing
		} catch (ClassCastException e) {
			// donothing
		}

		if (isTrimEmpty(aValue)) {
			aValue = sDefault;
		}
		return aValue;
	}

	/**
	 * 从属性里面读取一个字符串出来,如果空,返回"".
	 *
	 * @param aPROP
	 *            属性句柄
	 * @param itemName
	 *            属性名称
	 * @return String
	 */
	public static String getPROPString(PropertyResourceBundle aPROP, String itemName) {
		return getPROPString(aPROP, itemName, "");
	}

	/**
	 * 翻译一个字符串到目标编码.
	 *
	 * 如果缺省编码为空,则设置缺省编码为源编码.
	 *
	 * @param aStr
	 *            源字符串
	 * @param sDefaultEncode
	 *            缺省编码
	 * @param srcCharSet
	 *            源编码
	 * @param destCharSet
	 *            目标编码
	 * @return 编码后的字符串
	 */
	public static String getEXTCHARSETString(String aStr, String sDefaultEncode, String srcCharSet,
			String destCharSet) {
		String lastDefaultEncode = sDefaultEncode;
		String strTemp = null;

		try {
			strTemp = aStr;

			if (isBlank(lastDefaultEncode)) {
				lastDefaultEncode = srcCharSet;
			}
			// 如果源字符集不等于目标字符集
			if (!(sDefaultEncode.equalsIgnoreCase(destCharSet))) {
				if (strTemp != null) {
					if (isTrimEmpty(lastDefaultEncode)) {
						strTemp = new String(strTemp.getBytes(), destCharSet);
					} else {
						strTemp = new String(strTemp.getBytes(lastDefaultEncode), destCharSet);
					}
				}
			}
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}

		return (strTemp == null) ? "" : strTemp;
	}

	/**
	 * 对编过码的字符串进行解码.
	 *
	 * @param astr
	 *            String
	 * @param encoding
	 *            编码
	 * @return String
	 */
	public static String urlDecode(String astr, String encoding) {
		if (isBlank(astr)) {
			return "";
		}
		String aRes = astr;
		try {
			aRes = URLDecoder.decode(astr, encoding);
			// if (SysInfo.isJavaVersion14()) {
			// aRes = URLDecoder.decode(astr, encoding);
			// } else {
			// aRes = URLDecoder.decode(astr);
			// }
		} catch (Exception e) {
			e.printStackTrace();
		}

		return aRes;
	}

	/**
	 * 对字符串进行url编码.
	 *
	 * 1.3和1.4不同,用于GBK环境. 如果切换到1.4中在考虑增加一个函数.
	 *
	 * @param astr
	 *            源字符串
	 * @param encoding
	 *            编码
	 * @return String
	 */
	public static String urlEncode(String astr, String encoding) {
		if (isBlank(astr)) {
			return "";
		}
		String aRes = astr;
		try {
			// if (SysInfo.isJavaVersion14()) {
			// aRes = URLEncoder.encode(astr, encoding);
			// } else {
			// aRes = URLEncoder.encode(astr);
			// }
			aRes = URLEncoder.encode(astr, encoding);
		} catch (Exception e) {
			e.printStackTrace();
		}

		return aRes;
	}

	/**
	 * 字符串是否为空:null或者长度为0.
	 *
	 * @param astr
	 *            源字符串.
	 * @return boolean
	 */
	public static boolean isBlank(String astr) {
		return ((null == astr) || (astr.length() == 0));
	}

	/**
	 * 去掉左右空格后字符串是否为空.
	 *
	 * @param astr
	 *            String
	 * @return boolean
	 */
	public static boolean isTrimEmpty(String astr) {
		if ((null == astr) || (astr.length() == 0)) {
			return true;
		}
		if (isBlank(trim(astr))) {
			return true;
		}
		return false;
	}

	/**
	 * 截取一定长度的字符串,根据指定的编码来判断长度. 例如指定编码为GBK,则一个汉字为2个字符长度
	 *
	 * @param astr
	 *            String
	 * @param nlength
	 *            int
	 * @param destEncode
	 *            String
	 * @return String
	 */
	public static String msubstr(String astr, int nlength, String destEncode) {
		byte[] mybytes;
		try {
			mybytes = astr.getBytes(destEncode);

			if (mybytes.length <= nlength) {
				return astr;
			}
			if (nlength < 1) {
				return "";
			}

			for (int i = 0; i < astr.length(); i++) {
				String aTestStr = astr.substring(0, i + 1);
				int nDestLength = aTestStr.getBytes(destEncode).length;
				if (nDestLength > nlength) {
					if (i == 0) {
						return "";
					} else {
						return astr.substring(0, i);
					}
				}
			}

			return astr;
		} catch (UnsupportedEncodingException e) {
			return "";
		}
	}

	/**
	 * 返回带省略标记的截断的字符串.
	 *
	 * @param astr
	 *            源字符串
	 * @param nlength
	 *            截断的长度
	 * @param aDot
	 *            后缀
	 * @param encoding
	 *            编码
	 * @return String
	 */
	public static String getDotMsubstr(String astr, int nlength, String aDot, String encoding) {
		byte[] mybytes = astr.getBytes();

		// if not long enough,return old string
		if (mybytes.length <= nlength) {
			return astr;
		}

		int nLastLen = nlength - aDot.length();

		if (nLastLen < 1) {
			nLastLen = 1;

		}
		return msubstr(astr, nLastLen, encoding) + aDot;

	}

	/**
	 * 得到字符串的字符长度 按照指定编码测定.
	 *
	 * @param astr
	 *            String
	 * @param sDestEncode
	 *            String
	 * @return int
	 */
	public static int mlength(String astr, String sDestEncode) {
		try {
			return astr.getBytes(sDestEncode).length;
		} catch (UnsupportedEncodingException e) {
			return astr.getBytes().length;
		}
	}

	/**
	 * 连接2个字符串.
	 *
	 * @param aOriStr
	 *            源字符串
	 * @param aLinkSign
	 *            连接标记
	 * @param aLinkStr
	 *            要连接的字符串
	 * @return String
	 */
	public static String link2Str(String aOriStr, String aLinkSign, String aLinkStr) {
		if (isBlank(aOriStr)) {
			return aLinkStr;
		} else {
			return aOriStr + aLinkSign + aLinkStr;
		}
	}

	/**
	 * 连接字符串数组.
	 *
	 * @param astrBf
	 *            StringBuffer
	 * @param aryStr
	 *            String[]
	 * @return StringBuffer
	 */
	public static StringBuffer linkAryStr(StringBuffer astrBf, String[] aryStr) {
		for (int i = 0; i < aryStr.length; i++) {
			astrBf.append(aryStr[i]);
		}
		return astrBf;
	}

	/**
	 * 连接字符串数组.
	 *
	 * @param aryStr
	 *            String[]
	 * @param sSign
	 *            String
	 * @return String
	 */
	public static String linkAryStr(String[] aryStr, String sSign) {
		StringBuffer asbf = new StringBuffer();
		if (null == aryStr) {
			return asbf.toString();
		}

		for (int i = 0; i < aryStr.length; i++) {
			if (i > 0) {
				asbf.append(sSign);
			}
			asbf.append(aryStr[i]);
		}
		return asbf.toString();
	}

	/**
	 * if a String identify "true".
	 *
	 * @param aPropString
	 *            字符串
	 * @return true if y,yes,true,1
	 */
	public static boolean isTrueString(String aPropString) {
		String strTemp = aPropString.toLowerCase(Locale.US);
		return (strTemp.startsWith("true") || strTemp.startsWith("yes") || strTemp.startsWith("1")
				|| strTemp.startsWith("y"));
	}

	/**
	 * 将字符串里指定的字符串被代替字符串所替换 例如 指定的字符串 "sdupipo" 将里面出现p的字符穿都变成g
	 *
	 * @param source
	 *            指定字符串
	 * @param target
	 *            要被代替的字符串
	 * @param replace
	 *            代替字符串
	 * @return 替换后的字符串
	 */
	public static String stringReplace(String source, String target, String replace) {
		if (source != null && target != null && replace != null) {
			StringBuffer stringbuffer = new StringBuffer(source.length() + 256);

			int i = -1;
			do {
				i++;

				i = source.indexOf(target);
				if (i > -1) {
					stringbuffer.append(source.substring(0, i));

					stringbuffer.append(replace);

					source = source.substring(i + target.length());
				}
			} while (i != -1);

			stringbuffer.append(source);

			return stringbuffer.toString();
		} else {
			return source;
		}
	}

	/**
	 * 转换成全角的方法。
	 *
	 * 全角空格为12288，半角空格为32
	 *
	 * 其他字符半角(33-126)与全角(65281-65374)的对应关系是：均相差65248
	 *
	 * @param input
	 *            要转换的字符串
	 *
	 * @return 全角字符串
	 */
	public static String ToSBC(String input) {

		char[] c = input.toCharArray();
		for (int i = 0; i < c.length; i++) {
			if (c[i] == 32) {
				c[i] = (char) 12288;
				continue;
			}
			if (c[i] < 127)
				c[i] = (char) (c[i] + 65248);
		}
		return new String(c);
	}

	/**
	 * 转换成半角的方法
	 *
	 * 全角空格为12288，半角空格为32
	 *
	 * 其他字符半角(33-126)与全角(65281-65374)的对应关系是：均相差65248
	 *
	 * @param input
	 *            要转换的字符串
	 *
	 * @return 返回半角字符串
	 */
	public static String ToDBC(String input) {
		char[] c = input.toCharArray();
		for (int i = 0; i < c.length; i++) {
			if (c[i] == 12288) {
				c[i] = (char) 32;
				continue;
			}
			if (c[i] > 65280 && c[i] < 65375)
				c[i] = (char) (c[i] - 65248);
		}
		return new String(c);
	}

	public static String romoveEnter(String input) {
		return input.replaceAll("\r", "").replaceAll("\n", "");
	}

	/**
	 * 生成随机数的方法
	 *
	 * @param length
	 *            随机数的长度
	 * @return
	 */
	public static String generateString(int length) {
		StringBuffer sb = new StringBuffer();
		Random random = new Random();
		for (int i = 0; i < length; i++) {

			sb.append("ABCDEFGHIJKLMNOPQRSTUVWXYZ".charAt(random.nextInt("ABCDEFGHIJKLMNOPQRSTUVWXYZ".length())));
		}
		return sb.toString();
	}

	public static void main(String[] args) {
		// System.out.println(ToDBC("ｃｋｍｎ"));
		// System.out.println(ToSBC("ckmn"));
		System.out.println(msubstr("中文123哎哈你我qwe的行啊是的他", 3, "gbk"));
	}
}
