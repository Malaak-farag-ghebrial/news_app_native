package com.example.netowrk_training.database

import android.content.Context
import androidx.room.Database
import androidx.room.InvalidationTracker
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.netowrk_training.models.Article

@Database([Article::class], version = 1)
@TypeConverters(SourceTypeConverter::class)
abstract class ArticleDatabase : RoomDatabase() {
    abstract fun articleDAO() : ArticleDAO


    companion object {
        private var instance : ArticleDatabase? = null
        private var LOCK = Any()


        operator fun invoke(context: Context) = instance ?: synchronized(LOCK){
            instance?: createDatabase(context).also{
                instance = it
        }
        }

        private fun createDatabase(context: Context)= Room.databaseBuilder(
            context.applicationContext,
            ArticleDatabase::class.java,
            "news_db.db"
        ).build()


    }

}