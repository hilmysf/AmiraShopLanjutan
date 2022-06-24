package hilmysf.amirashoplanjutan.ui.log.opname

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.firebase.ui.firestore.FirestoreRecyclerAdapter
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.google.firebase.storage.StorageReference
import hilmysf.amirashoplanjutan.data.source.entities.Logs
import hilmysf.amirashoplanjutan.databinding.ItemLogListBinding
import hilmysf.amirashoplanjutan.helper.GlideApp

class LogAdapter(
    val context: Context?,
    options: FirestoreRecyclerOptions<Logs>,
    private val storageReference: StorageReference
) : FirestoreRecyclerAdapter<Logs, LogAdapter.LogViewHolder?>(options) {

    inner class LogViewHolder(val binding: ItemLogListBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): LogViewHolder {
        val binding =
            ItemLogListBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return LogViewHolder(binding)
    }

    override fun onBindViewHolder(holder: LogViewHolder, position: Int, model: Logs) {
        holder.binding.apply {
            val pathReference = storageReference.child(model.image)
            if (context != null) {
                GlideApp.with(context)
                    .load(pathReference)
                    .into(imgProduct)
            }
            tvProductName.text = model.productName
            tvLogDate.text = model.date
            tvLogTime.text = model.time
            tvLogMessage.text = model.message
            tvStatus.text = model.status
            tvProductStock.text = model.quantity
            cvItemLog.setOnClickListener {
                context?.startActivity(Intent(context, DetailOpnameLogActivity::class.java).apply {
                    putExtra(DetailOpnameLogActivity.OPNAME_LOGS_BUNDLE, model)
                })
            }
        }
    }
}