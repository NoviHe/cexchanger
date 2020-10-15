package com.cexchanger.cexchanger

import android.app.Activity
import android.app.AlertDialog
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.ArrayAdapter
import android.widget.Spinner
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.cexchanger.cexchanger.app.ApiConfig
import com.cexchanger.cexchanger.model.DepositResponse
import com.cexchanger.cexchanger.model.TbKurs
import com.cexchanger.cexchanger.model.UploadRequestBody
import com.cexchanger.cexchanger.util.SharedPref
import com.cexchanger.cexchanger.util.getFileName
import com.cexchanger.cexchanger.util.snackbar
import com.google.android.material.appbar.MaterialToolbar
import kotlinx.android.synthetic.main.activity_deposit.*
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.text.NumberFormat
import java.util.*
import kotlin.math.roundToInt

class DepositActivity : AppCompatActivity(), UploadRequestBody.UploadCallBack {
    val SELECT_PICTURE = 1

    lateinit var s: SharedPref
    lateinit var usd: Unit

    private var selectedImage: Uri? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_deposit)
        val toolbar = findViewById<MaterialToolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)
        toolbar.title = "Cexchanger"
        toolbar.subtitle = "Deposit"

        s = SharedPref(this)
        kursDepo()

        val bank = arrayOf("BCA", "MANDIRI", "BRI", "BNI", "BJB")
        val forex = arrayOf("Windsor")

        val spinner1 = findViewById<Spinner>(R.id.depo_jenis_bank)
        val spinner2 = findViewById<Spinner>(R.id.depo_jenis_forex)

        spinner1.adapter = ArrayAdapter(this, R.layout.spinner_input, bank)
        spinner2.adapter = ArrayAdapter(this, R.layout.spinner_input, forex)

        ConvertCurency()


        btn_galeri.setOnClickListener {
//            dispatchGaleryIntent()
            OpenImageChoser()
        }
        btn_depo_winsor.setOnClickListener {
            deposit()
        }

        btn_depo_reset.setOnClickListener {
            et_depo_nomor_akun.setText("")
            et_depo_nama_akun.setText("")
            et_depo_jumlah_rp.setText("")
            et_depo_jumlah_usd.setText("")
            et_depo_no_rekening.setText("")
            et_depo_atas_nama.setText("")
            et_depo_no_hp.setText("")
        }
    }

    private fun OpenImageChoser() {
        Intent(Intent.ACTION_PICK).also {
            it.type = "image/*"
            val mimesTypes = arrayOf("image/jpeg", "image/png")
            it.putExtra(Intent.EXTRA_MIME_TYPES, mimesTypes)
            startActivityForResult(it, REQUEST_CODE_IMAGE_PICKER)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK) {
            when (requestCode) {
                REQUEST_CODE_IMAGE_PICKER -> {
                    selectedImage = data?.data
                    image_res.setImageURI(selectedImage)
                }
            }
        }
    }

    companion object {
        private const val REQUEST_CODE_IMAGE_PICKER = 100
    }

    fun kursDepo() {
        ApiConfig.instanceRetrofit.kursDeposit().enqueue(object : Callback<TbKurs> {
            override fun onResponse(call: Call<TbKurs>, response: Response<TbKurs>) {
                val respon = response.body()!!
                if (respon.success == 1) {
                    s.setKurs(respon.data)
                    et_depo_kurs_deposit.isEnabled = false
                    et_depo_kurs_deposit.setText(respon.data.rupiah).toString()
                }
            }

            override fun onFailure(call: Call<TbKurs>, t: Throwable) {

            }

        })
    }

    fun ConvertCurency() {
        et_depo_jumlah_usd.isEnabled = false
        var current = ""
        et_depo_jumlah_rp.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                val getRp = et_depo_jumlah_rp.text.toString()
                val kursRupiah = s.getKurs()!!.rupiah
                et_depo_jumlah_rp_vis.setText(getRp.replace(".", ""))

                val getRpVis = et_depo_jumlah_rp_vis.text.toString()
                val rp = getRpVis.toDoubleOrNull()
                val res = rp?.div(kursRupiah.toDouble())
                if (res == null) {
                    et_depo_jumlah_usd.setText("")
                } else {
                    et_depo_jumlah_usd.setText(res.roundToInt().toString())
                }
            }

            override fun afterTextChanged(p0: Editable?) {
                if (p0.toString() != current) {
                    et_depo_jumlah_rp.removeTextChangedListener(this)
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
                    et_depo_jumlah_rp.setText(clean)
                    et_depo_jumlah_rp.setSelection(clean.length)
                    et_depo_jumlah_rp.addTextChangedListener(this)
                }
            }

        })
    }

    fun rupiah(number: Double): String {
        val localeID = Locale("in", "ID")
        val formatNumber = NumberFormat.getCurrencyInstance(localeID)
        return formatNumber.format(number).toString()
    }

    fun deposit() {
        if (et_depo_nomor_akun.text.isEmpty()) {
            et_depo_nomor_akun.error = "Nama Lengkap tidak boleh kosong"
            et_depo_nomor_akun.requestFocus()
            return
        } else if (et_depo_nama_akun.text.isEmpty()) {
            et_depo_nama_akun.error = "Email tidak boleh kosong"
            et_depo_nama_akun.requestFocus()
            return
        } else if (et_depo_jumlah_rp.text.isEmpty()) {
            et_depo_jumlah_rp.error = "Alamat tidak boleh kosong"
            et_depo_jumlah_rp.requestFocus()
            return
        } else if (et_depo_jumlah_usd.text.isEmpty()) {
            et_depo_jumlah_usd.error = "No HP tidak boleh kosong"
            et_depo_jumlah_usd.requestFocus()
            return
        } else if (et_depo_no_rekening.text.isEmpty()) {
            et_depo_no_rekening.error = "No HP tidak boleh kosong"
            et_depo_no_rekening.requestFocus()
            return
        } else if (et_depo_atas_nama.text.isEmpty()) {
            et_depo_atas_nama.error = "No HP tidak boleh kosong"
            et_depo_atas_nama.requestFocus()
            return
        } else if (et_depo_no_hp.text.isEmpty()) {
            et_depo_no_hp.error = "No HP tidak boleh kosong"
            et_depo_no_hp.requestFocus()
            return
        } else if (selectedImage == null) {
            root_layout.snackbar("Pilih Bukti")
            return
        }

        val parcelFileDescriptor = contentResolver.openFileDescriptor(
            selectedImage!!,
            "r",
            null
        ) ?: return
        val inputStream = FileInputStream(parcelFileDescriptor.fileDescriptor)
        val file = File(cacheDir, contentResolver.getFileName(selectedImage!!))
        val outputStream = FileOutputStream(file)
        inputStream.copyTo(outputStream)

        progress_bar.progress = 0
        val body = UploadRequestBody(file, "image", this)


        loadingDepo.visibility = View.VISIBLE
        val idUser = s.getUser()!!.id_user
        ApiConfig.instanceRetrofit.deposit(
            id_user = idUser,
            RequestBody.create(
                MediaType.parse("multipart/form-data"), et_depo_jumlah_usd.text.toString().replace(
                    ",",
                    "."
                )
            ),
            RequestBody.create(
                MediaType.parse("multipart/form-data"),
                et_depo_kurs_deposit.text.toString()
            ),
            RequestBody.create(
                MediaType.parse("multipart/form-data"), et_depo_jumlah_rp.text.toString()
            ),
            RequestBody.create(
                MediaType.parse("multipart/form-data"),
                depo_jenis_forex.getSelectedItem().toString()
            ),
            et_depo_nomor_akun.text.toString().toInt(),
            RequestBody.create(
                MediaType.parse("multipart/form-data"),
                et_depo_nama_akun.text.toString()
            ),
            RequestBody.create(
                MediaType.parse("multipart/form-data"),
                depo_jenis_bank.getSelectedItem().toString()
            ),
            RequestBody.create(
                MediaType.parse("multipart/form-data"),
                et_depo_no_rekening.text.toString()
            ),
            RequestBody.create(
                MediaType.parse("multipart/form-data"),
                et_depo_atas_nama.text.toString()
            ),
            RequestBody.create(
                MediaType.parse("multipart/form-data"),
                et_depo_no_hp.text.toString()
            ),
            MultipartBody.Part.createFormData("bukti_transfer", file.name, body)
        ).enqueue(object : Callback<DepositResponse> {
            override fun onResponse(
                call: Call<DepositResponse>,
                response: Response<DepositResponse>
            ) {
                loadingDepo.visibility = View.GONE
                val respon = response.body()!!
                if (respon.success == 1) {
                    val builder = AlertDialog.Builder(this@DepositActivity)
                    builder.setTitle("Deposit Berhasil")
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
                    root_layout.snackbar(respon.message)
                    Toast.makeText(
                        this@DepositActivity,
                        "Eroros: " + respon.message,
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }

            override fun onFailure(call: Call<DepositResponse>, t: Throwable) {
                loadingDepo.visibility = View.GONE
                val builder = AlertDialog.Builder(this@DepositActivity)
                builder.setTitle("Deposit Gagal")
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

    override fun onPrgresUpdate(percentage: Int) {
        progress_bar.progress = percentage
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }
}