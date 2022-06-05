package hilmysf.amirashoplanjutan.ui.product

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import dagger.hilt.android.lifecycle.HiltViewModel
import hilmysf.amirashoplanjutan.data.repositories.ProductsRepository
import hilmysf.amirashoplanjutan.data.source.entities.Products
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ProductViewModel @Inject constructor(private val productsRepository: ProductsRepository) :
    ViewModel() {
    private var _products = MutableLiveData<FirestoreRecyclerOptions<Products>>()
    val products: LiveData<FirestoreRecyclerOptions<Products>> get() = _products
    private var _isQueryChanged = MutableLiveData<Boolean>()
    val isQueryChanged: LiveData<Boolean> get() = _isQueryChanged

    fun addProduct(hashMapProduct: HashMap<String, Any>) {
        viewModelScope.launch(Dispatchers.IO) {
            productsRepository.addProduct(hashMapProduct)
        }
    }

    fun editProduct(product: Products, hashMapProduct: HashMap<String, Any>) {
        viewModelScope.launch(Dispatchers.IO) {
            productsRepository.editProduct(product, hashMapProduct)
        }
    }

    fun deleteProduct(product: Products) {
        viewModelScope.launch(Dispatchers.IO) { productsRepository.deleteProduct(product) }
    }

    fun addLogData(hashMapLog: HashMap<String, Any>) {
        viewModelScope.launch(Dispatchers.IO) {
            productsRepository.addLogData(hashMapLog)
        }
    }

    fun getProducts(): FirestoreRecyclerOptions<Products> {
        _isQueryChanged.postValue(true)
        return productsRepository.getProducts()
    }

    fun searchProducts(searchQuery: String): FirestoreRecyclerOptions<Products> {
        return if (searchQuery == "") {
            _isQueryChanged.postValue(false)
            productsRepository.getProducts()
        } else {
            _isQueryChanged.postValue(true)
            productsRepository.searchProducts(searchQuery)
        }
    }

    fun getUser(userId: String) = productsRepository.getUser(userId)

}