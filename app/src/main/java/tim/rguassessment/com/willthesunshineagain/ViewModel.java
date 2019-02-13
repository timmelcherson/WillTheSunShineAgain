package tim.rguassessment.com.willthesunshineagain;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.util.Log;

import java.util.List;

import tim.rguassessment.com.willthesunshineagain.db.City;
import tim.rguassessment.com.willthesunshineagain.db.DayForecast;
import tim.rguassessment.com.willthesunshineagain.db.RoomDB;
import tim.rguassessment.com.willthesunshineagain.db.ThreeHourlyForecast;

public class ViewModel extends AndroidViewModel {

    private Repository mRepository;
    private LiveData<List<City>> mFavoriteCities;
    private LiveData<List<City>> mAllCities;
    private LiveData<List<ThreeHourlyForecast>> mThreeHourlyForecast;
    private LiveData<List<DayForecast>> mDayForecast;


    private static final String TAG = "TAGG";


    public ViewModel(Application application){
        super(application);
        mRepository = Repository
                .getInstance(RoomDB.getDatabase(application.getApplicationContext()));
    }

    void parseNewForecast(int cityId){
        Log.d(TAG, "Parsing new forecasts instantly");
        mRepository.parseNewForecast(cityId);
    }

    LiveData<List<City>> getAllCities(){
        mAllCities = mRepository.getAllCities();
        return mAllCities;
    }

    LiveData<List<City>> getFavoriteCities(){
        mFavoriteCities = mRepository.getFavoriteCities();
        return mFavoriteCities;
    }

    LiveData<List<ThreeHourlyForecast>> getThreeHourlyForecasts(){
        mThreeHourlyForecast = mRepository.getThreeHourlyForecasts();
        return mThreeHourlyForecast;
    }

    LiveData<List<DayForecast>> getDayForecasts(){
        mDayForecast = mRepository.getDayForecasts();
        return mDayForecast;
    }

    List<Integer> getThreeHourlyTemperatures(){
        return mRepository.getThreeHourlyTemperatures();
    }

    List<Integer> getHumidities(){
        return mRepository.getHumidities();
    }

    List<Integer> getWindSpeeds(){
        return mRepository.getWindSpeeds();
    }


    int getCityTemp(int id){
        return mRepository.getCityTemp(id);
    }

    int getCityHumidity(int id){
        return mRepository.getCityHumidity(id);
    }

    int getCityWind(int id){
        return mRepository.getCityWind(id);
    }

    City fetchCity(int cityId){
        return mRepository.fetchCity(cityId);
    }

    City getCityByName(String name){
        return mRepository.getCityByName(name);
    }

    boolean lookForCityName(String s){
        return mRepository.lookForCityName(s);
    }

    void requestLocations(){
        mRepository.requestLocations();
    }

    void refreshCityList(){
        mRepository.refreshCityList();
    }

    public void insert(City city){
        mRepository.insert(city);
    }

    public void update(City city){
        mRepository.update(city);
    }

    public void delete(City city){
        mRepository.delete(city);
    }
}
