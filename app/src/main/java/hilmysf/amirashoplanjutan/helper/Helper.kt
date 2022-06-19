package hilmysf.amirashoplanjutan.helper

import android.app.AlertDialog
import android.content.Context
import java.text.NumberFormat
import java.util.*


object Helper {
    fun alertDialog(context: Context?, title: String?, message: String?) {
        val alertDialog = AlertDialog.Builder(context).create()
        alertDialog.setTitle(title)
        alertDialog.setMessage(message)
        alertDialog.setButton(
            AlertDialog.BUTTON_NEUTRAL,
            "Coba Lagi"
        ) { dialog, _ -> dialog.dismiss() }
        alertDialog.show()
    }

    fun camelCase(text: String): String {
        val words = text.split(" ").toMutableList()
        var output = ""
        for (word in words) {
            output += word.replaceFirstChar { if (it.isLowerCase()) it.titlecase(Locale.getDefault()) else it.toString() } + " "
        }
        output = output.trim()
        return output
    }

    fun currencyFormatter(number: Long): String =
        NumberFormat.getNumberInstance(Locale.US).format(number)

//    fun isLowChecker(products: Products): Boolean {
//        return products.quantity <= products.minQuantity
//    }
}