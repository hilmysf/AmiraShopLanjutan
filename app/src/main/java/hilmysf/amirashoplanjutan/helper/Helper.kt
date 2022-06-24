package hilmysf.amirashoplanjutan.helper

import android.app.AlertDialog
import android.content.Context
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.viewbinding.ViewBinding
import com.google.android.material.snackbar.BaseTransientBottomBar
import com.google.android.material.snackbar.Snackbar
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
        NumberFormat.getNumberInstance(Locale.GERMAN).format(number)

    fun showNetworkMessage(isConnected: Boolean, activity: AppCompatActivity, binding: ViewBinding) {
        val snackbar = Snackbar.make(
            activity.findViewById(android.R.id.content),
            "You are offline",
            Snackbar.LENGTH_LONG
        )
        snackbar.setAction("Try Again") {
            activity.finish()
            activity.overridePendingTransition(0, 0)
            activity.startActivity(activity.intent)
            activity.overridePendingTransition(0, 0)
        }
        snackbar.duration = BaseTransientBottomBar.LENGTH_INDEFINITE
        if (!isConnected) {
            snackbar.show()
            binding.root.visibility = View.INVISIBLE
        } else {
            snackbar.dismiss()
            binding.root.visibility = View.VISIBLE
        }
    }
//    fun isLowChecker(products: Products): Boolean {
//        return products.quantity <= products.minQuantity
//    }
}