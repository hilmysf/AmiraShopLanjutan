package hilmysf.amirashoplanjutan.ui.product.opname

import android.content.ContentValues
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.activity.viewModels
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.ktx.storage
import hilmysf.amirashoplanjutan.R
import hilmysf.amirashoplanjutan.data.source.entities.Products
import hilmysf.amirashoplanjutan.databinding.ActivityLowStockProductBinding
import hilmysf.amirashoplanjutan.ui.product.ProductViewModel
import hilmysf.amirashoplanjutan.ui.product.sell.SellAdapter

class LowStockProductActivity : AppCompatActivity() {
    private lateinit var binding: ActivityLowStockProductBinding
    private var lowStockProductAdapter: LowStockProductAdapter? = null
    private val viewModel: ProductViewModel by viewModels()
    private lateinit var storageReference: StorageReference
    private lateinit var options: FirestoreRecyclerOptions<Products>
    private lateinit var navController: NavController
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLowStockProductBinding.inflate(layoutInflater)
        setContentView(binding.root)
        navController = Navigation.findNavController(binding.root)
        storageReference = Firebase.storage.reference
        getProductsList(storageReference)
    }

    private fun getProductsList(storageReference: StorageReference) {
        Log.d(ContentValues.TAG, "DocumentSnapshot Home: $options")
        lowStockProductAdapter = LowStockProductAdapter(applicationContext, options, storageReference)
        with(binding.rvProducts) {
            layoutManager = GridLayoutManager(context, 2)
            setHasFixedSize(true)
            adapter = lowStockProductAdapter
            android.util.Log.d(android.content.ContentValues.TAG, "jumlah item ${adapter!!.itemCount}")
        }
    }
}