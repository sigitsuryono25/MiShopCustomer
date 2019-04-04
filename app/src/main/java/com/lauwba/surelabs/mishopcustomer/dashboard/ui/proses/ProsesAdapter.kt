package com.lauwba.surelabs.mishopcustomer.dashboard.ui.proses


import android.annotation.SuppressLint
import android.content.Context
import android.support.v7.widget.CardView
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.lauwba.surelabs.mishopcustomer.MiCarJekXpress.CarBikeBooking.CarBikeBooking
import com.lauwba.surelabs.mishopcustomer.R
import com.lauwba.surelabs.mishopcustomer.config.Constant
import com.lauwba.surelabs.mishopcustomer.config.HourToMillis
import com.lauwba.surelabs.mishopcustomer.dashboard.model.ProsesModel
import com.lauwba.surelabs.mishopcustomer.libs.ChangeFormat
import com.lauwba.surelabs.mishopcustomer.service.model.ItemPostService
import com.lauwba.surelabs.mishopcustomer.shop.markershop.MarkerShop
import com.lauwba.surelabs.mishopcustomer.shop.model.ItemPost
import com.lauwba.surelabs.mishopcustomer.shop.model.ShopOrderModel
import kotlinx.android.synthetic.main.item_proses.view.*
import org.jetbrains.anko.sdk27.coroutines.onClick
import org.jetbrains.anko.startActivity

class ProsesAdapter(
    private val mValues: MutableList<ProsesModel>?,
    private val i: Int
) : RecyclerView.Adapter<ProsesAdapter.ViewHolder>() {

    var color = intArrayOf(R.color.mishop, R.color.micar, R.color.mibike, R.color.miexpress, R.color.miservice)
    val statusDel = arrayListOf("Diterima", "Dipesankan", "Sedang Dijalan", "Batal", "Selesai")
    val statusCarBikeExpress = arrayListOf("Posting", "Diambil customer/mitra", "Tiba", "Sampai/selesai", "Batal")
    private var c: Context? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_proses, parent, false)
        c = parent.context
        return ViewHolder(view)
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        try {
            val item = mValues?.get(position)
            holder.tanggalTransaksi.text = item?.tanggalOrder?.let { HourToMillis.millisToDate(it) }
            holder.nominal.text = "Rp. " + ChangeFormat.toRupiahFormat2(item?.price_shop.toString())
            holder.jenis.background = c?.resources?.getDrawable(color[i])
            when (i) {
                0 -> {
                    holder.shopDetail.visibility = View.VISIBLE
                    getDetail(item, holder)
                    holder.detail.onClick {
                        c?.startActivity<MarkerShop>("idOrder" to item?.idOrder)
                    }
                }
                1 -> {
                    holder.carBikeExpress.visibility = View.VISIBLE
                    getDetailCarBikExpress(item, holder, Constant.TB_CAR)
                }
                2 -> {
                    holder.carBikeExpress.visibility = View.VISIBLE
                    getDetailCarBikExpress(item, holder, Constant.TB_BIKE)
                }
                3 -> {
                    holder.carBikeExpress.visibility = View.VISIBLE
                    holder.barangContainer.visibility = View.VISIBLE
                    getDetailCarBikExpress(item, holder, Constant.TB_EXPRESS)
                }
                4 -> {
                    holder.serviceDetail.visibility = View.VISIBLE
                    getDetailService(item, holder)
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun getDetail(
        item: ProsesModel?,
        holder: ViewHolder
    ) {
        val statusOrder = item?.status_order_shop
        val idOrder = item?.idOrder
        holder.status.text = statusOrder?.let { statusDel.get(it) }
        val ref = Constant.database.getReference(Constant.TB_SHOP)
        val order = Constant.database.getReference(Constant.TB_SHOP_ORDER)
        ref.orderByChild("idOrder").equalTo(idOrder)
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onCancelled(p0: DatabaseError) {

                }

                override fun onDataChange(p0: DataSnapshot) {
                    try {
                        for (issues in p0.children) {
                            val detailShop = issues.getValue(ItemPost::class.java)

                            holder.isiPesanan.text = detailShop?.deskripsi
                            val hargaShow = detailShop?.kenaikan?.let { detailShop.harga?.plus(it) }
                            holder.nominal.text = ChangeFormat.toRupiahFormat2(hargaShow.toString())
                            holder.ongkir.text = ChangeFormat.toRupiahFormat2(detailShop?.ongkos.toString())
                            order.orderByChild("id_order_shop").equalTo(item?.id_order_shop)
                                .addListenerForSingleValueEvent(object : ValueEventListener {
                                    override fun onCancelled(p0: DatabaseError) {

                                    }

                                    override fun onDataChange(p0: DataSnapshot) {
                                        try {
                                            for (issue in p0.children) {
                                                val data = issue.getValue(ShopOrderModel::class.java)
                                                holder.jumlahPesanan.text = data?.qty.toString()
                                                val harga = ChangeFormat.clearRp(holder.nominal.text.toString()).toInt()
                                                val ongkir = ChangeFormat.clearRp(holder.ongkir.text.toString()).toInt()
                                                val jumlah = holder.jumlahPesanan.text.toString().toInt()
                                                val total = harga.times(jumlah).plus(ongkir)
                                                holder.totalAkhir.text = ChangeFormat.toRupiahFormat2(total.toString())
                                            }
                                        } catch (e: Exception) {
                                            e.printStackTrace()
                                        }
                                    }
                                })
                        }
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }
            })
    }

    private fun getDetailService(
        item: ProsesModel?,
        holder: ViewHolder
    ) {
        val idOrder = item?.idOrderService
        val ref = Constant.database.getReference(Constant.TB_SERVICE_ORDER)
        ref.orderByChild("idOrderService").equalTo(idOrder)
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onCancelled(p0: DatabaseError) {

                }

                override fun onDataChange(p0: DataSnapshot) {
                    try {
                        for (issue in p0.children) {
                            val detailService = issue.getValue(ItemPostService::class.java)
                            val idOrder = detailService?.idOrder
                            Constant.database.getReference(Constant.TB_SERVICE).orderByChild("idOrder").equalTo(idOrder)
                                .addListenerForSingleValueEvent(object : ValueEventListener {
                                    override fun onCancelled(p0: DatabaseError) {

                                    }

                                    override fun onDataChange(p0: DataSnapshot) {
                                        for (issues in p0.children) {
                                            val service = issues.getValue(ItemPostService::class.java)
                                            holder.biayaJasa.text =
                                                ChangeFormat.toRupiahFormat2(service?.ship_service.toString())
                                            holder.nominalService.text =
                                                ChangeFormat.toRupiahFormat2(service?.harga.toString())
                                            holder.deskripsiService.text = service?.deskripsi
                                            holder.isiLayanan.text = service?.namaService

                                            val biaya = service?.ship_service
                                            val harga = service?.harga
                                            val total = harga?.let { biaya?.plus(it) }
                                            holder.totalAkhir.text = ChangeFormat.toRupiahFormat2(total.toString())
                                            val jadwal = "Terjadwal pada : " + detailService?.jadwal?.let {
                                                HourToMillis.millisToDateHour(it)
                                            }
                                            holder.status.text = jadwal
                                            holder.tanggalTransaksi.text = detailService?.tanggal_order?.let {
                                                HourToMillis.millisToDate(
                                                    it
                                                )
                                            }
                                        }
                                    }
                                })


                        }
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }
            })
    }

    private fun getDetailCarBikExpress(
        item: ProsesModel?,
        holder: ViewHolder,
        get: String
    ) {
        val idOrder = item?.idOrder
        val ref = Constant.database.getReference(get)
        ref.orderByChild("idOrder").equalTo(idOrder)
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onCancelled(p0: DatabaseError) {

                }

                override fun onDataChange(p0: DataSnapshot) {
                    try {
                        for (issues in p0.children) {
                            val detail = issues.getValue(CarBikeBooking::class.java)
                            holder.tanggalTransaksi.text = detail?.tanggal?.let { HourToMillis.millisToDate(it) }
                            holder.lokasi.text = detail?.lokasiAwal
                            holder.tujuan.text = detail?.lokasiTujuan
                            holder.namaBarang.text = detail?.namaBarang
                            holder.hargaCarBikeExpress.text = ChangeFormat.toRupiahFormat2(detail?.harga.toString())
                            holder.status.text = detail?.status?.let { statusCarBikeExpress.get(it) }
                            holder.totalAkhir.text = ChangeFormat.toRupiahFormat2(detail?.harga.toString())
                        }
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }
            })
    }

    override fun getItemCount(): Int = mValues?.size ?: 0

    inner class ViewHolder(mView: View) : RecyclerView.ViewHolder(mView) {
        val detail: CardView = mView.detail

        val serviceDetail: LinearLayout = mView.serviceDetail
        val shopDetail: LinearLayout = mView.shopDetail
        val carBikeExpress: LinearLayout = mView.carBikeExpress
        val barangContainer: LinearLayout = mView.barangContainer

        val tanggalTransaksi: TextView = mView.tanggalTransaksi
        val keterangan: TextView = mView.keterangan
        val jenis: View = mView.jenis
        val status: TextView = mView.status
        val isiPesanan: TextView = mView.isiPesanan
        val jumlahPesanan: TextView = mView.jumlahPesanan
        val nominal: TextView = mView.nominal
        val totalAkhir: TextView = mView.totalAkhir
        val ongkir: TextView = mView.ongkir


        val lokasi: TextView = mView.lokasi
        val tujuan: TextView = mView.tujuan
        val hargaCarBikeExpress: TextView = mView.hargaCarBikeExpress
        val namaBarang: TextView = mView.namaBarang


        val isiLayanan: TextView = mView.isiLayanan
        val deskripsiService: TextView = mView.deskripsiService
        val nominalService: TextView = mView.nominalService
        val biayaJasa: TextView = mView.biayaJasa
    }
}