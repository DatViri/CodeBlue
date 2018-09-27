package leo.me.la.codeblue.presentation

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.ViewModel
import android.util.Log
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.exceptions.CompositeException
import leo.me.la.codeblue.domain.FetchUserUseCase
import leo.me.la.codeblue.remote.User

class UserViewModel(
    private val fetchUserUseCase: FetchUserUseCase
) : ViewModel() {

    private val disposables = CompositeDisposable()
    private val _viewState = MutableLiveData<UserViewState>()
    val viewState : LiveData<UserViewState> = _viewState

    fun fetchUser(userId: Int) {
        disposables.add(
            fetchUserUseCase.fetchUserUseCase(userId)
                .doOnSubscribe {
                    _viewState.value = UserViewState.Loading
                }
                .subscribe({
                    _viewState.value = UserViewState.Success(it)
                }, {
                    _viewState.value = UserViewState.Failure(it)
                    Log.e("failed", (it as CompositeException).exceptions.map { it.message }.joinToString("\n"))
                })
        )
    }

    override fun onCleared() {
        super.onCleared()
        disposables.clear()
    }
}

sealed class UserViewState {
    object Loading : UserViewState()
    data class Success(val user: User) : UserViewState()
    data class Failure(val throwable: Throwable) : UserViewState()
}
