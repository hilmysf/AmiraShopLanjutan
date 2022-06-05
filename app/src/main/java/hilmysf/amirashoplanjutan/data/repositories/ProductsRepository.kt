package hilmysf.amirashoplanjutan.data.repositories

import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.AuthResult
import com.google.firebase.firestore.DocumentSnapshot
import hilmysf.amirashoplanjutan.data.source.entities.Logs
import hilmysf.amirashoplanjutan.data.source.entities.Products
import hilmysf.amirashoplanjutan.data.source.firebase.FirebaseSource
import javax.inject.Inject

class ProductsRepository @Inject constructor(private val firebaseSource: FirebaseSource) {
    fun getUser(userId: String): Task<DocumentSnapshot> = firebaseSource.getUser(userId)

    fun login(email: String, password: String): Task<AuthResult> =
        firebaseSource.login(email, password)

    fun signUp(email: String, password: String): Task<AuthResult> =
        firebaseSource.signUp(email, password)

    fun signOut() = firebaseSource.signOut()

    fun addProduct(hashMapProduct: HashMap<String, Any>) =
        firebaseSource.addProduct(hashMapProduct)

    fun editProduct(product: Products, hashMapProduct: HashMap<String, Any>) =
        firebaseSource.editProduct(product, hashMapProduct)

    fun deleteProduct(product: Products) = firebaseSource.deleteProduct(product)

    fun addLogData(hashMapLog: HashMap<String, Any>) = firebaseSource.addLog(hashMapLog)

    fun getProducts(): FirestoreRecyclerOptions<Products> = firebaseSource.getProducts()

    fun searchProducts(searchQuery: String) : FirestoreRecyclerOptions<Products> = firebaseSource.searchProducts(searchQuery)

    fun getLogs(): FirestoreRecyclerOptions<Logs> = firebaseSource.getLogs()
}