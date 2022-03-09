package com.enicholson125.calorielogger.data

import android.content.Context
import androidx.room.Database
import androidx.room.TypeConverters
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

/**
 * The Room database for this app
 */
@Database(entities = [CalorieLog::class], version = 2, exportSchema = true)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun calorieLogDAO(): CalorieLogDAO

    companion object {

        // For Singleton instantiation
        @Volatile private var instance: AppDatabase? = null

        fun getInstance(context: Context): AppDatabase {
            return instance ?: synchronized(this) {
                instance ?: buildDatabase(context).also { instance = it }
            }
        }

        val MIGRATION_1_2 = object : Migration(1, 2) {
            override fun migrate(database: SupportSQLiteDatabase) {
                database.execSQL("ALTER TABLE calorie_log ADD COLUMN sweet INTEGER NOT NULL default 1")
                database.execSQL("INSERT INTO calorie_log (id, time_logged, calories, description, sweet) VALUES ('2jkid', '2022-03-09 00:00:00', 1170, 'Daily Budget Full', 0)")
            }
        }

        private fun buildDatabase(context: Context): AppDatabase {
            return Room.databaseBuilder(context, AppDatabase::class.java, "meteor-db")
                .createFromAsset("seeddatabase.db")
                .addMigrations(MIGRATION_1_2)
                .build()
        }
    }
}
