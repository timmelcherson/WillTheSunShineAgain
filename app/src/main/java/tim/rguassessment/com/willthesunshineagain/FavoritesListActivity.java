package tim.rguassessment.com.willthesunshineagain;

import android.app.ActivityOptions;
import android.arch.lifecycle.ViewModelProviders;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.RotateAnimation;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import tim.rguassessment.com.willthesunshineagain.db.City;

public class FavoritesListActivity extends AppCompatActivity
        implements SharedPreferences.OnSharedPreferenceChangeListener,
        View.OnClickListener, SeekBar.OnSeekBarChangeListener {

    private RecyclerView recyclerView;
    private static ViewModel mViewModel;
    private FavoritesListAdapter mAdapter;

    private SharedPreferences sharedPrefs;
    private SharedPreferences.OnSharedPreferenceChangeListener listener;

    private List<City> mCities, mCitiesFiltered;
    private List<Integer> mFavoriteCityIds;
    private List<Integer> mCurrentTemperatures, mDailyTemperatures, mHumidites, mWindSpeeds;

    private ConstraintLayout mSeekbarLayout;
    private SeekBar mTempSeekbar, mHumiditySeekbar, mWindSeekbar;
    private TextView tv_min_temp, tv_max_temp, tv_min_humidity, tv_max_humidity, tv_min_wind, tv_max_wind;
    private ImageView mFilterToggleArrow;

    private int mMinTemp, mMaxTemp, mMinHumidity, mMaxHumidity, mMinWind, mMaxWind;

    private String CITIES_LIST;
    private String MIN_TEMP;
    private String MAX_TEMP;
    private static final String TAG = "TAGG";


    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.favoriteslist_activity);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);


        mViewModel = ViewModelProviders.of(this).get(ViewModel.class);
        sharedPrefs = getSharedPreferences(getString(R.string.preferences_file), MODE_PRIVATE);
        listener = this;

        buildRecyclerView();
        initializeFilterItems();
        populateRecyclerView();


        if (savedInstanceState != null){
            tv_min_temp.setText(String.format("%s", savedInstanceState.getInt(MIN_TEMP)));
            tv_max_temp.setText(String.format("%s", savedInstanceState.getInt(MAX_TEMP)));
        }

    }

    protected void onSaveInstanceState(Bundle outState){
        super.onSaveInstanceState(outState);
        outState.putInt(MIN_TEMP, mMinTemp);
        outState.putInt(MAX_TEMP, mMaxTemp);
    }


    @Override
    public void onClick(View v) {

        switch (v.getId()){

            case R.id.filter_toggle_arrow_right:
                if (mSeekbarLayout.getVisibility() == View.GONE){

                    final RotateAnimation rotDownAnim = new RotateAnimation(
                            0, 90, mFilterToggleArrow.getWidth()/2, mFilterToggleArrow.getHeight()/2);
                    rotDownAnim.setDuration(200);
                    rotDownAnim.setFillAfter(true);
                    mFilterToggleArrow.startAnimation(rotDownAnim);
                    mSeekbarLayout.setVisibility(View.VISIBLE);

                }
                else {

                    final RotateAnimation rotUpAnim = new RotateAnimation(
                            90, 0, mFilterToggleArrow.getWidth()/2, mFilterToggleArrow.getHeight()/2);
                    rotUpAnim.setDuration(200);
                    rotUpAnim.setFillAfter(true);
                    mFilterToggleArrow.startAnimation(rotUpAnim);
                    mSeekbarLayout.setVisibility(View.GONE);
                }
        }
    }

    public void buildRecyclerView(){
        recyclerView = findViewById(R.id.favlist_recycler_view);
        mAdapter = new FavoritesListAdapter(this);
        recyclerView.setAdapter(mAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        new ItemTouchHelper(new ItemTouchHelper
                .SimpleCallback(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {
            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView,
                                  @NonNull RecyclerView.ViewHolder viewHolder,
                                  @NonNull RecyclerView.ViewHolder viewHolder1) {
                return false;
            }

            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int i) {
                City city = mAdapter.getCityAt(viewHolder.getAdapterPosition());

                // Update and remove the city as favorite
                city.setFavorite(false);
                mViewModel.update(city);
                mFavoriteCityIds.remove(city.getCityId());
                mCities.remove(city);
                Toast.makeText(FavoritesListActivity.this, "Favorite removed", Toast.LENGTH_SHORT).show();

                // Update adapter with new list
                mAdapter.setCities(mCities);
            }
        }).attachToRecyclerView(recyclerView);
    }

    public void populateRecyclerView(){
        Gson gson = new Gson();
        String json = sharedPrefs.getString(getString(R.string.favorites_list_key), null);
        Type type = new TypeToken<ArrayList<Integer>>() {}.getType();
        mFavoriteCityIds = gson.fromJson(json, type);

        if (mFavoriteCityIds == null){
            mFavoriteCityIds = new ArrayList<>();
        }

        mCities = new ArrayList<>();

        for (int i = 0; i < mFavoriteCityIds.size(); i++){
            int cityId = mFavoriteCityIds.get(i);
            City city = mViewModel.fetchCity(cityId);
            mCities.add(city);
        }
        mAdapter.setCities(mCities);

        // Do not set filters until there is at least one favorite location
        if(mCities.size() > 0){
            setCurrentTemperatures();
            setHumidities();
            setWindSpeeds();
        }
    }

    public void initializeFilterItems(){
        mCurrentTemperatures = mViewModel.getThreeHourlyTemperatures();
        mHumidites = mViewModel.getHumidities();
        mWindSpeeds = mViewModel.getWindSpeeds();

        tv_min_temp = findViewById(R.id.temp_seekbar_tv_min);
        tv_max_temp = findViewById(R.id.temp_seekbar_tv_max);
        tv_min_humidity = findViewById(R.id.humidity_seekbar_tv_min);
        tv_max_humidity = findViewById(R.id.humidity_seekbar_tv_max);
        tv_min_wind = findViewById(R.id.wind_seekbar_tv_min);
        tv_max_wind = findViewById(R.id.wind_seekbar_tv_max);

        mCitiesFiltered = new ArrayList<>();

        mSeekbarLayout = findViewById(R.id.seekbar_layout);
        mFilterToggleArrow = findViewById(R.id.filter_toggle_arrow_right);
        mFilterToggleArrow.setOnClickListener(this);


        mTempSeekbar = findViewById(R.id.temp_seekbar);
        mHumiditySeekbar = findViewById(R.id.humidity_seekbar);
        mWindSeekbar = findViewById(R.id.wind_seekbar);

        mTempSeekbar.setOnSeekBarChangeListener(this);
        mHumiditySeekbar.setOnSeekBarChangeListener(this);
        mWindSeekbar.setOnSeekBarChangeListener(this);
        Log.d(TAG, "Favlist filter views initialized");
    }



    /**************** Set list max and min functions ****************/
    public void setCurrentTemperatures(){

        // Initialize with the first index
        if (mCurrentTemperatures != null){
            mMinTemp = mCurrentTemperatures.get(0);
            mMaxTemp = mCurrentTemperatures.get(0);
        }

        for (int i : mCurrentTemperatures){

            // Find lowest temperature in list
            if (i < mMinTemp){
                mMinTemp = i;
            }

            // Find highest temperature in list
            if (i > mMaxTemp){
                mMaxTemp = i;
            }
        }

        tv_min_temp.setText(String.valueOf(mMinTemp));
        tv_max_temp.setText(String.valueOf(mMaxTemp));
    }

    public void setHumidities(){

        // Initialize with the first index
        if (mHumidites != null){
            mMinHumidity = mHumidites.get(0);
            mMaxHumidity = mHumidites.get(0);
        }

        for (int i : mHumidites){

            // Find lowest temperature in list
            if (i < mMinHumidity){
                mMinHumidity = i;
            }

            // Find highest temperature in list
            if (i > mMaxHumidity){
                mMaxHumidity = i;
            }
        }

        tv_min_humidity.setText(String.valueOf(mMinHumidity));
        tv_max_humidity.setText(String.valueOf(mMaxHumidity));
    }

    public void setWindSpeeds(){

        // Initialize with the first index
        if (mWindSpeeds != null){
            mMinWind = mWindSpeeds.get(0);
            mMaxWind = mWindSpeeds.get(0);
        }

        for (int i : mWindSpeeds){

            // Find lowest temperature in list
            if (i < mMinWind){
                mMinWind = i;
            }

            // Find highest temperature in list
            if (i > mMaxWind){
                mMaxWind = i;
            }
        }

        tv_min_wind.setText(String.valueOf(mMinWind));
        tv_max_wind.setText(String.valueOf(mMaxWind));
    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

        double percentage = (double)(progress/100.0);

        switch (seekBar.getId()){
            case R.id.temp_seekbar:

                // Scale temperature range
                int tempScale = mMaxTemp - mMinTemp;
                int filterTemp = mMinTemp + (int)(tempScale * percentage);

                tv_min_temp.setText(String.valueOf(filterTemp));

                tempFilter(filterTemp);
                break;

            case R.id.humidity_seekbar:

                // Humidity and progress are both 0-100, no need for scale
                int filterHumidity = mMinHumidity;
                if (progress >= mMinHumidity){
                    filterHumidity = progress;
                }

                tv_min_humidity.setText(String.valueOf(filterHumidity));
                humidityFilter(filterHumidity);
                break;

            case R.id.wind_seekbar:

                // Scale wind range
                int windScale = mMaxWind - mMinWind;
                int filterWind = mMinWind + (int)(windScale * percentage);

                tv_min_wind.setText(String.valueOf(filterWind));

                windFilter(filterWind);
                break;
        }

    }


    // Filter functions
    public void tempFilter(int temp){

        for (City city : mCities){
            if(mCitiesFiltered.contains(city) && mViewModel.getCityTemp(city.getCityId()) < temp) {
                mCitiesFiltered.remove(city);
            }
            else if (!mCitiesFiltered.contains(city) && mViewModel.getCityTemp(city.getCityId()) > temp){
                mCitiesFiltered.add(city);
            }
        }

        // Make sure the filtered list is in alphabetical order
        Collections.sort(mCitiesFiltered, new Comparator<City>() {
            @Override
            public int compare(City c1, City c2) {
                return c1.getCityName().compareTo(c2.getCityName());
            }
        });

        mAdapter.filterByTemp(mCitiesFiltered);
    }

    public void humidityFilter(int humidity){

        for (City city : mCities){
            if(mCitiesFiltered.contains(city) && mViewModel.getCityHumidity(city.getCityId()) < humidity) {
                mCitiesFiltered.remove(city);
            }
            else if (!mCitiesFiltered.contains(city) && mViewModel.getCityHumidity(city.getCityId()) > humidity){
                mCitiesFiltered.add(city);
            }
        }

        // Make sure the filtered list is in alphabetical order
        Collections.sort(mCitiesFiltered, new Comparator<City>() {
            @Override
            public int compare(City c1, City c2) {
                return c1.getCityName().compareTo(c2.getCityName());
            }
        });

        mAdapter.filterByHumidity(mCitiesFiltered);
    }

    public void windFilter(int wind){

        for (City city : mCities){
            if(mCitiesFiltered.contains(city) && mViewModel.getCityWind(city.getCityId()) < wind  ) {
                mCitiesFiltered.remove(city);
            }
            else if (!mCitiesFiltered.contains(city) && mViewModel.getCityWind(city.getCityId()) >= wind){
                mCitiesFiltered.add(city);
            }
        }

        // Make sure the filtered list is in alphabetical order
        Collections.sort(mCitiesFiltered, new Comparator<City>() {
            @Override
            public int compare(City c1, City c2) {
                return c1.getCityName().compareTo(c2.getCityName());
            }
        });

        mAdapter.filterByWind(mCitiesFiltered);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu topRightMenu){
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.favorites_list_activity_topright_menu, topRightMenu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item){

        if (item.getItemId() == R.id.mi_favorites_list_menu){

            for (City city : mCities){
                city.setFavorite(false);
                mViewModel.update(city);
                mFavoriteCityIds.remove(city.getCityId());
            }
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onResume(){
        super.onResume();
        this.listener = this;
        sharedPrefs.registerOnSharedPreferenceChangeListener(this);
    }

    @Override
    protected void onPause() {
        super.onPause();

        SharedPreferences.Editor prefsEditor = sharedPrefs.edit();

        Gson gson = new Gson();
        String json = gson.toJson(mFavoriteCityIds);
        prefsEditor.putString(getString(R.string.favorites_list_key), json);

        prefsEditor.clear();
        prefsEditor.apply();

        sharedPrefs.unregisterOnSharedPreferenceChangeListener(this);
    }

    @Override
    public boolean onSupportNavigateUp() {
        super.onBackPressed();
        ActivityOptions.makeSceneTransitionAnimation(this).toBundle();
        return true;
    }


    @Override
    public void onBackPressed(){
        super.onBackPressed();
        ActivityOptions.makeSceneTransitionAnimation(this).toBundle();
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        if (key.equals(getString(R.string.preferences_file))){
        }
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {

    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {

    }

}
