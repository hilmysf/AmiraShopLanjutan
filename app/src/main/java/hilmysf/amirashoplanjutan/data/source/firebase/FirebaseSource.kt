package hilmysf.amirashoplanjutan.data.source.firebase

import android.content.ContentValues.TAG
import android.util.Log
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import hilmysf.amirashoplanjutan.data.source.entities.Logs
import hilmysf.amirashoplanjutan.data.source.entities.Products
import hilmysf.amirashoplanjutan.helper.Constant
import com.google.firebase.firestore.FirebaseFirestoreException

import com.google.firebase.firestore.QuerySnapshot




class FirebaseSource {
    private val firebaseAuth: FirebaseAuth by lazy {
        FirebaseAuth.getInstance()
    }
    private val firestore: FirebaseFirestore by lazy {
        FirebaseFirestore.getInstance()
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

    fun addProduct(hashMapProduct: HashMap<String, Any>) {
        val document = firestore.collection(Constant.PRODUCTS)
            .document()
        hashMapProduct[Constant.PRODUCT_ID] = document.id
        document.set(hashMapProduct)
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

    fun addLog(hashMapLog: HashMap<String, Any>) {
        val document = firestore.collection(Constant.LOGS)
            .document()
        hashMapLog[Constant.LOG_ID] = document.id
        document.set(hashMapLog)
    }

    fun getProducts(): FirestoreRecyclerOptions<Products> {
        val query = firestore.collection(Constant.PRODUCTS)
            .orderBy(Constant.NAME, Query.Direction.ASCENDING)
        return FirestoreRecyclerOptions.Builder<Products>()
            .setQuery(query, Products::class.java)
            .build()
    }

    fun searchProducts(searchQuery: String): FirestoreRecyclerOptions<Products>{
        val query = firestore.collection(Constant.PRODUCTS)
            .whereLessThanOrEqualTo(Constant.NAME, searchQuery)
            .orderBy(Constant.NAME, Query.Direction.ASCENDING)
//            .addSnapshotListener { value, error ->
//                if (error != null) {
//                    Log.w(TAG, "Listen failed.", e)
//                    return@addSnapshotListener
//                }
//                val products = ArrayList<String>()
////                var products = value?.toObjects(Products::class.java)
//                for(doc in value!!){
//                    doc.getString(Constant.NAME)?.let {
//                        products.add(it)
//                    }
//                }
//            }
        return FirestoreRecyclerOptions.Builder<Products>()
            .setQuery(query, Products::class.java)
//            .setQuery()
            .build()
    }

    fun getLogs(): FirestoreRecyclerOptions<Logs> {
        val query = firestore.collection(Constant.LOGS)
            .orderBy(Constant.DATE, Query.Direction.DESCENDING)
        return FirestoreRecyclerOptions.Builder<Logs>()
            .setQuery(query, Logs::class.java)
            .build()
    }
}