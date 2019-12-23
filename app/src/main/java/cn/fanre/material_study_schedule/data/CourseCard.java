package cn.fanre.material_study_schedule.data;

public class CourseCard{
    public String kecheng;
    public String didian;
    public String jiaoshi;
    public String banji;
    public String zhouci;
    public int jiestart;
    public int jieend;
    public int xingqi;
    public boolean[] jie;
    public boolean[] zhou;
    public int no;
    @Override
    public String toString() {
        return kecheng+"\n@"+didian;
    }
}