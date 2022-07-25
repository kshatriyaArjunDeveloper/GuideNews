package com.fruitfal.arjunsbi.core.utils

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.Toast
import androidx.fragment.app.Fragment
import java.text.SimpleDateFormat
import java.util.*

object HelperMethods {

    /** Time Related */

    private val dateTimeFormat = SimpleDateFormat("dd MMM yyyy, hh:mm", Locale.getDefault())
    private val dayMonthFormat = SimpleDateFormat("dd MMM", Locale.getDefault())

    fun getNewsFeedTime(newsFeedTime: String): String {
        try {
            val dateGiven: Date =
                SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.getDefault()).let {
                    it.timeZone = TimeZone.getTimeZone("UTC")
                    it.parse(newsFeedTime)
                }

            val dateNow = Calendar.getInstance().timeInMillis

            val newsDateString = dateTimeFormat.format(dateGiven)
            val nowDateString = dateTimeFormat.format(dateNow)

            val isSameHour = newsDateString.substring(0, 15) == nowDateString.substring(0, 15)
            val isSameDay = newsDateString.substring(0, 11) == nowDateString.substring(0, 11)

            return when {

                isSameHour -> { // 10 min ago
                    val newsMinutes = newsDateString.substring(16, 18).toInt()
                    val nowMinutes = nowDateString.substring(16, 18).toInt()
                    "${nowMinutes - newsMinutes} min ago"
                }

                isSameDay -> { // 10 hr ago Or 29 Aug if hours ago is -ve
                    val newsHours = newsDateString.substring(13, 15).toInt()
                    val nowHours = nowDateString.substring(13, 15).toInt()
                    val hoursAgo = nowHours - newsHours
                    if (hoursAgo > 0) "$hoursAgo hr ago" else dayMonthFormat.format(dateGiven) // 29 Aug
                }

                else -> dayMonthFormat.format(dateGiven) // 29 Aug
            }
        } catch (exception: Exception) {
            return "Time unknown"
        }
    }

    fun getNewsTime(newTime: String): String {
        return try {
            val dateGiven: Date =
                SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.getDefault()).let {
                    it.timeZone = TimeZone.getTimeZone("UTC")
                    it.parse(newTime)
                }

            dateTimeFormat.format(dateGiven)

        } catch (exception: Exception) {
            "Time unknown"
        }
    }


    /** Connection Check */
    fun Fragment.isInternetAvailable(): Boolean {
        val connectivityManager =
            context?.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

        // if the android version is equal to M or greater
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            val network = connectivityManager.activeNetwork ?: return false
            val activeNetwork = connectivityManager.getNetworkCapabilities(network) ?: return false
            return when {
                // When wifi has internet connectivity
                activeNetwork.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> true

                // When cellular internet connectivity
                activeNetwork.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> true

                // No Internet
                else -> false
            }
        } else {
            // if the android version is below M
            @Suppress("DEPRECATION") val networkInfo =
                connectivityManager.activeNetworkInfo ?: return false
            @Suppress("DEPRECATION")
            return networkInfo.isConnected
        }
    }

}
