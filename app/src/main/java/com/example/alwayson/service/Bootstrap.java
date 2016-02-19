package com.example.alwayson.service;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;

import com.example.alwayson.Constants;

import java.util.Calendar;

/**
 * 启动服务类
 */
public class Bootstrap {

	private static String TAG = Bootstrap.class.getSimpleName();

    /**
     * 启动服务
     * @param context 上下文
     * @param loadedFrom 启动来源 开机启动 自启动...
     */
	public static synchronized void startAlwaysOnService(Context context,
                                                          String loadedFrom) {

		if (AlwaysOnService.isRunning == false) {
			// start service
			Intent pIntent = new Intent(context, AlwaysOnService.class);
			pIntent.putExtra(Constants.STARTUP_ACTION_NAME, loadedFrom);
			context.startService(pIntent);

            //每隔10秒发送广播启动服务
			// enable 10 secs restart
			Intent mIntent = new Intent(context, AlarmBroadcastReceiver.class);
			mIntent.setFlags(Intent.FLAG_INCLUDE_STOPPED_PACKAGES);
			mIntent.putExtra(Constants.STARTUP_ACTION_NAME, loadedFrom);
			mIntent.setAction(AlarmBroadcastReceiver.ACTION_CUSTOM_ALARM);//设置Action
            //得到PendingIntent用于发送mIntent中指定的广播
			PendingIntent sender = PendingIntent.getBroadcast(context, 0,
                                                              mIntent, 0);
            //得到日历类 jia1秒
			Calendar time = Calendar.getInstance();
			time.setTimeInMillis(System.currentTimeMillis());
			time.add(Calendar.SECOND, 1);

            //闹钟管理类，用于指定时间发送一条广播
			AlarmManager am = (AlarmManager) context
                .getSystemService(Context.ALARM_SERVICE);

            //设置过一段时间发送广播 time.getTimeInMillis(),当前时间,Constants.ALARM_REPEAT_INTERVAL * 1000间隔时间
			am.setRepeating(AlarmManager.RTC_WAKEUP, time.getTimeInMillis(),
                            Constants.ALARM_REPEAT_INTERVAL * 1000, sender);

			// enable boot/powerkey restart
			setBootupListen(context, false);//设置显示
		}
	}

    /**
     * 停止服务
     * @param context
     */
	public static synchronized void stopAlwaysOnService(Context context) {
		
		// stop service
		Intent pIntent = new Intent(context, AlwaysOnService.class);
		context.stopService(pIntent);

		// cancel alarm restart
		Intent intent = new Intent(context, AlarmBroadcastReceiver.class);
		intent.setAction(AlarmBroadcastReceiver.ACTION_CUSTOM_ALARM);
		PendingIntent sender = PendingIntent
            .getBroadcast(context, 0, intent, 0);
		AlarmManager alarmManager = (AlarmManager) context
            .getSystemService(Context.ALARM_SERVICE);
		alarmManager.cancel(sender);

		// cancel boot/power key restart
		setBootupListen(context, false);//设置隐藏
	}

    /**
     * 设置显示还是隐藏图标
     * @param context
     * @param isEnabled
     */
	private static void setBootupListen(Context context, boolean isEnabled) {

		int flag = (isEnabled ? PackageManager.COMPONENT_ENABLED_STATE_ENABLED
                    : PackageManager.COMPONENT_ENABLED_STATE_DISABLED);
		ComponentName component = new ComponentName(context,
                                                    RebootBroadcastReceiver.class);

		context.getPackageManager().setComponentEnabledSetting(component, flag,
                                                               PackageManager.DONT_KILL_APP);
	}
}
