package com.cexchanger.cexchanger

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.viewpager.widget.ViewPager
import com.cexchanger.cexchanger.adaptor.AdapterSlider
import com.cexchanger.cexchanger.app.ApiConfig
import com.cexchanger.cexchanger.model.TbKurs
import com.cexchanger.cexchanger.util.SharedPref
import com.google.android.material.button.MaterialButton
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.text.DecimalFormat
import java.util.*


class HomeFragment : Fragment() {

    lateinit var vpSlider: ViewPager

    var currentPage: Int = 0
    lateinit var timer: Timer
    val DELAY_MS: Long = 5000
    val PERIOD_MS: Long = 5000

    val arrSlider = ArrayList<Int>()

    private lateinit var s: SharedPref
    lateinit var tvKursDepo: TextView
    lateinit var tvKursWD: TextView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val t = inflater.inflate(R.layout.fragment_home, container, false)

        s = SharedPref(activity!!)


        vpSlider = t.findViewById(R.id.vp_banner)
        arrSlider.add(R.drawable.banner2)
        arrSlider.add(R.drawable.banner3)
        arrSlider.add(R.drawable.banner4)
        arrSlider.add(R.drawable.banner5)

        val adapterSlider = AdapterSlider(arrSlider, activity)
        vpSlider.adapter = adapterSlider
        pageUpdate()

        tvKursDepo = t.findViewById(R.id.kursDepo)
        tvKursWD = t.findViewById(R.id.kursWD)


        val godepo = t.findViewById<MaterialButton>(R.id.go_deposit)
        val gowd = t.findViewById<MaterialButton>(R.id.go_withdraw)

        godepo.setOnClickListener {
            activity?.let {
                if (s.getStatusLogin()) {
                    var fragment: Fragment
                    fragment = DepositFragment()
                    replaceFragment(fragment)
                } else {
                    var intent = Intent(it, LoginActivity::class.java)
                    startActivity(intent)
                }

            }
        }

        gowd.setOnClickListener {
            activity?.let {
                if (s.getStatusLogin()) {
                    var fragment: Fragment
                    fragment = WithdrawFragment()
                    replaceFragment(fragment)
                } else {
                    startActivity(Intent(it, LoginActivity::class.java))
                }
            }
        }
        kursDepo()
        kursWD()
        return t
    }

    fun currencyFormat(amount: String): String? {
        val formatter = DecimalFormat("###,###,##0.00")
        return formatter.format(amount.toDouble())
    }

    fun kursWD() {
        ApiConfig.instanceRetrofit.kursWithdraw().enqueue(object : Callback<TbKurs> {
            override fun onResponse(call: Call<TbKurs>, response: Response<TbKurs>) {
                val respon = response.body()!!
                if (respon.success == 1) {
                    val res = currencyFormat(respon.data.rupiah)
                    tvKursWD.setText("Rp. " + res)
                }
            }

            override fun onFailure(call: Call<TbKurs>, t: Throwable) {

            }

        })
    }

    fun kursDepo() {
        ApiConfig.instanceRetrofit.kursDeposit().enqueue(object : Callback<TbKurs> {
            override fun onResponse(call: Call<TbKurs>, response: Response<TbKurs>) {
                val respon = response.body()!!
                if (respon.success == 1) {
                    val res = currencyFormat(respon.data.rupiah)
                    tvKursDepo.setText("Rp. " + res)
                }
            }

            override fun onFailure(call: Call<TbKurs>, t: Throwable) {

            }

        })
    }

    fun replaceFragment(someFragment: Fragment?) {
        val transaction = fragmentManager!!.beginTransaction()
        transaction.replace(R.id.fl_fragment, someFragment!!)
        transaction.addToBackStack(null)
        transaction.commit()
    }


    fun pageUpdate() {
        var handler = Handler()
        val Update: Runnable = Runnable {
            if (currentPage == arrSlider.size) {
                currentPage = 0
            }
            vpSlider.setCurrentItem(currentPage++, true)
        }
        timer = Timer()
        timer.schedule(object : TimerTask() {
            override fun run() {
                handler.post(Update)
            }
        }, DELAY_MS, PERIOD_MS)
    }

}