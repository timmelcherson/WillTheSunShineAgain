package tim.rguassessment.com.willthesunshineagain;

import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.WindowManager;

import tim.rguassessment.com.willthesunshineagain.db.City;

public class SettingsScreenActivity extends AppCompatActivity {

    private City city;
    private ViewModel mViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings_screen);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        mViewModel = ViewModelProviders.of(this).get(ViewModel.class);

        // Display the fragment as main content
        getSupportFragmentManager().beginTransaction()
                .replace(android.R.id.content, new AppPreferencesFragment())
                .commit();
    }

//    private void getIncomingIntent(){
//        Intent intent = getIntent();
//
//        // Put id of selected hometown as default if intent fails
//        if (intent.hasExtra("EXTRA_CITY_ID")){
//            int cityId = intent.getIntExtra("EXTRA_CITY_ID", 0);
//
//            Log.d("TAGG", "SETTINGS SCREEN HAS GOT CITY WITH ID: " + String.valueOf(cityId));
//
//        }
//
//    }

    boolean lookForCityName(String s){
        return mViewModel.lookForCityName(s);
    }

    City getCityByName(String name){
        return mViewModel.getCityByName(name);
    }

    public City getHomeCity(int cityId){
        return mViewModel.fetchCity(cityId);
    }

    @Override
    public boolean onSupportNavigateUp() {
        super.onBackPressed();
        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_right);
        return true;
    }

    @Override
    public void onBackPressed(){
        super.onBackPressed();
        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_right);
    }
}
