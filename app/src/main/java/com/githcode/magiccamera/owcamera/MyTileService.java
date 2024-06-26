package com.githcode.magiccamera.owcamera;

import android.content.Intent;
import android.os.Build;
import android.service.quicksettings.TileService;
import androidx.annotation.RequiresApi;
import android.util.Log;

/** Provides service for quick settings tile.
 */
@RequiresApi(api = Build.VERSION_CODES.N)
public class MyTileService extends TileService {
    private static final String TAG = "MyTileService";
    public static final String TILE_ID = "com.githcode.magiccamera.owcamera.TILE_CAMERA";

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onTileAdded() {
        super.onTileAdded();
    }

    @Override
    public void onTileRemoved() {
        super.onTileRemoved();
    }

    @Override
    public void onStartListening() {
        super.onStartListening();
    }

    @Override
    public void onStopListening() {
        super.onStopListening();
    }

    @Override
    public void onClick() {
        if( MyDebug.LOG )
            Log.d(TAG, "onClick");
        super.onClick();
        Intent intent = new Intent(this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.setAction(TILE_ID);
        startActivityAndCollapse(intent); // use this instead of startActivity() so that the notification panel doesn't remain pulled down
    }
}
