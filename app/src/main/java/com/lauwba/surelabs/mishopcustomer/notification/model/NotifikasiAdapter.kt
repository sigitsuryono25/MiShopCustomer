package com.lauwba.surelabs.mishopcustomer.notification.model


import android.content.Context
import android.support.v7.widget.CardView
import android.support.v7.widget.RecyclerView
import android.view.ContextMenu
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import com.lauwba.surelabs.mishopcustomer.MiCarJekXpress.MiBikeActivity
import com.lauwba.surelabs.mishopcustomer.MiCarJekXpress.MiCarActivity
import com.lauwba.surelabs.mishopcustomer.MiCarJekXpress.MiXpressActivity
import com.lauwba.surelabs.mishopcustomer.R
import com.lauwba.surelabs.mishopcustomer.libs.ChangeFormat
import com.lauwba.surelabs.mishopcustomer.shop.detail.DetailMiShopActivity
import kotlinx.android.synthetic.main.notifikasi_item.view.*
import org.jetbrains.anko.sdk27.coroutines.onClick
import org.jetbrains.anko.startActivity

class NotifikasiAdapter(
    private val mValues: MutableList<NotifikasiItem>, private val c: Context?
) : RecyclerView.Adapter<NotifikasiAdapter.ViewHolder>(), View.OnCreateContextMenuListener {


    override fun onCreateContextMenu(menu: ContextMenu?, v: View?, menuInfo: ContextMenu.ContextMenuInfo?) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    var color = intArrayOf(R.color.mishop, R.color.micar, R.color.mijek, R.color.miexpress, R.color.miservice)
    var jenis = arrayListOf("Mi-Shop", "Mi-Car", "Mi-Bike", "Mi-Express", "Mi-Service")

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.notifikasi_item, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = mValues[position]
        holder.type.background = c?.resources?.getDrawable(color[item.type ?: 0])
        holder.orderNumber.text = item.idOrder
        holder.deskripsi.text = item.deskripsi
        holder.typeJassa.text = jenis[item.type ?: 0]
//        holder.more.setOnCreateContextMenuListener(this)

        if (item.type == 0) {
            val harga = item.kenaikan?.let { item.ongkos?.let { it1 -> item.harga?.plus(it)?.plus(it1) } }
            holder.harga.text =
                "Rp. " + ChangeFormat.toRupiahFormat2(harga.toString())
            holder.harga.visibility = View.VISIBLE
            holder.ambil.visibility = View.GONE
            holder.tolak.visibility = View.GONE
            holder.detail.visibility = View.VISIBLE
            holder.detail.onClick {
                //            c?.toast(holder.orderNumber.text)
                c?.startActivity<DetailMiShopActivity>("idOrder" to holder.orderNumber.text.toString())
            }
        } else if (item.type == 1) {
            holder.ambil.visibility = View.GONE
            holder.tolak.visibility = View.GONE
            holder.detail.visibility = View.VISIBLE
            holder.detail.onClick {
                c?.startActivity<MiCarActivity>("idOrder" to holder.orderNumber.text.toString())
            }
        } else if (item.type == 2) {
            holder.ambil.visibility = View.GONE
            holder.tolak.visibility = View.GONE
            holder.detail.visibility = View.VISIBLE
            holder.detail.onClick {
                c?.startActivity<MiBikeActivity>("idOrder" to holder.orderNumber.text.toString())
            }
        } else if (item.type == 3) {
            holder.ambil.visibility = View.GONE
            holder.tolak.visibility = View.GONE
            holder.detail.visibility = View.VISIBLE
            holder.detail.onClick {
                c?.startActivity<MiXpressActivity>("idOrder" to holder.orderNumber.text.toString())
            }
        } else if (item.type == 4) {
            holder.harga.text = "Rp. " + ChangeFormat.toRupiahFormat2(item.harga.toString())
            holder.harga.visibility = View.VISIBLE
            holder.ambil.visibility = View.GONE
            holder.tolak.visibility = View.GONE
            holder.detail.visibility = View.VISIBLE

        }
    }

    override fun getItemCount(): Int = mValues.size


    inner class ViewHolder(mView: View) : RecyclerView.ViewHolder(mView) {
        val type: View = mView.type
        val content: CardView = mView.container
        val orderNumber: TextView = mView.orderNumber
        val deskripsi: TextView = mView.deskripsi
        val typeJassa: TextView = mView.typeJasa
        val harga: TextView = mView.harga

        val ambil: TextView = mView.ambil
        val tolak: TextView = mView.tolak
        val more: ImageView = mView.more
        val detail: TextView = mView.detail


    }
}
