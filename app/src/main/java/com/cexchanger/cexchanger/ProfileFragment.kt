package com.cexchanger.cexchanger

import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.cexchanger.cexchanger.app.ApiConfig
import com.cexchanger.cexchanger.model.BalanceResponse
import com.cexchanger.cexchanger.model.ResponseModel
import com.cexchanger.cexchanger.util.SharedPref
import de.hdodenhof.circleimageview.CircleImageView
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.text.DecimalFormat


class ProfileFragment : Fragment() {

    lateinit var s: SharedPref
    lateinit var btnEdit: ImageView
    lateinit var btnGoWD: ImageView
    lateinit var btnLogout: ImageView

    lateinit var tvUsername: TextView
    lateinit var tvEmail: TextView
    lateinit var tvNamaLengkap: TextView
    lateinit var tvAlamat: TextView
    lateinit var tvNoHP: TextView
    lateinit var loadingProfil: ProgressBar
    lateinit var noData: ScrollView
    lateinit var tvNoData: TextView
    lateinit var fotoProfil: CircleImageView
    lateinit var totalDepo:TextView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val t = inflater.inflate(R.layout.fragment_profile, container, false)

        s = SharedPref(activity!!)

        init(t)

        btnEdit.setOnClickListener {
            activity?.let {
                val go = Intent(it, ProfileEditActivity::class.java)
                startActivity(go)
            }
        }

        btnGoWD.setOnClickListener {
            activity?.let {
                var fragment: Fragment
                fragment = WithdrawFragment()
                replaceFragment(fragment)
            }
        }

        btnLogout()
        setData()
        setBalance()
        return t
    }

    fun btnLogout(){
        btnLogout.setOnClickListener {

            val builder = AlertDialog.Builder(activity)
            builder.setTitle("Logout")
            builder.setMessage("Apakah anda yakin logout?")

            builder.setPositiveButton(android.R.string.yes) { dialog, which ->
                Toast.makeText(
                    activity,
                    android.R.string.yes, Toast.LENGTH_SHORT
                ).show()
                s.setStatusLogin(false)
                activity?.let {
                    val go = Intent(it, MainActivity::class.java)
                    startActivity(go)
                }
            }

            builder.setNegativeButton(android.R.string.no) { dialog, which ->
                Toast.makeText(
                    activity,
                    android.R.string.no, Toast.LENGTH_SHORT
                ).show()
            }
            builder.show()
        }
    }

    fun currencyFormat(amount: Int): String? {
        val formatter = DecimalFormat("###,###,##0.00")
        return formatter.format(amount.toDouble())
    }

    fun setBalance() {
        val user = s.getUser()!!
        ApiConfig.instanceRetrofit.getBalance(user.id_user).enqueue(object :
            Callback<BalanceResponse> {
            override fun onResponse(
                call: Call<BalanceResponse>,
                response: Response<BalanceResponse>
            ) {
                val respon = response.body()!!
                val res = currencyFormat(respon.data)
                totalDepo.setText("Rp.  " + res)
            }

            override fun onFailure(call: Call<BalanceResponse>, t: Throwable) {
                Toast.makeText(activity, "Eroros: " + t.message, Toast.LENGTH_SHORT)
                    .show()
            }

        })
    }

    fun setData() {
        if (s.getUser() == null) {
            val intent = Intent(activity, LoginActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
            startActivity(intent)
            return
        }
        val user = s.getUser()!!

        Glide.with(activity).load("https://cexchanger.com/asset1/images/" + user.foto)
            .into(fotoProfil)

        ApiConfig.instanceRetrofit.getUser(user.id_user).enqueue(object : Callback<ResponseModel> {

            override fun onFailure(call: Call<ResponseModel>, t: Throwable) {
                loadingProfil.visibility = View.GONE
                noData.visibility = View.GONE
                tvNoData.visibility = View.VISIBLE
                Toast.makeText(activity, "Eroros: " + t.message, Toast.LENGTH_SHORT)
                    .show()
            }

            override fun onResponse(call: Call<ResponseModel>, response: Response<ResponseModel>) {
                val respon = response.body()!!
                if (respon.success == 1) {
                    tvUsername.text = respon.data.username
                    tvEmail.text = respon.data.email
                    tvNamaLengkap.text = respon.data.nama_lengkap
                    tvAlamat.text = respon.data.alamat
                    tvNoHP.text = respon.data.no_hp

                    loadingProfil.visibility = View.GONE
                }
                loadingProfil.visibility = View.GONE
            }
        })
    }

    private fun init(t: View) {
        btnEdit = t.findViewById(R.id.btn_edit_profile)
        btnGoWD = t.findViewById(R.id.go_wd)
        btnLogout = t.findViewById(R.id.btn_logout)

        tvUsername = t.findViewById(R.id.tv_username)
        tvEmail = t.findViewById(R.id.tv_email_2)
        tvNamaLengkap = t.findViewById(R.id.tv_nama_lengkap)
        tvAlamat = t.findViewById(R.id.tv_alamat)
        tvNoHP = t.findViewById(R.id.tv_no_hp)
        loadingProfil = t.findViewById(R.id.loadingProfil)
        noData = t.findViewById(R.id.noData)
        tvNoData = t.findViewById(R.id.tv_noData)
        fotoProfil = t.findViewById(R.id.foto_profil)
        totalDepo = t.findViewById(R.id.total_DEPO)
    }

    fun replaceFragment(someFragment: Fragment?) {
        val transaction = fragmentManager!!.beginTransaction()
        transaction.replace(R.id.fl_fragment, someFragment!!)
        transaction.addToBackStack(null)
        transaction.commit()
    }


}