package hilmysf.amirashoplanjutan.ui.product.opname

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.navigation.NavController
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.Glide.with
import com.firebase.ui.firestore.FirestoreRecyclerAdapter
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.google.firebase.storage.StorageReference
import com.google.rpc.Help
import hilmysf.amirashoplanjutan.data.source.entities.Products
import hilmysf.amirashoplanjutan.databinding.ItemProductGridBinding
import hilmysf.amirashoplanjutan.helper.GlideApp
import hilmysf.amirashoplanjutan.helper.Helper
import android.icu.lang.UCharacter.GraphemeClusterBreak.T

import androidx.annotation.NonNull
import android.icu.lang.UCharacter.GraphemeClusterBreak.T

import com.firebase.ui.firestore.ObservableSnapshotArray
import android.icu.lang.UCharacter.GraphemeClusterBreak.T
import java.text.NumberFormat
import java.util.*

class ProductGridAdapter(
    val context: Context?,
    options: FirestoreRecyclerOptions<Products>,
    private val navController: NavController,
    private val storageReference: StorageReference
) : FirestoreRecyclerAdapter<Products, ProductGridAdapter.ProductViewHolder?>(options) {

    inner class ProductViewHolder(val binding: ItemProductGridBinding) :
        RecyclerView.ViewHolder(binding.root)

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
                Log.d("TAG", "path reference: $model")
            }
            tvStockNumber.text = model.quantity.toString()
            tvProductName.text = Helper.camelCase(model.name)
            tvProductPrice.text = "Rp. ${Helper.currencyFormatter(model.price)}"
        }
        holder.binding.ibEditProduct.setOnClickListener {
            val directions = ProductFragmentDirections.actionProductToDetailProductActivity(model)
            navController.navigate(directions)
        }
    }
}