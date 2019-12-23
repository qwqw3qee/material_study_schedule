package cn.fanre.material_study_schedule;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;


import cn.fanre.material_study_schedule.data.Course;
import cn.fanre.material_study_schedule.data.CourseTable;
import cn.fanre.material_study_schedule.data.DataConf;
import cn.fanre.material_study_schedule.pool.Form;
import cn.fanre.material_study_schedule.pool.MyState;
import cn.fanre.material_study_schedule.utils.MyFileHelper;
import cn.fanre.material_study_schedule.utils.MyOkHttp2;
import okhttp3.*;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class CourseTableModule extends Form {
    private Handler handler;
    public CourseTableModule(Handler handler){this.handler=handler;}

    public void setHandler(Handler handler) {
        this.handler = handler;
    }

    public Handler getHandler() {
        return handler;
    }

    public void getCourseList(String xq){
        getCourseList(xq,userNumber);
    }
    public void getCourseList(final String xq,final String xh){
        String pars = "?xnxq01id="+xq+"&xs0101id="+xh+"&init=&sfFD=1&isview=1";
        String listTableURL = courseTableBaseURL + pars;
        //String listNoTableURl = courseNoTableBaseURL + pars;
        Log.i(MyState.TAG,listTableURL);
        new MyOkHttp2(handler){
            @Override
            protected void responsedSolve(Call call, Response response) throws IOException {
                if(response.isSuccessful()){
                    String html = response.body().string();
                    String path = "courseTable_"+xh+"_"+xq+".json";
                    //Log.i(MyState.TAG,html);
                    //System.out.println(path);
                    CourseTable courseTable = storeCourseList(html,xh,path);
                    if(courseTable!=null){
                        Message msg = new Message();
                        Bundle bundle = new Bundle();
                        DataConf dataConf = new DataConf(xh,xq,path);
                        bundle.putString("kind","courseTable");
                        bundle.putSerializable("dataConf",dataConf);
                        bundle.putSerializable("data",courseTable);
                        msg.what = MyState.SUCCESS;
                        msg.setData(bundle);
                        handler.sendMessage(msg);
                    }

                }
            }
        }.myConnect(listTableURL,Form.cookies);
    }
    public CourseTable storeCourseList(String html,String xh,String path){
        Document doc = Jsoup.parse(html);
        Elements isRight = doc.select("div#mxhDiv");
        if(isRight.size()==0){
            Message msg = new Message();
            msg.what=MyState.INCORRECT_HTML;
            msg.obj="获取课表失败！";
            handler.sendMessage(msg);
            return null;
        }
        Elements table = isRight.first().select("tr");
        if(table.size()==0){
            Message msg = new Message();
            msg.what=MyState.JSON_ERROR;
            msg.obj="生成课表失败！";
            handler.sendMessage(msg);
            return null;
        }
        CourseTable courseTable = new CourseTable();
        courseTable.setInfo("ok");
        courseTable.setXh(xh);
        List<Course> courses = new ArrayList<Course>();
        for(Element cur:table){
            Course course = new Course();
            Elements part = cur.select("td");
            if(part.size()<12){
                Message msg = new Message();
                msg.what=MyState.JSON_ERROR;
                msg.obj="生成课表失败！";
                handler.sendMessage(msg);
                return null;
            }
            course.setBanji(part.get(1).text());
            course.setRenshu(part.get(2).text());
            course.setBianhao(part.get(4).text());
            course.setKecheng(part.get(5).text());
            course.setJiaoshi(part.get(6).text());
            course.setYuanxi(part.get(7).text());
            course.setShijian(part.get(8).text());
            course.setDidian(part.get(9).text());
            course.setZhouci(part.get(10).text());
            course.setDanshuang(part.get(11).text());
            course.setFenzu(part.get(12).text());
            //System.out.println(course.toString());
            courses.add(course);
        }
        //System.out.println(courses.size());
        courseTable.setTable(courses);
        if(courses.size()>0)
            courseTable.setCode("ok");
        else
            courseTable.setCode("-1");
        //Log.i(MyState.TAG,courseTable.toString());
        Gson gson=new GsonBuilder().setPrettyPrinting().create();  //setPrettyPrinting()格式化json
        String json = gson.toJson(courseTable);
        //Log.i(MyState.TAG,json);
        if(MyFileHelper.JsonToFile(json,path)!=0){
            Message msg = new Message();
            msg.what=MyState.FILE_ERROR;
            msg.obj="保存课表失败！";
            handler.sendMessage(msg);
            return null;
        }
        return courseTable;
    }

    public void getPersonTable(String xq){
        getPersonTable(xq,"");
    }
    public void getPersonTable(String xq,String zc){
        String url = personTableBaseURL+"&istsxx=no&xnxqh="+xq+"&zc="+zc+"&xs0101id="+userNumber;
        System.out.println("课表网址："+url);
        new MyOkHttp2(handler){
            @Override
            protected void responsedSolve(Call call, Response response) throws IOException {
                byte[] ff = response.body().bytes();
                System.out.println(new String(ff));
                File file = new File("D:\\test.html");
                FileOutputStream out = new FileOutputStream(file);
                out.write(ff);
                out.close();
                System.out.println("文件输出完成");
            }
        }.myConnect(url,cookies);
    }

}
