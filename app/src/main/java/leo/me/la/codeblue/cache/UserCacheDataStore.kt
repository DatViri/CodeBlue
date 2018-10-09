package leo.me.la.codeblue.cache

import io.reactivex.Completable
import io.reactivex.Single
import leo.me.la.codeblue.remote.User

/**
 * interface definition of [UserCacheDataStore]
 */
interface UserCacheDataStore {
    fun storeUserInfo(user: User): Completable
    fun loadUserInfo(userId: Int): Single<CacheUser>
}

class UserCacheDataStoreImpl(
    private val userDao: UserDao
) : UserCacheDataStore {
    override fun storeUserInfo(user: User): Completable {
        return Completable.fromAction {
            userDao.insert(
                CacheUser(
                    userId = user.userId,
                    firstName = user.firstName,
                    lastName = user.lastName,
                    birthdate = user.birthdate,
                    gender = user.gender,
                    weight = user.weight,
                    height = user.height
                )
            )
        }
    }

    override fun loadUserInfo(userId: Int): Single<CacheUser> {
        return userDao.getUser(userId)
    }
}


