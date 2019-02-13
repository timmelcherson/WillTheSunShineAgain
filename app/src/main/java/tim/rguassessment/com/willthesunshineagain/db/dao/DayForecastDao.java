package tim.rguassessment.com.willthesunshineagain.db.dao;


import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;

import java.util.List;

import tim.rguassessment.com.willthesunshineagain.db.DayForecast;


@Dao
public interface DayForecastDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(DayForecast forecast);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertDay(DayForecast... forecasts);

    @Update
    void update(DayForecast forecast);

    @Update
    void updateDay(DayForecast... forecasts);

    @Delete
    void delete(DayForecast forecast);

    @Delete
    void deleteDay(DayForecast... forecasts);


    @Query("DELETE FROM day_forecast_table")
    void deleteAll();

    @Query("SELECT * FROM day_forecast_table")
    LiveData<List<DayForecast>> getDayForecasts();

    @Query("SELECT * FROM day_forecast_table WHERE city_id = :cityId")
    List<DayForecast> getDayForecast(int cityId);

    @Query("SELECT count(*) FROM day_forecast_table")
    int getNumberOfDayRows();

    @Query("SELECT COUNT(*) FROM day_forecast_table WHERE city_id = :id")
    int checkIfExists(int id);

}
