package leo.me.la.codeblue.domain

import io.reactivex.Scheduler
import io.reactivex.Single
import leo.me.la.codeblue.data.UserRepository
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class FetchUserUseCase(
    private val userRepository: UserRepository,
    private val threadExecutor: Scheduler,
    private val postExecutionThread: Scheduler
) {
    fun fetchUserUseCase(userId: Int, userToken:String) : Single<DataUser> {
        return userRepository.getUser(userId, userToken)
            .map {
                val year = Calendar.getInstance()
                    .also { cal ->
                        cal.time = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).parse(it.birthdate)
                    }
                    .get(Calendar.YEAR)
                DataUser(
                    it.firstName,
                    it.lastName,
                    Calendar.getInstance().get(Calendar.YEAR) - year,
                    it.gender,
                    it.weight,
                    it.height
                )
            }
            .subscribeOn(threadExecutor)
            .observeOn(postExecutionThread)
    }
}

data class DataUser(
    val firstName: String,
    val lastName: String,
    val age: Int,
    val gender: String,
    val weight: Int,
    val height: Int
)
