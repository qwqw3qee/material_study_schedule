package cn.fanre.material_study_schedule.utils;
import android.os.Handler;
import android.os.Message;
import android.util.Log;


import cn.fanre.material_study_schedule.pool.MyState;
import okhttp3.*;
import java.io.IOException;
import java.util.Map;

public class MyOkHttp2{
    private Handler handler;
    public MyOkHttp2(Handler handler){
        this.handler=handler;
    }
    public Handler getHandler() {
        return handler;
    }

    public void setHandler(Handler handler) {
        this.handler = handler;
    }

    protected void responsedSolve(Call call, Response response) throws IOException{}
    protected void failedSolve(){
        Log.i(MyState.TAG,"onFailure()");
        Message msg = new Message();
        msg.what=MyState.CONNECTION_ERROR;
        handler.sendMessage(msg);
    }
    public void myConnect(String url){
        Log.i(MyState.TAG,"进入myConnect()");
        OkHttpClient mOkHttpClient = new OkHttpClient();
        Request request = new Request.Builder().url(url).build();
        Call call = mOkHttpClient.newCall(request);
        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                failedSolve();
            }
            @Override
            public void onResponse(Call call, Response response) throws IOException {
                responsedSolve(call,response);
            }
        });
    }
    public void myConnect(String url,String cookies){
        OkHttpClient mOkHttpClient = new OkHttpClient();
        Request request = new Request.Builder().url(url).addHeader("cookie",cookies).build();
        Call call = mOkHttpClient.newCall(request);
        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                failedSolve();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                responsedSolve(call,response);
            }
        });

    }
    public void myConnect(String url, Map<String,String> header){

        OkHttpClient mOkHttpClient = new OkHttpClient();
        Request.Builder requestBuilder = new Request.Builder().url(url);
        for (Map.Entry<String, String> entry : header.entrySet()) {
            requestBuilder.addHeader(entry.getKey(),entry.getValue());
            //System.out.println("Key = " + entry.getKey() + ", Value = " + entry.getValue());
        }
        Request request = requestBuilder.build();
        Call call = mOkHttpClient.newCall(request);
        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                failedSolve();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                responsedSolve(call,response);
            }
        });

    }
    public void myConnect(String url, RequestBody body,String cookies){

        OkHttpClient mOkHttpClient = new OkHttpClient();
        Request.Builder requestBuilder = new Request.Builder().url(url).addHeader("cookie",cookies).post(body);
        Request request = requestBuilder.build();
        Call call = mOkHttpClient.newCall(request);
        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                failedSolve();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                responsedSolve(call,response);
            }
        });

    }
    public void myConnect(String url, Map<String,String> header,RequestBody body){

        OkHttpClient mOkHttpClient = new OkHttpClient();
        Request.Builder requestBuilder = new Request.Builder().url(url).post(body);
        for (Map.Entry<String, String> entry : header.entrySet()) {
            requestBuilder.addHeader(entry.getKey(),entry.getValue());
            //System.out.println("Key = " + entry.getKey() + ", Value = " + entry.getValue());
        }
        Request request = requestBuilder.build();
        Call call = mOkHttpClient.newCall(request);
        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                failedSolve();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                responsedSolve(call, response);
            }
        });

    }
}
