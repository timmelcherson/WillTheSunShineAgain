package tim.rguassessment.com.willthesunshineagain;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.app.ActivityOptions;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.text.Editable;
import android.text.TextWatcher;
import android.transition.Explode;
import android.transition.Slide;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.RotateAnimation;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import tim.rguassessment.com.willthesunshineagain.db.City;

public class SearchActivity extends AppCompatActivity {


    private RecyclerView recyclerView;
    private SearchAdapter mAdapter;
    private static ViewModel mViewModel;

    private List<City> mCities, mCitiesFiltered;

    public static final int ADD_FAVORITE_CITY_ACTIVITY_REQUEST_CODE = 1;
    private static final String TAG = "TAGG";


    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getWindow().requestFeature(Window.FEATURE_ACTIVITY_TRANSITIONS);

        setContentView(R.layout.search_activity);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        mViewModel = ViewModelProviders.of(this).get(ViewModel.class);
        mViewModel.getAllCities().observe(this, new Observer<List<City>>() {
            @Override
            public void onChanged(@Nullable final List<City> cities) {
                mCities = cities;
                mAdapter.setCities(cities);
            }
        });


//        if (savedInstanceState != null){
//            Log.d(TAG, "Min temp here in onCreate instancestate is : " + String.valueOf(mMinTemp));
//
//            tv_min_temp.setText(String.format("%s", savedInstanceState.getInt(MIN_TEMP)));
//            tv_max_temp.setText(String.format("%s", savedInstanceState.getInt(MAX_TEMP)));
//        }

        buildRecyclerView();

    }

    /*protected void onSaveInstanceState(Bundle outState){
        Log.d(TAG, "onSaveInstanceState()");
        super.onSaveInstanceState(outState);
        Log.d(TAG, "Min temp here in saveinstance is : " + String.valueOf(mMinTemp));
        outState.putInt(MIN_TEMP, mMinTemp);
        outState.putInt(MAX_TEMP, mMaxTemp);
    }*/


    public void buildRecyclerView(){
        recyclerView = findViewById(R.id.search_recycler_view);
        recyclerView.setHasFixedSize(true);
        mAdapter = new SearchAdapter(this);
        recyclerView.setAdapter(mAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu topRightMenu){
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.search_activity_topright_menu, topRightMenu);

        MenuItem searchItem = topRightMenu.findItem(R.id.action_filter);
        SearchView searchView = (SearchView)searchItem.getActionView();

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                mAdapter.getFilter().filter(s);
                return false;
            }
        });
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item){

        if (item.getItemId() == R.id.mi_search_menu){
            mViewModel.refreshCityList();
        }
        return super.onOptionsItemSelected(item);
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







}
