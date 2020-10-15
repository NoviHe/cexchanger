package com.cexchanger.cexchanger.model

import com.google.gson.annotations.SerializedName

class TbDeposit {
    @SerializedName("id_deposit")
    var id = 0

    var id_user = 0
    lateinit var jumlah_usd: String
    lateinit var kurs_deposit: String
    lateinit var jumlah_rp: String
    lateinit var jenis_forex: String
    var nomor_akun = 0
    lateinit var nama_akun: String
    lateinit var jenis_bank: String
    lateinit var no_rekening: String
    lateinit var atas_nama: String
    lateinit var no_hp: String
    lateinit var tgl: String
    lateinit var status: String
    lateinit var bukti_transfer: String
}