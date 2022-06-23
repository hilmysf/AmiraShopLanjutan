package hilmysf.amirashoplanjutan.ui.log

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.firebase.ui.firestore.FirestoreRecyclerAdapter
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import hilmysf.amirashoplanjutan.data.source.entities.SellLogs
import hilmysf.amirashoplanjutan.databinding.ItemLogSellBinding


class SellLogAdapter(
    val context: Context?,
    options: FirestoreRecyclerOptions<SellLogs>,
) : FirestoreRecyclerAdapter<SellLogs, SellLogAdapter.SellLogViewHolder?>(options) {

    inner class SellLogViewHolder(val binding: ItemLogSellBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): SellLogAdapter.SellLogViewHolder {
        val binding =
            ItemLogSellBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return SellLogViewHolder(binding)
    }

    override fun onBindViewHolder(holder: SellLogViewHolder, position: Int, model: SellLogs) {
        holder.binding.apply {
            var productsName = ""
            var totalPrice : Long = 0
            model.arrayProducts[0].forEach { (name, list) ->
                productsName = name
                totalPrice += list[0] as Long
            }
            tvProductName.text = productsName
            tvTotalPrice.text = totalPrice.toString()
            itemSellLogs.setOnClickListener {
//                context.startActivity(Intent(context, ))
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