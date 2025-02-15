package com.byagowi.persiancalendar.service

import android.annotation.TargetApi
import android.content.Intent
import android.graphics.drawable.Icon
import android.os.Build
import android.service.quicksettings.Tile
import android.service.quicksettings.TileService
import android.util.Log
import com.byagowi.persiancalendar.ui.MainActivity
import com.byagowi.persiancalendar.utils.Utils

/**
 * Created by Alireza Afkar on 19/6/2018AD.
 */
@TargetApi(Build.VERSION_CODES.N)
class PersianCalendarTileService : TileService() {
    override fun onClick() {
        try {
            startActivityAndCollapse(Intent(this, MainActivity::class.java)
                    .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK))
        } catch (e: Exception) {
            Log.e("TileService", "Tile onClick fail", e)
        }
    }

    override fun onStartListening() {
        val tile = qsTile ?: return
        val today = Utils.getTodayOfCalendar(Utils.getMainCalendar())

        tile.icon = Icon.createWithResource(this,
                Utils.getDayIconResource(today.dayOfMonth))
        tile.label = Utils.getWeekDayName(today)
        tile.contentDescription = Utils.getMonthName(today)
        // explicitly set Tile state to Active, fixes tile not being lit on some Samsung devices
        tile.state = Tile.STATE_ACTIVE
        tile.updateTile()
    }
}
