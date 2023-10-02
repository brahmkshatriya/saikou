package ani.saikou.subcriptions

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import ani.saikou.*
import ani.saikou.subcriptions.Subscription.Companion.defaultTime
import ani.saikou.subcriptions.Subscription.Companion.startSubscription
import ani.saikou.subcriptions.Subscription.Companion.timeMinutes
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class AlarmReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?) {
        when (intent?.action) {
            Intent.ACTION_BOOT_COMPLETED -> tryWith(true) {
                logger("Starting Saikou Subscription Service on Boot")
                context?.startSubscription()
            }
        }

        CoroutineScope(Dispatchers.IO).launch {
            val con = context ?: currContext() ?: return@launch
            if (isOnline(con)) Subscription.perform(con)
        }
    }

    companion object {

        fun alarm(context: Context) {
            val alarmIntent = Intent(context, AlarmReceiver::class.java)
            alarmIntent.action = "ani.saikou.ACTION_ALARM"

            val pendingIntent = PendingIntent.getBroadcast(
                context, 0, alarmIntent,
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
                } else {
                    PendingIntent.FLAG_UPDATE_CURRENT
                }
            )
            val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
            val curTime = loadData<Int>("subscriptions_time", context) ?: defaultTime

            if (timeMinutes[curTime] > 0)
                alarmManager.setRepeating(
                    AlarmManager.RTC,
                    System.currentTimeMillis(),
                    (timeMinutes[curTime] * 60 * 1000),
                    pendingIntent
                )
            else alarmManager.cancel(pendingIntent)
        }

    }
}