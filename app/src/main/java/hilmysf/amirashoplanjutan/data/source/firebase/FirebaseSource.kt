package hilmysf.amirashoplanjutan.data.source.firebase

import android.content.ContentValues.TAG
import android.content.Context
import android.net.Uri
import android.text.TextUtils
import android.util.Log
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.StorageTask
import com.google.firebase.storage.UploadTask
import hilmysf.amirashoplanjutan.data.source.entities.Logs
import hilmysf.amirashoplanjutan.data.source.entities.Products
import hilmysf.amirashoplanjutan.data.source.entities.SellLogs
import hilmysf.amirashoplanjutan.helper.Constant
import hilmysf.amirashoplanjutan.notification.NotificationManagers
import java.util.*

class FirebaseSource {
    private val firebaseAuth: FirebaseAuth by lazy {
        FirebaseAuth.getInstance()
    }
    private val firestore: FirebaseFirestore by lazy {
        FirebaseFirestore.getInstance()
    }
    private val firebaseStorage: FirebaseStorage by lazy {
        FirebaseStorage.getInstance()
    }
    private val storageRef: StorageReference by lazy {
        firebaseStorage.reference
    }

    fun getUser(userId: String): Task<DocumentSnapshot> {
        val documentReference = firestore.collection(Constant.USERS).document(userId)
        return documentReference.get()
    }

    fun login(email: String, password: String): Task<AuthResult> {
        return firebaseAuth.signInWithEmailAndPassword(email, password)
    }

    fun signUp(email: String, password: String): Task<AuthResult> {
        return firebaseAuth.createUserWithEmailAndPassword(email, password)
    }

    fun signOut() = firebaseAuth.signOut()

    fun addProduct(hashMapProduct: HashMap<String, Any>): DocumentReference {
        val document = firestore.collection(Constant.PRODUCTS)
            .document()
        hashMapProduct[Constant.IMAGE] = "products/${document.id}.png"
        hashMapProduct[Constant.PRODUCT_ID] = document.id
        document.set(hashMapProduct)
            .addOnSuccessListener {
                Log.d(TAG, "Berhasil upload produk")
            }
            .addOnFailureListener {
                Log.d(TAG, "gagal upload produk")
            }
        return document
    }

    fun editProduct(product: Products, hashMapProduct: HashMap<String, Any>) {
        firestore.collection(Constant.PRODUCTS)
            .document(product.productId)
            .set(hashMapProduct)
    }

    fun deleteProduct(product: Products) {
        firestore.collection(Constant.PRODUCTS)
            .document(product.productId)
            .delete()

    }

    fun checkoutProducts(checkoutHashMap: HashMap<Products, Int>, context: Context) {
        val productList: List<Products> = checkoutHashMap.keys.toList()
        Log.d(TAG, "checkoutHashMap: $checkoutHashMap")
        for (product in productList) {
            val isLow = checkoutHashMap[product]!! <= product.minQuantity
            val checkoutQuantity: Int? = checkoutHashMap[product]
            if (isLow) {
                NotificationManagers.triggerNotification(context, product, checkoutQuantity)
            }
            Log.d(TAG, "${product.quantity} <= ${product.minQuantity} adalah $isLow")
            firestore.collection(Constant.PRODUCTS)
                .document(product.productId)
                .update(
                    mapOf(
                        Constant.QUANTITY to checkoutQuantity,
                        Constant.IS_LOW to isLow
                    )
                )

                .addOnSuccessListener {
                    Log.d(TAG, "Berhasil mengubah kuantitas produk ${product.name}")

                }
                .addOnFailureListener {
                    Log.d(TAG, "Gagal mengubah kuantitas produk ${product.name}")
                }
        }
    }

    fun getCartProducts(cartHashMap: HashMap<Products, ArrayList<Any>>): FirestoreRecyclerOptions<Products> {
        val productNameList: MutableList<String> = mutableListOf()
        val product: List<Products> = cartHashMap.keys.toList()
        product.forEach {
            productNameList.add(it.name)
        }
        Log.d(TAG, "productName: $productNameList")
        Log.d(TAG, "List hash : $product")
        val query =
            firestore.collection(Constant.PRODUCTS)
                .whereIn(Constant.NAME, productNameList)

        return FirestoreRecyclerOptions.Builder<Products>()
            .setQuery(
                query, Products::
                class.java
            )
            .build()
    }

    fun getProducts(
        searchQuery: String,
        category: String
    ): FirestoreRecyclerOptions<Products> {
        Log.d(TAG, "query firebase source: $searchQuery")
        val query = if (!TextUtils.isEmpty(searchQuery) && category != "") {
            firestore.collection(Constant.PRODUCTS)
                .whereEqualTo(Constant.NAME, searchQuery)
                .whereEqualTo(Constant.CATEGORY, category)
        } else if (TextUtils.isEmpty(searchQuery) && category != "") {
            firestore.collection(Constant.PRODUCTS)
                .whereEqualTo(Constant.CATEGORY, category)
        } else if (!TextUtils.isEmpty(searchQuery) && category == "") {
            firestore.collection(Constant.PRODUCTS)
                .whereEqualTo(Constant.NAME, searchQuery)
        } else {
            Log.d(TAG, "daftar semua")
            firestore.collection(Constant.PRODUCTS)
                .orderBy(Constant.NAME, Query.Direction.ASCENDING)
        }
        return FirestoreRecyclerOptions.Builder<Products>()
            .setQuery(query, Products::class.java)
            .build()
    }

    fun getLowStockProducts(
        searchQuery: String
    ): FirestoreRecyclerOptions<Products> {
        val query = if (!TextUtils.isEmpty(searchQuery)) {
            firestore.collection(Constant.PRODUCTS)
                .whereEqualTo(Constant.IS_LOW, true)
                .whereEqualTo(Constant.NAME, searchQuery)

        } else {
            firestore.collection(Constant.PRODUCTS)
                .whereEqualTo(Constant.IS_LOW, true)
        }

        return FirestoreRecyclerOptions.Builder<Products>()
            .setQuery(query, Products::class.java)
            .build()
    }

    fun getProfileLogs(userName: String): FirestoreRecyclerOptions<Logs> {
        val query =
            firestore.collection(Constant.LOGS)
                .whereEqualTo(Constant.OWNER, userName)
                .orderBy(Constant.BY_DATE, Query.Direction.DESCENDING)
        return FirestoreRecyclerOptions.Builder<Logs>()
            .setQuery(query, Logs::class.java)
            .build()
    }

    fun getLogs(sortBy: String): FirestoreRecyclerOptions<Logs> {
        Log.d(TAG, "sortBy: $sortBy")
        val query = when (sortBy) {
            Constant.BY_DATE -> {
                firestore.collection(Constant.LOGS)
                    .orderBy(Constant.BY_DATE, Query.Direction.DESCENDING)
            }
            Constant.BY_NAME -> {
                firestore.collection(Constant.LOGS)
                    .orderBy(Constant.BY_NAME, Query.Direction.ASCENDING)
            }
            else -> {
                firestore.collection(Constant.LOGS)
                    .orderBy(Constant.BY_STATUS, Query.Direction.ASCENDING)
            }
        }
        return FirestoreRecyclerOptions.Builder<Logs>()
            .setQuery(query, Logs::class.java)
            .build()
    }

    fun getSellLogs(): FirestoreRecyclerOptions<SellLogs> {
        val query = firestore.collection(Constant.SELL_LOGS)

        return FirestoreRecyclerOptions.Builder<SellLogs>()
            .setQuery(query, SellLogs::class.java)
            .build()
    }

    fun addLog(hashMapLog: HashMap<String, Any>) {
        val document = firestore.collection(Constant.LOGS)
            .document()
        hashMapLog[Constant.LOG_ID] = document.id
        document.set(hashMapLog)
    }

    fun addSellLog(hashMapLog: HashMap<String, Any>) {
        val document = firestore.collection(Constant.SELL_LOGS)
            .document()
        hashMapLog[Constant.LOG_ID] = document.id
        document.set(hashMapLog)
    }

    fun uploadImage(imageReference: String, file: Uri?): StorageTask<UploadTask.TaskSnapshot> {
        var productRef = storageRef.child(imageReference)
        val name = "products/${productRef.name}"
        Log.d(TAG, "nama vs reference $name dan $imageReference")
        if (name == imageReference) {

            productRef.delete()
                .addOnSuccessListener {
                    Log.d(TAG, "berhasil dihapus")
                }
                .addOnFailureListener {
                    Log.d(TAG, "gagal menghapus")
                }
            productRef = storageRef.child(imageReference)
            Log.d(TAG, "download url: ${productRef.downloadUrl}")
        }
        return productRef.putFile(file!!)
    }


    fun deleteImage(product: Products) {
        val productRef = storageRef.child(product.image)
        productRef.delete()
            .addOnSuccessListener {
                product.image = ""
            }
    }
}