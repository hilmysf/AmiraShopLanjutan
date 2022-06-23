package hilmysf.amirashoplanjutan.ui.log

import android.app.AlertDialog
import android.content.ContentValues.TAG
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import dagger.hilt.android.AndroidEntryPoint
import hilmysf.amirashoplanjutan.data.source.entities.Logs
import hilmysf.amirashoplanjutan.databinding.FragmentLogBinding
import android.content.Context
import android.widget.RadioButton
import android.widget.RadioGroup
import android.widget.Toast
import androidx.core.view.children
import hilmysf.amirashoplanjutan.R
import hilmysf.amirashoplanjutan.helper.Constant
import hilmysf.amirashoplanjutan.ui.log.opname.LogAdapter


@AndroidEntryPoint
class LogFragment : Fragment() {
    private var logAdapter: LogAdapter? = null
    private lateinit var binding: FragmentLogBinding
    private lateinit var firestore: FirebaseFirestore
    private lateinit var options: FirestoreRecyclerOptions<Logs>
    private lateinit var hashMapLog: HashMap<String, Any>
    private val viewModel: LogViewModel by viewModels()
    private var sortBy = Constant.BY_DATE
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentLogBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        firestore = FirebaseFirestore.getInstance()
        hashMapLog = HashMap()
        val storageReference = Firebase.storage.reference
        val viewPager = binding.viewPager
        val tabLayout = binding.tabs
        tabLayout.setupWithViewPager(viewPager)
        val adapter = SectionsPagerAdapter(childFragmentManager)
        viewPager.adapter = adapter
//        getLogsList(storageReference)
//        binding.ibSort.setOnClickListener {
//            sortDialog()
//        }
    }

//    private fun getLogsList(storageReference: StorageReference) {
//        options = viewModel.getLogsData(sortBy)
//        Log.d(TAG, "DocumentSnapshot Home: $options")
//        logAdapter = LogAdapter(context, options, storageReference)
//        with(binding.rvLogs) {
//            layoutManager = LinearLayoutManager(context)
//            setHasFixedSize(true)
//            adapter = logAdapter
//            Log.d(TAG, "isi adapter: ${adapter?.itemCount}")
//        }
//    }

    override fun onStart() {
        super.onStart()
        logAdapter?.startListening()
    }

    override fun onStop() {
        super.onStop()
        logAdapter?.stopListening()
    }

    private fun sortBy(sortBy: String) {
        when (sortBy) {
            "Nama" -> this.sortBy = Constant.BY_NAME
            "Waktu" -> this.sortBy = Constant.BY_DATE
            "Tipe Transaksi" -> this.sortBy = Constant.BY_STATUS
        }
        Log.d(TAG, "sortBy: $sortBy")
        val newOptions = viewModel.getLogsData(this.sortBy)
        logAdapter?.updateOptions(newOptions)
    }

    private fun sortDialog() {
        val inflater =
            activity!!.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val formsView: View =
            inflater.inflate(R.layout.radio_button_log_sort, binding.root, false)
        val sort = formsView.findViewById<RadioGroup>(R.id.sort)
        val builder = AlertDialog.Builder(context!!, R.style.MultiChoiceAlertDialog).apply {
            setView(formsView)
            setTitle("Urutkan berdasarkan")
        }
        val alert = builder.create()
        sort.setOnCheckedChangeListener { group, checkedId ->
            for (child in group.children) {
                child as RadioButton
                if (child.id == checkedId) {
                    Toast.makeText(context, child.text, Toast.LENGTH_SHORT).show()
                    child.isChecked = true
                    sortBy(child.text.toString())
                    alert.dismiss()
                }
            }
        }
        alert.show()
    }
}