package hilmysf.amirashoplanjutan.ui.product.opname

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.firebase.ui.firestore.FirestoreRecyclerAdapter
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.google.firebase.storage.StorageReference
import hilmysf.amirashoplanjutan.data.source.entities.Products
import hilmysf.amirashoplanjutan.databinding.ItemProductListBinding
import hilmysf.amirashoplanjutan.helper.GlideApp
import hilmysf.amirashoplanjutan.helper.Helper
import hilmysf.amirashoplanjutan.ui.product.opname.ProductListAdapter.ProductViewHolder

class ProductListAdapter(
    val context: Context?,
    options: FirestoreRecyclerOptions<Products>,
    private val storageReference: StorageReference
) : FirestoreRecyclerAdapter<Products, ProductViewHolder?>(options) {

    inner class ProductViewHolder(val binding: ItemProductListBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ProductViewHolder {
        val binding =
            ItemProductListBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ProductViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ProductViewHolder, position: Int, model: Products) {
        holder.binding.apply {
            var pathReference = storageReference.child(model.image)
            if (context != null) {
//                Glide.with(context).load(model.image).into(imgProduct)
                GlideApp.with(context)
                    .load(pathReference)
                    .into(imgProduct)
            }
            tvProductName.text = Helper.camelCase(model.name)
            tvProductPrice.text = "Rp. ${model.price}"
            tvProductStock.text = "Stock: ${model.quantity}"
        }
    }
}