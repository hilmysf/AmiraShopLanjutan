package hilmysf.amirashoplanjutan.data.source.entities

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Products(
    var productId: String = "",
    var name: String = "",
    var price: Long = 0,
    var image: String = "",
    var quantity: Int = 0,
    var minQuantity: Int = 0,
    var category: String = "",
) : Parcelable
