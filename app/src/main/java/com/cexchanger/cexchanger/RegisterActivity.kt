package com.cexchanger.cexchanger

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import com.cexchanger.cexchanger.app.ApiConfig
import com.cexchanger.cexchanger.model.ResponseModel
import com.cexchanger.cexchanger.util.SharedPref
import com.cexchanger.cexchanger.util.snackbar
import kotlinx.android.synthetic.main.activity_deposit.*
import kotlinx.android.synthetic.main.activity_register.*
import kotlinx.android.synthetic.main.activity_register.loading
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class RegisterActivity : AppCompatActivity() {
    lateinit var s: SharedPref
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)
        s = SharedPref(this)

        register.setOnClickListener {
            register()
        }
    }

    fun register() {
        if (et_username.text.isEmpty()) {
            et_username.error = "Username tidak boleh kosong"
            et_username.requestFocus()
            return
        } else if (et_password.text.isEmpty()) {
            et_password.error = "Password tidak boleh kosong"
            et_password.requestFocus()
            return
        } else if (et_repassword.text.isEmpty()) {
            et_repassword.error = "Ulangi Password tidak boleh kosong"
            et_repassword.requestFocus()
            return
        } else if (et_nama_lengkap.text.isEmpty()) {
            et_nama_lengkap.error = "Nama Lengkap tidak boleh kosong"
            et_nama_lengkap.requestFocus()
            return
        } else if (et_email.text.isEmpty()) {
            et_email.error = "Email tidak boleh kosong"
            et_email.requestFocus()
            return
        } else if (et_alamat.text.isEmpty()) {
            et_alamat.error = "Alamat tidak boleh kosong"
            et_alamat.requestFocus()
            return
        } else if (et_no_hp.text.isEmpty()) {
            et_no_hp.error = "No HP tidak boleh kosong"
            et_no_hp.requestFocus()
            return
        }
        loading.visibility = View.VISIBLE

        ApiConfig.instanceRetrofit.register(
            et_username.text.toString(),
            et_password.text.toString(),
            et_repassword.text.toString(),
            et_nama_lengkap.text.toString(),
            et_alamat.text.toString(),
            et_email.text.toString(),
            et_no_hp.text.toString(),
            "avatar5.png"
        ).enqueue(object : Callback<ResponseModel> {
            override fun onResponse(call: Call<ResponseModel>, response: Response<ResponseModel>) {
                loading.visibility = View.GONE
                val respon = response.body()!!
                if (respon.success == 1) {
                    s.setStatusLogin(true)
                    s.setUser(respon.data)
                    val intent = Intent(this@RegisterActivity, MainActivity::class.java)
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
                    startActivity(intent)
                    finish()
                    Toast.makeText(
                        this@RegisterActivity,
                        "Selamat datang " + respon.data.nama_lengkap,
                        Toast.LENGTH_SHORT
                    ).show()
                } else {
                    container.snackbar(respon.message)
                    Toast.makeText(
                        this@RegisterActivity,
                        "Eroros: " + respon.message,
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }

            override fun onFailure(call: Call<ResponseModel>, t: Throwable) {
                loading.visibility = View.GONE
                Toast.makeText(this@RegisterActivity, "Eroros: " + t.message, Toast.LENGTH_SHORT)
                    .show()
            }

        })
    }

}