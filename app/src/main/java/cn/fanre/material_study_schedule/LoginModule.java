package cn.fanre.material_study_schedule;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import cn.fanre.material_study_schedule.pool.Form;
import cn.fanre.material_study_schedule.pool.MyState;
import cn.fanre.material_study_schedule.utils.MyOkHttp2;
import okhttp3.*;

import java.io.IOException;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class LoginModule extends Form {
    private Handler handler;
    public void setHandler(Handler handler) {
        this.handler = handler;
    }
    public Handler getHandler() {
        return handler;
    }
    public LoginModule(Handler handler){this.handler=handler;}
    public void getVerifyCodeImage() {
        new MyOkHttp2(handler){
            @Override
            protected void responsedSolve(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    byte[] Picture_bt = response.body().bytes();
                    Log.i(MyState.TAG, "验证码大小:" + Picture_bt.length + "B");
                    Bitmap bitmap = BitmapFactory.decodeByteArray(Picture_bt, 0, Picture_bt.length);
                    Headers headers = response.headers();
                    List<String> sessions = headers.values("Set-Cookie");
                    if(sessions.size()==0){
                        Message msg = new Message();
                        msg.what = MyState.FAILED;
                        msg.obj = "获取cookie失败！";
                        handler.sendMessage(msg);
                    }else{
                        String session = sessions.get(0);
                        cookies = session.substring(0, session.indexOf(";"));
                        Log.i(MyState.TAG, cookies);
                        Message msg = new Message();
                        msg.what = MyState.GET_VERIFYCODE_SUCCESSFUL;
                        msg.obj = bitmap;
                        handler.sendMessage(msg);
                    }
                }
            }
        }.myConnect(verifyImgSrcURL);
    }
    public void studentSubmit(final String userNumber,final String password,final String verifyCode){
        RequestBody requestBody = new FormBody.Builder()
                .add("dlfl", personType)
                .add("USERNAME", userNumber)
                .add("PASSWORD",password)
                .add("RANDOMCODE", verifyCode)
                .build();
        final Map<String,String> header = new HashMap<String,String>();
        header.put("cookie",cookies);
        header.put("User-Agent", userAgent);
        new MyOkHttp2(handler){
            @Override
            protected void responsedSolve(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    //System.out.println(response.body().string());
                    if(response.body().string().contains(okScript)) {
                        Form.verifyCode = verifyCode;
                        Form.userNumber = userNumber;
                        Form.password = password;
                        Log.i(MyState.TAG,"登陆成功~~~");
                        Message msg = new Message();
                        msg.what = MyState.LOGIN_SUCCESSFUL;
                        handler.sendMessage(msg);
                    }
                    else {
                        Message msg = new Message();
                        msg.what = MyState.LOGIN_FAILED;
                        handler.sendMessage(msg);
                    }
                }
            }
        }.myConnect(loginHomePage,header,requestBody);
    }
    public void parentGetGrades(String stuName,String stuIdNum,String verifyCode){
        String extUrl;
        try{
            extUrl = "xsxm="+URLEncoder.encode(stuName,"utf-8")+"&xssfzh="+stuIdNum+"&yzm="+verifyCode;
            Log.i(MyState.TAG,extUrl);
            Map<String,String> header = new HashMap<String, String>();
            header.put("cookie",cookies);
            header.put("User-Agent",userAgent);
            MediaType type = MediaType.parse("application/x-www-form-urlencoded");
            RequestBody rBody = RequestBody.create(type,extUrl);
            new MyOkHttp2(handler){
                @Override
                protected void responsedSolve(Call call, Response response) throws IOException {
                    if (response.isSuccessful()) {
                        String html = response.body().string().trim();
                        Log.i("mtest",html.length()+"");
                        String url = parentGradesURL+"?yzbh="+html;
                        new MyOkHttp2(handler).myConnect(url,cookies);
                    }
                }
            }.myConnect(loginParent,header,rBody);
        }catch (Exception e){
            e.printStackTrace();
            Message msg = new Message();
            msg.what = MyState.STRING_ERROR;
            msg.obj = "生成成绩链接出错！";
            handler.sendMessage(msg);
        }
    }
}
