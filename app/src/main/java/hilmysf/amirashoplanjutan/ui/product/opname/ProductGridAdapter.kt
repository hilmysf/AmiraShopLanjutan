package hilmysf.amirashoplanjutan.ui.product.opname

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.navigation.NavController
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide.with
import com.firebase.ui.firestore.FirestoreRecyclerAdapter
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.google.firebase.storage.StorageReference
import hilmysf.amirashoplanjutan.data.source.entities.Products
import hilmysf.amirashoplanjutan.databinding.ItemProductGridBinding
import hilmysf.amirashoplanjutan.helper.GlideApp

class ProductGridAdapter(
    val context: Context?,
    options: FirestoreRecyclerOptions<Products>,
    private val navController: NavController,
    private val storageReference: StorageReference
) : FirestoreRecyclerAdapter<Products, ProductGridAdapter.ProductViewHolder?>(options) {
    private var listProducts: List<Products> = emptyList()
//    fun setProducts(movies: List<Products>?) {
//        if (movies == null) return
//        this.listProducts = movies
//        notifyDataSetChanged()
//    }

    inner class ProductViewHolder(val binding: ItemProductGridBinding) :
        RecyclerView.ViewHolder(binding.root)

//    override fun onDataChanged() {
//        super.onDataChanged()
//        adapter
//    }
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ProductGridAdapter.ProductViewHolder {
        val binding =
            ItemProductGridBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ProductViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ProductViewHolder, position: Int, model: Products) {
        holder.binding.apply {
            if (context != null) {
                var pathReference = storageReference.child(model.image)
                GlideApp.with(context)
                    .load(pathReference)
                    .into(imgProduct)
//                Glide.with(context)
//                    .load(storageReference)
//                    .into(imgProduct)
            }
            tvProductName.text = model.name
            tvProductPrice.text = "Rp. ${model.price}"
        }
        holder.binding.ibEditProduct.setOnClickListener {
            val directions = ProductFragmentDirections.actionProductToDetailProductActivity(model)
            navController.navigate(directions)
        }
    }
}