package hilmysf.amirashoplanjutan.network

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager

class InternetChangeReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
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