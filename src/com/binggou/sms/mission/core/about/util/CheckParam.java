package com.binggou.sms.mission.core.about.util;

public class CheckParam {
	
	//测试参数是否唯空，是则给出缺省值。（参数中不能有null,undefined,和两边的空格）
    public static String CheckParam(String sRequestParam,String sDefaultValue)
    {
        if( sRequestParam!=null
            && (!sRequestParam.equals(""))
            && (!sRequestParam.trim().equals("undefined"))
            && (!sRequestParam.trim().equals("null")))
        {
            return sRequestParam.trim();
        }
        else{
            return  sDefaultValue;
        }
    }

    ///重载整型：测试参数是否唯空，是则给出缺省值。（参数中不能有null,undefined,和两边的空格）
    public static int CheckParam(String sRequestParam,int iDefaultValue)
    {
        if( sRequestParam!=null
            && (!sRequestParam.equals(""))
            && (!sRequestParam.trim().equals("undefined"))
            && (!sRequestParam.trim().equals("null")))
        {
            int iResult;
            try{
                iResult = Integer.parseInt(sRequestParam.trim());
            }
            catch(Exception e){
                return  iDefaultValue;
            }
            return iResult;
        }
        else{
            return  iDefaultValue;
        }
    }

    //重载float型：测试参数是否唯空，是则给出缺省值。（参数中不能有null,undefined,和两边的空格）
    public static double CheckParam(String sRequestParam,double fDefaultValue)
    {
        if( sRequestParam!=null
            && (!sRequestParam.equals(""))
            && (!sRequestParam.trim().equals("undefined"))
            && (!sRequestParam.trim().equals("null")))
        {
            double fResult;
			try{
                fResult = Double.parseDouble(sRequestParam.trim());
            }
            catch(Exception e){
                //ASSERT(false,"CheckParam()::Double.parseDouble()错误！");
                return  fDefaultValue;
            }
            return fResult;
        }
        else{
            return  fDefaultValue;
        }
    }
}
