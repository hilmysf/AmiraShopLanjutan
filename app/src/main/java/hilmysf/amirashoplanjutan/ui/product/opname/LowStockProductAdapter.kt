package hilmysf.amirashoplanjutan.ui.product.opname

import android.content.Context
import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.firebase.ui.firestore.FirestoreRecyclerAdapter
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.google.firebase.storage.StorageReference
import hilmysf.amirashoplanjutan.data.source.entities.Products
import hilmysf.amirashoplanjutan.databinding.ItemProductGridBinding
import hilmysf.amirashoplanjutan.helper.GlideApp
import hilmysf.amirashoplanjutan.helper.Helper
class LowStockProductAdapter(
    val context: Context?,
    options: FirestoreRecyclerOptions<Products>,
    private val storageReference: StorageReference
) : FirestoreRecyclerAdapter<Products, LowStockProductAdapter.ProductViewHolder?>(options) {

    inner class ProductViewHolder(val binding: ItemProductGridBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): LowStockProductAdapter.ProductViewHolder {
        val binding =
            ItemProductGridBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ProductViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ProductViewHolder, position: Int, model: Products) {
        holder.binding.apply {
            if (context != null) {
                val pathReference = storageReference.child(model.image)
                GlideApp.with(context)
                    .load(pathReference)
                    .into(imgProduct)
                Log.d("TAG", "path reference: $model")
            }
            tvStockNumber.text = model.quantity.toString()
            tvProductName.text = Helper.camelCase(model.name)
            tvProductPrice.text = "Rp. ${Helper.currencyFormatter(model.price)}"
        }
        holder.binding.ibEditProduct.setOnClickListener {
            context?.startActivity(Intent(context, DetailProductActivity::class.java).apply {
                this.putExtra(DetailProductActivity.PRODUCTS_BUNDLE, model)
                this.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            })
//            val directions = ProductFragmentDirections.actionProductToDetailProductActivity(model)
//            navController.navigate(directions)
        }
    }
}