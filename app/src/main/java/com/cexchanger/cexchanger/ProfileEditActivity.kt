package com.cexchanger.cexchanger

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.EditText
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.cexchanger.cexchanger.app.ApiConfig
import com.cexchanger.cexchanger.model.ResponseModel
import com.cexchanger.cexchanger.model.TbUser
import com.cexchanger.cexchanger.util.SharedPref
import com.google.android.material.appbar.MaterialToolbar
import kotlinx.android.synthetic.main.activity_profile_edit.*
import kotlinx.android.synthetic.main.activity_register.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ProfileEditActivity : AppCompatActivity() {

    lateinit var s: SharedPref

//    lateinit var etNamaLengkap: EditText
//    lateinit var etEmail: EditText
//    lateinit var etAlamat: EditText
//    lateinit var etNoHP: EditText
    lateinit var loadingProg: ProgressBar

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile_edit)
        val toolbar = findViewById<MaterialToolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)
        toolbar.title = "Cexchanger"
        toolbar.subtitle = "Setting"

        loadingProg = findViewById(R.id.loading)

        s = SharedPref(this)

        setData()

        btn_save.setOnClickListener {
            update()
        }

    }

    fun setData() {
        if (s.getUser() == null) {
            val intent = Intent(this, LoginActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
            startActivity(intent)
            return
        }
        val user = s.getUser()!!

        ApiConfig.instanceRetrofit.getUser(user.id_user).enqueue(object :Callback<ResponseModel> {
            override fun onResponse(call: Call<ResponseModel>, response: Response<ResponseModel>) {
                val respon = response.body()!!
                et_edit_email.setText(respon.data.email)
                et_edit_nama.setText(respon.data.nama_lengkap)
                et_edit_alamat.setText(respon.data.alamat)
                et_edit_phone.setText(respon.data.no_hp)

                loadingEdit.visibility = View.GONE
            }

            override fun onFailure(call: Call<ResponseModel>, t: Throwable) {
                loadingEdit.visibility = View.GONE
                Toast.makeText(this@ProfileEditActivity, "Eroros: " + t.message, Toast.LENGTH_SHORT)
                    .show()
            }
        })

    }

    fun update() {
        if (et_edit_nama.text.isEmpty()) {
            et_edit_nama.error = "Nama Lengkap tidak boleh kosong"
            et_edit_nama.requestFocus()
            return
        } else if (et_edit_email.text.isEmpty()) {
            et_edit_email.error = "Email tidak boleh kosong"
            et_edit_email.requestFocus()
            return
        } else if (et_edit_alamat.text.isEmpty()) {
            et_edit_alamat.error = "Alamat tidak boleh kosong"
            et_edit_alamat.requestFocus()
            return
        } else if (et_edit_phone.text.isEmpty()) {
            et_edit_phone.error = "No HP tidak boleh kosong"
            et_edit_phone.requestFocus()
            return
        }
        loadingProg.visibility = View.VISIBLE
        val idUser = s.getUser()!!.id_user

        ApiConfig.instanceRetrofit.update(
            idUser,
            et_edit_nama.text.toString(),
            et_edit_alamat.text.toString(),
            et_edit_email.text.toString(),
            et_edit_phone.text.toString(),
        ).enqueue(object : Callback<ResponseModel> {
            override fun onResponse(call: Call<ResponseModel>, response: Response<ResponseModel>) {
                loadingProg.visibility = View.GONE
                val respon = response.body()!!
                if (respon.success == 1) {
                    s.setUser(respon.data)
                    val intent = Intent(this@ProfileEditActivity, MainActivity::class.java)
                    startActivity(intent)
                    finish()
                    Toast.makeText(
                        this@ProfileEditActivity,
                        "Update Sukses ",
                        Toast.LENGTH_SHORT
                    ).show()
                } else {
                    Toast.makeText(
                        this@ProfileEditActivity,
                        "Eroros: " + respon.message,
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }

            override fun onFailure(call: Call<ResponseModel>, t: Throwable) {
                loadingProg.visibility = View.GONE
                Toast.makeText(this@ProfileEditActivity, "Eroros: " + t.message, Toast.LENGTH_SHORT)
                    .show()
            }

        })
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }

}