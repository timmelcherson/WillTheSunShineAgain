package tim.rguassessment.com.willthesunshineagain;

import android.app.ActivityOptions;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import java.lang.reflect.Type;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import tim.rguassessment.com.willthesunshineagain.db.City;
import tim.rguassessment.com.willthesunshineagain.db.DayForecast;
import tim.rguassessment.com.willthesunshineagain.db.ThreeHourlyForecast;

import static android.content.Intent.ACTION_VIEW;


public class MainActivity extends AppCompatActivity
        implements
        View.OnClickListener,
        SharedPreferences.OnSharedPreferenceChangeListener {

    /** Strings**/
    private static final String TAG = "TAGG";
    private String mHomeLocation, mTempUnits, mMeasurementUnits;

    /** TextViews **/
    private TextView tv_main_location, tv_main_temp, tv_main_wind, tv_main_humidity;
    private TextView col1_time, col2_time, col3_time, col4_time, col5_time;
    private TextView col1_temp, col2_temp, col3_temp, col4_temp, col5_temp;
    private TextView c1_t2_weekday, c2_t2_weekday, c3_t2_weekday, c4_t2_weekday, c5_t2_weekday;
    private TextView c1_t2_temp, c2_t2_temp, c3_t2_temp, c4_t2_temp, c5_t2_temp;
    private TextView tv_favorites_list;
    /** ImageViews **/
    private ImageView weatherIcon1, weatherIcon2, weatherIcon3, weatherIcon4, weatherIcon5;
    private ImageView c1_t2_icon, c2_t2_icon, c3_t2_icon, c4_t2_icon, c5_t2_icon;
    private ImageView iv_show_location, iv_search_location, mFavoriteStar;

    /** Other **/
    private ViewModel mViewModel;
    private City mCity;
    private int mCityId;
    private List<Integer> mFavoriteCityIds;

    /** Shared Preferences **/
    private SharedPreferences settings;
    private SharedPreferences sharedPrefs;
    private SharedPreferences.OnSharedPreferenceChangeListener listener;





    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Specify window options
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getWindow().requestFeature(Window.FEATURE_CONTENT_TRANSITIONS);
        getWindow().setAllowEnterTransitionOverlap(true);
        getWindow().setAllowReturnTransitionOverlap(true);


        setContentView(R.layout.activity_main);


        // Build a new actionbar, replacing the default actionbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        // Setting up shared preferences and preferences from Settings
        PreferenceManager.setDefaultValues(getApplicationContext(), R.xml.preferences, false);
        settings = PreferenceManager.getDefaultSharedPreferences(this);

        sharedPrefs = getSharedPreferences(getString(R.string.preferences_file), MODE_PRIVATE);
        listener = this;

        // Initialize variables
        mFavoriteCityIds = new ArrayList<>();
        mCity = new City();
        mViewModel = ViewModelProviders.of(this).get(ViewModel.class);


        // Find all view elements
        initializeViews();

        // Initialize observers of LiveData
        observeLiveDataItems();

        // Restore shared preferences and settings
        restorePreferences();

        // Handle eventual incoming intents when activity is created
        getIncomingIntent();
    }



    @Override
    public boolean onCreateOptionsMenu(Menu topRightMenu){
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.main_activity_topright_menuxml, topRightMenu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item){

        if (item.getItemId() == R.id.mi_settings){
            Intent intent = new Intent(getApplicationContext(), SettingsScreenActivity.class);
            startActivity(intent, ActivityOptions.makeSceneTransitionAnimation(this).toBundle());
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){

            case R.id.favoriteStar:

                if (!mFavoriteCityIds.contains(mCity.getCityId())){
                    Toast.makeText(this, "Location added to favorites", Toast.LENGTH_SHORT).show();
                    mCity.setFavorite(true);
                    mViewModel.update(mCity);
                    mFavoriteCityIds.add(mCity.getCityId());
                    mFavoriteStar.setImageResource(R.drawable.ic_star);
                }
                else {
                    Toast.makeText(this, "Location removed from favorites", Toast.LENGTH_SHORT).show();
                    int cityId = mCity.getCityId();
                    mCity.setFavorite(false);
                    mViewModel.update(mCity);
                    mFavoriteCityIds.remove(mFavoriteCityIds.get(mFavoriteCityIds.indexOf(cityId)));
                    mFavoriteStar.setImageResource(R.drawable.ic_star_border);
                }
                break;

            case R.id.nav_searchLocation:
                Intent searchIntent = new Intent(getApplicationContext(), SearchActivity.class);
                startActivity(searchIntent, ActivityOptions.makeSceneTransitionAnimation(this).toBundle());
                break;

            case R.id.nav_favoritesList:
                Intent favoritesIntent = new Intent(getApplicationContext(), FavoritesListActivity.class);
                startActivity(favoritesIntent, ActivityOptions.makeSceneTransitionAnimation(this).toBundle());
                break;

            case R.id.iv_show_location:
                openMap();
                break;
        }
    }

    public void initializeViews(){
        tv_main_humidity = findViewById(R.id.tv_main_humidity);
        tv_main_location = findViewById(R.id.tv_main_location);
        tv_main_temp = findViewById(R.id.tv_main_temp);
        tv_main_wind = findViewById(R.id.tv_main_wind);

        // View elements for table 1 (3 hourly forecast)
        col1_time = findViewById(R.id.col1_tv_time);
        col2_time = findViewById(R.id.col2_tv_time);
        col3_time = findViewById(R.id.col3_tv_time);
        col4_time = findViewById(R.id.col4_tv_time);
        col5_time = findViewById(R.id.col5_tv_time);

        weatherIcon1 = findViewById(R.id.col1_iv_icon);
        weatherIcon2 = findViewById(R.id.col2_iv_icon);
        weatherIcon3 = findViewById(R.id.col3_iv_icon);
        weatherIcon4 = findViewById(R.id.col4_iv_icon);
        weatherIcon5 = findViewById(R.id.col5_iv_icon);


        col1_temp = findViewById(R.id.col1_tv_temp);
        col2_temp = findViewById(R.id.col2_tv_temp);
        col3_temp = findViewById(R.id.col3_tv_temp);
        col4_temp = findViewById(R.id.col4_tv_temp);
        col5_temp = findViewById(R.id.col5_tv_temp);

        // View elements for table 2 (daily forecast)
        c1_t2_weekday = findViewById(R.id.c1_t2_weekday);
        c2_t2_weekday = findViewById(R.id.c2_t2_weekday);
        c3_t2_weekday = findViewById(R.id.c3_t2_weekday);
        c4_t2_weekday = findViewById(R.id.c4_t2_weekday);
        c5_t2_weekday = findViewById(R.id.c5_t2_weekday);

        c1_t2_icon = findViewById(R.id.c1_t2_icon);
        c2_t2_icon = findViewById(R.id.c2_t2_icon);
        c3_t2_icon = findViewById(R.id.c3_t2_icon);
        c4_t2_icon = findViewById(R.id.c4_t2_icon);
        c5_t2_icon = findViewById(R.id.c5_t2_icon);

        c1_t2_temp = findViewById(R.id.c1_t2_temp);
        c2_t2_temp = findViewById(R.id.c2_t2_temp);
        c3_t2_temp = findViewById(R.id.c3_t2_temp);
        c4_t2_temp = findViewById(R.id.c4_t2_temp);
        c5_t2_temp = findViewById(R.id.c5_t2_temp);
        
        mFavoriteStar = findViewById(R.id.favoriteStar);
        iv_search_location = findViewById(R.id.nav_searchLocation);
        tv_favorites_list = findViewById(R.id.nav_favoritesList);
        iv_show_location = findViewById(R.id.iv_show_location);
        
        mFavoriteStar.setOnClickListener(this);
        iv_search_location.setOnClickListener(this);
        tv_favorites_list.setOnClickListener(this);
        iv_show_location.setOnClickListener(this);
    }

    public void observeLiveDataItems(){

        mViewModel.getAllCities().observe(this, new Observer<List<City>>() {
            @Override
            public void onChanged(List<City> cities) {

                for (City city : cities){
                    if (city.getCityId() == mCityId || !cities.contains(city)){
                        mCity = mViewModel.fetchCity(mCityId);
                        updateCity(mCity);
                        mViewModel.parseNewForecast(city.getCityId());
                    }
                }


            }
        });

        mViewModel.getFavoriteCities().observe(this, new Observer<List<City>>() {
            @Override
            public void onChanged(List<City> cities) {
                Log.d(TAG, "Adding cities in favorites livedata list of size: " + String.valueOf(cities.size()));

                for (City city : cities){
                    Log.d(TAG, "Checking if another city list for favorites");
                    if (mFavoriteCityIds.contains(city.getCityId())){
                        Log.d(TAG, "A favorite city was found, get stuff for it NOOOOOOOOOW");
                        mViewModel.parseNewForecast(city.getCityId());
                    }
                }


            }
        });

        mViewModel.getThreeHourlyForecasts().observe(this, new Observer<List<ThreeHourlyForecast>>() {
            @Override
            public void onChanged(List<ThreeHourlyForecast> forecasts) {
                Log.d(TAG, "Adding forecastsHourly list of size: " + String.valueOf(forecasts.size()));

                List<ThreeHourlyForecast> tempList = new ArrayList<>();

                for (int i = 0; i < forecasts.size(); i++){
                    if (forecasts.get(i).getCityId() == mCity.getCityId() &&
                            !tempList.contains(forecasts.get(i))){

                        tempList.add(forecasts.get(i));
                    }
                }

                // If the loaded list has enough items, update UI. A minimum of 6 items is required.
                if (tempList.size() >= 6){
                    Log.d(TAG, "Adding hourly list of size: " + String.valueOf(tempList.size()));

                    updateThreeHourlyForecast(tempList);
                }

            }
        });

        mViewModel.getDayForecasts().observe(this, new Observer<List<DayForecast>>() {
            @Override
            public void onChanged(List<DayForecast> forecasts) {
                Log.d(TAG, "Adding forecastsDaily list of size: " + String.valueOf(forecasts.size()));

                List<DayForecast> tempList = new ArrayList<>();

                for (int i = 0; i < forecasts.size(); i++){
                    if (forecasts.get(i).getCityId() == mCity.getCityId() &&
                            !tempList.contains(forecasts.get(i))){

                        tempList.add(forecasts.get(i));
                    }
                }

                // If the loaded list has enough items, update UI. A minimum of 5 items is required.
                if (tempList.size() >= 5){
                    Log.d(TAG, "Adding daily list of size: " + String.valueOf(tempList.size()));

                    updateDailyForecast(tempList);
                }

            }
        });

    }


    @Override
    protected void onStart() {
        super.onStart();
//        Log.d(TAG, "In the onStart event handler");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
//        Log.d(TAG, "In the onDestroy event handler");
    }

    @Override
    protected void onStop() {
        super.onStop();
//        Log.d(TAG, "In the onStop event handler");
    }

    @Override
    protected void onPause() {
        super.onPause();


        SharedPreferences.Editor prefsEditor = sharedPrefs.edit();

        prefsEditor.putString(getString(R.string.pref_display_homeLocation_key), mHomeLocation);
        prefsEditor.putString(getString(R.string.pref_measurement_units_key), mMeasurementUnits);
        prefsEditor.putString(getString(R.string.pref_temperature_units_key), mTempUnits);

        Gson gson = new Gson();
        String json = gson.toJson(mFavoriteCityIds);
        prefsEditor.putString(getString(R.string.favorites_list_key), json);

        prefsEditor.clear();
        prefsEditor.apply();

        sharedPrefs.unregisterOnSharedPreferenceChangeListener(this);

    }

    @Override
    protected void onResume(){
        super.onResume();
        this.listener = this;
        sharedPrefs.registerOnSharedPreferenceChangeListener(this);
    }


    private void getIncomingIntent(){
        Intent intent = getIntent();

        // Put id of selected hometown as default if intent fails
        if (intent.hasExtra("EXTRA_CITY_ID_FROM_SEARCH")){

//            int cityId = intent.getIntExtra("EXTRA_CITY_ID_FROM_SEARCH", 0);

            mCityId = intent.getIntExtra("EXTRA_CITY_ID_FROM_SEARCH", 0);

            /*mCity = mViewModel.fetchCity(cityId);

            if (mFavoriteCityIds.contains(mCity.getCityId())){
                Log.d(TAG, "NOT NEW! NO GET");

            }
            else {
                Log.d(TAG, "ITS NEW! GO GET");
                mViewModel.parseNewForecast(mCity.getCityId());
            }

            updateCity(mCity);*/
        }

        if (intent.hasExtra("EXTRA_CITY_ID_FROM_FAVORITES")){

            mCityId = intent.getIntExtra("EXTRA_CITY_ID_FROM_FAVORITES", 0);
//            int cityId = intent.getIntExtra("EXTRA_CITY_ID_FROM_FAVORITES", 0);

//            mCity = mViewModel.fetchCity(cityId);

//            updateCity(mCity);
        }
    }


    private void updateCity(City city){

        mCity = city;

        if (mFavoriteCityIds.contains(city.getCityId())){
            mFavoriteStar.setImageResource(R.drawable.ic_star);
        }
        else {
            mFavoriteStar.setImageResource(R.drawable.ic_star_border);
        }

        String name = city.getCityName();

        tv_main_location.setText(name);

    }

    private void updateThreeHourlyForecast(List<ThreeHourlyForecast> list){

        // Update the most current forecast to the main views
        String windDirection = list.get(0).getWindDirection();
        int temperature = list.get(0).getTemperature();
        int humidity = list.get(0).getHumidity();

        //  Convert wind speed from mph to m/s
        double windMph = list.get(0).getWindSpeed();
        double windMs = Math.round((windMph * 0.44704)*100);

        tv_main_humidity.setText(String.format("%s %%", humidity));

        if (mTempUnits.equals("Celsius")){
            tv_main_temp.setText(String.valueOf(temperature));
        }
        else if (mTempUnits.equals("Fahrenheit")) {
            tv_main_temp.setText(String.valueOf(Math.round(temperature*1.8 + 32)));
        }

        if (mMeasurementUnits.equals("Metric units")){
            tv_main_wind.setText(String.format("%s m/s  %s", (windMs / 100), windDirection));
        }
        else {
            tv_main_wind.setText(String.format("%s mph  %s", windMph, windDirection));
        }

        // Update the TextViews of the table with three hourly forecasts, starting with the
        // forecast being next in line after the most recent
        col1_time.setText(String.format("%s:00",list.get(1).getMinutes() / 60));
        col2_time.setText(String.format("%s:00",list.get(2).getMinutes() / 60));
        col3_time.setText(String.format("%s:00",list.get(3).getMinutes() / 60));
        col4_time.setText(String.format("%s:00",list.get(4).getMinutes() / 60));
        col5_time.setText(String.format("%s:00",list.get(5).getMinutes() / 60));

        col1_temp.setText(String.format("%s C", list.get(1).getTemperature()));
        col2_temp.setText(String.format("%s C", list.get(2).getTemperature()));
        col3_temp.setText(String.format("%s C", list.get(3).getTemperature()));
        col4_temp.setText(String.format("%s C", list.get(4).getTemperature()));
        col5_temp.setText(String.format("%s C", list.get(5).getTemperature()));

        ArrayList<ImageView> weatherIconsTable1 = new ArrayList<>();
        weatherIconsTable1.add(weatherIcon1);
        weatherIconsTable1.add(weatherIcon2);
        weatherIconsTable1.add(weatherIcon3);
        weatherIconsTable1.add(weatherIcon4);
        weatherIconsTable1.add(weatherIcon5);


        for (int i = 0; i < 5; i++){
            int weather = list.get(i).getWeatherType();
            String iconName = "ic_weather_" + String.valueOf(weather);
            int iconId = getResources().getIdentifier(iconName, "drawable", getPackageName());

            weatherIconsTable1.get(i).setImageResource(iconId);
        }
    }

    private void updateDailyForecast(List<DayForecast> dailyList){

        // Update the TextViews of the table with daily forecasts
        c1_t2_temp.setText(String.format("%s / %s",
                dailyList.get(0).getDayMaxTemp(), dailyList.get(0).getNightMinTemp()));
        c2_t2_temp.setText(String.format("%s / %s",
                dailyList.get(1).getDayMaxTemp(), dailyList.get(1).getNightMinTemp()));
        c3_t2_temp.setText(String.format("%s / %s",
                dailyList.get(2).getDayMaxTemp(), dailyList.get(2).getNightMinTemp()));
        c4_t2_temp.setText(String.format("%s / %s",
                dailyList.get(3).getDayMaxTemp(), dailyList.get(3).getNightMinTemp()));
        c5_t2_temp.setText(String.format("%s / %s",
                dailyList.get(4).getDayMaxTemp(), dailyList.get(4).getNightMinTemp()));

        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");

        ArrayList<String> weekdays = new ArrayList<>();
        weekdays.add("Sun");
        weekdays.add("Mon");
        weekdays.add("Tue");
        weekdays.add("Wed");
        weekdays.add("Thu");
        weekdays.add("Fri");
        weekdays.add("Sat");

        c1_t2_weekday.setText(getString(R.string.tv_forecast_today));

        for (int i = 1; i < 5; i++){
            String sDate = dailyList.get(i).getDate();
            sDate = sDate.replace("Z", "");

            try {

                Date date = formatter.parse(sDate);
                Calendar c = Calendar.getInstance();
                c.setTime(date);
                int dayOfWeek = c.get(Calendar.DAY_OF_WEEK);

                switch (i){
                    case 1:
                        c2_t2_weekday.setText(weekdays.get(dayOfWeek-1));
                    case 2:
                        c3_t2_weekday.setText(weekdays.get(dayOfWeek-1));
                    case 3:
                        c4_t2_weekday.setText(weekdays.get(dayOfWeek-1));
                    case 4:
                        c5_t2_weekday.setText(weekdays.get(dayOfWeek-1));
                }
            } catch (java.text.ParseException e){
                e.printStackTrace();
            }
        }


        ArrayList<ImageView> weatherIconsTable2 = new ArrayList<>();
        weatherIconsTable2.add(c1_t2_icon);
        weatherIconsTable2.add(c2_t2_icon);
        weatherIconsTable2.add(c3_t2_icon);
        weatherIconsTable2.add(c4_t2_icon);
        weatherIconsTable2.add(c5_t2_icon);


        for (int i = 0; i < 5; i++){
            int weather = dailyList.get(i).getWeatherTypeDay();
            String iconName = "ic_weather_" + String.valueOf(weather);
            int iconId = getResources().getIdentifier(iconName, "drawable", getPackageName());

            weatherIconsTable2.get(i).setImageResource(iconId);
        }
    }

    private void restorePreferences(){

        settings = PreferenceManager.getDefaultSharedPreferences(this);

        mHomeLocation = settings.getString(
                getString(R.string.pref_display_homeLocation_key),
                getString(R.string.pref_display_homeLocation_default));

        mCityId = Integer.parseInt(mHomeLocation);

        mTempUnits = settings.getString(
                getString(R.string.pref_temperature_units_key),
                getString(R.string.pref_temperature_units_default));

        mMeasurementUnits = settings.getString(
                getString(R.string.pref_measurement_units_key),
                getString(R.string.pref_measurement_units_default));

        Gson gson = new Gson();
        String json = sharedPrefs.getString(getString(R.string.favorites_list_key), null);
        Type type = new TypeToken<ArrayList<Integer>>() {}.getType();
        mFavoriteCityIds = gson.fromJson(json, type);

        Log.d(TAG, "Sharedprefs favlist: " + String.valueOf(mFavoriteCityIds));
        if (mFavoriteCityIds == null){
            mFavoriteCityIds = new ArrayList<>();
        }


//        mCity = mViewModel.fetchCity(homeLocationId);

        /*while (mCity != null){
            updateCity(mCity);
            mViewModel.parseNewForecast(mCity.getCityId());
            break;
        }*/
    }

    private void openMap(){

        String lon = String.valueOf(mCity.getCityCoordLon());
        String lat = String.valueOf(mCity.getCityCoordLat());
        String path = String.format("geo:%s,%s", lat, lon);

        // Parse location Uri
        Uri mapUri = Uri.parse(path);

        // Send intent with action
        Intent mapIntent = new Intent(ACTION_VIEW, mapUri);

        if (mapIntent.resolveActivity(getPackageManager()) != null){
            // Send intent to some map-program
            startActivity(mapIntent);
        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        super.onBackPressed();
        return true;
    }

    @Override
    public void onBackPressed(){
        super.onBackPressed();
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {

        if (key.equals(getString(R.string.preferences_file))){

            Log.d(TAG, "Preference changed in mainactivity, added city");

            Gson gson = new Gson();
            String json = sharedPrefs.getString(getString(R.string.favorites_list_key), null);
            Type type = new TypeToken<ArrayList<Integer>>() {}.getType();
            mFavoriteCityIds = gson.fromJson(json, type);


//            Log.d(TAG, "home location changed down here to: " + mHomeLocation);
//            Log.d(TAG, "Measurements changed down here to: " + mMeasurementUnits);
//            Log.d(TAG, "TempUnits changed down here to: " + mTempUnits);

        }

    }


}
