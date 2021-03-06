/*
 * Enable Viacam for Android, a camera based mouse emulator
 *
 * Copyright (C) 2015-16 Cesar Mauri Loba (CREA Software Systems)
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package com.crea_si.eviacam.a11yservice;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;

import com.crea_si.eviacam.R;

/**
 * Manage pause/resume notifications
 */
class ServiceNotification {
    /**
     * Type of notification to display
     */
    static final int NOTIFICATION_ACTION_NONE = 0;
    static final int NOTIFICATION_ACTION_PAUSE = 1;
    static final int NOTIFICATION_ACTION_RESUME = 2;

    /*
     * constants for notifications
     */
    private static final int NOTIFICATION_ID = 1;
    private static final String NOTIFICATION_FILTER_ACTION = "ENABLE_DISABLE_EVIACAM";
    static final String NOTIFICATION_ACTION_NAME = "action";

    private final Service mService;

    private final BroadcastReceiver mBroadcastReceiver;

    private int mAction= NOTIFICATION_ACTION_NONE;

    private boolean mInitDone = false;

    /**
     * Constructor
     *
     * @param s  service
     * @param bc broadcast receiver which will be called when the notification is tapped
     */
    ServiceNotification(Service s, BroadcastReceiver bc) {
        mService = s;
        mBroadcastReceiver = bc;
    }

    public void init() {
        if (mInitDone) return;

        /* register notification receiver */
        IntentFilter iFilter = new IntentFilter(NOTIFICATION_FILTER_ACTION);
        mService.registerReceiver(mBroadcastReceiver, iFilter);

        updateNotification();

        mInitDone = true;
    }

    public void cleanup() {
        if (!mInitDone) return;

        // Remove as foreground service
        mService.stopForeground(true);

        // Remove notification
        NotificationManager notificationManager =
                (NotificationManager) mService.getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.cancel(NOTIFICATION_ID);

        mService.unregisterReceiver(mBroadcastReceiver);

        mInitDone = false;
    }

    /**
     * Create and register the notification as foreground service
     */
    private void updateNotification () {
        Notification noti = createNotification(mService, mAction);
        NotificationManager notificationManager =
                (NotificationManager) mService.getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(NOTIFICATION_ID, noti);

        // Register as foreground service
        mService.startForeground(ServiceNotification.NOTIFICATION_ID, noti);
    }

    /**
     * Set the action of the notification and update accordingly
     * @param action of the notification
     *             NOTIFICATION_ACTION_NONE
     *             NOTIFICATION_ACTION_PAUSE
     *             NOTIFICATION_ACTION_RESUME
     */
    void update (int action) {
        if (mAction == action) return;
        mAction= action;

        updateNotification ();
    }

    /**
     * Create the notification
     * @param c context
     * @param action code of the action
     * @return return notification object
     */
    private static Notification createNotification(Context c, int action) {
        // notification initialization
        Intent intent = new Intent(NOTIFICATION_FILTER_ACTION);
        intent.putExtra(NOTIFICATION_ACTION_NAME, action);

        PendingIntent pIntent = PendingIntent.getBroadcast
                (c, NOTIFICATION_ID, intent, PendingIntent.FLAG_UPDATE_CURRENT);

        CharSequence text;
        int iconId;
        if (action == NOTIFICATION_ACTION_NONE) {
            text = c.getText(R.string.app_name);
            iconId = R.drawable.ic_notification_enabled;
        } else if (action == NOTIFICATION_ACTION_PAUSE) {
            text = c.getText(R.string.running_click_to_pause);
            iconId = R.drawable.ic_notification_enabled;
        } else if (action == NOTIFICATION_ACTION_RESUME) {
            text = c.getText(R.string.stopped_click_to_resume);
            iconId = R.drawable.ic_notification_disabled;
        }
        else throw new IllegalStateException();

        return new Notification.Builder(c)
                .setContentTitle(c.getText(R.string.app_name))
                .setContentText(text)
                .setSmallIcon(iconId)
                .setContentIntent(pIntent)
                .build();
    }
}
