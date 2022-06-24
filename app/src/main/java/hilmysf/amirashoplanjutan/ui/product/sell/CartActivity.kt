package hilmysf.amirashoplanjutan.ui.product.sell

import android.content.ContentValues.TAG
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.net.ConnectivityManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.activity.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.ktx.storage
import dagger.hilt.android.AndroidEntryPoint
import hilmysf.amirashoplanjutan.data.source.entities.Products
import hilmysf.amirashoplanjutan.databinding.ActivityCartBinding
import hilmysf.amirashoplanjutan.helper.Constant
import hilmysf.amirashoplanjutan.helper.DateHelper
import hilmysf.amirashoplanjutan.helper.Helper
import hilmysf.amirashoplanjutan.network.InternetChangeReceiver
import hilmysf.amirashoplanjutan.notification.NotificationManagers

@AndroidEntryPoint
class CartActivity : AppCompatActivity(), InternetChangeReceiver.ConnectivityReceiverListener {
    private lateinit var binding: ActivityCartBinding
    private val viewModel: SellViewModel by viewModels()
    private lateinit var options: FirestoreRecyclerOptions<Products>
    private var cartAdapter: CartAdapter? = null
    private var checkoutHashMap: HashMap<Products, Int> = HashMap()
    private var hashMapProductsLog: HashMap<String, ArrayList<Any>> = HashMap()
    private var userName: String = ""
    private lateinit var mAuth: FirebaseAuth
    private lateinit var userId: String
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCartBinding.inflate(layoutInflater)
        setContentView(binding.root)
        registerReceiver(
            InternetChangeReceiver(),
            IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION)
        )
        supportActionBar?.title = "Keranjang"
        supportActionBar?.hide()
        mAuth = FirebaseAuth.getInstance()
        userId = mAuth.currentUser!!.uid
        val sharedPreferences =
            getSharedPreferences(Constant.SHARED_PREFERENCES, Context.MODE_PRIVATE)
        userName = sharedPreferences?.getString(Constant.NAME, "User").toString()
        val storageReference = Firebase.storage.reference
        val intent = intent
        val cartHashMap =
            intent.getSerializableExtra(SellActivity.HASH_MAP_PRODUCTS) as? HashMap<Products, ArrayList<Any>>
        cartHashMap?.let { defaultTotalPrice(it) }
        if (cartHashMap?.isNotEmpty()!!) {
            getProductsData(storageReference, cartHashMap)
        } else {
            showEmptyMessage(binding)
        }
        onItemClick(cartHashMap, binding)
        binding.btnCheckout.setOnClickListener {
            viewModel.checkoutProducts(checkoutHashMap, applicationContext)
            addSellLogsData()
            startActivity(Intent(this, SellActivity::class.java).apply {
                this.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
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

    private fun defaultTotalPrice(hashMapProducts: HashMap<Products, ArrayList<Any>>) {
        var totalPrice: Long = 0
        hashMapProducts.forEach { (k, v) ->
            val productName = k.name
            val productPrice = v[0] as Long
            val productQuantity = v[1] as Int
            val reducedQuantity = reduceQuantity(k, productQuantity)
            Log.d(TAG, "key: $k, values: $v")
            totalPrice += productPrice
            binding.tvTotalValue.text = "Rp. ${Helper.currencyFormatter(totalPrice)}"
            if (totalPrice.toInt() == 0) {
                binding.tvTotalValue.text = "Rp. 0"
            }
            checkoutHashMap[k] = reducedQuantity
            hashMapProductsLog[productName] = arrayListOf(productPrice, productQuantity)
            Log.d(TAG, "HashMapLogs: $hashMapProductsLog")
        }
    }

    private fun setTotalPrice(
        binding: ActivityCartBinding,
        product: Products,
        productPrice: Int,
        productQuantity: Int,
        hashMapProducts: HashMap<Products, ArrayList<Any>>
    ) {
        // totalPrice += currentStock x price
        var productName = product.name
        var totalPrice: Long = 0
        var reducedQuantity = reduceQuantity(product, productQuantity)
        hashMapProducts[product] = arrayListOf(productPrice.toLong(), productQuantity)
        checkoutHashMap[product] = reducedQuantity
        hashMapProductsLog[productName] = arrayListOf(productPrice, productQuantity)
        hashMapProducts.forEach { (_, v) ->
            val value: Long = v[0] as Long
            totalPrice += value
            binding.tvTotalValue.text = "Rp. ${Helper.currencyFormatter(totalPrice)}"
            if (totalPrice.toInt() == 0) {
                binding.tvTotalValue.text = "Rp. 0"
            }
        }
        Log.d(TAG, "Hashmap checkout: $checkoutHashMap")
        Log.d(TAG, "hashmap: $hashMapProducts")
        Log.d(TAG, "HashMap logs: $hashMapProductsLog")
    }

    private fun onItemClick(
        cartHashMap: HashMap<Products, ArrayList<Any>>,
        binding: ActivityCartBinding
    ) {
        cartAdapter?.onValueClick = { product, productPrice, productQuantity ->
            setTotalPrice(binding, product, productPrice.toInt(), productQuantity, cartHashMap)
            if (productQuantity == 0) {
                cartHashMap.remove(product)
                checkoutHashMap.remove(product)
                hashMapProductsLog.remove(product.name)
                if (cartHashMap.isEmpty()) {
                    onBackPressed()
                } else {
                    val newOptions = viewModel.getCartProducts(cartHashMap)
                    cartAdapter?.updateOptions(newOptions)
                }
                Log.d(TAG, "new carthashmap: $cartHashMap")

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

    private fun addSellLogsData() {
        val date = DateHelper.dateFormat()
        val time = DateHelper.timeFormat()
        val created = FieldValue.serverTimestamp()
        val hashMapLog = hashMapOf(
            Constant.CREATED to created,
            Constant.DATE to date,
            Constant.TIME to time,
            Constant.HASH_PRODUCTS to hashMapProductsLog,
            Constant.OWNER to userName
        )
        viewModel.addSellLogsData(hashMapLog)
    }

    override fun onStart() {
        super.onStart()
        NotificationManagers.createNotificationChannel(this)
        cartAdapter?.startListening()
    }

    override fun onStop() {
        super.onStop()
        cartAdapter?.stopListening()
    }

    override fun onNetworkConnectionChanged(isConnected: Boolean) {
        Helper.showNetworkMessage(isConnected, this, binding)
    }

    override fun onResume() {
        super.onResume()
        InternetChangeReceiver.connectivityReceiverListener = this
    }

    private fun reduceQuantity(product: Products, checkoutQuantity: Int): Int {
        Log.d(
            TAG,
            "kuantitas asli: ${product.quantity}\n checkout: $checkoutQuantity\n Sisa: ${product.quantity - checkoutQuantity}"
        )
        return product.quantity - checkoutQuantity
    }
}