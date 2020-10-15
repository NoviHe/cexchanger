package com.cexchanger.cexchanger

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import com.cexchanger.cexchanger.util.SharedPref
import com.google.android.material.bottomnavigation.BottomNavigationView
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    private val statusLogin = false

    private lateinit var s:SharedPref

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        s=SharedPref(this)

        bottom_navigation.setOnNavigationItemSelectedListener(OnBottomNavListener)
//        bottom_navigation.getOrCreateBadge(R.id.item_withdraw).apply {
//            number = 10
//            isVisible = true
//            backgroundColor = resources.getColor(R.color.colorPrimary)
//        }

        var fr = supportFragmentManager.beginTransaction()
        fr.add(R.id.fl_fragment, HomeFragment())
        fr.commit()
    }

    private val OnBottomNavListener = BottomNavigationView.OnNavigationItemSelectedListener { i ->
        var selectedFr: Fragment = HomeFragment()

        when (i.itemId) {
            R.id.item_home -> {
                selectedFr = HomeFragment()
            }
            R.id.item_deposit -> {
                if (s.getStatusLogin()){
                    selectedFr = DepositFragment()
                } else {
                    startActivity(Intent(this, LoginActivity::class.java))
                }
            }
            R.id.item_withdraw -> {
                if (s.getStatusLogin()){
                    selectedFr = WithdrawFragment()
                } else {
                    startActivity(Intent(this, LoginActivity::class.java))
                }
            }
            R.id.item_profile -> {
                if (s.getStatusLogin()){
                    selectedFr = ProfileFragment()
                } else {
                    startActivity(Intent(this, LoginActivity::class.java))
                }
            }
        }

        var fr = supportFragmentManager.beginTransaction()
        fr.replace(R.id.fl_fragment, selectedFr)
        fr.commit()
        true
    }

}