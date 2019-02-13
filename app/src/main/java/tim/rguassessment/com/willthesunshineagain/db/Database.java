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
        {City.class, ThreeHourlyForecast.class, DayForecast.class}, version = 4)
@TypeConverters({Converters.class})
public abstract class Database extends RoomDatabase{


    public abstract CityDao cityDao();
    public abstract ThreeHourlyForecastDao hourlyDao();
    public abstract DayForecastDao dayDao();

    private static Database INSTANCE;
    private static final String TAG = "TAGG";


    static final Migration MIGRATION_1_2 = new Migration(1, 2) {
        @Override
        public void migrate(SupportSQLiteDatabase database) {
            database.execSQL("ALTER TABLE 'three_hourly_forecast_table' ADD COLUMN 'wind_direction' TEXT");
        }
    };


    static final Migration MIGRATION_2_3 = new Migration(2, 3) {
        @Override
        public void migrate(@NonNull SupportSQLiteDatabase database) {
            database.execSQL("CREATE TABLE daily_forecast_table_temp (uid INTEGER NOT NULL, " +
                    "city_id INTEGER NOT NULL, day_max_temp INTEGER, night_min_temp INTEGER," +
                    "weather_type_day INTEGER, PRIMARY KEY(uid), FOREIGN KEY(city_id) REFERENCES city_table(city_id))");
            /*database.execSQL("INSERT INTO daily_forecast_table_temp " +
                    "SELECT city_id, day_max_temp, night_max_temp, weather_type_day FROM three_hourly_forecast_table");*/
            database.execSQL("DROP TABLE daily_forecast_table");
            database.execSQL("ALTER TABLE daily_forecast_table_temp RENAME TO daily_forecast_table");
        }
    };


    static final Migration MIGRATION_3_4 = new Migration(3, 4) {
        @Override
        public void migrate(@NonNull SupportSQLiteDatabase database) {
            database.execSQL("DROP TABLE daily_forecast_table");

        }
    };



    public static Database getDatabase(final Context context){
        if (INSTANCE == null){
            synchronized (Database.class){
                if (INSTANCE == null){
                    INSTANCE = Room.databaseBuilder(context.getApplicationContext(),
                            Database.class, "city_database")
                            .addMigrations(MIGRATION_1_2, MIGRATION_2_3, MIGRATION_3_4)
                            .allowMainThreadQueries()
                            .fallbackToDestructiveMigration()
                            .addCallback(sDatabaseCallback)
                            .build();
                }
            }
        }
        return INSTANCE;
    }


    private static Database.Callback sDatabaseCallback =
            new Database.Callback(){
                @Override
                public void onCreate(@NonNull SupportSQLiteDatabase db){
                    super.onCreate(db);
                }
            };
}

