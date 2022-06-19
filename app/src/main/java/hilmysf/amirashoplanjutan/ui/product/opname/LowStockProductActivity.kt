package hilmysf.amirashoplanjutan.ui.product.opname

import android.content.Intent
import android.content.IntentFilter
import android.net.ConnectivityManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.widget.SearchView
import androidx.recyclerview.widget.GridLayoutManager
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.ktx.storage
import dagger.hilt.android.AndroidEntryPoint
import hilmysf.amirashoplanjutan.data.source.entities.Products
import hilmysf.amirashoplanjutan.databinding.ActivityLowStockProductBinding
import hilmysf.amirashoplanjutan.helper.Helper
import hilmysf.amirashoplanjutan.network.InternetChangeReceiver
import hilmysf.amirashoplanjutan.ui.HomeActivity

@AndroidEntryPoint
class LowStockProductActivity : AppCompatActivity(), SearchView.OnQueryTextListener,
    InternetChangeReceiver.ConnectivityReceiverListener {
    private lateinit var binding: ActivityLowStockProductBinding
    private var lowStockProductAdapter: LowStockProductAdapter? = null
    private val viewModel: ProductViewModel by viewModels()
    private lateinit var storageReference: StorageReference
    private lateinit var options: FirestoreRecyclerOptions<Products>
    private val query: String = ""
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLowStockProductBinding.inflate(layoutInflater)
        setContentView(binding.root)
        registerReceiver(
            InternetChangeReceiver(),
            IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION)
        )
        supportActionBar?.hide()
        storageReference = Firebase.storage.reference
        searchViewConfiguration(binding)
        getProductsList(storageReference)
        binding.ibBack.setOnClickListener {
            if (this.isTaskRoot) {
                startActivity(Intent(this, HomeActivity::class.java))
            } else {
                onBackPressed()
            }
        }
    }

    private fun getProductsList(storageReference: StorageReference) {
        options = viewModel.getLowStockProduct(query)
        lowStockProductAdapter =
            LowStockProductAdapter(applicationContext, options, storageReference)
        with(binding.rvProducts) {
            layoutManager = GridLayoutManager(context, 2)
            setHasFixedSize(true)
            adapter = lowStockProductAdapter
        }
    }

    override fun onStart() {
        super.onStart()
        lowStockProductAdapter?.startListening()
    }

    override fun onStop() {
        super.onStop()
        lowStockProductAdapter?.stopListening()
    }

    override fun onQueryTextSubmit(query: String?): Boolean {
        return true
    }

    override fun onQueryTextChange(query: String): Boolean {
        val newOptions = viewModel.getLowStockProduct(query)
        lowStockProductAdapter?.updateOptions(newOptions)
        return true
    }

    private fun searchViewConfiguration(binding: ActivityLowStockProductBinding) {
        val searchView = binding.searchView
        searchView.setOnQueryTextListener(this)
        searchView.isSubmitButtonEnabled = false
    }

    override fun onNetworkConnectionChanged(isConnected: Boolean) {
        Helper.showNetworkMessage(isConnected, this, binding)
    }

    override fun onResume() {
        super.onResume()
        InternetChangeReceiver.connectivityReceiverListener = this
    }
}