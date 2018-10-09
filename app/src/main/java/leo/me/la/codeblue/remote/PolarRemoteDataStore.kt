package leo.me.la.codeblue.remote

import io.reactivex.Single

interface PolarRemoteDataStore {
    fun getUserInfo(userId: Int, userToken: String) : Single<User>
}

class PolarRemoteDataStoreImpl(
    private val polarApi: PolarApi
) : PolarRemoteDataStore {
    override fun getUserInfo(userId: Int, userToken: String): Single<User> {
        return polarApi.getUserInfo(userId, "Bearer $userToken")
    }
}
