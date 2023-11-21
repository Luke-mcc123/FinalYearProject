package com.example.finalyearproject.room

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import kotlinx.coroutines.CoroutineScope


@Database(entities = arrayOf(Product::class), version = 1, exportSchema = false)
public abstract class ProjectRoomDatabase : RoomDatabase() {
    abstract fun productDao(): ProductDao

    companion object {
        // Singleton prevents multiple instances of database opening at the same time
        @Volatile
        private var INSTANCE: ProjectRoomDatabase? = null

        fun getDatabase(context: Context, scope: CoroutineScope): ProjectRoomDatabase {
            // if the INSTANCE is not null, then return it else create database
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    ProjectRoomDatabase::class.java,
                    "project_database"
                ).build()
                INSTANCE = instance
                // return instance
                instance
            }
        }

        private class ProductDatabaseCallback(
            private val scope: CoroutineScope
        ): RoomDatabase.Callback(){
            // Override the onCreate method to populate teh database
            override fun onCreate(db: SupportSQLiteDatabase) {
                super.onCreate(db)
            }
        }

        suspend fun populateDatabase(productDao: ProductDao){
            var product = Product(1, 12345, "Fanta", "500ml bottle", "N/A", 3, "2024-10-23")
            productDao.insertAll(product)
        }
    }
}

