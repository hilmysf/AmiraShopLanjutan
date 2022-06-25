package hilmysf.amirashoplanjutan.ui.log.sell

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import hilmysf.amirashoplanjutan.databinding.ItemLogSellDetailBinding
import hilmysf.amirashoplanjutan.helper.Helper

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
        val productQuantity = productValue[0][1] as Long
        val productsPrice = productValue[0][0] as Long / productQuantity

        holder.binding.apply {
            tvProductName.text = Helper.camelCase(productName[0])
            tvProductQuantity.text = "x${productQuantity}"
            tvTotalPrice.text = "Rp. ${Helper.currencyFormatter(productsPrice)}"
        }
    }

    override fun getItemCount(): Int {
        return productList.size
    }

}