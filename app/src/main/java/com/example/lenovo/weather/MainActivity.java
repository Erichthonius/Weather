package com.example.lenovo.weather;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import java.util.Calendar;
import java.util.List;

import com.example.lenovo.weather.Service.WeatherService;
import com.example.lenovo.weather.Service.WeatherService.WeatherServiceBinder;
import WeatherBear.FutureWeather;
import WeatherBear.HoursWeatherBean;
import WeatherBear.PMBean;
import WeatherBear.WeatherBean;
import swiperefresh.PullToRefreshBase;
import swiperefresh.PullToRefreshScrollView;

public class MainActivity extends Activity {

    private PullToRefreshScrollView pullToRefreshScrollView;
    private ScrollView scrollView;
    private Context mcontext,ncontext;
    private WeatherService mService;
    private TextView tv_city,//选择城市
            release,//发布时间
            tv_now_weather,//现在天气
            tv_today_temp,//今天温度
            tv_now_temp,//现在温度
            tv_aqi,//空气指数
            tv_quality,//空气质量
            tv_next_three,//未来间隔三个小时
            tv_next_six,
            tv_next_nine,
            tv_next_twelve,
            tv_next_fifteen,
            tv_temp_three,//未来间隔三个小时温度
            tv_temp_six,
            tv_temp_nine,
            tv_temp_twelve,
            tv_temp_fifteen,
            tv_today_temp_a,
            tv_today_temp_b,
            tommorrow,//明天
            tv_tommorrow_temp_a,//明天最高
            tv_tommorrow_temp_b,//明天最低
            third,//后天
            tv_third_temp_a,//后天最高
            tv_third_temp_b,//后天最低
            fourth,//大后天
            tv_fourth_temp_a,//大后天最高
            tv_fourth_temp_b,//大后天最低
            tv_excise_index,//晨练指数
            tv_humidity,//湿度
            tv_wind,//风力风向
            tv_uv_index,//紫外线指数
            tv_wear_index;//穿衣指数
    private ImageView iv_now_weather,//现在天气
            iv_next_three,//未来间隔三个小时天气
            iv_next_six,
            iv_next_nine,
            iv_next_twelve,
            iv_next_fifteen,
            iv_today_weather,//今天天气
            iv_tommorrow_weather,//明天天气
            iv_third_weather,//后天天气
            iv_fourth_weather;//大后天天气
    private RelativeLayout rl_city;

private boolean isRunning=false;
    private int count=0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mcontext = this;
//       mService.getCityWeather();
        init();
        InitService();
    }
    public void InitService(){
        Intent intent=new Intent(mcontext, WeatherService.class);
        startService(intent);
        bindService(intent,conn,Context.BIND_AUTO_CREATE);
    }
    ServiceConnection conn=new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            mService=((WeatherServiceBinder)service).getService();
            mService.setCallBack(new WeatherService.OnParserCallBack() {
                @Override
                public void OnParserComPlete(List<HoursWeatherBean> list, PMBean pmBean, WeatherBean weatherBean) {
                    pullToRefreshScrollView.onRefreshComplete();
                    if(list!=null&&list.size()>=5){
                        setHourWeatherViews(list);
                    }
                    if(pmBean!=null){
                         setPMViews(pmBean);
                    }
                    if (weatherBean != null) {
                          setWeatherViews(weatherBean);
                    }
                }
            });
            mService.getCityWeather();
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            mService.removeCallBack();
        }
    };

    private void setPMViews(PMBean bean){
        tv_aqi.setText(bean.getAqi());
        tv_quality.setText(bean.getQuality());
    }
    private void setHourWeatherViews(List<HoursWeatherBean> list){
        setHourData(tv_next_three,iv_next_three,tv_temp_three,list.get(0));
        setHourData(tv_next_six,iv_next_six,tv_temp_six,list.get(1));
        setHourData(tv_next_nine, iv_next_nine, tv_temp_nine, list.get(2));
        setHourData(tv_next_twelve, iv_next_twelve, tv_temp_twelve, list.get(3));
        setHourData(tv_next_fifteen, iv_next_fifteen, tv_temp_fifteen, list.get(4));
    }
    private void setHourData(TextView tv_hour,ImageView iv_weather,TextView tv_temp,HoursWeatherBean bean){
        String prefixstr=null;
        int time=Integer.valueOf(bean.getTime());
        if(time>=6&&time<18){
            prefixstr="d";
        }else {
            prefixstr="n";
        }
        tv_hour.setText(bean.getTime()+"时");
        iv_weather.setImageResource(getResources().getIdentifier(prefixstr + bean.getWeather_id(),
                "drawable", "com.example.lenovo.weather"));
        tv_temp.setText(bean.getTemp()+"°");
    }
    private void setWeatherViews(WeatherBean bean){
                tv_city.setText(bean.getCity());
                release.setText(bean.getRelease());
                tv_now_weather.setText(bean.getWeather_str()+"°");
        String[] tempArr=bean.getTemp().split("~");
        String temp_str_b=tempArr[1].substring(0,tempArr[1].indexOf("℃"));
        String temp_str_a=tempArr[0].substring(0,tempArr[0].indexOf("℃"));
                tv_today_temp.setText("↑  "+temp_str_b+"°  ↓"+temp_str_a+"°");
                // ;8℃~20℃ ↑ ↓°
                tv_now_temp.setText(bean.getNow_temp()+"℃");
                iv_today_weather.setImageResource(getResources().getIdentifier("d" + bean.getWeather_id(), "drawable", "com.example.lenovo.weather"));
                tv_today_temp_a.setText(temp_str_a+"°");
                tv_today_temp_b.setText(temp_str_b+"°");
            List<FutureWeather> futurelist=bean.getFutureWeatherList();
                if(futurelist.size()==3){
                    setFutureData(tommorrow, iv_tommorrow_weather,
                            tv_tommorrow_temp_a, tv_tommorrow_temp_b, futurelist.get(0));
                    setFutureData(third, iv_third_weather, tv_third_temp_a,
                            tv_third_temp_b, futurelist.get(1));
                    setFutureData(fourth, iv_fourth_weather, tv_fourth_temp_a,
                            tv_fourth_temp_b, futurelist.get(2));
                }
        Calendar c=Calendar.getInstance();
        int time=c.get(Calendar.HOUR_OF_DAY);
        String prefixstr=null;
        if(time>=6&&time<18){
            prefixstr="d";
        }else {
            prefixstr="n";
        }
        iv_now_weather.setImageResource(getResources().getIdentifier(prefixstr + bean.getWeather_id(), "drawable",
                "com.example.lenovo.weather"));
        tv_humidity.setText(bean.getHumdity());
        tv_wear_index.setText(bean.getDressing_index());
        tv_uv_index.setText(bean.getUv_index());
        tv_wind.setText(bean.getWind());
        tv_excise_index.setText(bean.getTv_excise_index());

    }
    private void setFutureData(TextView tv_week,ImageView iv_weather,TextView tv_temp_a,TextView tv_temp_b,FutureWeather bean){
        tv_week.setText(bean.getWeek());
        iv_weather.setImageResource(getResources().getIdentifier("d" + bean.getWeather_id(), "drawable",
                "com.example.lenovo.weather"));
        String[] tempArr=bean.getTemp().split("~");
        String temp_str_b=tempArr[1].substring(0,tempArr[1].indexOf("℃"));
        String temp_str_a=tempArr[0].substring(0,tempArr[0].indexOf("℃"));
        tv_temp_a.setText(temp_str_a + "°");
        tv_temp_b.setText(temp_str_b + "°");
    }
    //获取城市天气

    private void init() {
        pullToRefreshScrollView=(PullToRefreshScrollView)findViewById(R.id.pull_to_refresh_scrollview);
        pullToRefreshScrollView.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener<ScrollView>() {
            @Override
            public void onRefresh(PullToRefreshBase<ScrollView> refreshView) {
                mService.getCityWeather();
        }
        });
        scrollView=pullToRefreshScrollView.getRefreshableView();
        rl_city=(RelativeLayout)findViewById(R.id.rl_city);
        rl_city.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivityForResult(new Intent(mcontext,CityActivity.class),1);
            }
        });
        //TextView初始化
        tv_city =(TextView)findViewById(R.id.tv_city);
        release =(TextView)findViewById(R.id.release);
        tv_now_weather =(TextView)findViewById(R.id.tv_now_weather);
        tv_today_temp =(TextView)findViewById(R.id.tv_today_temp);
        tv_now_temp=(TextView)findViewById(R.id.tv_now_temp);
        tv_aqi =(TextView)findViewById(R.id.tv_aqi);
        tv_quality =(TextView)findViewById(R.id.tv_quality);
        tv_next_three =(TextView)findViewById(R.id.tv_next_three);
        tv_next_six =(TextView)findViewById(R.id.tv_next_six);
        tv_next_nine =(TextView)findViewById(R.id.tv_next_nine);
        tv_next_twelve =(TextView)findViewById(R.id.tv_next_twelve);
        tv_next_fifteen =(TextView)findViewById(R.id.tv_next_fifteen);
        tv_temp_three =(TextView)findViewById(R.id.tv_temp_three);
        tv_temp_six =(TextView)findViewById(R.id.tv_temp_six);
        tv_temp_nine =(TextView)findViewById(R.id.tv_temp_nine);
        tv_temp_twelve =(TextView)findViewById(R.id.tv_temp_twelve);
        tv_temp_fifteen =(TextView)findViewById(R.id.tv_temp_fifteen);
        tv_today_temp_a =(TextView)findViewById(R.id.tv_today_temp_a);
        tv_today_temp_b =(TextView)findViewById(R.id.tv_today_temp_b);
        tommorrow =(TextView)findViewById(R.id.tommorrow);
        tv_tommorrow_temp_a =(TextView)findViewById(R.id.tv_tommorrow_temp_a);
        tv_tommorrow_temp_b =(TextView)findViewById(R.id.tv_tommorrow_temp_b);
        third =(TextView)findViewById(R.id.third);
        tv_third_temp_a =(TextView)findViewById(R.id.tv_third_temp_a);
        tv_third_temp_b =(TextView)findViewById(R.id.tv_third_temp_b);
        fourth =(TextView)findViewById(R.id.fourth);
        tv_fourth_temp_a =(TextView)findViewById(R.id. tv_fourth_temp_a);
        tv_fourth_temp_b =(TextView)findViewById(R.id.tv_fourth_temp_b);
        tv_excise_index =(TextView)findViewById(R.id.tv_excise_index);
        tv_humidity =(TextView)findViewById(R.id.tv_humidity);
        tv_wind =(TextView)findViewById(R.id.tv_wind);
        tv_uv_index =(TextView)findViewById(R.id.tv_uv_index);
        tv_wear_index =(TextView)findViewById(R.id.tv_wear_index);
        //ImageView初始化
        iv_now_weather=(ImageView)findViewById(R.id.iv_now_weather);
        iv_next_three=(ImageView)findViewById(R.id.iv_next_three);
        iv_next_six=(ImageView)findViewById(R.id.iv_next_six);
        iv_next_nine=(ImageView)findViewById(R.id.iv_next_nine);
        iv_next_twelve=(ImageView)findViewById(R.id.iv_next_twelve);
        iv_next_fifteen=(ImageView)findViewById(R.id.iv_next_fifteen);
        iv_today_weather=(ImageView)findViewById(R.id. iv_today_weather);
        iv_tommorrow_weather=(ImageView)findViewById(R.id.iv_tommorrow_weather);
        iv_third_weather=(ImageView)findViewById(R.id.iv_third_weather);
        iv_fourth_weather=(ImageView)findViewById(R.id.iv_fourth_weather);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode==1&&resultCode==1){
            String city=data.getStringExtra("city");
            mService.getCityWeather(city);
        }
    }

    @Override
    protected void onDestroy() {

        unbindService(conn);
        super.onDestroy();
    }
}
