package com.icsseseguridad.locationsecurity.service.background;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class PositionAlarmReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        PositionIntentService.run(context);
    }
}
