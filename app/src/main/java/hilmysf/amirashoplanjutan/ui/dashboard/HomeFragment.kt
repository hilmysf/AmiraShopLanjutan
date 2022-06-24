package hilmysf.amirashoplanjutan.ui.dashboard

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.Navigation
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.ktx.storage
import dagger.hilt.android.AndroidEntryPoint
import hilmysf.amirashoplanjutan.R
import hilmysf.amirashoplanjutan.data.source.entities.Products
import hilmysf.amirashoplanjutan.databinding.FragmentHomeBinding
import hilmysf.amirashoplanjutan.helper.Constant
import hilmysf.amirashoplanjutan.helper.Helper
import hilmysf.amirashoplanjutan.notification.NotificationManagers
import hilmysf.amirashoplanjutan.ui.product.opname.ProductListAdapter


@AndroidEntryPoint
class HomeFragment : Fragment() {

    private lateinit var binding: FragmentHomeBinding
    private lateinit var firestore: FirebaseFirestore
    private lateinit var mAuth: FirebaseAuth
    private lateinit var userId: String
    private var productListAdapter: ProductListAdapter? = null
    private lateinit var options: FirestoreRecyclerOptions<Products>
    private var userName = ""
    private val viewModel: HomeViewModel by viewModels()
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentHomeBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        firestore = FirebaseFirestore.getInstance()
        mAuth = FirebaseAuth.getInstance()
        userId = mAuth.currentUser!!.uid
        val storageReference = Firebase.storage.reference
        val sharedPreferences =
            activity?.getSharedPreferences(Constant.SHARED_PREFERENCES, Context.MODE_PRIVATE)
        userName = sharedPreferences?.getString(Constant.NAME, "User").toString()
        binding.tvHallo.text = "Hallo, ${Helper.camelCase(userName)}!"
        getProductsData(storageReference)
        navigation(requireView())
    }

    private fun getProductsData(storageReference: StorageReference) {
        options = viewModel.getProducts("", Constant.SEMUA)
        productListAdapter = ProductListAdapter(context, options, storageReference)
        with(binding.rvProducts) {
            addItemDecoration(DividerItemDecoration(context, DividerItemDecoration.VERTICAL))
            layoutManager = LinearLayoutManager(context)
            setHasFixedSize(true)
            adapter = productListAdapter
        }
    }

    private fun navigation(view: View) {
        val navController = Navigation.findNavController(view)
        binding.cvAddTem.setOnClickListener {
            navController.navigate(R.id.action_home_to_detailProductActivity)
        }
        binding.cvSellItem.setOnClickListener {
            navController.navigate(R.id.action_home_to_sellActivity)
        }
        binding.cvLowStock.setOnClickListener {
            navController.navigate(R.id.action_home_to_lowStockProductActivity)
        }
        binding.searchView.setOnClickListener {
            navController.navigate(R.id.action_home_to_product)
        }
    }

    override fun onStart() {
        super.onStart()
        NotificationManagers.createNotificationChannel(activity!!)
        productListAdapter?.startListening()
    }

    override fun onStop() {
        super.onStop()
        productListAdapter?.stopListening()
    }
}