package cn.fanre.material_study_schedule.utils;

import android.content.Context;
import android.content.SharedPreferences;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.lang.reflect.Type;
import java.util.List;

import cn.fanre.material_study_schedule.data.CourseCard;
import cn.fanre.material_study_schedule.data.CourseTable;
import cn.fanre.material_study_schedule.data.CourseTableDivide;
import cn.fanre.material_study_schedule.data.DataConf;
import cn.fanre.material_study_schedule.data.ScoresList;
import cn.fanre.material_study_schedule.pool.EnvironmentPool;

public class MyFileHelper {
    public static int JsonToFile(String json,String path){
        try{
            File file = new File(path);
            FileOutputStream out = MyApplication.getContext().openFileOutput(path,Context.MODE_PRIVATE);
            out.write(json.getBytes());
            out.close();
        }catch (Exception e){
            e.printStackTrace();
            return -1;
        }
        return 0;
    }
    public static String readJsonFile(String path){
        String json = "";
        FileInputStream fis;
        try{
            fis = MyApplication.getContext().openFileInput(path);
            byte[] buffer = new byte[fis.available()];
            fis.read(buffer);
            json = new String(buffer);
            fis.close();
        }catch (Exception e){
            e.printStackTrace();
            return null;
        }
        return json;
    }
    public static boolean saveInfo(){
        SharedPreferences sp = MyApplication.getContext().getSharedPreferences("data.conf",Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        Gson gson = new Gson();
        String myCourseTableList = gson.toJson(EnvironmentPool.myCourseTableList);
        String myScoresListsList = gson.toJson(EnvironmentPool.myScoresListsList);
        String courseConf = gson.toJson(EnvironmentPool.courseConf);
        String scoresConf = gson.toJson(EnvironmentPool.scoresConf);

        editor.putString("myCourseTableList",myCourseTableList);
        editor.putString("myScoresListsList",myScoresListsList);
        editor.putString("courseConf",courseConf);
        editor.putString("scoresConf",scoresConf);
        editor.putInt("curSemPos",EnvironmentPool.curSemPos);
        editor.putInt("semWeekStart",EnvironmentPool.semWeekStart);  //第一周的位置
        editor.putString("firstYear",EnvironmentPool.firstYear);
        editor.putString("userNumber",EnvironmentPool.userNumber);
        editor.putString("password",EnvironmentPool.password);
        editor.commit();
        return true;
    }
    public static void getInfo(){
        SharedPreferences sp = MyApplication.getContext().getSharedPreferences("data.conf",Context.MODE_PRIVATE);

        Gson gson = new Gson();
        Type listType1 = new TypeToken<List<String>>(){}.getType();
        Type listType2 = new TypeToken<List<DataConf>>(){}.getType();

        String myCourseTableList = sp.getString("myCourseTableList","");
        String myScoresListsList = sp.getString("myScoresListsList","");
        String courseConf = sp.getString("courseConf","");
        String scoresConf = sp.getString("scoresConf","");


        EnvironmentPool.myCourseTableList = gson.fromJson(myCourseTableList,listType1);
        EnvironmentPool.myScoresListsList = gson.fromJson(myScoresListsList,listType1);
        EnvironmentPool.courseConf = gson.fromJson(courseConf,listType2);
        EnvironmentPool.scoresConf = gson.fromJson(scoresConf,listType2);

        EnvironmentPool.curSemPos = sp.getInt("curSemPos",EnvironmentPool.curSemPos);
        EnvironmentPool.semWeekStart = sp.getInt("semWeekStart",EnvironmentPool.semWeekStart);  //第一周的位置
        if(EnvironmentPool.semWeekStart>0){
            EnvironmentPool.semWeekNow = EnvironmentPool.currentWeek-EnvironmentPool.semWeekStart+1;
        }
        EnvironmentPool.firstYear = sp.getString("firstYear",EnvironmentPool.firstYear);
        EnvironmentPool.userNumber = sp.getString("userNumber",EnvironmentPool.userNumber);
        EnvironmentPool.password = sp.getString("password",EnvironmentPool.password);

        if(EnvironmentPool.courseConf!=null){
            for(DataConf dataConf:EnvironmentPool.courseConf){
                String json = readJsonFile(dataConf.path);
                CourseTable courseTable = gson.fromJson(json,CourseTable.class);
                EnvironmentPool.allCourse.put(dataConf.xq,courseTable);
                List<CourseCard> courseCards = CourseTableDivide.courseTableDivide(courseTable.getTable());
                EnvironmentPool.courseCards.put(dataConf.xq,courseCards);
            }
        }
        if(EnvironmentPool.scoresConf!=null){
            for(DataConf dataConf:EnvironmentPool.scoresConf){
                String json = readJsonFile(dataConf.path);
                ScoresList scoresLists = gson.fromJson(json,ScoresList.class);
                EnvironmentPool.allScores.put(dataConf.xq,scoresLists);
            }
        }
    }
}
