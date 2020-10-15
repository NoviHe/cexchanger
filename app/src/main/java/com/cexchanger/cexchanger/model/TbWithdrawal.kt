package com.cexchanger.cexchanger.model

import com.google.gson.annotations.SerializedName

class TbWithdrawal {
    @SerializedName("id_withdrawal")
    var id = 0

    var id_user = 0
    var jumlah_usd = 0
    lateinit var kurs_deposit: String
    lateinit var jumlah_rp: String
    lateinit var jenis_forex: String
    var nomor_akun = 0
    lateinit var nama_akun: String
    lateinit var jenis_bank: String
    lateinit var bank_lain: String
    var no_rekening = 0
    lateinit var atas_nama: String
    lateinit var cabang:String
    lateinit var no_hp: String
    lateinit var status: String
    lateinit var tgl: String
}