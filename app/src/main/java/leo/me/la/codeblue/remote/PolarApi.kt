package leo.me.la.codeblue.remote

import com.google.gson.annotations.SerializedName
import io.reactivex.Single
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Path

interface PolarApi {
    @GET("v3/users/{user-id}")
    fun getUserInfo(
        @Path("user-id") userId: Int,
        @Header("Authorization") token: String = "Bearer caf5efd8628fe216c30b08cd63649edc"
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
