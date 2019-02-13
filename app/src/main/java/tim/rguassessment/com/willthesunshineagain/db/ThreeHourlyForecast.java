package tim.rguassessment.com.willthesunshineagain.db;


import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.ForeignKey;
import android.arch.persistence.room.Index;
import android.arch.persistence.room.PrimaryKey;
import android.support.annotation.NonNull;

import java.util.Date;


@Entity(tableName = "three_hourly_forecast_table", indices = {@Index("uid")},
foreignKeys = {
        @ForeignKey(
                entity = City.class,
                parentColumns = "city_id",
                childColumns = "city_id",
                onDelete = ForeignKey.CASCADE,
                onUpdate = ForeignKey.CASCADE
        )
})
public class ThreeHourlyForecast {

    @PrimaryKey(autoGenerate = true)
    @NonNull
    @ColumnInfo(name = "uid")
    private int uid;

    @NonNull
    @ColumnInfo(name = "city_id")
    private int cityId;

    @ColumnInfo(name = "feels_like_temp")
    private int feelsLikeTemp;

    @ColumnInfo(name = "humidity")
    private int humidity;

    @ColumnInfo(name = "temperature")
    private int temperature;

    @ColumnInfo(name = "visibility")
    private int visibility;

    @ColumnInfo(name = "wind_speed")
    private int windSpeed;

    @ColumnInfo(name = "weather_type")
    private int weatherType;

    @ColumnInfo(name = "date")
    private Date date;

    @ColumnInfo(name = "minutes")
    private int minutes;

    @ColumnInfo(name = "wind_direction")
    private String windDirection;

    @NonNull
    public int getUid() {
        return uid;
    }

    public void setUid(@NonNull int uid) {
        this.uid = uid;
    }

    @NonNull
    public int getCityId() {
        return cityId;
    }

    public void setCityId(@NonNull int cityId) {
        this.cityId = cityId;
    }

    public int getFeelsLikeTemp() {
        return feelsLikeTemp;
    }

    public void setFeelsLikeTemp(int feelsLikeTemp) {
        this.feelsLikeTemp = feelsLikeTemp;
    }

    public int getHumidity() {
        return humidity;
    }

    public void setHumidity(int humidity) {
        this.humidity = humidity;
    }

    public int getTemperature() {
        return temperature;
    }

    public void setTemperature(int temperature) {
        this.temperature = temperature;
    }

    public int getVisibility() {
        return visibility;
    }

    public void setVisibility(int visibility) {
        this.visibility = visibility;
    }

    public int getWindSpeed() {
        return windSpeed;
    }

    public void setWindSpeed(int windSpeed) {
        this.windSpeed = windSpeed;
    }

    public String getWindDirection() {
        return windDirection;
    }

    public void setWindDirection(String windDirection) {
        this.windDirection = windDirection;
    }

    public int getWeatherType() {
        return weatherType;
    }

    public void setWeatherType(int weatherType) {
        this.weatherType = weatherType;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public int getMinutes() {
        return minutes;
    }

    public void setMinutes(int minutes) {
        this.minutes = minutes;
    }
}
