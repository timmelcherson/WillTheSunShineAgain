package tim.rguassessment.com.willthesunshineagain;

import android.app.Activity;
import android.app.Application;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Response;
import tim.rguassessment.com.willthesunshineagain.db.City;
import tim.rguassessment.com.willthesunshineagain.db.DayForecast;
import tim.rguassessment.com.willthesunshineagain.db.Database;
import tim.rguassessment.com.willthesunshineagain.db.DayForecast;
import tim.rguassessment.com.willthesunshineagain.db.RoomDB;
import tim.rguassessment.com.willthesunshineagain.db.ThreeHourlyForecast;
import tim.rguassessment.com.willthesunshineagain.db.dao.CityDao;
import tim.rguassessment.com.willthesunshineagain.db.dao.DayForecastDao;
import tim.rguassessment.com.willthesunshineagain.db.dao.DayForecastDao;
import tim.rguassessment.com.willthesunshineagain.db.dao.ThreeHourlyForecastDao;

public class Repository {

    private static Repository sInstance;
    private static RoomDB mMyDatabase;
    private static CityDao mCityDao;
    private static ThreeHourlyForecastDao mHourlyDao;
    private static DayForecastDao mDayDao;

    private LiveData<List<City>> mFavoriteCities;
    private LiveData<List<City>> mAllCities;
    private List<Integer> mFavoriteLocationIdList;

    private static final String TAG = "TAGG";
    private static final String metoffice_baseUrl = "http://datapoint.metoffice.gov.uk/public/data/val/wxfcs/all/json/";
    private static final String metoffice_apiKey = "key=87e3f22a-4197-4b87-882b-0bb20097a5b4";
    private static final String metoffice_sitelist = "sitelist?";
    private static final String metoffice_3hourly_forecast = "?res=3hourly&";
    private static final String mettofice_daily_forecast = "?res=daily&";

    private OkHttpClient client;

    private int numberOfLocations;

    private Repository(final RoomDB MyDatabase){

        mMyDatabase = MyDatabase;
        mCityDao = mMyDatabase.cityDao();
        mHourlyDao = mMyDatabase.hourlyDao();
        mDayDao = mMyDatabase.dayDao();

        // Fetch the locations if there is none or if the amount of locations differ from last time
        if(mCityDao.getNumberOfRows() == 0 || numberOfLocations != mCityDao.getNumberOfRows()){
            Log.d(TAG, "Should not refresh cities but is dong anyway wtf");
            refreshCityList();
        }

        initialForecastParse();

    }

    static Repository getInstance(final RoomDB MyDatabase){

        if (sInstance == null){
            synchronized (Repository.class){
                if (sInstance == null){
                    sInstance = new Repository(MyDatabase);
                }
            }
        }

        return sInstance;
    }

    private void initialForecastParse(){

        // Make sure the forecasts are fresh when app starts and repository is created
        // If tables are not emptied the old forecasts will persist and clutter tables up
        mHourlyDao.deleteAll();
        mDayDao.deleteAll();

        // Initially only parse all cities that are marked as favorites
        int length = mCityDao.getNumberOfFavorites();

        Log.d(TAG, "Amount of favorites:" + String.valueOf(length));
        mFavoriteLocationIdList = mCityDao.getFavoriteCityIds();

        for (int i = 0; i < length; i++){
            requestThreeHourlyForecast(mFavoriteLocationIdList.get(i));
            requestDayForecast(mFavoriteLocationIdList.get(i));
        }
    }

    void parseNewForecast(int cityId){

        // Count number of rows with cityId. If 0 it has not been downloaded before
        if (mHourlyDao.checkIfExists(cityId) == 0){
            Log.d(TAG, "checked if hourly forecast exists, it didnt so fetch");
            requestThreeHourlyForecast(cityId);
        }

        if (mDayDao.checkIfExists(cityId) == 0){
            Log.d(TAG, "checked if daily forecast exists, it didnt so fetch");

            requestDayForecast(cityId);
        }

    }


    LiveData<List<City>> getAllCities(){
        mAllCities = mCityDao.getAllCities();
        return mAllCities;
    }

    LiveData<List<City>> getFavoriteCities(){
        mFavoriteCities = mCityDao.getAllFavoriteCities();
        return mFavoriteCities;
    }
    
    LiveData<List<ThreeHourlyForecast>> getThreeHourlyForecasts(){
        return mMyDatabase.hourlyDao().getThreeHourlyForecasts();
    }

    LiveData<List<DayForecast>> getDayForecasts(){
        return mMyDatabase.dayDao().getDayForecasts();
    }

    List<Integer> getThreeHourlyTemperatures(){
        return mHourlyDao.getThreeHourlyTemperatures();
    }

    List<Integer> getHumidities(){
        return mHourlyDao.getHumidities();
    }

    List<Integer> getWindSpeeds(){
        return mHourlyDao.getWindSpeeds();
    }

    int getCityTemp(int id){
        return mHourlyDao.getCityTemp(id);
    }

    int getCityHumidity(int id){
        return mHourlyDao.getCityHumidity(id);
    }

    int getCityWind(int id){
        return mHourlyDao.getCityWind(id);
    }

    City fetchCity(int cityId){
        return mCityDao.fetchCity(cityId);
    }

    boolean lookForCityName(String s){
        return mCityDao.lookForCityName(s);
    }

    City getCityByName(String name){
        return mCityDao.getCityByName(name);
    }

    void refreshCityList(){
        mCityDao.deleteAll();
        requestLocations();
    }
    
    void requestLocations(){

        String url = metoffice_baseUrl + metoffice_sitelist + metoffice_apiKey;

        okhttp3.Request request = new okhttp3.Request.Builder().url(url).build();

        client = new OkHttpClient();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                e.printStackTrace();
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull okhttp3.Response response) throws IOException {
                if (response.isSuccessful()){
                    final String myResponse = response.body().string();
                    jsonParseLocations(myResponse);
                }
            }
        });

    }

    private void requestThreeHourlyForecast(final int cityId){

        String id = String.valueOf(cityId);

        String url = metoffice_baseUrl + id + metoffice_3hourly_forecast + metoffice_apiKey;

        Log.d(TAG, "3 hourly URL: " + url);

        client = new OkHttpClient();

        okhttp3.Request request = new okhttp3.Request.Builder().url(url).build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                e.printStackTrace();
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                if (response.isSuccessful()){
                    final String myResponse = response.body().string();
                    jsonParseThreeHourlyForecast(cityId, myResponse);
                }
            }
        });

    }

    private void requestDayForecast(final int cityId){

        String id = String.valueOf(cityId);

        String url = metoffice_baseUrl + id + mettofice_daily_forecast + metoffice_apiKey;

        client = new OkHttpClient();

        okhttp3.Request request = new okhttp3.Request.Builder().url(url).build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                e.printStackTrace();
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                if (response.isSuccessful()){
                    final String myResponse = response.body().string();
                    jsonParseDayForecast(cityId, myResponse);
                }
            }
        });
    }

    private void jsonParseLocations(String string){

        try {

            JSONObject response = new JSONObject(string);

            /** Variables in the JSON according to API documenation **/

            String cityName = "";

            double lon, lat;
            lon = lat = 0.0;

            int id = 0;


            if (response.has("Locations")){
                JSONObject locations = response.getJSONObject("Locations");

                if (locations.has("Location")){

                    JSONArray tempArray = locations.getJSONArray("Location");

                    for (int i = 0; i < tempArray.length(); i++){

                        JSONObject tempLocation = tempArray.getJSONObject(i);

                        if (tempLocation.has("unitaryAuthArea")){
                            if (tempLocation.get("unitaryAuthArea") == "Aberdeenshire"){
                                Log.d(TAG, "Aberdeenshire town spotted at index: " + String.valueOf(i));
                            }
                        }
                    }
                }

                if (locations.has("Location")){

                    JSONArray locationArray = locations.getJSONArray("Location");

                    for (int i = 0; i < locationArray.length(); i++){

                        JSONObject location = locationArray.getJSONObject(i);

                        if (location.has("id")){
                            id = location.getInt("id");
                        }

                        if (location.has("latitude")){
                            lat = location.getDouble("latitude");
                        }

                        if (location.has("longitude")){
                            lon = location.getDouble("longitude");
                        }

                        if (location.has("name")){
                            cityName = location.getString("name");
                        }

                        City city = new City();
                        city.setCityId(id);
                        city.setCityName(cityName);
                        city.setCityCoordLon(lon);
                        city.setCityCoordLat(lat);
                        city.setFavorite(false);

                        insert(city);

                        numberOfLocations++;
                    }
                }
            }


        } catch (JSONException e) {
            e.printStackTrace();
        }

        Log.d(TAG, "Number of locations at last: " + String.valueOf(numberOfLocations));
    }

    private void jsonParseThreeHourlyForecast(final int cityId, String string){

        try {

            JSONObject response = new JSONObject(string);

            if (response.has("SiteRep")){

                JSONObject siteRep = response.getJSONObject("SiteRep");

                if (siteRep.has("DV")){

                    JSONObject DV = siteRep.getJSONObject("DV");

                    if (DV.has("Location")){

                        JSONObject location = DV.getJSONObject("Location");

                        if (location.has("Period")){

                            JSONArray period = location.getJSONArray("Period");

                            // Fetch forecast for 2 days
                            for (int i = 0; i < 2; i++){

                                JSONObject periodData = period.getJSONObject(i);

                                if (periodData.has("Rep")){

                                    JSONArray rep = periodData.getJSONArray("Rep");

                                    for (int j = 0; j < rep.length(); j++){

                                        JSONObject repData = rep.getJSONObject(j);
                                        ThreeHourlyForecast forecast = new ThreeHourlyForecast();

                                        forecast.setCityId(cityId);

                                        if (repData.has("D")){
                                            forecast.setWindDirection(repData.getString("D"));
                                        }

                                        if (repData.has("F")){
                                            forecast.setFeelsLikeTemp(repData.getInt("F"));
                                        }

                                        if (repData.has("H")){
                                            forecast.setHumidity(repData.getInt("H"));
                                        }

                                        if (repData.has("S")){
                                            forecast.setWindSpeed(repData.getInt("S"));
                                        }

                                        if (repData.has("T")){
                                            forecast.setTemperature(repData.getInt("T"));
                                        }

                                        if (repData.has("V")){

                                            switch (repData.getString("V")){

                                                case "UN":
                                                    forecast.setVisibility(0);
                                                    break;
                                                case "VP":
                                                    forecast.setVisibility(1);
                                                    break;
                                                case "PO":
                                                    forecast.setVisibility(10);
                                                    break;
                                                case "MO":
                                                    forecast.setVisibility(41);
                                                    break;
                                                case "GO":
                                                    forecast.setVisibility(20);
                                                    break;
                                                case "VG":
                                                    forecast.setVisibility(40);
                                                    break;
                                                case "EX":
                                                    forecast.setVisibility(41);
                                                    break;
                                            }
                                        }

                                        if (repData.has("W")){
                                            forecast.setWeatherType(repData.getInt("W"));
                                        }

                                        if (repData.has("$")){
                                            forecast.setMinutes(repData.getInt("$"));
                                        }
                                        insertThreeHourlyForecast(forecast);
                                    }
                                }
                            }
                        }
                    }
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void jsonParseDayForecast(final int cityId, String string){

        try {

            JSONObject response = new JSONObject(string);

            if (response.has("SiteRep")){

                JSONObject siteRep = response.getJSONObject("SiteRep");

                if (siteRep.has("DV")){

                    JSONObject DV = siteRep.getJSONObject("DV");

                    if (DV.has("Location")){

                        JSONObject location = DV.getJSONObject("Location");

                        if (location.has("Period")){

                            JSONArray period = location.getJSONArray("Period");

                            // Fetch all days
                            for (int i = 0; i < period.length(); i++){

                                JSONObject periodData = period.getJSONObject(i);

                                DayForecast forecast = new DayForecast();

                                forecast.setCityId(cityId);

                                if (periodData.has("value")){
                                    forecast.setDate(periodData.getString("value"));
                                }


                                if (periodData.has("Rep")){

                                    JSONArray rep = periodData.getJSONArray("Rep");

                                    // Rep will always have length 2 in Day forecasts,
                                    // First iteration is day, next is night
                                    for (int j = 0; j < rep.length(); j++){

                                        JSONObject repData = rep.getJSONObject(j);

                                        if (repData.has("Dm")){
                                            forecast.setDayMaxTemp(repData.getInt("Dm"));
                                        }

                                        if (repData.has("W")){
                                            forecast.setWeatherTypeDay(repData.getInt("W"));
                                        }


                                        if (repData.has("Nm")){
                                            forecast.setNightMinTemp(repData.getInt("Nm"));
                                        }
                                    }
                                    insertDayForecast(forecast);
                                }
                            }
                        }
                    }
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }



    // AsyncTasks for City class objects
    public void insert(City city){
        new InsertAsyncTask().execute(city);
    }

    private static class InsertAsyncTask extends AsyncTask<City, Void, Void>{

        @Override
        protected Void doInBackground(final City... cities){
            mCityDao.insert(cities[0]);
            return null;
        }
    }

    public void update(City city){
        new UpdateAsyncTask(mCityDao).execute(city);
    }

    private static class UpdateAsyncTask extends AsyncTask<City, Void, Void>{

        private CityDao mAsyncTaskDao;

        UpdateAsyncTask(CityDao dao){
            mAsyncTaskDao = dao;
        }

        @Override
        protected Void doInBackground(final City... cities){
            mAsyncTaskDao.update(cities[0]);
            Log.d(TAG, "Updating city with something");
            return null;
        }
    }

    public void delete(City city){
        new DeleteAsyncTask(mCityDao).execute(city);
    }

    private static class DeleteAsyncTask extends AsyncTask<City, Void, Void>{

        private CityDao mAsyncTaskDao;

        DeleteAsyncTask(CityDao dao){
            mAsyncTaskDao = dao;
        }

        @Override
        protected Void doInBackground(final City... cities){
            mAsyncTaskDao.delete(cities[0]);
            return null;
        }
    }


    // AsyncTasks for ThreeHourlyForecast class objects
    private void insertThreeHourlyForecast(ThreeHourlyForecast forecast){
        new InsertThreeHourlyForecastAsyncTask().execute(forecast);
    }

    private static class InsertThreeHourlyForecastAsyncTask extends AsyncTask<ThreeHourlyForecast, Void, Void>{

        @Override
        protected Void doInBackground(final ThreeHourlyForecast... forecasts){
            mHourlyDao.insert(forecasts[0]);
            return null;
        }
    }


    // AsyncTasks for DayForecast class objects
    private void insertDayForecast(DayForecast forecast){
        new InsertDayForecastAsyncTask().execute(forecast);
    }

    private static class InsertDayForecastAsyncTask extends AsyncTask<DayForecast, Void, Void>{

        @Override
        protected Void doInBackground(final DayForecast... forecasts){
            mDayDao.insert(forecasts[0]);
            return null;
        }
    }



}
