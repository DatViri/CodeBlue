package leo.me.la.codeblue

import android.arch.lifecycle.Observer
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import leo.me.la.codeblue.presentation.UserViewModel
import leo.me.la.codeblue.presentation.UserViewState
import org.koin.android.viewmodel.ext.android.viewModel

class InfoActivity : AppCompatActivity() {

    private val userViewModel : UserViewModel by viewModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        userViewModel.viewState.observe(this, Observer {
            it?.run(this@InfoActivity::render)
        })
        userViewModel.fetchUser(UserIdentities.user1)
    }

    private fun render(viewState: UserViewState) {
        when(viewState) {
            UserViewState.Loading -> {
                Log.d("loading", "loaded")
            }
            is UserViewState.Failure -> {
                Log.e("failed", viewState.throwable.toString())
            }
            is UserViewState.Success -> {
                Log.d("success", viewState.user.firstName)
            }
        }
    }
}
