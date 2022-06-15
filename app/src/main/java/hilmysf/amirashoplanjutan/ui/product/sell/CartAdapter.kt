package hilmysf.amirashoplanjutan.ui.product.sell

import android.content.Context
import android.util.Log
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

class CartAdapter(
    val context: Context?,
    options: FirestoreRecyclerOptions<Products>,
    private val storageReference: StorageReference,
    private val cartHashMap: HashMap<Products, ArrayList<Any>>
) : FirestoreRecyclerAdapter<Products, CartAdapter.CartViewHolder?>(options) {
    var onItemClick: ((product: Products, itemCount: Int) -> Unit)? = null
    var onValueClick: ((product: Products, productTotalPrice: Long, itemQuantity: Int) -> Unit)? =
        null
//    var totalPrice: Long = 0

    inner class CartViewHolder(val binding: ItemProductSellBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): CartAdapter.CartViewHolder {
        val binding =
            ItemProductSellBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return CartViewHolder(binding)
    }

    override fun onBindViewHolder(
        holder: CartAdapter.CartViewHolder,
        position: Int,
        model: Products
    ) {
        holder.binding.apply {
            var cartQuantity = cartHashMap[model]?.get(1) as? Number
            val pathReference = storageReference.child(model.image)
            if (context != null) {
                GlideApp.with(context)
                    .load(pathReference)
                    .into(imgProduct)
            }
            tvProductName.text = Helper.camelCase(model.name)
            tvProductPrice.text = "Rp. ${model.price}"
            quantityNumberPicker.max = model.quantity.toInt()
            tvProductStock.text = model.quantity.toString()
            if (cartQuantity != null) {
                quantityNumberPicker.value = cartQuantity.toInt()
            }
            quantityNumberPicker.setValueChangedListener { itemCount, _ ->
                Log.d("value picker: ", itemCount.toString())
                totalPrice(itemCount, model)
            }
            btnAddToCart.visibility = View.GONE
            quantityNumberPicker.visibility = View.VISIBLE
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
}