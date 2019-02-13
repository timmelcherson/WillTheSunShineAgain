package tim.rguassessment.com.willthesunshineagain.db;


import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.ForeignKey;
import android.arch.persistence.room.PrimaryKey;
import android.support.annotation.NonNull;


@Entity(tableName = "day_forecast_table",
        foreignKeys = {
                @ForeignKey(
                        entity = City.class,
                        parentColumns = "city_id",
                        childColumns = "city_id",
                        onDelete = ForeignKey.CASCADE,
                        onUpdate = ForeignKey.CASCADE
                )
        })
public class DayForecast {

    @PrimaryKey(autoGenerate = true)
    @NonNull
    @ColumnInfo(name = "uid")
    private int uid;

    @NonNull
    @ColumnInfo(name = "city_id")
    private int cityId;

    @ColumnInfo(name = "day_max_temp")
    private int dayMaxTemp;

    @ColumnInfo(name = "night_min_temp")
    private int nightMinTemp;

    @ColumnInfo(name = "weather_type_day")
    private int weatherTypeDay;

    @ColumnInfo(name = "date")
    private String date;
    
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

    public int getDayMaxTemp() {
        return dayMaxTemp;
    }

    public void setDayMaxTemp(int dayMaxTemp) {
        this.dayMaxTemp = dayMaxTemp;
    }

    public int getNightMinTemp() {
        return nightMinTemp;
    }

    public void setNightMinTemp(int nightMinTemp) {
        this.nightMinTemp = nightMinTemp;
    }

    public int getWeatherTypeDay() {
        return weatherTypeDay;
    }

    public void setWeatherTypeDay(int weatherTypeDay) {
        this.weatherTypeDay = weatherTypeDay;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }
}
