package hilmysf.amirashoplanjutan.data.source.entities

import android.os.Parcelable
import com.google.firebase.firestore.ServerTimestamp
import kotlinx.parcelize.Parcelize
import kotlinx.parcelize.RawValue
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.HashMap

@Parcelize
data class SellLogs(
    var logId: String = "",
    @ServerTimestamp
    var created: Date? = null,
    var date: String = "",
    var time: String = "",
    var quantity: String = "",
    var arrayProducts: @RawValue ArrayList<HashMap<String, ArrayList<Any>>> = arrayListOf()
) : Parcelable
