package leo.me.la.codeblue.cache

import android.arch.persistence.room.Room
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module.module

val cacheModule = module {
    factory<UserCacheDataStore> {
        UserCacheDataStoreImpl(get())
    }
    factory { get<MyRoomDatabase>().userDao() }

    single {
        Room.databaseBuilder(
            androidContext(),
            MyRoomDatabase::class.java,
            "codeblue_database"
        )
            .fallbackToDestructiveMigration()
            .build()
    }
}
