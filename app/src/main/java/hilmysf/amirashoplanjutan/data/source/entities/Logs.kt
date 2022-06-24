package hilmysf.amirashoplanjutan.data.source.entities

import android.os.Parcelable
import com.google.firebase.firestore.ServerTimestamp
import kotlinx.parcelize.Parcelize
import kotlinx.parcelize.RawValue
import java.util.*

@Parcelize
data class Logs(
    var logId: String = "",
    var image: String = "",
    var productName: String = "",
    var status: String = "",
    @ServerTimestamp
    var created: Date? = null,
    var message: String = "",
    var date: String = "",
    var time: String = "",
    var quantity: String = "",
    var owner: String = "",
    var changedAttribute: @RawValue HashMap<String, ArrayList<Any>> = hashMapOf()
) : Parcelable