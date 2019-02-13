package tim.rguassessment.com.willthesunshineagain.db;

import android.arch.persistence.db.SupportSQLiteDatabase;
import android.arch.persistence.room.Room;
import android.arch.persistence.room.RoomDatabase;
import android.arch.persistence.room.TypeConverters;
import android.arch.persistence.room.migration.Migration;
import android.content.Context;
import android.support.annotation.NonNull;

import tim.rguassessment.com.willthesunshineagain.db.Converters.Converters;
import tim.rguassessment.com.willthesunshineagain.db.dao.CityDao;
import tim.rguassessment.com.willthesunshineagain.db.dao.DayForecastDao;
import tim.rguassessment.com.willthesunshineagain.db.dao.ThreeHourlyForecastDao;


@android.arch.persistence.room.Database(entities =
        {City.class, ThreeHourlyForecast.class, DayForecast.class}, version = 3)
@TypeConverters({Converters.class})
public abstract class RoomDB extends RoomDatabase{


    public abstract CityDao cityDao();
    public abstract ThreeHourlyForecastDao hourlyDao();
    public abstract DayForecastDao dayDao();

    private static RoomDB INSTANCE;
    private static final String TAG = "TAGG";


    static final Migration MIGRATION_1_2 = new Migration(1, 2) {
        @Override
        public void migrate(@NonNull SupportSQLiteDatabase database) {
            database.execSQL("CREATE TABLE temp_table (uid INTEGER PRIMARY KEY NOT NULL, " +
                    "city_id INTEGER NOT NULL, day_max_temp INTEGER NOT NULL, night_min_temp INTEGER NOT NULL, " +
                    "weather_type_day INTEGER NOT NULL, FOREIGN KEY(city_id) REFERENCES city_table(city_id) " +
                    "ON DELETE CASCADE ON UPDATE CASCADE)");
            database.execSQL("DROP TABLE day_forecast_table");
            database.execSQL("ALTER TABLE temp_table RENAME TO day_forecast_table");
        }
    };


    static final Migration MIGRATION_2_3 = new Migration(2, 3) {
        @Override
        public void migrate(@NonNull SupportSQLiteDatabase database) {
            database.execSQL("ALTER TABLE day_forecast_table ADD COLUMN date TEXT");
        }
    };


    public static RoomDB getDatabase(final Context context){
        if (INSTANCE == null){
            synchronized (Database.class){
                if (INSTANCE == null){
                    INSTANCE = Room.databaseBuilder(context.getApplicationContext(),
                            RoomDB.class, "city_database")
                            .addMigrations(MIGRATION_1_2, MIGRATION_2_3)
                            .allowMainThreadQueries()
                            .fallbackToDestructiveMigration()
                            .addCallback(sDatabaseCallback)
                            .build();
                }
            }
        }
        return INSTANCE;
    }


    private static RoomDB.Callback sDatabaseCallback =
            new RoomDB.Callback(){
                @Override
                public void onCreate(@NonNull SupportSQLiteDatabase db){
                    super.onCreate(db);
                }
            };
}

