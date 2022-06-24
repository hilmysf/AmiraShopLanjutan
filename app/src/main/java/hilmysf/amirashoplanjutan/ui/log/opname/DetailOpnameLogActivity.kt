package hilmysf.amirashoplanjutan.ui.log.opname

import android.content.ContentValues.TAG
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import dagger.hilt.android.AndroidEntryPoint
import hilmysf.amirashoplanjutan.R
import hilmysf.amirashoplanjutan.data.source.entities.Logs
import hilmysf.amirashoplanjutan.data.source.entities.SellLogs
import hilmysf.amirashoplanjutan.databinding.ActivityDetailOpnameLogBinding
import hilmysf.amirashoplanjutan.databinding.ActivityDetailSellLogBinding
import hilmysf.amirashoplanjutan.helper.Helper
import hilmysf.amirashoplanjutan.ui.log.sell.DetailSellLogAdapter

@AndroidEntryPoint
class DetailOpnameLogActivity : AppCompatActivity() {
    companion object {
        const val OPNAME_LOGS_BUNDLE = "opname_logs_bundle"
    }

    private var productList: ArrayList<HashMap<String, ArrayList<Any>>> = arrayListOf()
    private var detailSellLogAdapter: DetailOpnameAdapter? = null
    private lateinit var binding: ActivityDetailOpnameLogBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailOpnameLogBinding.inflate(layoutInflater)
        setContentView(binding.root)
        val opnameLogsBundle = intent.extras?.getParcelable<Logs>(OPNAME_LOGS_BUNDLE)
        Log.d(TAG, "extras: $opnameLogsBundle")
        val changedAttribute = opnameLogsBundle?.changedAttribute as HashMap<String, ArrayList<Any>>
        bind(opnameLogsBundle)
        changedAttribute.let { getLogsList(it) }
        binding.btnBackHome.setOnClickListener {
            onBackPressed()
        }
    }

    private fun bind(logs: Logs) {
        binding.apply {
            tvOwner.text = "Admin: ${Helper.camelCase(logs.owner)}"
            tvTimestamp.text = "${logs.date} ${logs.time}"
            tvProductName.text = Helper.camelCase(logs.productName)
            tvStatus.text = logs.status
        }
    }

    private fun getLogsList(changedAttribute: HashMap<String, ArrayList<Any>>) {
        Log.d(TAG, "changedAttribute: $changedAttribute")
        val attribute = changedAttribute.keys.toList()
        val attributeFromTo = changedAttribute.values.toList()
        var arrayAttribute = arrayListOf<HashMap<String, ArrayList<Any>>>()
        attribute.forEachIndexed { i, _ ->
            arrayAttribute.add(hashMapOf(attribute[i] to attributeFromTo[i]))
        }
        Log.d(TAG, "arrayAttribute: $arrayAttribute")
        detailSellLogAdapter = DetailOpnameAdapter(arrayAttribute)
        with(binding.rvDetailLog) {
            layoutManager = LinearLayoutManager(context)
            setHasFixedSize(true)
            adapter = detailSellLogAdapter
            addItemDecoration(DividerItemDecoration(context, DividerItemDecoration.VERTICAL))
        }
    }
}