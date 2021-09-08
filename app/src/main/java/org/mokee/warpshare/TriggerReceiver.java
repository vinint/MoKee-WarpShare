/*
 * Copyright (C) 2019 The MoKee Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.mokee.warpshare;

import static android.bluetooth.le.BluetoothLeScanner.EXTRA_CALLBACK_TYPE;
import static android.bluetooth.le.BluetoothLeScanner.EXTRA_LIST_SCAN_RESULT;

import android.app.PendingIntent;
import android.bluetooth.le.ScanResult;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

public class TriggerReceiver extends BroadcastReceiver {

    public static final String TAG = "TriggerReceiver";

    public static final String EXTRA_CALLBACK_INTENT = "callback";

    static PendingIntent getTriggerIntent(Context context) {
        final PendingIntent callbackIntent = ReceiverService.getTriggerIntent(context);

        final Intent intent = new Intent(context, TriggerReceiver.class);
        intent.putExtra(TriggerReceiver.EXTRA_CALLBACK_INTENT, callbackIntent);

        return PendingIntent.getBroadcast(context, callbackIntent.hashCode(),
                intent, PendingIntent.FLAG_UPDATE_CURRENT);
    }

    public static PendingIntent getTriggerIntent2(Context context, int callbackType, ScanResult result) {
        final PendingIntent callbackIntent = ReceiverService.getTriggerIntent(context);

        final Intent intent = new Intent(context, TriggerReceiver.class);
        intent.putExtra(TriggerReceiver.EXTRA_CALLBACK_INTENT, callbackIntent);
        intent.putExtra(EXTRA_CALLBACK_TYPE,callbackType);
        ArrayList scanList = new ArrayList<>();
        scanList.add(result);
        intent.putExtra(EXTRA_LIST_SCAN_RESULT,scanList);

        return PendingIntent.getBroadcast(context, callbackIntent.hashCode(),
                intent, PendingIntent.FLAG_UPDATE_CURRENT);
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.d(TAG, "onReceive");
        final PendingIntent callbackIntent = intent.getParcelableExtra(EXTRA_CALLBACK_INTENT);
        try {
            callbackIntent.send(context, 0, intent);
        } catch (PendingIntent.CanceledException e) {
            Log.e(TAG, "Failed sending callback", e);
        }
    }

}
