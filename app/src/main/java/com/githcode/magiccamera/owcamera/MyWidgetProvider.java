package com.githcode.magiccamera.owcamera;

import com.githcode.magiccamera.owcamera.MyDebug;
import com.githcode.magiccamera.owcamera.MainActivity;
import com.githcode.magiccamera.owcamera.R;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.util.Log;
import android.widget.RemoteViews;

/** Handles the Magic Camera lock screen widget. Lock screen widgets are no
 *  longer supported in Android 5 onwards (instead Magic Camera can be launched
 *  from the lock screen using the standard camera icon), but this is kept here
 *  for older Android versions.
 */
public class MyWidgetProvider extends AppWidgetProvider {
    private static final String TAG = "MyWidgetProvider";
    
    // see http://developer.android.com/guide/topics/appwidgets/index.html
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int [] appWidgetIds) {
        if( MyDebug.LOG )
            Log.d(TAG, "onUpdate");
        if( MyDebug.LOG )
            Log.d(TAG, "length = " + appWidgetIds.length);

        for(int appWidgetId : appWidgetIds) {
            if( MyDebug.LOG )
                Log.d(TAG, "appWidgetId: " + appWidgetId);

            PendingIntent pendingIntent;
            // for now, always put up the keyguard if the device is PIN locked etc
            /*SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
            if( sharedPreferences.getBoolean(MainActivity.getShowWhenLockedPreferenceKey(), true) ) {
                if( MyDebug.LOG )
                    Log.d(TAG, "do show above lock screen");
                Intent intent = new Intent(context, MyWidgetProvider.class);
                intent.setAction("com.githcode.magiccamera.owcamera.LAUNCH_OPEN_CAMERA");
                pendingIntent = PendingIntent.getBroadcast(context, 0, intent, 0);
            }
            else*/ {
                /*if( MyDebug.LOG )
                    Log.d(TAG, "don't show above lock screen");*/
                Intent intent = new Intent(context, MainActivity.class);

                int flags = PendingIntent.FLAG_UPDATE_CURRENT;
                if( Build.VERSION.SDK_INT >= Build.VERSION_CODES.M )
                    flags = flags | PendingIntent.FLAG_IMMUTABLE; // needed for targetting Android 12+, but fine to set it all versions from Android 6 onwards
                pendingIntent = PendingIntent.getActivity(context, 0, intent,  flags);
            }

            RemoteViews remote_views = new RemoteViews(context.getPackageName(), R.layout.widget_layout);
            remote_views.setOnClickPendingIntent(R.id.widget_launch_open_camera, pendingIntent);
            /*if( sharedPreferences.getBoolean(MainActivity.getShowWhenLockedPreferenceKey(), true) ) {
                remote_views.setTextViewText(R.id.launch_open_camera, "Magic Camera (unlocked)");
            }
            else {
                remote_views.setTextViewText(R.id.launch_open_camera, "Magic Camera (locked)");
            }*/

            appWidgetManager.updateAppWidget(appWidgetId, remote_views);
        }
    }

    /*@Override
    public void onReceive(Context context, Intent intent) {
        if( MyDebug.LOG ) {
            Log.d(TAG, "onReceive " + intent);
        }
        if (intent.getAction().equals("com.githcode.magiccamera.owcamera.LAUNCH_OPEN_CAMERA")) {
            if( MyDebug.LOG )
                Log.d(TAG, "Launching MainActivity");
            final Intent activity = new Intent(context, MainActivity.class);
            activity.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(activity);
            if( MyDebug.LOG )
                Log.d(TAG, "done");
        }
        super.onReceive(context, intent);
    }*/
}
