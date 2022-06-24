package hilmysf.amirashoplanjutan.ui.log.opname

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import hilmysf.amirashoplanjutan.databinding.ItemLogOpnameDetailBinding
import hilmysf.amirashoplanjutan.helper.Constant
import hilmysf.amirashoplanjutan.helper.Helper

class DetailOpnameAdapter(private val productList: ArrayList<HashMap<String, ArrayList<Any>>>) :
    RecyclerView.Adapter<DetailOpnameAdapter.DetailOpnameLogViewHolder>() {
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): DetailOpnameAdapter.DetailOpnameLogViewHolder {
        val binding =
            ItemLogOpnameDetailBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return DetailOpnameLogViewHolder(binding)
    }

    inner class DetailOpnameLogViewHolder(val binding: ItemLogOpnameDetailBinding) :
        RecyclerView.ViewHolder(binding.root)


    override fun onBindViewHolder(
        holder: DetailOpnameAdapter.DetailOpnameLogViewHolder,
        position: Int
    ) {
        val productValue = productList[position].values.toList()
        val attribute = productList[position].keys.toList()

        val attributeFrom = if (attribute[0].equals(Constant.PRICE, true)) {
            "Rp. ${Helper.currencyFormatter(productValue[0][0] as Long)}"
        } else {
            productValue[0][0]
        }
        val attributeTo = if (attribute[0].equals(Constant.PRICE, true)) {
            "Rp. ${Helper.currencyFormatter(productValue[0][1] as Long)}"
        } else {
            productValue[0][1]
        }


        holder.binding.apply {
            tvAttributeName.text = Helper.camelCase(attribute[0])
            tvAttributeFrom.text = attributeFrom.toString()
            tvAttributeTo.text = attributeTo.toString()
        }
    }

    override fun getItemCount(): Int {
        return productList.size
    }
}