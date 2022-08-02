package hilmysf.amirashoplanjutan.ui.product.sell

import android.content.ContentValues.TAG
import android.content.Intent
import android.content.IntentFilter
import android.net.ConnectivityManager
import android.os.Bundle
import android.util.Log
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.recyclerview.widget.LinearLayoutManager
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.ktx.storage
import dagger.hilt.android.AndroidEntryPoint
import hilmysf.amirashoplanjutan.data.source.entities.Products
import hilmysf.amirashoplanjutan.databinding.ActivitySellBinding
import hilmysf.amirashoplanjutan.helper.Constant
import hilmysf.amirashoplanjutan.helper.Helper
import hilmysf.amirashoplanjutan.network.InternetChangeReceiver

@AndroidEntryPoint
class SellActivity : AppCompatActivity(), SearchView.OnQueryTextListener,
    InternetChangeReceiver.ConnectivityReceiverListener {
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
        registerReceiver(
            InternetChangeReceiver(),
            IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION)
        )
        supportActionBar?.hide()
        val storageReference = Firebase.storage.reference
        searchViewConfiguration(binding)
        getProducts(storageReference)
        onProductsClick()
        navigation()
    }

    private fun navigation() {
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

    private fun getProducts(storageReference: StorageReference) {
        options = viewModel.getProducts("", Constant.SEMUA)
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
            val value: Long = v[0] as Long
            Log.d(TAG, "nilai value: $value")
            Log.d(TAG, "key: $k, values: $v")
            totalPrice += value
            binding.tvTotalPrice.text = "Rp. ${Helper.currencyFormatter(totalPrice)}"
            if (totalPrice.toInt() == 0) {
                binding.tvTotalPrice.text = "Rp. 0"
            }
        }
        Log.d(TAG, "hashmap: $hashMapProducts")
    }

    private fun searchViewConfiguration(binding: ActivitySellBinding) {
        val searchView = binding.searchView
        searchView.setOnQueryTextListener(this)
        searchView.isSubmitButtonEnabled = false
    }

    override fun onQueryTextSubmit(query: String?): Boolean {
        return true
    }

    override fun onQueryTextChange(query: String): Boolean {
        val newOptions = viewModel.getProducts(query, Constant.SEMUA)
        sellAdapter?.updateOptions(newOptions)
        return true
    }

    override fun onNetworkConnectionChanged(isConnected: Boolean) {
        Helper.showNetworkMessage(isConnected, this, binding)
    }

    override fun onResume() {
        super.onResume()
        InternetChangeReceiver.connectivityReceiverListener = this
    }

    override fun onStop() {
        super.onStop()
        sellAdapter?.stopListening()
    }

    override fun onStart() {
        super.onStart()
        hashMapProducts.clear()
        sellAdapter?.startListening()
    }
}