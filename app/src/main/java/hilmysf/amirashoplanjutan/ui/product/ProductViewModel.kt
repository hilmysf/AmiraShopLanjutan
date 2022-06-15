package hilmysf.amirashoplanjutan.ui.product

import android.net.Uri
import android.widget.ProgressBar
import androidx.lifecycle.*
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.google.firebase.firestore.DocumentReference
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
//        viewModelScope.launch(Dispatchers.IO) {
        productsRepository.addProduct(hashMapProduct)
//        }


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

    fun getProducts(
        searchQuery: String
    ): FirestoreRecyclerOptions<Products> {
        return productsRepository.getProducts(searchQuery)
    }

    fun getUser(userId: String) = productsRepository.getUser(userId)

    fun uploadImage(imageReference: String, file: Uri?): StorageTask<UploadTask.TaskSnapshot> =
//        viewModelScope.launch(Dispatchers.IO) {
        productsRepository.uploadImage(imageReference, file)
//        }
//    }

    fun deleteImage(product: Products) {
        viewModelScope.launch(Dispatchers.IO) {
            productsRepository.deleteImage(product)
        }
    }

    fun lowStockProduct(): FirestoreRecyclerOptions<Products> =
        productsRepository.getLowStockProducts()
}