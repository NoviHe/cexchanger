package com.cexchanger.cexchanger.util

import android.app.Activity
import android.content.Context
import android.content.SharedPreferences
import com.cexchanger.cexchanger.model.TbKurs
import com.cexchanger.cexchanger.model.TbUser
import com.google.gson.Gson

class SharedPref(activity: Activity) {
    val mypref = "MAIN_PREF"
    val sp: SharedPreferences
    val login = "login"

    val user = "user"
    val datas = "data"

    init {
        sp = activity.getSharedPreferences(mypref, Context.MODE_PRIVATE)
    }

    fun setStatusLogin(status: Boolean) {
        sp.edit().putBoolean(login, status).apply()
    }

    fun getStatusLogin(): Boolean {
        return sp.getBoolean(login, false)
    }

    fun setUser(value: TbUser) {
        val data = Gson().toJson(value, TbUser::class.java)
        sp.edit().putString(user, data).apply()
    }

    fun getUser(): TbUser? {
        val data = sp.getString(user, null) ?: return null
        val json = Gson().fromJson<TbUser>(data, TbUser::class.java)
        return json
    }

    fun setKurs(value: TbKurs.kurs) {
        val data = Gson().toJson(value, TbKurs.kurs::class.java)
        sp.edit().putString(datas, data).apply()
    }

    fun getKurs(): TbKurs.kurs? {
        val data = sp.getString(datas, null) ?: return null
        val json = Gson().fromJson<TbKurs.kurs>(data, TbKurs.kurs::class.java)
        return json
    }
}