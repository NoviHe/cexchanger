package com.cexchanger.cexchanger.adaptor

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.cexchanger.cexchanger.R
import com.cexchanger.cexchanger.model.DepositResponse
import com.cexchanger.cexchanger.model.TbDeposit
import kotlinx.android.synthetic.main.item_history.view.*

class DepositAdapter(private val list: ArrayList<TbDeposit>) :
    RecyclerView.Adapter<DepositAdapter.DepositViewHolder>() {
    inner class DepositViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bind(tbDeposit:TbDeposit) {
            with(itemView) {
                tv_nama_akun.text = tbDeposit.nama_akun
                tv_status.text = tbDeposit.status
                tv_jumlah_rp.text = "Rp. " + tbDeposit.jumlah_rp
                tv_jumlah_usd.text = "$. " + tbDeposit.jumlah_usd
                tv_tgl.text = tbDeposit.tgl
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DepositViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_history, parent, false)
        return DepositViewHolder(view)
    }

    override fun onBindViewHolder(holder: DepositViewHolder, position: Int) {
        holder.bind(list[position])
    }

    override fun getItemCount(): Int = list.size
}