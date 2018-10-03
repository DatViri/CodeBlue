package leo.me.la.codeblue

import android.app.Activity
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

class MainActivity : AppCompatActivity() {

    private val fragment1 = SliderFragment()
    private val fragment2 = SliderFragment()
    private val fragment3 = SliderFragment()
    lateinit var adapter: myPagerAdapter

    private lateinit var preferences: SharedPreferences
    private val prefShowIntro = "Intro"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        preferences = getSharedPreferences("InstroSlider", Context.MODE_PRIVATE)

        if(!preferences.getBoolean(prefShowIntro,true)){
            startActivity(Intent(this, DashboardActivity::class.java))
            finish()
        }


        fragment1.setTitle("Welcome")
        fragment2.setTitle("Fast")
        fragment3.setTitle("Correct")

        adapter = myPagerAdapter(supportFragmentManager)
        adapter.list.add(fragment1)
        adapter.list.add(fragment2)
        adapter.list.add(fragment3)

        view_pager.adapter = adapter
        btn_next.setOnClickListener{
            view_pager.currentItem++
        }

        btn_skip.setOnClickListener {
            goToDashboard()
        }

        view_pager.addOnPageChangeListener(object : ViewPager.OnPageChangeListener{
            override fun onPageScrollStateChanged(p0: Int) {
            }

            override fun onPageScrolled(p0: Int, p1: Float, p2: Int) {
            }

            override fun onPageSelected(p0: Int) {
                if(p0 == adapter.list.size-1){
                    //last page
                    btn_next.text = "DONE"
                    btn_next.setOnClickListener{
                        goToDashboard()
                    }
                } else{
                    //has next
                    btn_next.text = "NEXT"
                    btn_next.setOnClickListener {
                        view_pager.currentItem++
                    }
                }
                when(view_pager.currentItem){
                    0 ->{
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

    fun goToDashboard(){
        startActivity(Intent(this, DashboardActivity::class.java))
        finish()
        val editor = preferences.edit()
        editor.putBoolean(prefShowIntro,false)
        editor.apply()
    }

    class myPagerAdapter(manager: FragmentManager): FragmentPagerAdapter(manager){

        val list : MutableList<Fragment> = ArrayList()

        override fun getItem(position: Int): Fragment {
            return list[position]
        }

        override fun getCount(): Int {
            return list.size
        }
    }
}
