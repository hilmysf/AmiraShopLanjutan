package hilmysf.amirashoplanjutan.notification

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.fragment.app.FragmentActivity
import hilmysf.amirashoplanjutan.R
import hilmysf.amirashoplanjutan.data.source.entities.Products
import hilmysf.amirashoplanjutan.helper.Constant
import hilmysf.amirashoplanjutan.ui.product.opname.LowStockProductActivity
import java.util.concurrent.atomic.AtomicInteger

object NotificationManagers {
    private const val CHANNEL_ID = "channel_1"
    private const val CHANNEL_NAME = "channel_name"
    private val c: AtomicInteger = AtomicInteger(0)
    private fun getID(): Int {
        return c.incrementAndGet()
    }

    fun createNotificationChannel(activity: FragmentActivity) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val channel =
                NotificationChannel(CHANNEL_ID, CHANNEL_NAME, importance)
            val notificationManager: NotificationManager =
                activity.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }

    fun triggerNotification(context: Context, product: Products, checkoutQuantity: Int?) {
        val intent = Intent(context, LowStockProductActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        val pendingIntent: PendingIntent =
            PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_IMMUTABLE)

        val builder = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_notification)
            .setContentTitle("Stok Menipis!")
            .setContentText("Stok ${product.name} tersisa $checkoutQuantity.")
            .setStyle(
                NotificationCompat.BigTextStyle()
                    .bigText("Stok ${product.name} tersisa $checkoutQuantity. Segera lakukan pengisian stok kembali!")
            )
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
        with(NotificationManagerCompat.from(context)) {
            notify(getID(), builder.build())
        }
    }

    fun triggerNotification(context: Context, hashMapProducts: HashMap<String, Any>) {
        val intent = Intent(context, LowStockProductActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        val pendingIntent: PendingIntent =
            PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_IMMUTABLE)

        val builder = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_notification)
            .setContentTitle("Stok Menipis!")
            .setContentText("Stok ${hashMapProducts[Constant.NAME]} tersisa ${hashMapProducts[Constant.QUANTITY]}.")
            .setStyle(
                NotificationCompat.BigTextStyle()
                    .bigText("Stok ${hashMapProducts[Constant.NAME]} tersisa ${hashMapProducts[Constant.QUANTITY]}. Segera lakukan pengisian stok kembali!")
            )
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
        with(NotificationManagerCompat.from(context)) {
            notify(getID(), builder.build())
        }
    }
}