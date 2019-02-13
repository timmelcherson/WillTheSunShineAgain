package tim.rguassessment.com.willthesunshineagain.db.dao;


import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;

import java.util.ArrayList;
import java.util.List;

import tim.rguassessment.com.willthesunshineagain.db.City;
import tim.rguassessment.com.willthesunshineagain.db.ThreeHourlyForecast;


@Dao
public interface ThreeHourlyForecastDao {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    void insert(ThreeHourlyForecast forecast);

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    void insertHourly(ThreeHourlyForecast... forecasts);

    @Update
    void update(ThreeHourlyForecast forecast);

    @Update
    void updateHourly(ThreeHourlyForecast... forecasts);

    @Delete
    void delete(ThreeHourlyForecast forecast);

    @Delete
    void deleteHourly(ThreeHourlyForecast... forecasts);


    @Query("DELETE FROM three_hourly_forecast_table")
    void deleteAll();

    @Query("SELECT count(*) FROM three_hourly_forecast_table")
    int numberOfForecastRows();

//    @Query("SELECT * FROM three_hourly_forecast_table")
//    LiveData<List<ThreeHourlyForecast>> getThreeHourlyForecast();

    @Query("SELECT COUNT(*) FROM three_hourly_forecast_table WHERE city_id = :id")
    int checkIfExists(int id);


    @Query("SELECT * FROM three_hourly_forecast_table")
    LiveData<List<ThreeHourlyForecast>> getThreeHourlyForecasts();

    @Query("SELECT * FROM three_hourly_forecast_table WHERE city_id = :cityId")
    List<ThreeHourlyForecast> getForecast(int cityId);

    @Query("SELECT temperature FROM three_hourly_forecast_table WHERE city_id = :id")
    int getCityTemp(int id);

    @Query("SELECT humidity FROM three_hourly_forecast_table WHERE city_id = :id")
    int getCityHumidity(int id);

    @Query("SELECT wind_speed FROM three_hourly_forecast_table WHERE city_id = :id")
    int getCityWind(int id);

    @Query("SELECT DISTINCT temperature FROM three_hourly_forecast_table")
    List<Integer> getThreeHourlyTemperatures();

    @Query("SELECT humidity FROM three_hourly_forecast_table")
    List<Integer> getHumidities();

    @Query("SELECT wind_speed FROM three_hourly_forecast_table")
    List<Integer> getWindSpeeds();
}
