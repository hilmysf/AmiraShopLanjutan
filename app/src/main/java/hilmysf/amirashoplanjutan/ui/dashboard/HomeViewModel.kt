package hilmysf.amirashoplanjutan.ui.dashboard

import androidx.lifecycle.ViewModel
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import dagger.hilt.android.lifecycle.HiltViewModel
import hilmysf.amirashoplanjutan.data.repositories.ProductsRepository
import hilmysf.amirashoplanjutan.data.source.entities.Products
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(private val productsRepository: ProductsRepository) :
    ViewModel() {

    fun getProducts(
        query: String,
        category: String
    ): FirestoreRecyclerOptions<Products> = productsRepository.getProducts(query, category)

}