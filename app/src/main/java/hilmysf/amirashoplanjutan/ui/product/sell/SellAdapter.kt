package hilmysf.amirashoplanjutan.ui.product.sell

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.firebase.ui.firestore.FirestoreRecyclerAdapter
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.google.firebase.storage.StorageReference
import hilmysf.amirashoplanjutan.data.source.entities.Products
import hilmysf.amirashoplanjutan.databinding.ItemProductSellBinding
import hilmysf.amirashoplanjutan.helper.GlideApp
import hilmysf.amirashoplanjutan.helper.Helper

class SellAdapter(
    val context: Context?,
    options: FirestoreRecyclerOptions<Products>,
    private val storageReference: StorageReference
) : FirestoreRecyclerAdapter<Products, SellAdapter.SellViewHolder?>(options) {
    var onItemClick: ((product: Products, itemCount: Int) -> Unit)? = null
    var onValueClick: ((product: Products, productTotalPrice: Long, itemQuantity: Int) -> Unit)? =
        null

    inner class SellViewHolder(val binding: ItemProductSellBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): SellViewHolder {
        val binding =
            ItemProductSellBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return SellViewHolder(binding)
    }

    override fun onBindViewHolder(holder: SellViewHolder, position: Int, model: Products) {
        holder.binding.apply {
            val pathReference = storageReference.child(model.image)
            if (context != null) {
                GlideApp.with(context)
                    .load(pathReference)
                    .into(imgProduct)
            }
            if (model.quantity <= 0){
                btnAddToCart.isEnabled = false
            }
            tvProductName.text = Helper.camelCase(model.name)
            tvProductPrice.text = "Rp. ${Helper.currencyFormatter(model.price)}"
            tvProductStock.text = model.quantity.toString()
            quantityNumberPicker.max = model.quantity
            quantityNumberPicker.setOnClickListener {

            }
            quantityNumberPicker.setValueChangedListener { itemCount, action ->
                if (itemCount == 0) {
                    visibility(false, this)
                    onItemClick?.invoke(model, -1)
                    totalPrice(-1, model)
                }
                totalPrice(itemCount, model)
            }
            btnAddToCart.setOnClickListener {
                if (model.quantity != 0) {
                    visibility(true, this)
                }
                quantityNumberPicker.value = 1
                onItemClick?.invoke(model, 1)
                totalPrice(1, model)
            }
        }
    }

    private fun totalPrice(itemCount: Int, model: Products) {
        var totalPrice: Long = 0
        if (itemCount == 0) {
            onValueClick?.invoke(model, totalPrice, itemCount)
        } else {
            totalPrice = itemCount * model.price
            onValueClick?.invoke(model, totalPrice, itemCount)
        }
    }

    private fun visibility(isBtnClicked: Boolean, binding: ItemProductSellBinding) {
        binding.apply {
            if (isBtnClicked) {
                btnAddToCart.visibility = View.INVISIBLE
                quantityNumberPicker.visibility = View.VISIBLE
            } else {
                quantityNumberPicker.visibility = View.INVISIBLE
                btnAddToCart.visibility = View.VISIBLE
            }
        }
    }
}