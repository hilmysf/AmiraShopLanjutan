package hilmysf.amirashoplanjutan.ui.log

import android.content.ContentValues
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.ktx.storage
import dagger.hilt.android.AndroidEntryPoint
import hilmysf.amirashoplanjutan.R
import hilmysf.amirashoplanjutan.data.source.entities.Logs
import hilmysf.amirashoplanjutan.data.source.entities.SellLogs
import hilmysf.amirashoplanjutan.databinding.FragmentOpnameLogBinding
import hilmysf.amirashoplanjutan.databinding.FragmentSellLogBinding

@AndroidEntryPoint
class SellLogFragment : Fragment() {
    private var sellLogAdapter: SellLogAdapter? = null
    private lateinit var binding: FragmentSellLogBinding
    private lateinit var firestore: FirebaseFirestore
    private val viewModel: LogViewModel by viewModels()
    private lateinit var options: FirestoreRecyclerOptions<SellLogs>
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentSellLogBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        firestore = FirebaseFirestore.getInstance()
        val storageReference = Firebase.storage.reference
        getLogsList(storageReference)
    }

    private fun getLogsList(storageReference: StorageReference) {
        options = viewModel.getSellLogs()
        Log.d(ContentValues.TAG, "DocumentSnapshot Home: $options")
        sellLogAdapter = SellLogAdapter(context, options)
        with(binding.rvSellLogs) {
            layoutManager = LinearLayoutManager(context)
            setHasFixedSize(true)
            adapter = sellLogAdapter
            Log.d(ContentValues.TAG, "isi adapter: ${adapter?.itemCount}")
            addItemDecoration(DividerItemDecoration(context, DividerItemDecoration.VERTICAL))
        }
    }

    override fun onStart() {
        super.onStart()
        sellLogAdapter?.startListening()
    }

    override fun onStop() {
        super.onStop()
        sellLogAdapter?.stopListening()
    }
}