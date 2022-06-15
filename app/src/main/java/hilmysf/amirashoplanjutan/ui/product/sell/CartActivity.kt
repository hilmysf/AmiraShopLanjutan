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
import hilmysf.amirashoplanjutan.databinding.ActivityCartBinding
import hilmysf.amirashoplanjutan.helper.Helper

@AndroidEntryPoint
class CartActivity : AppCompatActivity() {
    private lateinit var binding: ActivityCartBinding
    private val viewModel: SellViewModel by viewModels()
    private lateinit var options: FirestoreRecyclerOptions<Products>
    private var cartAdapter: CartAdapter? = null
    private var checkoutHashMap: HashMap<Products, Int> = HashMap()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCartBinding.inflate(layoutInflater)
        setContentView(binding.root)
        supportActionBar?.title = "Keranjang"
        supportActionBar?.hide()
        val storageReference = Firebase.storage.reference
        val intent = intent
        val cartHashMap =
            intent.getSerializableExtra(SellActivity.HASH_MAP_PRODUCTS) as? HashMap<Products, ArrayList<Any>>
        cartHashMap?.let { totalPrice(it) }
        if (cartHashMap?.isNotEmpty()!!) {
            getProductsData(storageReference, cartHashMap)
        } else {
            showEmptyMessage(binding)
        }
        onItemClick(cartHashMap, binding)
        binding.btnCheckout.setOnClickListener {
            viewModel.checkoutProducts(checkoutHashMap)
            startActivity(Intent(this, SellActivity::class.java).apply {
                this.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            })
        }
    }

    private fun getProductsData(
        storageReference: StorageReference,
        cartHashMap: HashMap<Products, ArrayList<Any>>
    ) {
        options = viewModel.getCartProducts(cartHashMap)
        val newHashMap = cartHashMap.filterValues { it != arrayListOf<Any>(0, 0) }
        cartAdapter = CartAdapter(
            applicationContext, options, storageReference,
            newHashMap as HashMap<Products, ArrayList<Any>>
        )
        with(binding.rvProducts) {
            layoutManager = LinearLayoutManager(context)
            setHasFixedSize(true)
            adapter = cartAdapter
        }
    }

    private fun reduceQuantity(product: Products, checkoutQuantity: Int): Int {
        Log.d(
            TAG,
            "kuantitas asli: ${product.quantity}\n checkout: $checkoutQuantity\n Sisa: ${product.quantity - checkoutQuantity}"
        )
        return product.quantity - checkoutQuantity
    }

    private fun totalPrice(hashMapProducts: HashMap<Products, ArrayList<Any>>) {
        var totalPrice: Long = 0
        hashMapProducts.forEach { (k, v) ->
            var productPrice = v[0] as Long
            var productQuantity = v[1] as Int
            Log.d(TAG, "key: $k, values: $v")
            totalPrice += productPrice
            binding.tvTotalValue.text = "Rp. ${Helper.currencyFormatter(totalPrice)}"
            if (totalPrice.equals(0)) {
                binding.tvTotalValue.text = "Rp. 0"
            }
            checkoutHashMap[k] = reduceQuantity(k, productQuantity)
        }
    }

    private fun totalPrice(
        binding: ActivityCartBinding,
        product: Products,
        productPrice: Int,
        productQuantity: Int,
        hashMapProducts: HashMap<Products, ArrayList<Any>>
    ) {
        // totalPrice += currentStock x price
        var totalPrice: Long = 0
        hashMapProducts[product] = arrayListOf(productPrice.toLong(), productQuantity)
        checkoutHashMap[product] = reduceQuantity(product, productQuantity)
        hashMapProducts.forEach { (k, v) ->
            var value: Long = v[0] as Long
            totalPrice += value
            binding.tvTotalValue.text = "Rp. ${Helper.currencyFormatter(totalPrice)}"
            if (totalPrice.equals(0)) {
                binding.tvTotalValue.text = "Rp. 0"
            }
        }
        Log.d(TAG, "Hashmap checkout: $checkoutHashMap")
        Log.d(TAG, "hashmap: $hashMapProducts")
    }

    private fun onItemClick(
        cartHashMap: HashMap<Products, ArrayList<Any>>,
        binding: ActivityCartBinding
    ) {
        cartAdapter?.onValueClick = { product, productPrice, productQuantity ->
            if (productQuantity == 0) {
                Log.d(TAG, "item berubah")
                cartHashMap.remove(product)
                checkoutHashMap.remove(product)
                Log.d(TAG, "new carthashmap: $cartHashMap")
                val newOptions = viewModel.getCartProducts(cartHashMap)
                cartAdapter?.updateOptions(newOptions)
            } else {
                totalPrice(binding, product, productPrice.toInt(), productQuantity, cartHashMap)
            }
        }
    }

    private fun showEmptyMessage(binding: ActivityCartBinding) {
        binding.apply {
            imgEmptyState.visibility = View.VISIBLE
            tvEmptyTitle.visibility = View.VISIBLE
            tvEmptyMessage.visibility = View.VISIBLE
            tvTotalPlaceholder.visibility = View.INVISIBLE
            tvTotalValue.visibility = View.INVISIBLE
            btnCheckout.visibility = View.INVISIBLE
            tvTitle.visibility = View.INVISIBLE
        }
    }

    override fun onStart() {
        super.onStart()
        cartAdapter?.startListening()
    }

    override fun onStop() {
        super.onStop()
        cartAdapter?.stopListening()
    }
}