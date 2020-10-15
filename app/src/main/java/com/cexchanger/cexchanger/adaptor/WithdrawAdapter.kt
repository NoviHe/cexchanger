package com.cexchanger.cexchanger.adaptor

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.cexchanger.cexchanger.R
import com.cexchanger.cexchanger.model.TbDeposit
import com.cexchanger.cexchanger.model.TbWithdrawal
import kotlinx.android.synthetic.main.item_history.view.*

class WithdrawAdapter(private val list: ArrayList<TbWithdrawal>):RecyclerView.Adapter<WithdrawAdapter.WithdrawViewHolder>() {
    inner class WithdrawViewHolder(itemView: View) :RecyclerView.ViewHolder(itemView){
        fun bind(data: TbWithdrawal) {
            with(itemView) {
                tv_nama_akun.text = data.nama_akun
                tv_status.text = data.status
                tv_jumlah_rp.text = "$. " + data.jumlah_usd
                tv_jumlah_usd.text = "Rp. " + data.jumlah_rp
                tv_tgl.text = data.tgl
                kondisi.setImageResource(R.drawable.ic_withdraw)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): WithdrawViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_history, parent, false)
        return WithdrawViewHolder(view)
    }

    override fun onBindViewHolder(holder: WithdrawViewHolder, position: Int) {
        holder.bind(list[position])
    }

    override fun getItemCount(): Int = list.size
}