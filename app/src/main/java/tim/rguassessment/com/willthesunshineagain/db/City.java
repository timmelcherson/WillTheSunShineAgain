package tim.rguassessment.com.willthesunshineagain.db;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.ForeignKey;
import android.arch.persistence.room.PrimaryKey;
import android.support.annotation.NonNull;

@Entity(tableName = "city_table")
public class City {

    // Table columns
    @PrimaryKey
    @NonNull
    @ColumnInfo(name = "city_id")
    private int cityId;

    @ColumnInfo(name = "city_name")
    private String cityName;

    @ColumnInfo(name = "lon")
    private double cityCoordLon;

    @ColumnInfo(name ="lat")
    private double cityCoordLat;

    private boolean favorite;

    @NonNull
    public int getCityId() {
        return cityId;
    }

    public void setCityId(@NonNull int cityId) {
        this.cityId = cityId;
    }

    public String getCityName() {
        return cityName;
    }

    public void setCityName(String cityName) {
        this.cityName = cityName;
    }

    public double getCityCoordLon() {
        return cityCoordLon;
    }

    public void setCityCoordLon(double cityCoordLon) {
        this.cityCoordLon = cityCoordLon;
    }

    public double getCityCoordLat() {
        return cityCoordLat;
    }

    public void setCityCoordLat(double cityCoordLat) {
        this.cityCoordLat = cityCoordLat;
    }

    public boolean isFavorite() {
        return favorite;
    }

    public void setFavorite(boolean favorite) {
        this.favorite = favorite;
    }

    @Override
    public String toString(){
        return "Not defined yet";
    }


}
