package com.example.lenovo.weather.Service;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.widget.Toast;

import com.thinkland.sdk.android.DataCallBack;
import com.thinkland.sdk.android.JuheData;
import com.thinkland.sdk.android.Parameters;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.concurrent.CountDownLatch;

import WeatherBear.FutureWeather;
import WeatherBear.HoursWeatherBean;
import WeatherBear.PMBean;
import WeatherBear.WeatherBean;

/**
 * Created by lenovo on 2016/6/3 0003.
 */
public class WeatherService extends Service {

   private String city;
    private boolean isRunning=false;
    private int count=0;
    private Context mcontext;
    private WeatherServiceBinder binder=new WeatherServiceBinder();
    private List<HoursWeatherBean> list;
    private PMBean pmBean;
    private WeatherBean weatherBean;
    private OnParserCallBack callBack;
    private final int REPEAT_MSG = 0x01;
    private final int CALLBACK_OK = 0x02;
    private final int CALLBACK_ERROR = 0x04;


    public  interface OnParserCallBack{
        public void OnParserComPlete(List<HoursWeatherBean> list, PMBean pmBean,WeatherBean weatherBean);
    }

    public IBinder onBind(Intent intent) {
        return binder;
    }
    Handler mHandler = new Handler() {

        public void handleMessage(Message msg) {
            // TODO Auto-generated method stub

            switch (msg.what) {
                case REPEAT_MSG:

                    getCityWeather();
                    sendEmptyMessageDelayed(REPEAT_MSG, 30 * 60 * 1000);
                    break;
                case CALLBACK_OK:
                    if (callBack != null) {
                        callBack.OnParserComPlete(list, pmBean, weatherBean);
                    }
                    isRunning = false;
                    break;
                case CALLBACK_ERROR:
                    Toast.makeText(getApplicationContext(), "loading error", Toast.LENGTH_SHORT).show();
                    break;

                default:
                    break;
            }
        }

    };


    @Override
    public void onCreate() {
        city="厦门";
        super.onCreate();
    }
    public void getCityWeather(String city){
        this.city=city;
        getCityWeather();
    }
    public void setCallBack(OnParserCallBack callBack){
        this.callBack=callBack;
    }
    public void removeCallBack(){
        callBack=null;
    }
    public void getCityWeather() {
        if(isRunning){
            return;
        }
        isRunning=true;
        final CountDownLatch countDownLatch = new CountDownLatch(3);
        final Parameters params = new Parameters();
        params.add("cityname", city);
        params.add("dtype", "json");
        params.add("format", "2");
        params.add("key", "c9c77ce39a769207c6c0268f79f44955");
        JuheData.executeWithAPI(mcontext, 39, "http://v.juhe.cn/weather/index", JuheData.GET,
                params, new DataCallBack() {
                    @Override
                    public void onSuccess(int i, String s) {
                        count++;
                         weatherBean = parserWeather(s);
                        countDownLatch.countDown();
                    }
                    @Override
                    public void onFinish() {
                    }

                    @Override
                    public void onFailure(int i, String s, Throwable throwable) {
                    }
                });
        Parameters params1 = new Parameters();
        params1.add("cityname", city);
        params1.add("dtype", "json");
        params1.add("key", "c9c77ce39a769207c6c0268f79f44955");
        JuheData.executeWithAPI(mcontext, 39, "http://v.juhe.cn/weather/forecast3h", JuheData.GET,
                params1, new DataCallBack() {
                    @Override
                    public void onSuccess(int i, String s) {
                        count++;
                         list = parserforecast3h(s);
                        countDownLatch.countDown();
//                        if(list!=null&&list.size()>=5) {
//                           // setHourWeatherViews(list);
//                        }
//                        if(count==3){
//                           // pullToRefreshScrollView.onRefreshComplete();
//                            callBack.OnParserComPlete(list,pmBean,weatherBean);
//                            isRunning=true;
//                        }
                    }

                    @Override
                    public void onFinish() {

                    }

                    @Override
                    public void onFailure(int i, String s, Throwable throwable) {

                    }
                });
        Parameters params2 = new Parameters();
        params2.add("city", city);
        params2.add("key", "e6e0b7ded0b0117886a3d44da8dccc95");
        JuheData.executeWithAPI(mcontext, 33, "http://web.juhe.cn:8080/environment/air/pm", JuheData.GET,
                params2, new DataCallBack() {
                    @Override
                    public void onSuccess(int i, String s) {
                        count++;
                        pmBean=parserPM(s);
                        countDownLatch.countDown();
//                        if(pmBean!=null){
//                          //  setPMViews(bear);
//                        }
//
//                    if(count==3){
//                         //   pullToRefreshScrollView.onRefreshComplete();
//                            callBack.OnParserComPlete(list,pmBean,weatherBean);
//                            isRunning=true;
//                        }
                    }

                    @Override
                    public void onFinish() {

                    }

                    @Override
                    public void onFailure(int i, String s, Throwable throwable) {

                    }
                });
        new Thread() {

            @Override
            public void run() {
                // TODO Auto-generated method stub
                try {
                    countDownLatch.await();
                    mHandler.sendEmptyMessage(CALLBACK_OK);
                } catch (InterruptedException ex) {
                    mHandler.sendEmptyMessage(CALLBACK_ERROR);
                    return;
                }
            }

        }.start();
    }
    private WeatherBean parserWeather(String s){
        SimpleDateFormat sdf=new SimpleDateFormat("yyyyMMdd");
        WeatherBean bean=null;
        try {
            JSONObject json=new JSONObject(s);
            int code=json.getInt("resultcode");
            int error_code=json.getInt("error_code");
            if(error_code==0&&code==200){
                JSONObject resultjson=json.getJSONObject("result");
                bean=new WeatherBean();
                //解析today
                JSONObject todayjson=resultjson.getJSONObject("today");
                bean.setCity(todayjson.getString("city"));
                bean.setUv_index(todayjson.getString("uv_index"));
                bean.setTv_excise_index(todayjson.getString("exercise_index"));
                bean.setTemp(todayjson.getString("temperature"));
                bean.setWeather_str(todayjson.getString("weather"));
                bean.setWeather_id(todayjson.getJSONObject("weather_id").getString("fa"));
                bean.setDressing_index(todayjson.getString("dressing_index")+todayjson.getString("dressing_advice"));
                //解析sk
                JSONObject skjson=resultjson.getJSONObject("sk");
                bean.setWind(skjson.getString("wind_direction") + skjson.getString("wind_strength"));
                bean.setNow_temp(skjson.getString("temp"));
                bean.setRelease(skjson.getString("time"));
                bean.setHumdity(skjson.getString("humidity"));
                //解析Future
                JSONArray futurearray=resultjson.getJSONArray("future");
                Date date=new Date(System.currentTimeMillis());
                List<FutureWeather> futurelist=new ArrayList<FutureWeather>();
                for(int i=0;i<futurearray.length();i++){

                    JSONObject futurejson=futurearray.getJSONObject(i);
                    FutureWeather futurebean=new FutureWeather();
                    Date  date1=sdf.parse(futurejson.getString("date"));
                    if(!date1.after(date)){
                        continue;
                    }
                    futurebean.setTemp(futurejson.getString("temperature"));
                    futurebean.setWeek(futurejson.getString("week"));
                    futurebean.setWeather_id(futurejson.getJSONObject("weather_id").getString("fa"));
                    futurelist.add(futurebean);
                    if(futurelist.size()==3){
                        break;
                    }
//                    else {
//                        Toast.makeText(getApplicationContext(), "WEATHER_ERROR", Toast.LENGTH_SHORT).show();
//                    }
                }
                bean.setFutureWeatherList(futurelist);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return bean;

    }
    private List<HoursWeatherBean>  parserforecast3h(String s)  {
        List<HoursWeatherBean> list=null;
        SimpleDateFormat sdf=new SimpleDateFormat("yyyyMMddhhmmss");
        Date date=new Date(System.currentTimeMillis());
        try {
            JSONObject json=new JSONObject(s);
            int code = json.getInt("resultcode");
            int error_code=json.getInt("error_code");
            if(error_code==0&&code==200){
                list=new ArrayList<HoursWeatherBean>();
                JSONArray resultArray=json.getJSONArray("result");
                for(int i=0;i<resultArray.length();i++){
                    JSONObject hourjson=resultArray.getJSONObject(i);
                    Date hDate=sdf.parse( hourjson.getString("sfdate"));
                    if(!hDate.after(date)){
                        continue;
                    }
                    HoursWeatherBean bean=new HoursWeatherBean();
                    bean.setWeather_id(hourjson.getString("weatherid"));
                    bean.setTemp(hourjson.getString("temp1"));
                    Calendar c=Calendar.getInstance();
                    c.setTime(hDate);
                    bean.setTime(c.get(Calendar.HOUR_OF_DAY)+"");
                    list.add(bean);
                    if(list.size()==5){
                        break;
                    }
//                    else {
//                        Toast.makeText(getApplicationContext(), "HOURS_ERROR", Toast.LENGTH_SHORT).show();
//                    }
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return list;
    }
    private PMBean parserPM(String s){
        PMBean bear=null;
        try {
            JSONObject json=new JSONObject(s);
            int code=json.getInt("resultcode");
            int error_code=json.getInt("error_code");
            if(error_code == 0&&code == 200){
                JSONObject pmJSON=json.getJSONArray("result").getJSONObject(0);
                bear=new PMBean();
                bear.setAqi(pmJSON.getString("AQI"));
                bear.setQuality((pmJSON.getString("quality")));
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
        return bear;
    }



    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return super.onStartCommand(intent, flags, startId);
    }
    public  class WeatherServiceBinder extends Binder{
        public WeatherService getService(){
            return WeatherService.this;
        }
    }
}
