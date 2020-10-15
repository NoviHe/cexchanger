package com.cexchanger.cexchanger

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.cexchanger.cexchanger.app.ApiConfig
import com.cexchanger.cexchanger.model.ResponseModel
import com.cexchanger.cexchanger.util.SharedPref
import com.cexchanger.cexchanger.util.snackbar
import kotlinx.android.synthetic.main.activity_login.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class LoginActivity : AppCompatActivity(), View.OnClickListener {

    lateinit var s: SharedPref

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        s = SharedPref(this)

        val loginBtn: Button = findViewById(R.id.login)
        val registerBtn: TextView = findViewById(R.id.go_register)

        loginBtn.setOnClickListener(this)
        registerBtn.setOnClickListener(this)

        forgot_password.setOnClickListener {
            val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse("https://cexchanger.com/login_user/forgot"))
            startActivity(browserIntent)

        }
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.login -> {
                login()
            }
            R.id.go_register -> {
                val intentMove = Intent(this@LoginActivity, RegisterActivity::class.java)
                startActivity(intentMove)
            }
        }
    }

    fun login() {
        if (lg_username.text.isEmpty()) {
            lg_username.error = "Username tidak boleh kosong"
            lg_username.requestFocus()
            return
        } else if (lg_password.text.isEmpty()) {
            lg_password.error = "Password tidak boleh kosong"
            lg_password.requestFocus()
            return
        }
        loading.visibility = View.VISIBLE

        ApiConfig.instanceRetrofit.login(
            lg_username.text.toString(),
            lg_password.text.toString()
        ).enqueue(object : Callback<ResponseModel> {
            override fun onResponse(call: Call<ResponseModel>, response: Response<ResponseModel>) {
                loading.visibility = View.GONE
                val respon = response.body()!!
                if (respon.success == 1) {
                    s.setStatusLogin(true)
                    s.setUser(respon.data)
                    finish()
                    Toast.makeText(
                        this@LoginActivity,
                        "Selamat datang " + respon.data.nama_lengkap,
                        Toast.LENGTH_SHORT
                    ).show()
                } else {
                    container.snackbar(respon.message)
                    Toast.makeText(
                        this@LoginActivity,
                        "Eroros: " + respon.message,
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }

            override fun onFailure(call: Call<ResponseModel>, t: Throwable) {
                loading.visibility = View.GONE
                Toast.makeText(this@LoginActivity, "Eroros: " + t.message, Toast.LENGTH_SHORT)
                    .show()
            }

        })
    }
}