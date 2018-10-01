package leo.me.la.codeblue

import android.annotation.SuppressLint
import android.arch.lifecycle.Observer
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import kotlinx.android.synthetic.main.activity_info.dob
import kotlinx.android.synthetic.main.activity_info.dobValue
import kotlinx.android.synthetic.main.activity_info.height
import kotlinx.android.synthetic.main.activity_info.heightValue
import kotlinx.android.synthetic.main.activity_info.name
import kotlinx.android.synthetic.main.activity_info.nameValue
import kotlinx.android.synthetic.main.activity_info.profilePicture
import kotlinx.android.synthetic.main.activity_info.weight
import kotlinx.android.synthetic.main.activity_info.weightValue
import leo.me.la.codeblue.presentation.UserViewModel
import leo.me.la.codeblue.presentation.UserViewState
import org.koin.android.viewmodel.ext.android.viewModel

class InfoActivity : AppCompatActivity() {

    private val userViewModel : UserViewModel by viewModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_info)
        userViewModel.viewState.observe(this, Observer {
            it?.run(this@InfoActivity::render)
        })
        userViewModel.fetchUser(UserIdentities.user1)
    }

    @SuppressLint("SetTextI18n")
    private fun render(viewState: UserViewState) {
        when(viewState) {
            UserViewState.Loading -> {
            }
            is UserViewState.Failure -> {
            }
            is UserViewState.Success -> {
                showViews(setOf(
                    name,
                    nameValue,
                    dobValue,
                    dob,
                    weight,
                    weightValue,
                    heightValue,
                    height,
                    profilePicture
                ))
                with(viewState.user) {
                    nameValue.text = "$firstName $lastName"
                    dobValue.text = birthdate
                    weightValue.text = "${weight}kg"
                    heightValue.text = "${height}cm"
                }
            }
        }
    }

    private fun showViews(views: Set<View>) {
        views.forEach {
            it.visibility = View.VISIBLE
        }
    }
}
