package leo.me.la.codeblue

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Color
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentPagerAdapter
import android.support.v4.view.ViewPager
import kotlinx.android.synthetic.main.activity_main.*
import leo.me.la.codeblue.fragment.SliderFragment

private const val prefShowIntro = "Intro"

/**
 * This activity shows a viewpager to tell user how to use the application
 */
class MainActivity : AppCompatActivity() {

    private val adapter: PagerAdapter by lazy {
        PagerAdapter(
            supportFragmentManager,
            listOf(
                SliderFragment.instance(R.drawable.step1, getString(R.string.step1)),
                SliderFragment.instance(R.drawable.step2, getString(R.string.step2)),
                SliderFragment.instance(R.drawable.step3, getString(R.string.step3))
            )
        )
    }

    private lateinit var preferences: SharedPreferences


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        preferences = getSharedPreferences("InstroSlider", Context.MODE_PRIVATE)

        if (!preferences.getBoolean(prefShowIntro, true)) {
            startActivity(Intent(this, DashboardActivity::class.java))
            finish()
        }

        view_pager.adapter = adapter
        btn_next.setOnClickListener {
            view_pager.currentItem++
        }

        btn_skip.setOnClickListener {
            goToDashboard()
        }

        view_pager.addOnPageChangeListener(object : ViewPager.OnPageChangeListener {
            override fun onPageScrollStateChanged(p0: Int) {
            }

            override fun onPageScrolled(p0: Int, p1: Float, p2: Int) {
            }

            override fun onPageSelected(p0: Int) {
                if (p0 == adapter.list.size - 1) {
                    //last page
                    btn_next.text = getString(R.string.done)
                    btn_next.setOnClickListener {
                        goToDashboard()
                    }
                } else {
                    //has next
                    btn_next.text = getString(R.string.next)
                    btn_next.setOnClickListener {
                        view_pager.currentItem++
                    }
                }
                when (view_pager.currentItem) {
                    0 -> {
                        indicator_1.setTextColor(Color.WHITE)
                        indicator_2.setTextColor(Color.GRAY)
                        indicator_3.setTextColor(Color.GRAY)
                    }
                    1 -> {
                        indicator_1.setTextColor(Color.GRAY)
                        indicator_2.setTextColor(Color.WHITE)
                        indicator_3.setTextColor(Color.GRAY)
                    }
                    2 -> {
                        indicator_1.setTextColor(Color.GRAY)
                        indicator_2.setTextColor(Color.GRAY)
                        indicator_3.setTextColor(Color.WHITE)
                    }
                }
            }
        })
    }

    fun goToDashboard() {
        startActivity(Intent(this, DashboardActivity::class.java))
        finish()
        preferences.edit()
            .putBoolean(prefShowIntro, false)
            .apply()
    }

    class PagerAdapter(
        manager: FragmentManager,
        val list: List<Fragment>
    ) : FragmentPagerAdapter(manager) {

        override fun getItem(position: Int): Fragment {
            return list[position]
        }

        override fun getCount(): Int {
            return list.size
        }
    }
}
