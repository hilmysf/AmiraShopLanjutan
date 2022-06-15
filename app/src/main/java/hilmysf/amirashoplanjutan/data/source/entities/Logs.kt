package hilmysf.amirashoplanjutan.data.source.entities

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Logs(
    var logId: String = "",
    var image: String = "",
    var name: String = "",
    var status: String = "",
    var created: String = "",
    var message: String = "",
    var date: String = "",
    var time: String = "",
    var quantity: String = ""
) : Parcelable