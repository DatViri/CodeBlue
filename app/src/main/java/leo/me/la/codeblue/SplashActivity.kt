package leo.me.la.codeblue

import android.animation.ValueAnimator
import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_info.*
import kotlinx.android.synthetic.main.activity_splash.*
import java.util.*

class SplashActivity : AppCompatActivity() {
    private val popupAnimator = ValueAnimator.ofFloat(1f, 0.9f)
            .also {
                it.duration = 500
                it.repeatMode = ValueAnimator.REVERSE
                it.repeatCount = ValueAnimator.INFINITE
            }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        val background = object : Thread(){
            override fun run() {
                Thread.sleep(4000)
                val intent = Intent(baseContext, MainActivity::class.java)
                startActivity(intent)
                finish()
            }
        }
        background.start()

        popupAnimator.apply {
            addUpdateListener {
                fullscreen_content.scaleX = it.animatedValue as Float
                fullscreen_content.scaleY = it.animatedValue as Float
            }
            start()
        }
        app_name.translateY(getHeight().toFloat(),500)
        app_banner.translateY(getHeight().toFloat(),500)
    }
}