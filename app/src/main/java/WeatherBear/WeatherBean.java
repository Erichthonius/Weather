package WeatherBear;

import java.util.List;

/**
 * Created by lenovo on 2016/5/16 0016.
 */
public class WeatherBean {
    private String city;
    private String release;
    private String weather_id;
    private String weather_str;
    private String temp;
    private String now_temp;
    private String felt_temp;
    private String humdity;
    private String wind;
    private String uv_index;
    private String dressing_index;
    private String tv_excise_index;

    public String getTv_excise_index() {
        return tv_excise_index;
    }

    public void setTv_excise_index(String tv_excise_index) {
        this.tv_excise_index = tv_excise_index;
    }

    private List<FutureWeather> futureWeatherList;

    public List<FutureWeather> getFutureWeatherList() {
        return futureWeatherList;
    }

    public void setFutureWeatherList(List<FutureWeather> futureWeatherList) {
        this.futureWeatherList = futureWeatherList;
    }


    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getDressing_index() {
        return dressing_index;
    }

    public void setDressing_index(String dressing_index) {
        this.dressing_index = dressing_index;
    }

    public String getFelt_temp() {
        return felt_temp;
    }

    public void setFelt_temp(String felt_temp) {
        this.felt_temp = felt_temp;
    }


    public String getHumdity() {
        return humdity;
    }

    public void setHumdity(String humdity) {
        this.humdity = humdity;
    }

    public String getNow_temp() {
        return now_temp;
    }

    public void setNow_temp(String now_temp) {
        this.now_temp = now_temp;
    }

    public String getRelease() {
        return release;
    }

    public void setRelease(String release) {
        this.release = release;
    }

    public String getTemp() {
        return temp;
    }

    public void setTemp(String temp) {
        this.temp = temp;
    }

    public String getUv_index() {
        return uv_index;
    }

    public void setUv_index(String uv_index) {
        this.uv_index = uv_index;
    }

    public String getWeather_id() {
        return weather_id;
    }

    public void setWeather_id(String weather_id) {
        this.weather_id = weather_id;
    }

    public String getWeather_str() {
        return weather_str;
    }

    public void setWeather_str(String weather_str) {
        this.weather_str = weather_str;
    }

    public String getWind() {
        return wind;
    }

    public void setWind(String wind) {
        this.wind = wind;
    }

}
