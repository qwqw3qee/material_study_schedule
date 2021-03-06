package cn.fanre.material_study_schedule;

import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import cn.fanre.material_study_schedule.data.CourseCard;
import cn.fanre.material_study_schedule.pool.EnvironmentPool;
import cn.fanre.material_study_schedule.pool.MyState;

public class CourseTableListAdapter extends RecyclerView.Adapter<CourseTableListAdapter.ViewHolder> {

    private int curZhou;
    private List<CourseCard> courseCards;
    private List<List<Integer>> data;
    private List<Integer> sumLoc;
    private static String[] weekText= new String[]{"","周一","周二","周三","周四","周五","周六","周日"};

    public void setCurZhu(int curZhou) {
        this.curZhou = curZhou;
        if(curZhou==EnvironmentPool.semWeekNow){
            getSum();
        }
    }

    public int getCurZhou() {
        return curZhou;
    }

    public void setXq(String xq) {
        courseCards = EnvironmentPool.courseCards.get(xq);
        updateCards();
        getSum();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        CardView cardView;
        TextView kecheng;
        TextView didian;
        TextView zhouci;
        TextView shijian;
        TextView jiaoshi;
        TextView banji;

        public ViewHolder(View view) {
            super(view);
            kecheng = (TextView) view.findViewById(R.id.card_kecheng);
            didian = (TextView) view.findViewById(R.id.card_didian);
            zhouci = (TextView) view.findViewById(R.id.card_zhouci);
            shijian = (TextView) view.findViewById(R.id.card_shijian);
            jiaoshi = (TextView) view.findViewById(R.id.card_jiaoshi);
            banji = (TextView) view.findViewById(R.id.card_banji);
        }
    }
    public CourseTableListAdapter(String xq,int curZhou) {
        this.curZhou = curZhou;
        if(xq==null||EnvironmentPool.courseCards==null||EnvironmentPool.courseCards.get(xq)==null){
            courseCards = new ArrayList<>();
        }else {
            this.courseCards = EnvironmentPool.courseCards.get(xq);

        }
        updateCards();
        getSum();

    }
    private void updateCards(){
        data = new ArrayList<>();
        for(int i = 0; i < 7; ++i){
            List<Integer> tem = new ArrayList<>();
            data.add(tem);
        }
       for(CourseCard card:courseCards){
           data.get(card.xingqi-1).add(card.no);
       }
    }
    private int getSum(){
        sumLoc = new ArrayList<>();
        int n = data.get(EnvironmentPool.currentWeekDay-1).size();
        for(int i = 0; i < n; ++i){
            int loc = data.get(EnvironmentPool.currentWeekDay-1).get(i);
            if(courseCards.get(loc).zhou[EnvironmentPool.semWeekNow]){
                sumLoc.add(loc);
            }
       }
       Log.i(MyState.TAG,"今日共"+sumLoc.size()+"课");
       return sumLoc.size();
    }
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_course_card,
                parent, false);
        return new ViewHolder(view);
    }
    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        CourseCard card = courseCards.get(sumLoc.get(position));
        holder.kecheng.setText(card.kecheng);
        holder.banji.setText(card.banji);
        holder.jiaoshi.setText(card.jiaoshi);
        holder.didian.setText(card.didian);
        holder.zhouci.setText(card.zhouci+"周");
        holder.shijian.setText(weekText[card.xingqi]+" "+card.jiestart+"-"+card.jieend+"节");
    }
    @Override
    public int getItemCount() {
        return sumLoc.size();
    }
}