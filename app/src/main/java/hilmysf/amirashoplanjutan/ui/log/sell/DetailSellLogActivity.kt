package hilmysf.amirashoplanjutan.ui.log.sell

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import dagger.hilt.android.AndroidEntryPoint
import hilmysf.amirashoplanjutan.data.source.entities.SellLogs
import hilmysf.amirashoplanjutan.databinding.ActivityDetailSellLogBinding
import hilmysf.amirashoplanjutan.helper.Helper

@AndroidEntryPoint
class DetailSellLogActivity : AppCompatActivity() {
    companion object {
        const val SELL_LOGS_BUNDLE = "sell_logs_bundle"
    }

    private var productList: ArrayList<HashMap<String, ArrayList<Any>>> = arrayListOf()
    private var detailSellLogAdapter: DetailSellLogAdapter? = null
    private lateinit var binding: ActivityDetailSellLogBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailSellLogBinding.inflate(layoutInflater)
        setContentView(binding.root)
        val sellLogsBundle = intent.extras?.getParcelable<SellLogs>(SELL_LOGS_BUNDLE)
        val hashMapProducts = sellLogsBundle?.hashMapProducts
        bind(sellLogsBundle)
        hashMapProducts?.let { getLogsList(it) }
        binding.btnBackHome.setOnClickListener {
            onBackPressed()
        }
    }

    private fun bind(sellLogs: SellLogs?) {
        var totalPrice: Long = 0
        binding.apply {
            tvOwner.text = "Admin: ${sellLogs?.owner}"
            tvTimestamp.text = "${sellLogs?.date} ${sellLogs?.time}"
            sellLogs!!.hashMapProducts.forEach { (_, list) ->
                totalPrice += list[0] as Long
            }
            tvTotalValue.text = "Rp. ${Helper.currencyFormatter(totalPrice)}"
        }
    }

    private fun getLogsList(hashMapProducts: HashMap<String, ArrayList<Any>>) {
        val productsName = hashMapProducts.keys.toList()
        val productsList = hashMapProducts.values.toList()
        var arrayProducts = arrayListOf<HashMap<String, ArrayList<Any>>>()
        productsName.forEachIndexed { i, _ ->
            arrayProducts.add(hashMapOf(productsName[i] to productsList[i]))
        }
        detailSellLogAdapter = DetailSellLogAdapter(arrayProducts)
        with(binding.rvDetailLog) {
            layoutManager = LinearLayoutManager(context)
            setHasFixedSize(true)
            adapter = detailSellLogAdapter
            addItemDecoration(DividerItemDecoration(context, DividerItemDecoration.VERTICAL))
        }
    }
}