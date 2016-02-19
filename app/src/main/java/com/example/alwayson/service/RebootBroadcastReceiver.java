package com.example.alwayson.service;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

/**
 * 监听开机及屏幕唤醒广播
 */
public class RebootBroadcastReceiver extends BroadcastReceiver {
	private static final String LOG_TAG = RebootBroadcastReceiver.class.getSimpleName();

	@Override
	public void onReceive(Context context, Intent intent) {
		Log.v(LOG_TAG, "onReceive");

        /**
         *
         * Intent.ACTION_USER_PRESENT 屏幕唤醒解锁广播
         * Intent.ACTION_BOOT_COMPLETED 开机广播
         *
         */
		if ((intent.getAction().equals(Intent.ACTION_USER_PRESENT))
				|| (intent.getAction().equals(Intent.ACTION_BOOT_COMPLETED))) {
			Bootstrap.startAlwaysOnService(context, intent.getAction());//启动服务
		}
	}
}
