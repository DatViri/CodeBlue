package leo.me.la.codeblue.remote

import com.google.gson.annotations.SerializedName
import io.reactivex.Single
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Path

/**
 * Retrofit interface definition of Polar api
 * <a href="https://www.polar.com/accesslink-api/"/>
 */
interface PolarApi {
    @GET("v3/users/{user-id}")
    fun getUserInfo(
        @Path("user-id") userId: Int,
        @Header("Authorization") token: String
    ) : Single<User>
}

data class User(
    @SerializedName("polar-user-id")
    val userId : Int,
    @SerializedName("first-name")
    val firstName: String,
    @SerializedName("last-name")
    val lastName: String,
    val birthdate: String,
    val gender: String,
    val weight: Int,
    val height: Int
)
