package com.cexchanger.cexchanger

import android.content.Intent
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.content.res.AppCompatResources.getDrawable
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.cexchanger.cexchanger.adaptor.WithdrawAdapter
import com.cexchanger.cexchanger.app.ApiConfig
import com.cexchanger.cexchanger.model.TbWithdrawal
import com.cexchanger.cexchanger.util.SharedPref
import kotlinx.android.synthetic.main.fragment_withdraw.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class WithdrawFragment : Fragment() {

    lateinit var s: SharedPref
    private val list = ArrayList<TbWithdrawal>()
    lateinit var rvDepoHistori: RecyclerView
    lateinit var kosong: TextView
    lateinit var btnGoWd: TextView
    lateinit var loading: ProgressBar

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val t = inflater.inflate(R.layout.fragment_withdraw, container, false)
        init(t)
        s = SharedPref(activity!!)

        rvDepoHistori?.setHasFixedSize(true)
        rvDepoHistori?.layoutManager = LinearLayoutManager(activity!!)
        
        getHistoriWithdraw()

        btnGoWd.setOnClickListener {
            activity?.let {
                val goWithdraw = Intent(it, WithdrawActivity::class.java)
                startActivity(goWithdraw)
            }
        }
        return t
    }

    private fun init(t: View) {
        rvDepoHistori = t?.findViewById(R.id.rv_histori)
        kosong = t.findViewById(R.id.kosong)
        btnGoWd = t.findViewById(R.id.btn_go_withdraw)
        loading = t.findViewById(R.id.loading)
    }

    fun getHistoriWithdraw() {
        val idUser = s.getUser()!!.id_user
        ApiConfig.instanceRetrofit.getWithdrawal(idUser).enqueue(object :
            Callback<ArrayList<TbWithdrawal>> {
            override fun onResponse(
                call: Call<ArrayList<TbWithdrawal>>,
                response: Response<ArrayList<TbWithdrawal>>
            ) {
                loading.visibility = View.GONE
                val respon = response.body()!!
                if (respon.isEmpty()){
                    kosong.visibility = View.VISIBLE
                    kosong.text = "Riwayat Deposit kosong"
                }
                val responseCode = response.code().toShort()
                respon?.let { list.addAll(it) }
                val adatper = WithdrawAdapter(list)
                rvDepoHistori?.adapter = adatper
            }

            override fun onFailure(call: Call<ArrayList<TbWithdrawal>>, t: Throwable) {
                loading.visibility = View.GONE
                kosong.visibility = View.VISIBLE
                Toast.makeText(activity, "Eroros: " + t.message, Toast.LENGTH_SHORT)
                    .show()
            }

        })
    }

}