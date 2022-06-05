package hilmysf.amirashoplanjutan.helper

import android.app.AlertDialog
import android.content.Context
import android.content.DialogInterface

object Helper {
    fun alertDialog(context: Context?, title: String?, message: String?){
        val alertDialog = AlertDialog.Builder(context).create()
        alertDialog.setTitle(title)
        alertDialog.setMessage(message)
        alertDialog.setButton(
            AlertDialog.BUTTON_NEUTRAL, "Coba Lagi",
            DialogInterface.OnClickListener { dialog, _ -> dialog.dismiss() })
        alertDialog.show()
    }
}