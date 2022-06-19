package hilmysf.amirashoplanjutan.ui.product.sell

import android.content.Context
import androidx.lifecycle.ViewModel
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import dagger.hilt.android.lifecycle.HiltViewModel
import hilmysf.amirashoplanjutan.data.repositories.ProductsRepository
import hilmysf.amirashoplanjutan.data.source.entities.Products
import javax.inject.Inject

@HiltViewModel
class SellViewModel @Inject constructor(private val productsRepository: ProductsRepository) :
    ViewModel() {
    fun getProducts(
        searchQuery: String,
        category: String
    ): FirestoreRecyclerOptions<Products> = productsRepository.getProducts(searchQuery, category)

    fun getCartProducts(cartList: HashMap<Products, ArrayList<Any>>): FirestoreRecyclerOptions<Products> =
        productsRepository.getCartProducts(cartList)

    fun checkoutProducts(checkoutHashMap: HashMap<Products, Int>, context: Context) =
        productsRepository.checkoutProducts(checkoutHashMap, context)

}