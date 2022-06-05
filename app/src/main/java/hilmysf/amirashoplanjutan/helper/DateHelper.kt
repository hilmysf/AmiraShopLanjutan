package hilmysf.amirashoplanjutan.helper

import java.text.SimpleDateFormat
import java.util.*

object DateHelper {
    private lateinit var date: String
    private lateinit var time: String
    private lateinit var created: String
    val calendar: Calendar = Calendar.getInstance()
    private lateinit var simpleDateFormat: SimpleDateFormat
    fun dateFormat(): String {
        simpleDateFormat = SimpleDateFormat("dd-MM-yyyy", Locale.getDefault())
        date = simpleDateFormat.format(calendar.time).toString()
        return date
    }

    fun timeFormat(): String {
        simpleDateFormat = SimpleDateFormat("HH:mm:ss", Locale.getDefault())
        time = simpleDateFormat.format(calendar.time).toString()
        return time
    }

    fun createdFormat(): String {
        simpleDateFormat = SimpleDateFormat("dd-MM-yyyy HH:mm:ss", Locale.getDefault())
        created = simpleDateFormat.format(calendar.time).toString()
        return created
    }
}