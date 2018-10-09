package leo.me.la.codeblue.presentation

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.ViewModel
import io.reactivex.disposables.CompositeDisposable
import leo.me.la.codeblue.domain.BMIStatus
import leo.me.la.codeblue.domain.CalculateBMIUseCase
import leo.me.la.codeblue.domain.DataUser
import leo.me.la.codeblue.domain.FetchUserUseCase

class UserViewModel(
    private val fetchUserUseCase: FetchUserUseCase,
    private val bmiUseCase: CalculateBMIUseCase
) : ViewModel() {

    private val disposables = CompositeDisposable()
    private val _viewState = MutableLiveData<UserViewState>()
    val viewState : LiveData<UserViewState> = _viewState

    fun fetchUser(userId: Int, userToken: String) {
        disposables.add(
            fetchUserUseCase.fetchUserUseCase(userId,userToken)
                .flatMap {
                    bmiUseCase.calculateBMI(it.weight, it.height)
                        .map { bmi ->
                            Pair(it, bmi)
                        }
                }
                .doOnSubscribe {
                    _viewState.value = UserViewState.Loading
                }
                .subscribe({
                    _viewState.value = UserViewState.Success(it.first, it.second)
                }, {
                    _viewState.value = UserViewState.Failure(it)
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
    data class Success(val user: DataUser, val bmi: BMIStatus) : UserViewState()
    data class Failure(val throwable: Throwable) : UserViewState()
}
