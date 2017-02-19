package com.example.lenovo.weather;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.ContextThemeWrapper;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.thinkland.sdk.android.DataCallBack;
import com.thinkland.sdk.android.JuheData;
import com.thinkland.sdk.android.Parameters;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import Adapter.CityListAdapter;

/**
 * Created by lenovo on 2016/6/2 0002.
 */
public class CityActivity extends Activity {
    private ListView lv_city;
    private Context mcontext;
    private  List<String> list;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_city);
        initViews();
        getCities();
    }

    private void initViews() {
        findViewById(R.id.iv_back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        lv_city = (ListView) findViewById(R.id.lv_city);
    }

    public void getCities() {
        Parameters params = new Parameters();
        params.add("key", "c9c77ce39a769207c6c0268f79f44955");
        JuheData.executeWithAPI(mcontext, 39, "http://v.juhe.cn/weather/citys", JuheData.GET,
                params, new DataCallBack() {
                    @Override
                    public void onSuccess(int i, String s) {
                        try {
                            JSONObject json=new JSONObject(s);
                            int code = json.getInt("resultcode");
                            int error_code=json.getInt("error_code");
                            if(error_code==0&&code==200) {
                                list=new ArrayList<String>();
                                JSONArray resultArray=json.getJSONArray("result");
                                Set<String> citySet=new HashSet<String>();
                                for(int a=0;a<resultArray.length();a++){
                                    String city=resultArray.getJSONObject(a).getString("city");
                                    citySet.add(city);
                                }
                                list.addAll(citySet);
                                CityListAdapter adapter=new CityListAdapter(CityActivity.this,list);
                                lv_city.setAdapter(adapter);
                                lv_city.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                                    @Override
                                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                                        Intent intent=new Intent();
                                        intent.putExtra("city",list.get(position));
                                        setResult(1,intent);
                                        finish();
                                    }
                                });
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }

                    @Override
                    public void onFinish() {
                    }

                    @Override
                    public void onFailure(int i, String s, Throwable throwable) {
                    }
                });
    }
}
