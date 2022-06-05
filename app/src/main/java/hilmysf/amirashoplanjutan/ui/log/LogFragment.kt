package hilmysf.amirashoplanjutan.ui.log

import android.content.ContentValues.TAG
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.ktx.storage
import dagger.hilt.android.AndroidEntryPoint
import hilmysf.amirashoplanjutan.data.source.entities.Logs
import hilmysf.amirashoplanjutan.databinding.FragmentLogBinding

@AndroidEntryPoint
class LogFragment : Fragment() {
    private var logAdapter: LogAdapter? = null
    private lateinit var binding: FragmentLogBinding
    private lateinit var firestore: FirebaseFirestore
    private lateinit var options: FirestoreRecyclerOptions<Logs>
    private lateinit var hashMapLog: HashMap<String, Any>
    private val viewModel: LogViewModel by viewModels()
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentLogBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        firestore = FirebaseFirestore.getInstance()
        hashMapLog = HashMap()
        val storageReference = Firebase.storage.reference
        getLogsList(storageReference)
//        val navController = Navigation.findNavController(view)
//        fetchData()
//        getProductsList(navController, storageReference)
//        binding.productsFab.setOnClickListener {
//            navController.navigate(R.id.action_product_to_detailProductActivity)
//        }
    }

    //    private fun addLogData(){
//        hashMapLog = hashMapOf(
//            Constant.NAME to name,
//            Constant.QUANTITY to quantity,
//            Constant.STATUS to status,
//            Constant.DATE to date,
//            Constant.TIME to time,
//            Constant.IMAGE to imageReference,
//
//        )
//        viewModel.addLogData(has)
//    }
    private fun getLogsList(storageReference: StorageReference) {
        options = viewModel.getLogsData()
        Log.d(TAG, "DocumentSnapshot Home: $options")
        logAdapter = LogAdapter(context, options, storageReference)
        with(binding.rvLogs) {
            layoutManager = LinearLayoutManager(context)
            setHasFixedSize(true)
            adapter = logAdapter
            Log.d(TAG, "isi adapter: ${adapter?.itemCount}")
        }
    }
    override fun onStart() {
        super.onStart()
        logAdapter?.startListening()
    }

    override fun onStop() {
        super.onStop()
        logAdapter?.stopListening()
    }
}