package hilmysf.amirashoplanjutan.ui.product.sell

import android.content.ContentValues.TAG
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.activity.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.ktx.storage
import dagger.hilt.android.AndroidEntryPoint
import hilmysf.amirashoplanjutan.data.source.entities.Products
import hilmysf.amirashoplanjutan.databinding.ActivitySellBinding
import hilmysf.amirashoplanjutan.helper.Helper

@AndroidEntryPoint
class SellActivity : AppCompatActivity() {
    companion object {
        const val HASH_MAP_PRODUCTS = "hashMapProducts"
    }

    private lateinit var binding: ActivitySellBinding
    private val viewModel: SellViewModel by viewModels()
    private lateinit var options: FirestoreRecyclerOptions<Products>
    private var sellAdapter: SellAdapter? = null
    private var totalItem: Int = 0
    private var hashMapProducts: HashMap<Products, ArrayList<Any>> = HashMap()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySellBinding.inflate(layoutInflater)
        setContentView(binding.root)
        supportActionBar?.hide()
        val storageReference = Firebase.storage.reference
        getProductsData(storageReference)
        onProductsClick()
        binding.ibBack.setOnClickListener {
            onBackPressed()
        }
        binding.btnToCart.setOnClickListener {
            Log.d(TAG, "keclick navigation")
            startActivity(Intent(applicationContext, CartActivity::class.java).apply {
                putExtra(HASH_MAP_PRODUCTS, hashMapProducts)
            })
        }
    }

    private fun getProductsData(storageReference: StorageReference) {
        options = viewModel.getProducts("")
        sellAdapter = SellAdapter(applicationContext, options, storageReference)
        with(binding.rvProducts) {
            layoutManager = LinearLayoutManager(context)
            setHasFixedSize(true)
            adapter = sellAdapter
        }
    }

    private fun onProductsClick() {
        sellAdapter?.onItemClick = { _, itemCount ->
            totalItem += itemCount
            binding.tvTotalItem.text = "Total $totalItem Produk"
            if (itemCount == 0) {
                hashMapProducts.clear()
            }
        }
        sellAdapter?.onValueClick = { product, productPrice, productQuantity ->
            totalPrice(binding, product, productPrice.toInt(), productQuantity)
            if (productQuantity == 0) {
                hashMapProducts.remove(product)
            }
        }
    }

    private fun totalPrice(
        binding: ActivitySellBinding,
        product: Products,
        productPrice: Int,
        productQuantity: Int
    ) {
        // totalPrice += currentStock x price
        var totalPrice: Long = 0
        hashMapProducts[product] = arrayListOf(productPrice.toLong(), productQuantity)
        hashMapProducts.forEach { (k, v) ->
            var value: Long = v[0] as Long
            Log.d(TAG, "nilai value: $value")
            Log.d(TAG, "key: $k, values: $v")
            totalPrice += value
            binding.tvTotalPrice.text = "Rp. ${Helper.currencyFormatter(totalPrice)}"
            if (totalPrice.equals(0)) {
                binding.tvTotalPrice.text = "Rp. 0"
            }
        }
        Log.d(TAG, "hashmap: $hashMapProducts")
    }

    override fun onStart() {
        super.onStart()
        hashMapProducts.clear()
        sellAdapter?.startListening()
    }

    override fun onStop() {
        super.onStop()
        sellAdapter?.stopListening()
    }
}