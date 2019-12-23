package cn.fanre.material_study_schedule;

import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.kelin.scrollablepanel.library.PanelAdapter;

import java.util.ArrayList;
import java.util.List;

import cn.fanre.material_study_schedule.data.CourseCard;
import cn.fanre.material_study_schedule.pool.EnvironmentPool;
import cn.fanre.material_study_schedule.utils.MyApplication;

public class TestPanelAdapter extends PanelAdapter {

    private int curZhou; //当前周
    private List<CourseCard> courseCards;
    private List<List<Integer>> data;
    private static int firstWidth = EnvironmentPool.dip2px(MyApplication.getContext(),18);
    private static int firstHeight = EnvironmentPool.dip2px(MyApplication.getContext(),20);
    private static int normalHeight = EnvironmentPool.dip2px(MyApplication.getContext(),110);
    private static int normalWidth=(EnvironmentPool.screenWidth-firstWidth)/7;
    private static int colors[] = new int[]{R.color.cadetblue,R.color.fuchsia,R.color.orange,R.color.brown
            ,R.color.purple,R.color.plum,R.color.green,R.color.limegreen
            ,R.color.slateblue,R.color.aliceblue,R.color.blanchedalmond,R.color.firebrick
            ,R.color.lime,R.color.yellow,R.color.wheat,R.color.khaki
            ,R.color.mistyrose,R.color.chartreuse,R.color.aqua,R.color.palevioletred};
    private static String[] weekText= new String[]{"","周一","周二","周三","周四","周五","周六","周日"};
    private static String[] jieText= new String[]{"","01\n02","03\n04","05\n06","07\n08","09\n10"};

    public void setCurZhou(int curZhou) {
        this.curZhou = curZhou;
        updateCards();
    }

    public int getCurZhou() {
        return curZhou;
    }

    public void setXq(String xq) {
        courseCards = EnvironmentPool.courseCards.get(xq);
        updateCards();
    }

    private void updateCards(){
        data = new ArrayList<>();
        for(int i = 0; i < 6; ++i){
            List<Integer> tem = new ArrayList<>();
            for(int j = 0; j < 8; ++j){
                tem.add(-1);
            }
            data.add(tem);
        }
        for(CourseCard card:courseCards){
            if(card.jie!=null){
                for(int i = 1; i <= 5; i ++){
                    if(card.jie[i*2-1]){
                        if(data.get(i).get(card.xingqi)<0||card.zhou[curZhou]){
                            data.get(i).set(card.xingqi,card.no);
                        }
                    }
                }
            }
        }
    }
    public TestPanelAdapter(String xq,int curZhou){
        this.curZhou=curZhou;
        if(xq==null||EnvironmentPool.courseCards==null||EnvironmentPool.courseCards.get(xq)==null){
            courseCards = new ArrayList<>();
        }else {
            this.courseCards = EnvironmentPool.courseCards.get(xq);
        }
        updateCards();
    }
    @Override
    public int getRowCount() {
        return data.size();
    }

    @Override
    public int getColumnCount() {
        return data.get(0).size();
    }

    @Override
    public int getItemViewType(int row, int column) {
        return super.getItemViewType(row, column);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int row, int column) {
        TitleViewHolder titleViewHolder = (TitleViewHolder) holder;
        int loc = data.get(row).get(column);
        //titleViewHolder.params.height;//设置当前控件布局的高度
        //titleViewHolder.titleLinearlayout.setLayoutParams(titleViewHolder.params);//将设置好的布局参数应用到控件中
        if(column==EnvironmentPool.currentWeekDay&&curZhou==EnvironmentPool.semWeekNow){
            titleViewHolder.titleLinearlayout.setBackgroundColor(MyApplication.getContext().getResources().getColor(R.color.lightskyblue));
        }else {
            titleViewHolder.titleLinearlayout.setBackgroundColor(0x00000000);
        }
        if(row==0&&column==0){
            titleViewHolder.params.width=firstWidth;
            titleViewHolder.params.height=firstHeight;
            titleViewHolder.titleLinearlayout.setLayoutParams(titleViewHolder.params);
            titleViewHolder.titleTextView.setText("");
        } else if(row==0){
            titleViewHolder.params.width=normalWidth;
            titleViewHolder.params.height=firstHeight;
            titleViewHolder.titleLinearlayout.setLayoutParams(titleViewHolder.params);
            titleViewHolder.titleCardView.setMinimumWidth(50);
            titleViewHolder.titleTextView.setText(weekText[column]);
        }else if(column==0){
            titleViewHolder.params.width=firstWidth;
            titleViewHolder.params.height=normalHeight;
            titleViewHolder.titleLinearlayout.setLayoutParams(titleViewHolder.params);
            titleViewHolder.titleTextView.setText(jieText[row]);
        }else{
            titleViewHolder.params.width=normalWidth;
            titleViewHolder.params.height=normalHeight;
            titleViewHolder.titleLinearlayout.setLayoutParams(titleViewHolder.params);
            if(loc>=0&&courseCards.get(loc).zhou!=null){
                if(courseCards.get(loc).zhou[curZhou]){
                    titleViewHolder.titleCardView.setCardBackgroundColor(MyApplication.getContext().getResources().getColor(colors[courseCards.get(loc).no%20]));
                }else{
                    titleViewHolder.titleCardView.setCardBackgroundColor(MyApplication.getContext().getResources().getColor(R.color.silver));
                }
                titleViewHolder.titleTextView.setText(courseCards.get(loc).toString());
            }
            else{
                titleViewHolder.titleTextView.setText("");
                titleViewHolder.titleCardView.setCardBackgroundColor(MyApplication.getContext().getResources().getColor(R.color.white));

            }
        }
    }
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        return new TestPanelAdapter.TitleViewHolder(LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_course_cell, parent, false));
    }

    private static class TitleViewHolder extends RecyclerView.ViewHolder {
        public TextView titleTextView;
        public CardView titleCardView;
        public LinearLayout titleLinearlayout;
        public ViewGroup.LayoutParams params;
        public TitleViewHolder(View view) {
            super(view);
            this.titleTextView = (TextView) view.findViewById(R.id.item_course_cell_text);
            this.titleCardView = (CardView) view.findViewById(R.id.item_course_cell_card);
            this.titleLinearlayout = (LinearLayout)view.findViewById(R.id.item_course_cell_ll);
            this.params= (ViewGroup.LayoutParams) this.titleLinearlayout.getLayoutParams();//获取当前控件的布局对象
        }
    }
}