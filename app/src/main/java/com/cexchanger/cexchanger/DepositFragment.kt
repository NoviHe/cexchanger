package com.cexchanger.cexchanger


import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.cexchanger.cexchanger.adaptor.DepositAdapter
import com.cexchanger.cexchanger.app.ApiConfig
import com.cexchanger.cexchanger.model.DepositResponse
import com.cexchanger.cexchanger.model.TbDeposit
import com.cexchanger.cexchanger.util.SharedPref
import kotlinx.android.synthetic.main.activity_login.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit

class DepositFragment : Fragment() {

    lateinit var s: SharedPref
    private val list = ArrayList<TbDeposit>()
    lateinit var rvDepoHistori :RecyclerView
    lateinit var kosong : TextView
    lateinit var loading: ProgressBar

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val t = inflater.inflate(R.layout.fragment_deposit, container, false)

        val btnGoDepo = t.findViewById<TextView>(R.id.btn_go_deposit)
        loading = t.findViewById(R.id.loading)

        rvDepoHistori = t.findViewById(R.id.rv_histori)
        kosong = t.findViewById(R.id.kosong)
        s = SharedPref(activity!!)

        rvDepoHistori.setHasFixedSize(true)
        rvDepoHistori.layoutManager = LinearLayoutManager(activity!!)

        setHistori()

        btnGoDepo.setOnClickListener {
            activity?.let {
                val goDeposit = Intent(it, DepositActivity::class.java)
                startActivity(goDeposit)
            }
        }
        return t
    }

    fun setHistori(){
        val idUser = s.getUser()!!.id_user
        ApiConfig.instanceRetrofit.getDeposit(idUser).enqueue(object : Callback<ArrayList<TbDeposit>>{
            override fun onResponse(
                call: Call<ArrayList<TbDeposit>>,
                response: Response<ArrayList<TbDeposit>>
            ) {
                loading.visibility = View.GONE
                val respon = response.body()!!
                if (respon.isEmpty()){
                    kosong.visibility = View.VISIBLE
                    kosong.text = "Riwayat Deposit kosong"
                }
                val responseCode = response.code().toShort()
                respon?.let { list.addAll(it) }
                val adatper = DepositAdapter(list)
                rvDepoHistori.adapter = adatper
            }

            override fun onFailure(call: Call<ArrayList<TbDeposit>>, t: Throwable) {
                loading.visibility = View.GONE
                kosong.visibility = View.VISIBLE
                Toast.makeText(activity, "Eroros: " + t.message, Toast.LENGTH_SHORT)
                    .show()
            }

        })
    }


}