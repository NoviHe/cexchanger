package com.cexchanger.cexchanger

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import com.cexchanger.cexchanger.util.SharePreference

class SplashActivity : AppCompatActivity() {
    lateinit var pre: SharePreference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        pre = SharePreference(this)

        Handler().postDelayed({
            var i = Intent()
//            if (!pre.fristInstall) {
            i = Intent(this, WalkThroughActivity::class.java)
//            } else {
//                i = Intent(this, MainActivity::class.java)
//            }
            startActivity(i)
            finish()
        }, 3000)
    }
}