package hilmysf.amirashoplanjutan.ui.log

import androidx.lifecycle.ViewModel
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import dagger.hilt.android.lifecycle.HiltViewModel
import hilmysf.amirashoplanjutan.data.repositories.ProductsRepository
import hilmysf.amirashoplanjutan.data.source.entities.Logs
import hilmysf.amirashoplanjutan.data.source.entities.SellLogs
import javax.inject.Inject

@HiltViewModel
class LogViewModel @Inject constructor(private val productsRepository: ProductsRepository) :
    ViewModel() {

    fun getLogs(sortBy: String): FirestoreRecyclerOptions<Logs> = productsRepository.getLogs(sortBy)

    fun getSellLogs(): FirestoreRecyclerOptions<SellLogs> = productsRepository.getSellLogs()
}