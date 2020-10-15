package com.cexchanger.cexchanger

import android.app.AlertDialog
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.AdapterView
import android.widget.AdapterView.OnItemSelectedListener
import android.widget.ArrayAdapter
import android.widget.Spinner
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.cexchanger.cexchanger.app.ApiConfig
import com.cexchanger.cexchanger.model.ResponBank
import com.cexchanger.cexchanger.model.TbKurs
import com.cexchanger.cexchanger.model.TbMasterbank
import com.cexchanger.cexchanger.model.WithdrawResponse
import com.cexchanger.cexchanger.util.SharedPref
import com.cexchanger.cexchanger.util.snackbar
import com.google.android.material.appbar.MaterialToolbar
import kotlinx.android.synthetic.main.activity_deposit.*
import kotlinx.android.synthetic.main.activity_withdraw.*
import kotlinx.android.synthetic.main.activity_withdraw.root_layout
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.text.NumberFormat
import java.util.*
import kotlin.collections.ArrayList


class WithdrawActivity : AppCompatActivity() {
    lateinit var s: SharedPref
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_withdraw)
        val toolbar = findViewById<MaterialToolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)
        toolbar.title = "Cexchanger"
        toolbar.subtitle = "Withdrawal"

        s = SharedPref(this)
        kursWD()
//        val kursRupiah = s.getKurs()!!.rupiah
//        et_wd_dollar.setText(kursRupiah).toString()
        val bank = arrayOf("BCA", "MANDIRI", "BNI", "BJB", "BRI", "BNI", "Bank Lainnya")
        val spinner1 = findViewById<Spinner>(R.id.wd_jenis_bank)
        spinner1.adapter = ArrayAdapter(this, R.layout.spinner_input, bank)

        val forex = arrayOf("Windsor")
        val spinner2 = findViewById<Spinner>(R.id.wd_jenis_forex)
        spinner2.adapter = ArrayAdapter(this, R.layout.spinner_input, forex)


//        getBank()
//
        onselected()

        ConvertCurency()

        btn_wd_winsor.setOnClickListener {
            withdraw()
        }

        btnReset()
    }

    fun onselected() {
        wd_jenis_bank.onItemSelectedListener = object : OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>,
                view: View,
                position: Int,
                id: Long
            ) {
                val selectedName = parent.getItemAtPosition(position).toString()

                if (selectedName == "Bank Lainnya") {
                    et_bank_lainnya.visibility = View.VISIBLE
                } else {
                    et_bank_lainnya.visibility = View.GONE
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }
    }

    fun kursWD() {
        ApiConfig.instanceRetrofit.kursWithdraw().enqueue(object : Callback<TbKurs> {
            override fun onResponse(call: Call<TbKurs>, response: Response<TbKurs>) {
                val respon = response.body()!!
                if (respon.success == 1) {
                    s.setKurs(respon.data)
                    et_wd_dollar.isEnabled = false
                    et_wd_dollar.setText(respon.data.rupiah).toString()
                }
            }

            override fun onFailure(call: Call<TbKurs>, t: Throwable) {

            }

        })
    }

    fun btnReset() {
        btn_wd_reset.setOnClickListener {
            et_wd_nomor_akun.setText("")
            et_wd_nama_akun.setText("")
            et_wd_jumlah_rp.setText("")
            et_wd_jumlah_usd.setText("")
            et_wd_no_rekening.setText("")
            et_wd_atas_nama.setText("")
            et_wd_no_hp.setText("")
            et_wd_cabang.setText("")
        }
    }

    fun getBank() {
        ApiConfig.instanceRetrofit.jenisBank().enqueue(object : Callback<ResponBank> {
            override fun onResponse(call: Call<ResponBank>, response: Response<ResponBank>) {
                val respon = response.body()!!
                if (respon.success == 1) {
                    val bankItems = listOf(respon.data)
                    val listSpinner: MutableList<String> = ArrayList()
                    for (i in bankItems.indices) {
                        listSpinner.add(bankItems[i].nama_bank)
                    }

                    val adapter = ArrayAdapter(
                        this@WithdrawActivity,
                        android.R.layout.simple_spinner_item, listSpinner
                    )
                    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                    wd_jenis_bank.setAdapter(adapter)
                } else {
                    Toast.makeText(
                        this@WithdrawActivity,
                        "Eroros: " + respon.message,
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }

            override fun onFailure(call: Call<ResponBank>, t: Throwable) {
                t.message?.let { root_layout.snackbar(it) }
                Toast.makeText(
                    this@WithdrawActivity,
                    "Eroros: " + t.message,
                    Toast.LENGTH_SHORT
                ).show()
            }

        })
    }

    fun ConvertCurency() {
        et_wd_jumlah_rp.isEnabled = false
        var current = ""
        et_wd_jumlah_usd.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                val getUsd = et_wd_jumlah_usd.text.toString()

                val kursRupiah = s.getKurs()!!.rupiah

                val usd = getUsd.replace(".", "").toIntOrNull()
                val res = usd?.times(kursRupiah.toInt())
                if (res == null) {
                    et_wd_jumlah_rp.setText("")
                } else {
                    et_wd_jumlah_rp.setText(res.toString())
                }
            }

            override fun afterTextChanged(p0: Editable?) {
                if (p0.toString() != current) {
                    et_wd_jumlah_usd.removeTextChangedListener(this)
                    val local = Locale("id", "id")
                    val replaceable = String.format(
                        "[Rp,.\\s]",
                        NumberFormat.getCurrencyInstance().currency
                            .getSymbol(local)
                    )
                    val cleanString = p0.toString().replace(
                        replaceable.toRegex(),
                        ""
                    )
                    val parsed: Double
                    parsed = try {
                        cleanString.toDouble()
                    } catch (e: NumberFormatException) {
                        0.00
                    }
                    val formatter = NumberFormat
                        .getCurrencyInstance(local)
                    formatter.maximumFractionDigits = 0
                    formatter.isParseIntegerOnly = true
                    val formatted = formatter.format(parsed)
                    val replace = String.format(
                        "[Rp\\s]",
                        NumberFormat.getCurrencyInstance().currency
                            .getSymbol(local)
                    )
                    val clean = formatted.replace(replace.toRegex(), "")
                    current = formatted
                    et_wd_jumlah_usd.setText(clean)
                    et_wd_jumlah_usd.setSelection(clean.length)
                    et_wd_jumlah_usd.addTextChangedListener(this)
                }
            }

        })
    }


    fun withdraw() {
        if (et_wd_nomor_akun.text.isEmpty()) {
            et_wd_nomor_akun.error = "Nama Lengkap tidak boleh kosong"
            et_wd_nomor_akun.requestFocus()
            return
        } else if (et_wd_nama_akun.text.isEmpty()) {
            et_wd_nama_akun.error = "Email tidak boleh kosong"
            et_wd_nama_akun.requestFocus()
            return
        } else if (et_wd_jumlah_rp.text.isEmpty()) {
            et_wd_jumlah_rp.error = "Alamat tidak boleh kosong"
            et_wd_jumlah_rp.requestFocus()
            return
        } else if (et_wd_jumlah_usd.text.isEmpty()) {
            et_wd_jumlah_usd.error = "No HP tidak boleh kosong"
            et_wd_jumlah_usd.requestFocus()
            return
        } else if (et_wd_no_rekening.text.isEmpty()) {
            et_wd_no_rekening.error = "No HP tidak boleh kosong"
            et_wd_no_rekening.requestFocus()
            return
        } else if (et_wd_atas_nama.text.isEmpty()) {
            et_wd_atas_nama.error = "No HP tidak boleh kosong"
            et_wd_atas_nama.requestFocus()
            return
        } else if (et_wd_no_hp.text.isEmpty()) {
            et_wd_no_hp.error = "No HP tidak boleh kosong"
            et_wd_no_hp.requestFocus()
            return
        } else if (et_wd_cabang.text.isEmpty()) {
            et_wd_cabang.error = "No HP tidak boleh kosong"
            et_wd_cabang.requestFocus()
            return
        }
        loadingWD.visibility = View.VISIBLE
        val idUser = s.getUser()!!.id_user

        ApiConfig.instanceRetrofit.withdrawal(
            idUser,
            et_wd_jumlah_usd.text.toString().replace(".", "").toInt(),
            et_wd_dollar.text.toString(),
            et_wd_jumlah_rp.text.toString().replace(",", "."),
            wd_jenis_forex.getSelectedItem().toString(),
            et_wd_nomor_akun.text.toString().toInt(),
            et_wd_nama_akun.text.toString(),
            wd_jenis_bank.getSelectedItem().toString(),
            et_bank_lainnya.text.toString(),
            et_wd_no_rekening.text.toString().toInt(),
            et_wd_atas_nama.text.toString(),
            et_wd_cabang.text.toString(),
            et_wd_no_hp.text.toString()
        ).enqueue(object : Callback<WithdrawResponse> {
            override fun onResponse(
                call: Call<WithdrawResponse>,
                response: Response<WithdrawResponse>
            ) {
                loadingWD.visibility = View.GONE
                val respon = response.body()!!
                if (respon.success == 1) {
                    val builder = AlertDialog.Builder(this@WithdrawActivity)
                    builder.setTitle("Withdrawal Berhasil")
                    builder.setMessage(respon.message)

                    builder.setPositiveButton(android.R.string.yes) { dialog, which ->
                        Toast.makeText(
                            applicationContext,
                            android.R.string.yes, Toast.LENGTH_SHORT
                        ).show()
                        finish()
                    }
                    builder.show()
                } else {
                    Toast.makeText(
                        this@WithdrawActivity,
                        "Eroros: " + respon.message,
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }

            override fun onFailure(call: Call<WithdrawResponse>, t: Throwable) {
                loadingWD.visibility = View.GONE
                val builder = AlertDialog.Builder(this@WithdrawActivity)
                builder.setTitle("Withdrawal Gagal")
                builder.setMessage("Error :" + t.message)

                builder.setPositiveButton(android.R.string.yes) { dialog, which ->
                    Toast.makeText(
                        applicationContext,
                        android.R.string.yes, Toast.LENGTH_SHORT
                    ).show()
                    finish()
                }
                builder.show()
            }

        })
    }


    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }
}