package com.cexchanger.cexchanger.app

import com.cexchanger.cexchanger.model.*
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.http.*

interface ApiService {

    @FormUrlEncoded
    @POST("register")
    fun register(
        @Field("username") username: String,
        @Field("password") password: String,
        @Field("repassword") repassword: String,
        @Field("nama_lengkap") nama_lengkap: String,
        @Field("alamat") alamat: String,
        @Field("email") email: String,
        @Field("no_hp") no_hp: String,
        @Field("foto") foto: String
    ): Call<ResponseModel>

    @FormUrlEncoded
    @POST("login")
    fun login(
        @Field("username") username: String,
        @Field("password") password: String
    ): Call<ResponseModel>

    @FormUrlEncoded
    @POST("update-user")
    fun update(
        @Field("id_user") id_user: Int,
        @Field("nama_lengkap") nama_lengkap: String,
        @Field("alamat") alamat: String,
        @Field("email") email: String,
        @Field("no_hp") no_hp: String
    ): Call<ResponseModel>

    @FormUrlEncoded
    @POST("get-user")
    fun getUser(
        @Field("id_user") id_user: Int
    ): Call<ResponseModel>

    @FormUrlEncoded
    @POST("get-deposit")
    fun getDeposit(
        @Field("id_user") id_user: Int
    ): Call<ArrayList<TbDeposit>>

    @FormUrlEncoded
    @POST("get-withdraw")
    fun getWithdrawal(
        @Field("id_user") id_user: Int
    ): Call<ArrayList<TbWithdrawal>>

    @FormUrlEncoded
    @POST("get-balance")
    fun getBalance(
        @Field("id_user") id_user: Int
    ): Call<BalanceResponse>

    @Multipart
    @POST("deposit")
    fun deposit(
        @Part("id_user") id_user: Int,
        @Part("jumlah_usd") jumlah_usd: RequestBody,
        @Part("kurs_deposit") kurs_deposit: RequestBody,
        @Part("jumlah_rp") jumlah_rp: RequestBody,
        @Part("jenis_forex") jenis_forex: RequestBody,
        @Part("nomor_akun") nomor_akun: Int,
        @Part("nama_akun") nama_akun: RequestBody,
        @Part("jenis_bank") jenis_bank: RequestBody,
        @Part("no_rekening") no_rekening: RequestBody,
        @Part("atas_nama") atas_nama: RequestBody,
        @Part("no_hp") no_hp: RequestBody,
        @Part bukti_transfer: MultipartBody.Part
    ): Call<DepositResponse>

    @FormUrlEncoded
    @POST("withdraw")
    fun withdrawal(
        @Field("id_user") id_user: Int,
        @Field("jumlah_usd") jumlah_usd: Int,
        @Field("kurs_deposit") kurs_deposit: String,
        @Field("jumlah_rp") jumlah_rp: String,
        @Field("jenis_forex") jenis_forex: String,
        @Field("nomor_akun") nomor_akun: Int,
        @Field("nama_akun") nama_akun: String,
        @Field("jenis_bank") jenis_bank: String,
        @Field("bank_lain") bank_lain: String,
        @Field("no_rekening") no_rekening: Int,
        @Field("atas_nama") atas_nama: String,
        @Field("cabang") cabang: String,
        @Field("no_hp") no_hp: String
    ): Call<WithdrawResponse>


    @GET("bank")
    fun jenisBank(): Call<ResponBank>

    @GET("kurs-withdraw")
    fun kursWithdraw(): Call<TbKurs>

    @GET("kurs-deposit")
    fun kursDeposit(): Call<TbKurs>


}