package leo.me.la.codeblue

import android.app.Activity
import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import kotlinx.android.synthetic.main.activity_dashboard.*

class DashboardActivity : AppCompatActivity() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_dashboard)

        btn_start.setOnClickListener {
            goToAugmentedImageActivity()
        }
    }

    private fun goToAugmentedImageActivity(){
        startActivity(Intent(this, AugmentedImageActivity::class.java))
        finish()
    }
}
