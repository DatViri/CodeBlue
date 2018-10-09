package leo.me.la.codeblue.data

import android.arch.persistence.room.EmptyResultSetException
import io.reactivex.Single
import leo.me.la.codeblue.cache.UserCacheDataStore
import leo.me.la.codeblue.remote.PolarRemoteDataStore
import leo.me.la.codeblue.remote.User

interface UserRepository {
    fun getUser(userId: Int, userToken: String) : Single<User>
}

class UserRepositoryImpl(
    private val polarRemoteDataStore: PolarRemoteDataStore,
    private val cacheDataStore: UserCacheDataStore
) : UserRepository {
    override fun getUser(userId: Int, userToken: String): Single<User> {
        return cacheDataStore.loadUserInfo(userId)
            .flatMap {
                // if user in database is saved longer than 5 minutes, then the data is out-dated
                // fetch for his info from Polar api
                if (System.currentTimeMillis() - it.savedTime > 5 * 60 * 1000) {
                    polarRemoteDataStore.getUserInfo(userId, userToken)
                        .doOnSuccess { user ->
                            // Save the info into cache after fetched successfully
                            cacheDataStore.storeUserInfo(user)
                        }
                } else {
                    // if user in database is saved shorter than 5 minutes, it can be reused
                    Single.just(
                        User(
                            it.userId,
                            it.firstName,
                            it.lastName,
                            it.birthdate,
                            it.gender,
                            it.weight,
                            it.height
                        )
                    )
                }
            }
            .onErrorResumeNext {
                if (it is EmptyResultSetException) {
                    // If there is nothing inside the database, fetch for the user from remote service
                    polarRemoteDataStore.getUserInfo(userId, userToken)
                        .doOnSuccess { user ->
                            cacheDataStore.storeUserInfo(user)
                        }
                } else {
                    Single.error(it)
                }
            }
    }
}
