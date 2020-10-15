package com.cexchanger.cexchanger

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Html
import android.util.Log
import android.widget.TextView
import androidx.viewpager.widget.ViewPager
import com.cexchanger.cexchanger.adaptor.WalkthroughAdaptor
import com.cexchanger.cexchanger.util.SharePreference
import kotlinx.android.synthetic.main.activity_walk_through.*

class WalkThroughActivity : AppCompatActivity() {

    lateinit var wkAdaptor: WalkthroughAdaptor
    val dots = arrayOfNulls<TextView>(4)
    var currentPage: Int = 0
    lateinit var pre: SharePreference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_walk_through)

        pre = SharePreference(this)
        wkAdaptor = WalkthroughAdaptor(this)
        vp_walkthrough.adapter = wkAdaptor
        dotsIndicator(currentPage)
        initAction()
    }

    fun initAction() {
        vp_walkthrough.addOnPageChangeListener(object : ViewPager.OnPageChangeListener {
            override fun onPageScrolled(
                position: Int,
                positionOffset: Float,
                positionOffsetPixels: Int
            ) {

            }

            override fun onPageSelected(position: Int) {
                dotsIndicator(position)
                currentPage = position

                if (position == dots.size - 1) {
                    tv_lanjutkan.setText("SELESAI")
                }
            }

            override fun onPageScrollStateChanged(state: Int) {
                Log.e("bob", state.toString())
            }

        })

        tv_lanjutkan.setOnClickListener {
            if (vp_walkthrough.currentItem + 1 < dots.size) {
                vp_walkthrough.currentItem += 1
            } else {
//                pre.fristInstall = true
                val intent = Intent(this@WalkThroughActivity, MainActivity::class.java)
                startActivity(intent)
                finish()
            }
        }

        tv_lewati.setOnClickListener {
//            pre.fristInstall = true
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            finish()
        }
    }

    fun dotsIndicator(p: Int) {
        ll_dots.removeAllViews()

        for (i in 0..dots.size - 1) {
            dots[i] = TextView(this)
            dots[i]?.textSize = 35f
            dots[i]?.setTextColor(resources.getColor(R.color.grey))
            dots[i]?.text = Html.fromHtml("&#8226;")

            ll_dots.addView(dots[i])
        }

        if (dots.size > 0) {
            dots[p]?.setTextColor(resources.getColor(R.color.colorPrimary))
        }
    }
}