package hilmysf.amirashoplanjutan.ui.log.sell

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import hilmysf.amirashoplanjutan.databinding.ItemLogSellDetailBinding

class DetailSellLogAdapter(private val productList: ArrayList<HashMap<String, ArrayList<Any>>>) :
    RecyclerView.Adapter<DetailSellLogAdapter.DetailSellLogViewHolder>() {
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): DetailSellLogAdapter.DetailSellLogViewHolder {
        val binding =
            ItemLogSellDetailBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return DetailSellLogViewHolder(binding)
    }

    inner class DetailSellLogViewHolder(val binding: ItemLogSellDetailBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onBindViewHolder(
        holder: DetailSellLogAdapter.DetailSellLogViewHolder,
        position: Int
    ) {
        val productValue = productList[position].values.toList()
        val productName = productList[position].keys.toList()
        val productQuantity = productValue[0][1]
        val productsPrice = productValue[0][0]

        holder.binding.apply {
            tvProductName.text = productName[0].toString()
            tvProductQuantity.text = "x${productQuantity}"
            tvTotalPrice.text = "Rp. $productsPrice"
        }
    }

    override fun getItemCount(): Int {
        return productList.size
    }

}