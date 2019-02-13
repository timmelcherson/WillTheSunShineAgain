package tim.rguassessment.com.willthesunshineagain.db.dao;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;

import java.util.List;

import tim.rguassessment.com.willthesunshineagain.db.City;

@Dao
public interface CityDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(City city);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertCities(City... cities);

    @Update
    void update(City city);

    @Update
    void updateCities(City... cities);

    @Delete
    void delete(City city);

    @Delete
    void deleteCities(City... cities);

    @Query("SELECT * FROM city_table WHERE favorite = 'true' ORDER BY city_name ASC")
    LiveData<List<City>> getAllFavoriteCities();

    @Query("SELECT * FROM city_table ORDER BY city_name ASC")
    LiveData<List<City>> getAllCities();

    @Query("SELECT count(*) FROM city_table")
    int getNumberOfRows();

    @Query("SELECT count(*) FROM city_table WHERE favorite = 1")
    int getNumberOfFavorites();

    @Query("SELECT city_id FROM city_table")
    List<Integer> getCityIds();

    @Query("SELECT city_id FROM city_table WHERE favorite = 1")
    List<Integer> getFavoriteCityIds();

    @Query("SELECT * FROM city_table WHERE city_id = :cityId")
    City fetchCity(int cityId);

    @Query("DELETE FROM city_table")
    void deleteAll();

    @Query("SELECT city_name FROM city_table WHERE city_id = :cityId")
    String getCityNameFromId(int cityId);

    @Query("SELECT city_name FROM city_table")
    List<String> getCityNames();

    @Query("SELECT DISTINCT * FROM city_table WHERE city_name = :name")
    boolean lookForCityName(String name);

    @Query("SELECT DISTINCT * FROM city_table WHERE city_name = :name")
    City getCityByName(String name);

}
