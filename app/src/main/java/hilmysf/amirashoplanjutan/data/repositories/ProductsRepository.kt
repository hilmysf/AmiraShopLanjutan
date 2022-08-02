package hilmysf.amirashoplanjutan.data.repositories

import android.content.Context
import android.net.Uri
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.AuthResult
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.storage.StorageTask
import com.google.firebase.storage.UploadTask
import hilmysf.amirashoplanjutan.data.source.entities.Logs
import hilmysf.amirashoplanjutan.data.source.entities.Products
import hilmysf.amirashoplanjutan.data.source.entities.SellLogs
import hilmysf.amirashoplanjutan.data.source.firebase.FirebaseSource
import javax.inject.Inject

class ProductsRepository @Inject constructor(private val firebaseSource: FirebaseSource) {
    fun getUser(userId: String): Task<DocumentSnapshot> = firebaseSource.getUser(userId)

    fun getCartProducts(cartList: HashMap<Products, ArrayList<Any>>): FirestoreRecyclerOptions<Products> =
        firebaseSource.getCartProducts(cartList)

    fun login(email: String, password: String): Task<AuthResult> =
        firebaseSource.login(email, password)

    fun signUp(email: String, password: String): Task<AuthResult> =
        firebaseSource.signUp(email, password)

    fun signOut() = firebaseSource.signOut()

    fun addProduct(hashMapProduct: HashMap<String, Any>): DocumentReference =
        firebaseSource.addProduct(hashMapProduct)

    fun editProduct(product: Products, hashMapProduct: HashMap<String, Any>) =
        firebaseSource.editProduct(product, hashMapProduct)

    fun deleteProduct(product: Products) = firebaseSource.deleteProduct(product)

    fun addLogData(hashMapLog: HashMap<String, Any>) = firebaseSource.addLog(hashMapLog)

    fun addSellLogsData(hashMapLog: HashMap<String, Any>) = firebaseSource.addSellLog(hashMapLog)

    fun getProducts(
        searchQuery: String,
        category: String
    ): FirestoreRecyclerOptions<Products> = firebaseSource.getProducts(searchQuery, category)

    fun getLowStockProducts(searchQuery: String): FirestoreRecyclerOptions<Products> =
        firebaseSource.getLowStockProducts(searchQuery)

    fun getProfileLogs(userName: String): FirestoreRecyclerOptions<Logs> =
        firebaseSource.getProfileLogs(userName)

    fun getLogs(sortBy: String): FirestoreRecyclerOptions<Logs> = firebaseSource.getLogs(sortBy)
    fun getSellLogs(): FirestoreRecyclerOptions<SellLogs> = firebaseSource.getSellLogs()

    fun uploadImage(imageReference: String, file: Uri?): StorageTask<UploadTask.TaskSnapshot> =
        firebaseSource.uploadImage(imageReference, file)

    fun deleteImage(product: Products) = firebaseSource.deleteImage(product)

    fun checkoutProducts(checkoutHashMap: HashMap<Products, Int>, context: Context) =
        firebaseSource.checkoutProducts(checkoutHashMap, context)
}