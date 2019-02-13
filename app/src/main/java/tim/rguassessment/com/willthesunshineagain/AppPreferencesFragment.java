package tim.rguassessment.com.willthesunshineagain;


import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.preference.EditTextPreference;
import android.support.v7.preference.PreferenceFragmentCompat;
import android.support.v7.preference.PreferenceManager;
import android.util.Log;
import android.widget.Toast;

import tim.rguassessment.com.willthesunshineagain.db.City;


public class AppPreferencesFragment extends PreferenceFragmentCompat
        implements android.support.v7.preference.Preference.OnPreferenceChangeListener {

    public static final String TAG = "TAGG";
    public static final int SELECT_HOME_LOCATIONREQUEST_CODE = 1;
    private SharedPreferences sharedPrefs;
    private City homeCity;


    @Override
    public void onCreatePreferences(Bundle bundle, String rootKey){
        setPreferencesFromResource(R.xml.preferences, rootKey);


        sharedPrefs = PreferenceManager.getDefaultSharedPreferences(getActivity().getApplicationContext());


        String savedHomeLocation = sharedPrefs.getString(getString(R.string.pref_display_homeLocation_key),
                        getString(R.string.pref_display_homeLocation_summary_default));
        Log.d(TAG, "savedHomeLocation is: " + savedHomeLocation);

        if (!getString(R.string.pref_display_homeLocation_default).equals(savedHomeLocation)){
            // the display_name Preference has been set by the user, so use it as the summary of the Preference
            EditTextPreference homeLocationPref = (EditTextPreference)findPreference(
                    getString(R.string.pref_display_homeLocation_key));
            homeLocationPref.setSummary(savedHomeLocation);
        }

        EditTextPreference homeLocationPref = (EditTextPreference)findPreference(
                getString(R.string.pref_display_homeLocation_key));
        homeLocationPref.setOnPreferenceChangeListener(this);

        // Temperature units
        String savedTemperatureUnits = sharedPrefs.getString(
                getString(R.string.pref_temperature_units_key),
                getString(R.string.pref_temperature_units_summary));

        android.support.v7.preference.ListPreference tempPref = (android.support.v7.preference.ListPreference)
                findPreference(getString(R.string.pref_temperature_units_key));

        tempPref.setSummary(savedTemperatureUnits);
        tempPref.setOnPreferenceChangeListener(this);


        // Measurement units
        String savedMeasurementUnits = sharedPrefs.getString(
                getString(R.string.pref_measurement_units_key),
                getString(R.string.pref_measurement_units_summary));
        android.support.v7.preference.ListPreference measurementsPref = (android.support.v7.preference.ListPreference)
                findPreference(getString(R.string.pref_measurement_units_key));

        measurementsPref.setSummary(savedMeasurementUnits);
        measurementsPref.setOnPreferenceChangeListener(this);

    }


    @Override
    public boolean onPreferenceChange(android.support.v7.preference.Preference preference, Object newValue) {
        Log.d(TAG, "Change in preferences fragment");
        if (preference == findPreference(getString(R.string.pref_display_homeLocation_key))){

            EditTextPreference pref = (EditTextPreference)findPreference(getString(R.string.pref_display_homeLocation_key));

            if ("".equals(newValue)){

                pref.setSummary(getString(R.string.pref_display_homeLocation_summary_default));
            }
            else {
                pref.setSummary(String.valueOf(newValue));
            }
        }

        // Comment
        if (preference == findPreference(getString(R.string.pref_temperature_units_key))){

            preference.setSummary(String.valueOf(newValue));
        }

        // Comment
        if (preference == findPreference(getString(R.string.pref_measurement_units_key))){

            preference.setSummary(String.valueOf(newValue));
        }

        return true;
    }


}
