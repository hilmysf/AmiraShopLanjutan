package hilmysf.amirashoplanjutan.ui.product.opname

import android.Manifest
import android.content.ContentValues.TAG
import android.content.Context
import android.content.Intent
import android.content.Intent.ACTION_OPEN_DOCUMENT
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.text.TextUtils
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.*
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat

import androidx.navigation.navArgs
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import dagger.hilt.android.AndroidEntryPoint
import hilmysf.amirashoplanjutan.R
import hilmysf.amirashoplanjutan.databinding.ActivityDetailProductBinding
import hilmysf.amirashoplanjutan.helper.Constant
import hilmysf.amirashoplanjutan.helper.Helper
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.ktx.storage
import hilmysf.amirashoplanjutan.data.source.entities.Products
import hilmysf.amirashoplanjutan.helper.DateHelper
import hilmysf.amirashoplanjutan.helper.GlideApp
import hilmysf.amirashoplanjutan.ui.product.ProductViewModel
import java.io.ByteArrayOutputStream
import java.text.NumberFormat
import java.util.*
import kotlin.collections.HashMap


@AndroidEntryPoint
class DetailProductActivity : AppCompatActivity(),
    AdapterView.OnItemSelectedListener {
    companion object {
        private val REQUIRED_PERMISSIONS = arrayOf(Manifest.permission.CAMERA)
        private const val REQUEST_CODE_PERMISSIONS = 10
    }

    private lateinit var storage: FirebaseStorage
    private lateinit var storageRef: StorageReference
    private lateinit var binding: ActivityDetailProductBinding
    private lateinit var spinner: Spinner
    private lateinit var firestore: FirebaseFirestore
    private var imageReference: String = ""
    private var productId: String = ""
    private var name: String = ""
    private var quantity: Long = 0
    private var status: String = ""
    private var product: Products? = null
    private lateinit var mAuth: FirebaseAuth
    private lateinit var userId: String
    private lateinit var selectedImgUri: Uri
    private lateinit var hashMapLog: HashMap<String, Any>
    private lateinit var hashMapProduct: HashMap<String, Any>
    private val viewModel: ProductViewModel by viewModels()
    private val args: DetailProductActivityArgs by navArgs()

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_delete_item -> {
                val options = arrayOf<CharSequence>("Batal", "Hapus")
                AlertDialog.Builder(this)
                    .apply {
                        setTitle("Apakah Anda yakin?")
                        setMessage("Apakah Anda ingin menghapus produk ini? Produk yang telah dihapus tidak dapat dikembalikan")
                        setNegativeButton(
                            options[0]
                        ) { dialog, _ ->
                            dialog.dismiss()
                        }
                        setPositiveButton(options[1]) { _, _ ->
                            status = "Menghapus"
                            deleteData(product)
                            finish()
                        }
                        show()
                    }
                true
            }
            else -> {
                super.onOptionsItemSelected(item)
            }
        }
    }

    private fun allPermissionsGranted() = REQUIRED_PERMISSIONS.all {
        ContextCompat.checkSelfPermission(baseContext, it) == PackageManager.PERMISSION_GRANTED
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == REQUEST_CODE_PERMISSIONS) {
            if (!allPermissionsGranted()) {
                Toast.makeText(
                    this,
                    "Tidak mendapatkan permission.",
                    Toast.LENGTH_SHORT
                ).show()
                finish()
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailProductBinding.inflate(layoutInflater)
        setContentView(binding.root)
        supportActionBar?.title = "Stok Barang"
        if (!allPermissionsGranted()) {
            ActivityCompat.requestPermissions(
                this,
                REQUIRED_PERMISSIONS,
                REQUEST_CODE_PERMISSIONS
            )
        }
        val arguments = args
        mAuth = FirebaseAuth.getInstance()
        userId = mAuth.currentUser!!.uid
        product = arguments.product
        storage = Firebase.storage
        storageRef = storage.reference
        spinner = binding.spinnerCategory
        firestore = FirebaseFirestore.getInstance()
        hashMapProduct = HashMap()
        hashMapLog = HashMap()
        getUser()
        spinnerAdapterConf(product)
        attachProduct(product)
        onClick(product)

    }

//    private fun currencyFormatter(number: Long): String =
//        NumberFormat.getNumberInstance(Locale.US).format(number)

    private fun attachProduct(product: Products?) {
        if (product != null) {
            binding.apply {
//                productId = product.productId
                name = product.name
                Log.d(TAG, "uri foto: ${product.image}")
                val pathReference = storageRef.child(product.image)
                GlideApp.with(applicationContext)
                    .load(pathReference)
                    .into(imgProduct)
                edtProductName.setText(product.name)
                quantityNumberPicker.value = product.quantity.toInt()
                minQuantityNumberPicker.value = product.minQuantity.toInt()
//                edtPriceValue.setText(currencyFormatter(product.price))
                edtPriceValue.setText(product.price.toString())
            }
        }
    }

    private fun onClick(product: Products?) {
        binding.imgProduct.setOnClickListener {
            val options = arrayOf<CharSequence>("Foto", "Gallery")
            AlertDialog.Builder(this).apply {
                setTitle("Mau foto apa gallery")
                setItems(options) { _, item ->
                    if (options[item] == "Foto") {
                        startTakePhoto()
                    } else {
                        startGallery()
                    }
                }
                show()
            }
        }
        binding.btnAddItem.setOnClickListener {
            binding.apply {
                if (imgProduct.drawable != null && !TextUtils.isEmpty(edtProductName.text) && !TextUtils.isEmpty(
                        edtPriceValue.text
                    )
                ) {
                    if (product == null) {
                        Log.d(TAG, "Ini tambah data")
                        status = "Menambah"
                        addData()
                    } else {
                        Log.d(TAG, "Ini edit data")
                        status = "Mengubah"
                        editData(product)
                    }
                    addLogData()
                    Log.d(TAG, "Add Log Data ya")
                    finish()
                } else {
                    Helper.alertDialog(
                        this@DetailProductActivity,
                        "Isi data lengkap!",
                        "Foto, nama, atau harga produk belum diinput"
                    )
                }
            }
        }
    }

    private fun spinnerAdapterConf(product: Products?) {
        ArrayAdapter.createFromResource(
            this,
            R.array.products_category,
            android.R.layout.simple_spinner_item
        ).also { adapter ->
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            spinner.adapter = adapter
            if (product != null) {
                val spinnerPosition = adapter.getPosition(product.category)
                spinner.setSelection(spinnerPosition)
            }
        }
    }

    override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
    }

    override fun onNothingSelected(p0: AdapterView<*>?) {
    }

    private fun bind(isEdit: Boolean) {
        name = binding.edtProductName.text.toString()
        quantity = binding.quantityNumberPicker.value.toLong()
        val minQuantity = binding.minQuantityNumberPicker.value.toLong()
        val category = binding.spinnerCategory.selectedItem.toString()
        val price = binding.edtPriceValue.text
        val priceCheck = if (price.isNullOrEmpty()) 0 else price.toString().toLong()
        imageReference = if (!isEdit) {
            "products/$name.png"
    //            productId = "$name-$category"
    //            hashMapProduct[Constant.PRODUCT_ID] = productId
        } else {
            "products/${product?.name}.png"
    //            productId = product?.productId.toString()
    //            hashMapProduct[Constant.PRODUCT_ID] = product?.productId.toString()
        }
        hashMapProduct = hashMapOf(
            Constant.NAME to name,
            Constant.QUANTITY to quantity,
            Constant.MIN_QUANTITY to minQuantity,
            Constant.CATEGORY to category,
            Constant.PRICE to priceCheck,
            Constant.IMAGE to imageReference
        )
    }

    private fun deleteData(product: Products?) {
        Log.d(TAG, "delete data: $product")
        product?.let { viewModel.deleteProduct(it) }
    }

    private fun addData() {
        bind(false)
        uploadToCloud(imageReference, selectedImgUri)
//        firestore.collection(Constant.PRODUCTS).document(productId)
//            .set(hashMapProduct)
        viewModel.addProduct(hashMapProduct)
        Log.d(TAG, "nambah data $product")
    }

    private fun editData(product: Products?) {
        bind(true)
        Log.d(TAG, "edit data $product")
        if (product != null) {
            hashMapProduct[Constant.PRODUCT_ID] = product.productId
        }
        product?.let { viewModel.editProduct(it, hashMapProduct) }
    }

    private fun getUser() {
        viewModel.getUser(userId)
            .addOnSuccessListener { document ->
                if (document != null) {
                    val name = document.getString(Constant.NAME)
                    val message = "${messageBuilder()} oleh $name"
                    hashMapLog[Constant.MESSAGE] = message
                    Log.d(TAG, "DocumentSnapshot data: ${document.data}")
                } else {
                    Log.d(TAG, "No such document")
                }
            }
            .addOnFailureListener { exception ->
                Log.d(TAG, "get failed with ", exception)
            }
    }

    private fun addLogData() {
        val date = DateHelper.dateFormat()
        val time = DateHelper.timeFormat()
        val created = DateHelper.createdFormat()
        val quantityString = "$quantity Barang"
//        val message = "${messageBuilder()} oleh $userName"
//        val timestamp = FieldValue.serverTimestamp()
        hashMapLog = hashMapOf(
            Constant.CREATED to created,
            Constant.NAME to name,
            Constant.QUANTITY to quantityString,
            Constant.STATUS to status,
            Constant.DATE to date,
            Constant.TIME to time,
            Constant.IMAGE to imageReference,
//            Constant.MESSAGE to message
        )
        viewModel.addLogData(hashMapLog)
    }

    private fun messageBuilder(): String {
        var message = ""
        when (status) {
            "Menambah" -> message = "Ditambah"
            "Menghapus" -> message = "Dihapus"
            "Mengubah" -> message = "Diubah"
        }
        return message
    }

    private val launcherIntentGallery = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == RESULT_OK) {
            selectedImgUri = result.data?.data as Uri
            applicationContext.contentResolver.takePersistableUriPermission(
                selectedImgUri,
                Intent.FLAG_GRANT_READ_URI_PERMISSION or Intent.FLAG_GRANT_WRITE_URI_PERMISSION
            )
            binding.imgProduct.apply {
                setImageURI(selectedImgUri)
                Log.d(TAG, "Uri: $selectedImgUri")
            }
        }
    }

    private fun startGallery() {
        val intent = Intent().apply {
            action = ACTION_OPEN_DOCUMENT
            type = "image/*"
            addFlags(Intent.FLAG_GRANT_PERSISTABLE_URI_PERMISSION)
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        }
        val chooser = Intent.createChooser(intent, "Choose a Picture")
        launcherIntentGallery.launch(chooser)
    }

    private val launcherIntentCamera = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == RESULT_OK) {
            val selectedImgBitmap = result?.data?.extras?.get("data") as Bitmap
            selectedImgUri = getImageUri(applicationContext, selectedImgBitmap)
            binding.imgProduct.apply {
                setImageURI(selectedImgUri)
                Log.d(TAG, "Uri: $selectedImgUri")
            }
        }
    }

    private fun startTakePhoto() {
        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        launcherIntentCamera.launch(intent)
    }

    private fun getImageUri(context: Context, image: Bitmap): Uri {
        val bytes = ByteArrayOutputStream()
        image.compress(Bitmap.CompressFormat.JPEG, 100, bytes)
        val path = MediaStore.Images.Media.insertImage(
            context.contentResolver,
            image,
            "Title",
            null
        )
        return Uri.parse(path)
    }

    private fun uploadToCloud(imageReference: String, file: Uri?) {
        val productRef = storageRef.child(imageReference)
        if (file != null) {
            productRef.putFile(file)
        }
    }
}