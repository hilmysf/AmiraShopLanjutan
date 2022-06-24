package hilmysf.amirashoplanjutan.ui.log.sell

import android.content.Context
import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.firebase.ui.firestore.FirestoreRecyclerAdapter
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import hilmysf.amirashoplanjutan.data.source.entities.SellLogs
import hilmysf.amirashoplanjutan.databinding.ItemLogSellBinding
import hilmysf.amirashoplanjutan.helper.Helper
import hilmysf.amirashoplanjutan.ui.log.sell.DetailSellLogActivity.Companion.SELL_LOGS_BUNDLE


class SellLogAdapter(
    val context: Context?,
    options: FirestoreRecyclerOptions<SellLogs>,
) : FirestoreRecyclerAdapter<SellLogs, SellLogAdapter.SellLogViewHolder?>(options) {

    inner class SellLogViewHolder(val binding: ItemLogSellBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): SellLogViewHolder {
        val binding =
            ItemLogSellBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return SellLogViewHolder(binding)
    }

    override fun onBindViewHolder(holder: SellLogViewHolder, position: Int, model: SellLogs) {
        holder.binding.apply {
            Log.d("adapter", "model: $model")
            var productsNameList = model.hashMapProducts.keys.toList()
            var totalPrice: Long = 0
            model.hashMapProducts.forEach { (_, list) ->
                totalPrice += list[0] as Long
            }
            tvLogDate.text = "${model.date} ${model.time}"
            tvLogMessage.text = "Dijual Oleh ${model.owner}"
            tvProductName.text = Helper.camelCase(productsNameList[0])
            tvTotalPrice.text = "Rp. ${Helper.currencyFormatter(totalPrice)}"
            itemSellLogs.setOnClickListener {
                context?.startActivity(Intent(context, DetailSellLogActivity::class.java).apply {
                    putExtra(SELL_LOGS_BUNDLE, model)
                })
            }
        }
    }
//    private fun setStatusColor(tv: TextView){
//        when(tv.text){
//            "Mengubah" -> {
//                var shapeDrawable =ShapeDrawable.;
//                shapeDrawable.getPaint()
//                    .setColor(ContextCompat.getColor(mContext, R.color.colorToSet));
//                tv.background =
//                    context?.let { ContextCompat.getColor(it, R.color.edit_color).toDrawable() }
//            }
//            "Menambah" -> tv.background =
//                context?.let {
//                    ContextCompat.getColor(it, R.color.add_color).toDrawable()
//                }
//            "Menghapus" -> tv.background =
//                context?.let { ContextCompat.getColor(it, R.color.delete_color).toDrawable() }
//        }
//    }
}