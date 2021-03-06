package cn.fanre.material_study_schedule;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.NavigationView;
import android.support.design.widget.TabLayout;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;

import com.kelin.scrollablepanel.library.ScrollablePanel;

import java.util.ArrayList;
import java.util.List;

import cn.fanre.material_study_schedule.pool.EnvironmentPool;
import cn.fanre.material_study_schedule.pool.MyState;
import cn.fanre.material_study_schedule.utils.MyFileHelper;
import cn.fanre.material_study_schedule.utils.ToastUtil;

public class MainActivity extends AppCompatActivity {

    private DrawerLayout mDrawerLayout;

    private Toolbar toolbar;

    private ActionBarDrawerToggle drawerToggle;
    private CoordinatorLayout cl;
    private NavigationView navView;
    private List<View> tabViews;
    private TabLayout mTabTl;
    private ViewPager mViewPager;

    private TestPanelAdapter testPanelAdapter;
    private ScrollablePanel courseTablePanel;
    private RecyclerView courseTableListRV;
    private CourseTableListAdapter courseTableListAdapter;
    private String weekList[];



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        weekList= new String[25];
        for (int i = 0; i < 25; ++i){
            weekList[i] = "第"+(i+1)+"周";
        }
        Log.i(MyState.TAG,"主界面创建");
        MyFileHelper.getInfo();
        toolbar = (Toolbar)findViewById(R.id.custom_toolbar);
        cl=(CoordinatorLayout)findViewById(R.id.main_cl);
        mTabTl = (TabLayout)findViewById(R.id.main_tab);
        mViewPager = (ViewPager)findViewById(R.id.main_pageview);
        mDrawerLayout = (DrawerLayout)findViewById(R.id.drawer_layout);
        navView =(NavigationView)findViewById(R.id.main_nav_view);

        initView();
        relateTabAndViewPager();
        initViewPager();
        if(EnvironmentPool.myCourseTableList!=null){
            updateCourseTable(EnvironmentPool.semWeekNow,EnvironmentPool.myCourseTableList.get(EnvironmentPool.curSemPos));
            updateCourseList(EnvironmentPool.semWeekNow,EnvironmentPool.myCourseTableList.get(EnvironmentPool.curSemPos));
        }

    }

    private void initView() {
        setSupportActionBar(toolbar);
        drawerToggle=new ActionBarDrawerToggle(this,mDrawerLayout,toolbar,R.string.drawer_open,R.string.drawer_close){
            @Override
            public void onDrawerSlide(View drawerView, float slideOffset) {
                WindowManager windowManager= (WindowManager) getSystemService(
                        getApplicationContext().WINDOW_SERVICE);
                Display display=windowManager.getDefaultDisplay();
                cl.layout(navView.getRight(),
                        0,
                        display.getWidth()+navView.getRight(),
                        display.getHeight());
                super.onDrawerSlide(drawerView, slideOffset);
            }

        };
        mDrawerLayout.addDrawerListener(drawerToggle);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setHomeAsUpIndicator(R.drawable.ic_menu_white_24dp);
        }
        navView.setCheckedItem(R.id.nav_import);
        navView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                switch (menuItem.getItemId()){
                    case R.id.nav_import:
                        Intent intent = new Intent(MainActivity.this,DataImportActivity.class);
                        startActivityForResult(intent,1);
                        break;
                    case R.id.nav_scores:
                        startActivity(new Intent(MainActivity.this,ScoresListActivity.class));
                        break;
                    case R.id.nav_info:
                        new AlertDialog.Builder(MainActivity.this).setTitle("关于").setMessage("学习助手 V0.0.3").setPositiveButton("确定",null).show();
                        break;
                }
                mDrawerLayout.closeDrawers();
                return true;
            }
        });
    }
    @Override
    public void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        drawerToggle.syncState();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        drawerToggle.onConfigurationChanged(newConfig);
    }

    public boolean onCreateOptionsMenu(Menu menu){
        getMenuInflater().inflate(R.menu.menu_custom_bar,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()){
            case android.R.id.home:
                mDrawerLayout.openDrawer(GravityCompat.START);
                break;
            case R.id.ic_cur_week:
                if(testPanelAdapter.getCurZhou()!=EnvironmentPool.semWeekNow){
                    updateCourseTable(EnvironmentPool.semWeekNow);
                    mTabTl.getTabAt(1).setText("第" + (EnvironmentPool.semWeekNow) + "周");
                }else {
                    new AlertDialog.Builder(MainActivity.this).setTitle("修改当前周").setSingleChoiceItems(
                            weekList, EnvironmentPool.semWeekNow - 1 >= 0 ? EnvironmentPool.semWeekNow - 1 : 0,
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    EnvironmentPool.semWeekNow = which + 1;
                                    EnvironmentPool.semWeekStart = EnvironmentPool.currentWeek - EnvironmentPool.semWeekNow + 1;
                                    updateCourseTable(EnvironmentPool.semWeekNow);
                                    updateCourseList(EnvironmentPool.semWeekNow);
                                    mTabTl.getTabAt(1).setText("第" + (which + 1) + "周");
                                    dialog.dismiss();
                                }
                            }).setNegativeButton("取消", null).show();
                }
                break;
            case R.id.ic_mark:
                if(EnvironmentPool.myCourseTableList==null){
                    new AlertDialog.Builder(MainActivity.this).setTitle("注意").setMessage("请先导入数据!").setPositiveButton("确定",null).show();
                }else{
                    new AlertDialog.Builder(MainActivity.this).setTitle("更改学期").setSingleChoiceItems(
                            EnvironmentPool.myCourseTableList.toArray(new String[EnvironmentPool.myCourseTableList.size()]), EnvironmentPool.curSemPos,
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    EnvironmentPool.curSemPos = which;
                                    updateCourseTable(EnvironmentPool.myCourseTableList.get(which));
                                    updateCourseList(EnvironmentPool.myCourseTableList.get(which));
                                    dialog.dismiss();

                                }
                            }).setNegativeButton("取消", null).show();
                }
                break;
            case R.id.ic_settings:
                ToastUtil.show("敬请期待");
                break;
            default:
        }
        return true;
    }
    //Tab和PageView关联
    private void relateTabAndViewPager(){
        mViewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(mTabTl));
        mTabTl.addOnTabSelectedListener(new TabLayout.ViewPagerOnTabSelectedListener(mViewPager));
        mTabTl.getTabAt(1).setText("第"+(EnvironmentPool.semWeekNow)+"周");
        mTabTl.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {}
            @Override
            public void onTabUnselected(TabLayout.Tab tab) {}
            @Override
            public void onTabReselected(TabLayout.Tab tab) {
                if(tab.getPosition()==1){
                    new AlertDialog.Builder(MainActivity.this).setTitle("选择要查看的周").setSingleChoiceItems(
                            weekList,testPanelAdapter.getCurZhou()-1>=0?testPanelAdapter.getCurZhou()-1:0,
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    updateCourseTable(which+1);
                                    mTabTl.getTabAt(1).setText("第"+(which+1)+"周");
                                    dialog.dismiss();
                                }
                            }).setNegativeButton("取消", null).show();
                }
            }
        });
    }
    //ViewPager相关
    private void initViewPager(){

        //------------------------ViewPager用法----------------------------------------------
        LayoutInflater inflater = getLayoutInflater();
        View view1 = inflater.inflate(R.layout.fragment_course_list,null);
        View view2 = inflater.inflate(R.layout.fragment_course_table,null);
        //初始化子布局
        initCourseList(view1);
        initCourseTable(view2);
        tabViews = new ArrayList<View>();
        tabViews.add(view1);
        tabViews.add(view2);
        PagerAdapter pagerAdapter = new PagerAdapter() {
            @Override
            public int getCount() {
                return tabViews.size();
            }

            @Override
            public boolean isViewFromObject(@NonNull View view, @NonNull Object o) {
                return view == o;
            }

            @Override
            public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
                container.removeView(tabViews.get(position));
            }
            @NonNull
            @Override
            public Object instantiateItem(@NonNull ViewGroup container, int position) {
                container.addView(tabViews.get(position));

                return tabViews.get(position);
            }
        };
        mViewPager.setAdapter(pagerAdapter);
        //=========================================================================
    }
    private void initCourseList(View view){

        courseTableListAdapter = new CourseTableListAdapter(EnvironmentPool.allSemesters.get(EnvironmentPool.curSemPos),EnvironmentPool.semWeekNow);
        courseTableListRV = (RecyclerView)view.findViewById(R.id.course_list_recycler_view);
        courseTableListRV.setAdapter(courseTableListAdapter);
        courseTableListRV.setLayoutManager(new LinearLayoutManager(MainActivity.this));
    }
    private void initCourseTable(View view){

        testPanelAdapter = new TestPanelAdapter(EnvironmentPool.allSemesters.get(EnvironmentPool.curSemPos),EnvironmentPool.semWeekNow);
        courseTablePanel = (ScrollablePanel) view.findViewById(R.id.scrollable_panel);
        courseTablePanel.setPanelAdapter(testPanelAdapter);
    }
    public void updateCourseList(int zhou){
        courseTableListAdapter.setCurZhu(zhou);
        courseTableListAdapter.notifyDataSetChanged();
    }
    public void updateCourseList(String xq){
        courseTableListAdapter.setXq(xq);
    }
    public void updateCourseList(int zhou,String xq){
        courseTableListAdapter.setCurZhu(zhou);
        courseTableListAdapter.setXq(xq);
        courseTableListAdapter.notifyDataSetChanged();
    }
    public void updateCourseTable(int zhou){
        testPanelAdapter.setCurZhou(zhou);
        courseTablePanel.notifyDataSetChanged();
    }
    public void updateCourseTable(String xq){
        testPanelAdapter.setXq(xq);
        courseTablePanel.notifyDataSetChanged();
    }
    public void updateCourseTable(int zhou,String xq){
        testPanelAdapter.setCurZhou(zhou);        courseTableListAdapter.notifyDataSetChanged();

        testPanelAdapter.setXq(xq);
        courseTablePanel.notifyDataSetChanged();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==1){
            if(resultCode==1){
                int state = data.getIntExtra("state",-1);
                if(state>0){
                    updateCourseTable(EnvironmentPool.myCourseTableList.get(EnvironmentPool.curSemPos));
                    updateCourseList(EnvironmentPool.myCourseTableList.get(EnvironmentPool.curSemPos));
                }
            }
        }
    }

    @Override
    protected void onDestroy() {
        MyFileHelper.saveInfo();
        super.onDestroy();
    }
}
