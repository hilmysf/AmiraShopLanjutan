package hilmysf.amirashoplanjutan.ui.log.sell

import android.content.ContentValues
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.google.firebase.firestore.FirebaseFirestore
import dagger.hilt.android.AndroidEntryPoint
import hilmysf.amirashoplanjutan.data.source.entities.SellLogs
import hilmysf.amirashoplanjutan.databinding.FragmentSellLogBinding
import hilmysf.amirashoplanjutan.ui.log.LogViewModel

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
        getLogsList()
    }

    private fun getLogsList() {
        options = viewModel.getSellLogs()
        Log.d(ContentValues.TAG, "DocumentSnapshot Home: $options")
        sellLogAdapter = SellLogAdapter(context, options)
        with(binding.rvSellLogs) {
            layoutManager = LinearLayoutManager(context)
            setHasFixedSize(true)
            adapter = sellLogAdapter
            Log.d(ContentValues.TAG, "isi adapter: ${adapter?.itemCount}")
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