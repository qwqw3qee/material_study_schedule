package cn.fanre.material_study_schedule.pool;


public class Form {
    protected static final  String personType = "0";
    protected static String userNumber;
    protected static String password;
    protected static String verifyCode;
    protected final static String indexPage = "http://kdjw.hnust.edu.cn:8080"; //教务网域名
    protected final static String loginHomePage = indexPage + "/kdjw/Logon.do?method=logon";
    protected final static String loginParent = indexPage +"/kdjw/xscjcx_check.jsp";
    protected final static String parentGradesURL = indexPage + "/kdjw/xscjcx.jsp";
    protected final static String verifyImgSrcURL = indexPage + "/kdjw/verifycode.servlet";

    protected final static String personTableBaseURL = indexPage + "/kdjw/tkglAction.do?method=goListKbByXs";
    protected final static String courseTableBaseURL = indexPage + "/kdjw/jiaowu/pkgl/llsykb/llsykb_list.jsp";
    protected final static String courseNoTableBaseURL = indexPage + "/kdjw/jiaowu/pkgl/llsykb/llsykb_list_no.jsp";
    protected final static String scoresListBaseURL = indexPage + "/kdjw/xszqcjglAction.do?method=queryxscj";
    protected final static String userAgent = "Mozilla/5.0 (Windows NT 10.0; WOW64; Trident/7.0; rv:11.0) like Gecko";
    protected final static String okScript =  "<script language='javascript'>window.location.href='" + indexPage +"/kdjw/framework/main.jsp';</script>";

    protected static String cookies;       //save cookies
    protected volatile static  int  state = 0;


    public static String getCookies() {
        return cookies;
    }

    public static String getUserNumber() {
        return userNumber;
    }
}
