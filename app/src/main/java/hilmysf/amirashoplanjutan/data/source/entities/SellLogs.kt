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
    var owner: String = "",
    var hashMapProducts: @RawValue HashMap<String, ArrayList<Any>> = hashMapOf()
) : Parcelable
