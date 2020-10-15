package com.cexchanger.cexchanger.model

class TbKurs {
    var success = 0
    lateinit var message:String
    var data = kurs()

    inner class kurs{
        var id_kurs = 0
        lateinit var rupiah :String
        lateinit var dollar:String
        lateinit var jenis:String
    }
}