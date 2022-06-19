package hilmysf.amirashoplanjutan.ui.product.opname

import android.content.ContentValues.TAG
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.viewModels
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.recyclerview.widget.GridLayoutManager
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.google.android.material.chip.Chip
import com.google.firebase.firestore.*
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.StorageReference
import hilmysf.amirashoplanjutan.R
import com.google.firebase.storage.ktx.storage
import dagger.hilt.android.AndroidEntryPoint
import hilmysf.amirashoplanjutan.data.source.entities.Products
import hilmysf.amirashoplanjutan.databinding.FragmentProductBinding
import hilmysf.amirashoplanjutan.helper.Constant

@AndroidEntryPoint
class ProductFragment : Fragment(), SearchView.OnQueryTextListener {
    private var productGridAdapter: ProductGridAdapter? = null
    private lateinit var binding: FragmentProductBinding
    private lateinit var firestore: FirebaseFirestore
    private lateinit var options: FirestoreRecyclerOptions<Products>
    private var query: String = ""
    private var category: String = Constant.SEMUA
    private lateinit var storageReference: StorageReference
    private lateinit var navController: NavController
    private val viewModel: ProductViewModel by viewModels()
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentProductBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        firestore = FirebaseFirestore.getInstance()
        storageReference = Firebase.storage.reference
        navController = Navigation.findNavController(view)
        options = viewModel.getProducts(query, category)
        searchViewConfiguration(binding)
        getProductsList(navController, storageReference)
        binding.productsFab.setOnClickListener {
            navController.navigate(R.id.action_product_to_detailProductActivity)
        }
//        binding.chipsCategories.chipsCategoriesGroup.check(R.id.chip_all)
        binding.chipsCategories.chipsCategoriesGroup.setOnCheckedChangeListener { group, checkedId ->
            val newId = if (checkedId == -1) {
                2131296885
            } else {
                checkedId
            }
            var category = ""
            Log.d(TAG, "checkId: $newId")
            val chip: Chip = group.findViewById(newId)
            category = if (chip.text.toString() != "Semua") {
                chip.text.toString()
            } else {
                Constant.SEMUA
            }
            Log.d(TAG, "category: $category")
            val newOptions = viewModel.getProducts(query, category)
            productGridAdapter?.updateOptions(newOptions)
        }
    }

    private fun getProductsList(navController: NavController, storageReference: StorageReference) {
        Log.d(TAG, "DocumentSnapshot Home: $options")
        productGridAdapter = ProductGridAdapter(context, options, navController, storageReference)
        with(binding.rvProducts) {
            layoutManager = GridLayoutManager(context, 2)
            setHasFixedSize(true)
            adapter = productGridAdapter
            Log.d(TAG, "jumlah item ${adapter!!.itemCount}")
        }
    }

    override fun onStart() {
        super.onStart()
        productGridAdapter?.startListening()
    }

    override fun onStop() {
        super.onStop()
        productGridAdapter?.stopListening()
    }

    override fun onQueryTextSubmit(query: String?): Boolean {
        return true
    }

    override fun onQueryTextChange(query: String): Boolean {
        val newOptions = viewModel.getProducts(query, category)
        productGridAdapter?.updateOptions(newOptions)
        return true
    }

    private fun searchViewConfiguration(fragmentProductBinding: FragmentProductBinding) {
        val searchView = fragmentProductBinding.searchView
        searchView.setOnQueryTextListener(this)
        searchView.isSubmitButtonEnabled = false
    }
}