package tim.rguassessment.com.willthesunshineagain.utils;


import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import tim.rguassessment.com.willthesunshineagain.db.City;

public class ForecastLocationsJsonParser {

    // The following are keys used in the JSON returned by the web service to describe
    // each site.
    public static final String KEY_LOCATIONS = "Locations";
    public static final String KEY_LOCATION = "Location";
    public static final String KEY_ID = "id";
    public static final String KEY_NAME = "name";
    public static final String KEY_LATITUDE = "latitude";
    public static final String KEY_LONGITUDE = "longitude";

    /**
     * Processes the json String json, creating a {@link List} of {@link City} objects
     * corresponding to the sites described in json.
     * @param json A String containing json downloaded from the Met Office API forecast location web service.
     * @return
     */
    public static List<City> processJson(String json){
        List<City> locations = new ArrayList<City>();

        try {
            // Create the top level, root JSON Object from the json String.
            JSONObject locationsObj = new JSONObject(json);
            // Get the locationsObj child for the locationsObj key "Locations"
            JSONObject locationObj = locationsObj.getJSONObject(KEY_LOCATIONS);
            // Get the array of locations that is the value of the locationObj's "Location" key
            JSONArray locationArray = locationObj.getJSONArray(KEY_LOCATION);
            // Loop through each JSON object in the locationArray, i.e. each site that a forecast is available for
            for (int i = 0; i < locationArray.length(); i++){
                // for each JSON Object in locationsArray
                // create a new City object for this JSON Object
                City forecastLocation = new City();
                // add forecastLocation to the List that will be returned by this method
                locations.add(forecastLocation);

                // Get the JSON Object at position i of the locationArray
                JSONObject location = locationArray.getJSONObject(i);


                // for this location, gee the String value of the "id" key, which defines the ID of the location.
                String id = location.getString(KEY_ID);
                forecastLocation.setCityId(Integer.parseInt(id));

                // for this location, gee the String value of the "name" key, which defines the ID of the location.
                String name = location.getString(KEY_NAME);
                forecastLocation.setCityName(name);

                // get the double value of the location's "latitude" key
                double latitude = location.getDouble(KEY_LATITUDE);
                forecastLocation.setCityCoordLat(latitude);

                // get the double value of teh location's "longitude" key
                double longitude = location.getDouble(KEY_LONGITUDE);
                forecastLocation.setCityCoordLon(longitude);

            }
        } catch (JSONException e) {
            // there was an exception processing the json, possibly with the json String, so print the stack trace.
            e.printStackTrace();
        }

        return locations;
    }
}
