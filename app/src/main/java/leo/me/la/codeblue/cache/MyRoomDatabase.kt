package leo.me.la.codeblue.cache

import android.arch.persistence.room.Database
import android.arch.persistence.room.RoomDatabase


@Database(entities = [CacheUser::class], version = 1)
abstract class MyRoomDatabase : RoomDatabase() {
    abstract fun userDao() : UserDao
}
