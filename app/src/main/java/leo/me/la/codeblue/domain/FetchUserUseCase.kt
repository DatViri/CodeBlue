package leo.me.la.codeblue.domain

import io.reactivex.Scheduler
import io.reactivex.Single
import leo.me.la.codeblue.data.UserRepository
import leo.me.la.codeblue.remote.User

class FetchUserUseCase(
    private val userRepository: UserRepository,
    private val threadExecutor: Scheduler,
    private val postExecutionThread: Scheduler
) {
    fun fetchUserUseCase(userId: Int) : Single<User> {
        return userRepository.getUser(userId)
            .subscribeOn(threadExecutor)
            .observeOn(postExecutionThread)
    }
}
