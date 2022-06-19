package hilmysf.amirashoplanjutan.network

import android.app.AlertDialog
import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build
import android.content.Intent

import android.content.BroadcastReceiver
import android.content.ContentValues.TAG
import android.util.Log

class InternetChangeReceiver : BroadcastReceiver() {
//    private fun isInternetAvailable(context: Context): Boolean {
//        var result = false
//        val connectivityManager =
//            context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
//            val networkCapabilities = connectivityManager.activeNetwork ?: return false
//            val actNw =
//                connectivityManager.getNetworkCapabilities(networkCapabilities) ?: return false
//            result = when {
//                actNw.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> true
//                actNw.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> true
//                actNw.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET) -> true
//                else -> false
//            }
//        } else {
//            connectivityManager.run {
//                connectivityManager.activeNetworkInfo?.run {
//                    result = when (type) {
//                        ConnectivityManager.TYPE_WIFI -> true
//                        ConnectivityManager.TYPE_MOBILE -> true
//                        ConnectivityManager.TYPE_ETHERNET -> true
//                        else -> false
//                    }
//
//                }
//            }
//        }
//        Log.d(TAG, "isAvailableInternet: $result")
//        return result
//    }

    override fun onReceive(context: Context, intent: Intent) {
//        val isInternetAvailable: Boolean = isInternetAvailable(context)
//        if (!isInternetAvailable) {
//            val title = "Koneksi Terputus"
//            val message = "Koneksi Anda terputus. Cek kembali koneksi Anda dan coba lagi."
//            val option = arrayOf<CharSequence>("Coba lagi")
//            AlertDialog.Builder(context).apply {
//                setTitle(title)
//                setMessage(message)
//                setItems(option){ _, item ->
//                    if(option[item] == "Coba lagi"){
//
//                    }
//                }
//                show()
//            }
//        }
        connectivityReceiverListener?.onNetworkConnectionChanged(isConnectedOrConnectiong(context))

    }

    private fun isConnectedOrConnectiong(context: Context): Boolean {
        val connMgr = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val networkInfo = connMgr.activeNetworkInfo
        return networkInfo != null && networkInfo.isConnectedOrConnecting
    }

    interface ConnectivityReceiverListener {
        fun onNetworkConnectionChanged(isConnected: Boolean)
    }

    companion object {
        var connectivityReceiverListener: ConnectivityReceiverListener? = null
    }
}