package com.example.myapplication.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

/**
 * Main Room database for the application.
 *
 * Stores hiking sessions and recorded route points.
 * This database is implemented as a singleton to ensure
 * a single source of truth across the app.
 */
@Database(entities = [Hike::class, RoutePoint::class], version = 1)
abstract class AppDatabase : RoomDatabase() {

    /**
     * Provides access to hike-related database operations.
     *
     * @return DAO for performing CRUD operations on hikes and route points.
     */
    abstract fun hikeDao(): HikeDao

    /**
     * Singleton provider for the [AppDatabase] instance.
     *
     * Ensures thread-safe lazy initialization of the Room database.
     */
    companion object {
        /**
         * Volatile singleton instance of the database.
         *
         * Marked as volatile to ensure visibility across threads.
         */
        @Volatile private var INSTANCE: AppDatabase? = null

        /**
         * Returns the singleton instance of the application database.
         *
         * Creates the database if it does not already exist using
         * the application context to avoid leaking activities.
         *
         * @param context Context used to initialize the database.
         * @return Singleton [AppDatabase] instance.
         */
        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "hiking_app.db"
                ).build().also { INSTANCE = it }
            }
        }
    }
}