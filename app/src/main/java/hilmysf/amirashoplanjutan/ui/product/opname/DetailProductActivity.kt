package hilmysf.amirashoplanjutan.ui.product.opname

import android.Manifest
import android.content.ContentValues.TAG
import android.content.Context
import android.content.Intent
import android.content.Intent.ACTION_OPEN_DOCUMENT
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.net.ConnectivityManager
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.provider.MediaStore
import android.text.TextUtils
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.WindowManager
import android.widget.*
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.navigation.navArgs
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.ktx.storage
import dagger.hilt.android.AndroidEntryPoint
import hilmysf.amirashoplanjutan.R
import hilmysf.amirashoplanjutan.data.source.entities.Products
import hilmysf.amirashoplanjutan.databinding.ActivityDetailProductBinding
import hilmysf.amirashoplanjutan.helper.Constant
import hilmysf.amirashoplanjutan.helper.DateHelper
import hilmysf.amirashoplanjutan.helper.GlideApp
import hilmysf.amirashoplanjutan.helper.Helper
import hilmysf.amirashoplanjutan.network.InternetChangeReceiver
import hilmysf.amirashoplanjutan.notification.NotificationManagers
import java.io.ByteArrayOutputStream
import java.util.*
import kotlin.collections.HashMap


@AndroidEntryPoint
class DetailProductActivity : AppCompatActivity(),
    AdapterView.OnItemSelectedListener, InternetChangeReceiver.ConnectivityReceiverListener {
    companion object {
        private val REQUIRED_PERMISSIONS = arrayOf(Manifest.permission.CAMERA)
        private const val REQUEST_CODE_PERMISSIONS = 10
        const val PRODUCTS_BUNDLE = "products_bundle"
    }

    private lateinit var storage: FirebaseStorage
    private lateinit var storageRef: StorageReference
    private lateinit var binding: ActivityDetailProductBinding
    private lateinit var spinner: Spinner
    private lateinit var firestore: FirebaseFirestore
    private var imageReference: String = ""
    private var name: String = ""
    private var quantity: Int = 0
    private var status: String = ""
    private var product: Products? = null
    private lateinit var mAuth: FirebaseAuth
    private lateinit var userId: String
    private var selectedImgUri: Uri = Uri.EMPTY
    private var hashMapProduct: HashMap<String, Any> = HashMap()
    private var changedAttribute: HashMap<String, ArrayList<Any>> = HashMap()
    private val viewModel: ProductViewModel by viewModels()
    private val args: DetailProductActivityArgs by navArgs()
    private var userName: String = ""

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu, menu)
        val item = menu?.findItem(R.id.action_delete_item)
        if (product == null) {
            item?.isVisible = false
            this.invalidateOptionsMenu()
        }
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_delete_item -> {
                val options = arrayOf<CharSequence>("Batal", "Hapus")
                androidx.appcompat.app.AlertDialog.Builder(this)
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
                            deleteProduct(product)
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
        registerReceiver(
            InternetChangeReceiver(),
            IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION)
        )
        supportActionBar?.title = "Stok Barang"
        if (!allPermissionsGranted()) {
            ActivityCompat.requestPermissions(
                this,
                REQUIRED_PERMISSIONS,
                REQUEST_CODE_PERMISSIONS
            )
        }
        val arguments = args
        val intent = intent.getParcelableExtra<Products>(PRODUCTS_BUNDLE)
        mAuth = FirebaseAuth.getInstance()
        userId = mAuth.currentUser!!.uid
        product = if (arguments.product == null) {
            intent
        } else {
            arguments.product
        }
        val sharedPreferences =
            getSharedPreferences(Constant.SHARED_PREFERENCES, Context.MODE_PRIVATE)
        userName = sharedPreferences?.getString(Constant.NAME, "User").toString()
        storage = Firebase.storage
        storageRef = storage.reference
        spinner = binding.spinnerCategory
        firestore = FirebaseFirestore.getInstance()
        spinnerAdapterConf(product)
        attachProduct(product)
        onClick(product)
    }


    private fun attachProduct(product: Products?) {
        if (product != null) {
            binding.apply {
                name = product.name
                val pathReference = storageRef.child(product.image)
                GlideApp.with(applicationContext)
                    .load(pathReference)
                    .diskCacheStrategy(DiskCacheStrategy.NONE)
                    .skipMemoryCache(true)
                    .centerInside()
                    .into(imgProduct)
                edtProductName.setText(product.name)
                quantityNumberPicker.value = product.quantity
                minQuantityNumberPicker.value = product.minQuantity
                edtPriceValue.setText(product.price.toString())
            }
        }
    }

    private fun onClick(product: Products?) {
        binding.imgProduct.setOnClickListener {
            val options = arrayOf<CharSequence>("Foto", "Gallery")
            androidx.appcompat.app.AlertDialog.Builder(this).apply {
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
            binding.progressBar.visibility = View.VISIBLE
            binding.apply {
                if (imgProduct.drawable != null && !TextUtils.isEmpty(edtProductName.text) && !TextUtils.isEmpty(
                        edtPriceValue.text
                    )
                ) {
                    if (product == null) {
                        Log.d(TAG, "Ini tambah data")
                        status = "Menambah"
                        addProduct()
                    } else {
                        Log.d(TAG, "Ini edit data")
                        status = "Mengubah"
                        editProduct(product)
                        Log.d(TAG, "islow: $product")
                    }
                    Handler(Looper.getMainLooper()).postDelayed({
                        finish()
                        binding.progressBar.visibility = View.GONE
                    }, 2000)
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

    private fun attributeChanges(
        name: String,
        quantity: Int,
        minQuantity: Int,
        category: String,
        priceCheck: Long
    ) {
        if (name != product?.name) {
            changedAttribute[Constant.NAME] = arrayListOf(product!!.name, name)
        }
        if (quantity != product?.quantity) {
            changedAttribute[Constant.QUANTITY] = arrayListOf(product!!.quantity, quantity)
        }
        if (minQuantity != product?.minQuantity) {
            changedAttribute[Constant.MIN_QUANTITY] =
                arrayListOf(product!!.minQuantity, minQuantity)
        }
        if (category != product?.category) {
            changedAttribute[Constant.CATEGORY] = arrayListOf(product!!.category, category)
        }
        if (priceCheck != product?.price) {
            changedAttribute[Constant.PRICE] = arrayListOf(product!!.price, priceCheck)
        }
    }

    private fun bind(isEdit: Boolean) {
        name = binding.edtProductName.text.toString().lowercase()
        quantity = binding.quantityNumberPicker.value
        val minQuantity = binding.minQuantityNumberPicker.value
        val category = binding.spinnerCategory.selectedItem.toString()
        val price = binding.edtPriceValue.text
        val priceCheck = if (price.isNullOrEmpty()) 0 else price.toString().toLong()
        val isLow: Boolean = quantity <= minQuantity
        Log.d(TAG, "isLow bind: $isLow")
        if (isEdit) {
            attributeChanges(name, quantity, minQuantity, category, priceCheck)
        }
        hashMapProduct = hashMapOf(
            Constant.NAME to name,
            Constant.QUANTITY to quantity,
            Constant.MIN_QUANTITY to minQuantity,
            Constant.CATEGORY to category,
            Constant.PRICE to priceCheck,
            Constant.IS_LOW to isLow
        )
    }

    private fun deleteProduct(product: Products?) {
        Log.d(TAG, "delete data: $product")
        product?.let {
            viewModel.deleteProduct(it)
            addLogData(it.image)
            Helper.makeToast(applicationContext, "Berhasil menghapus ${it.name}")
        }
    }

    private fun addProduct() {
        bind(false)
        viewModel.addProduct(hashMapProduct)
            .addSnapshotListener { value, _ ->
                if (value != null) {
                    val image = value.getString(Constant.IMAGE)
                    imageReference = image.toString()
                    Log.d(TAG, "imageref: $imageReference dan uri: $selectedImgUri")
                    uploadToCloud(imageReference, selectedImgUri)
                    addLogData(imageReference)
                }
            }
        Helper.makeToast(applicationContext, "Berhasil menambah ${product?.name}")
        Log.d(TAG, "nambah data $product")

    }

    private fun editProduct(product: Products?) {
        bind(true)
        Log.d(TAG, "edit data $product")
        if (product != null) {
            val image = product.image
            hashMapProduct[Constant.PRODUCT_ID] = product.productId
            hashMapProduct[Constant.IMAGE] = image
            if (selectedImgUri != Uri.EMPTY) {
                Log.d(TAG, "edit foto uri: $selectedImgUri")
                uploadToCloud(image, selectedImgUri)
                Log.d(TAG, "edit imageref: $image dan uri: $selectedImgUri")
            } else {
                Log.d(TAG, "Selected IMG URI: $selectedImgUri")
            }
            if (changedAttribute.isNotEmpty()) {
                addLogData(product.image)
            }
        }
        if (hashMapProduct[Constant.IS_LOW] == true) {
            NotificationManagers.triggerNotification(applicationContext, hashMapProduct)
        }
        product?.let {
            viewModel.editProduct(it, hashMapProduct)
            Helper.makeToast(applicationContext, "Berhasil mengubah ${it.name}")
        }
    }

    private fun addLogData(imageReference: String) {
        val date = DateHelper.dateFormat()
        val time = DateHelper.timeFormat()
        val created = FieldValue.serverTimestamp()
        val quantityString = "$quantity Barang"
        val message = "${messageBuilder()} oleh $userName"
        val hashMapLog = hashMapOf(
            Constant.CREATED to created,
            Constant.PRODUCT_NAME to name,
            Constant.QUANTITY to quantityString,
            Constant.STATUS to status,
            Constant.DATE to date,
            Constant.TIME to time,
            Constant.IMAGE to imageReference,
            Constant.CHANGED_ATTRIBUTE to changedAttribute,
            Constant.OWNER to userName,
            Constant.MESSAGE to message
        )
        viewModel.addLogData(hashMapLog)
        Log.d(TAG, "Add Log Data ya $hashMapLog")
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
        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE).apply {
            addFlags(Intent.FLAG_GRANT_PERSISTABLE_URI_PERMISSION)
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        }
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
        val progressBar = binding.progressBar
        viewModel.uploadImage(imageReference, file)
            .addOnProgressListener {
                progressBar.visibility = View.VISIBLE
                window.setFlags(
                    WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                    WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE
                )
            }
            .addOnSuccessListener {
                Log.d(TAG, "Berhasil upload foto")
                progressBar.visibility = View.GONE
                window.clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
            }
            .addOnFailureListener {
                progressBar.visibility = View.GONE
                Log.d(TAG, "gagal upload foto")
            }
    }

    override fun onStart() {
        super.onStart()
        NotificationManagers.createNotificationChannel(this)
    }

    override fun onNetworkConnectionChanged(isConnected: Boolean) {
        Helper.showNetworkMessage(isConnected, this, binding)
    }

    override fun onResume() {
        super.onResume()
        InternetChangeReceiver.connectivityReceiverListener = this
    }
}