package com.nhom.movieapp.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

/**
- Room Database chuẩn Singleton.

- Khi thêm Entity mới (UserEntity, WatchlistEntity...), thêm vào `entities`
- và tăng `version` (hoặc gỡ cài đặt app khi đang phát triển).
 */
@Database(
    entities = [MovieEntity::class, UserEntity::class],
    version = 2,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {

    abstract fun movieDao(): MovieDao

    // Thêm dòng này để AuthViewModel có thể gọi userDao()
    abstract fun userDao(): UserDao

    // abstract fun watchlistDao(): WatchlistDao  // bạn C thêm

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getInstance(context: Context): AppDatabase =
            INSTANCE ?: synchronized(this) {
                INSTANCE ?: Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "movie_app.db"
                )
                    .fallbackToDestructiveMigration(dropAllTables = true)
                    .build()
                    .also { INSTANCE = it }
            }
    }
}