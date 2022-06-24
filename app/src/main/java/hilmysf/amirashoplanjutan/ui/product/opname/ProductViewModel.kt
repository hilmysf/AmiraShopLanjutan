package hilmysf.amirashoplanjutan.ui.product.opname

import android.net.Uri
import androidx.lifecycle.*
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.google.firebase.storage.StorageTask
import com.google.firebase.storage.UploadTask
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

    fun addProduct(hashMapProduct: HashMap<String, Any>) =
        productsRepository.addProduct(hashMapProduct)


    fun editProduct(product: Products, hashMapProduct: HashMap<String, Any>) {
        viewModelScope.launch(Dispatchers.IO) {
            productsRepository.editProduct(product, hashMapProduct)
        }
    }

    fun deleteProduct(product: Products) {
        viewModelScope.launch(Dispatchers.IO) { productsRepository.deleteProduct(product) }
    }

    fun addLogData(hashMapLog: HashMap<String, Any>) {
            productsRepository.addLogData(hashMapLog)

    }

    fun getProducts(
        searchQuery: String,
        category: String
    ): FirestoreRecyclerOptions<Products> {
        return productsRepository.getProducts(searchQuery, category)
    }

    fun uploadImage(imageReference: String, file: Uri?): StorageTask<UploadTask.TaskSnapshot> =
        productsRepository.uploadImage(imageReference, file)

    fun deleteImage(product: Products) {
        viewModelScope.launch(Dispatchers.IO) {
            productsRepository.deleteImage(product)
        }
    }

    fun getLowStockProduct(searchQuery: String): FirestoreRecyclerOptions<Products> =
        productsRepository.getLowStockProducts(searchQuery)
}