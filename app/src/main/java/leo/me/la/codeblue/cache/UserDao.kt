package leo.me.la.codeblue.cache

import android.arch.persistence.room.ColumnInfo
import android.arch.persistence.room.Dao
import android.arch.persistence.room.Entity
import android.arch.persistence.room.Insert
import android.arch.persistence.room.PrimaryKey
import android.arch.persistence.room.Query
import io.reactivex.Single

@Dao
interface UserDao {
    @Insert
    fun insert(user: CacheUser)
    @Query("SELECT * from user WHERE user_id = :id")
    fun getUser(id: Int) : Single<CacheUser>
}

@Entity(tableName = "user")
data class CacheUser(
    @PrimaryKey
    @ColumnInfo(name = "user_id")
    val userId: Int,
    @ColumnInfo(name = "fname")
    val firstName: String,
    @ColumnInfo(name = "lname")
    val lastName: String,
    @ColumnInfo(name = "bday")
    val birthdate: String,
    @ColumnInfo(name = "gender")
    val gender: String,
    @ColumnInfo(name = "weight")
    val weight: Int,
    @ColumnInfo(name = "height")
    val height: Int,
    @ColumnInfo(name = "savedTime")
    val savedTime: Long = System.currentTimeMillis()
)
