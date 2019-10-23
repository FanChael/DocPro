package com.skl.hooktest;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.os.Build;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;

public class NotifyUtil {
    private static String CHANNEL_ID = "111";

    public static void notifyUtil(Context context){
        // 1. Set the notification content - 创建通知基本内容
        // https://developer.android.google.cn/training/notify-user/build-notification.html#builder
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_launcher_background)
                .setContentTitle("My notification")
                // 这是单行
                //.setContentText("Much longer text that cannot fit one line...")
                // 这是多行
                .setStyle(new NotificationCompat.BigTextStyle()
                        .bigText("Much longer text that cannot fit one line..." +
                                "Much longer text that cannot fit one line..." +
                                "Much longer text that cannot fit one line..."))
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setAutoCancel(true);

        // 2. Create a channel and set the importance - 8.0后需要设置Channel
        // https://developer.android.google.cn/training/notify-user/build-notification.html#builder
        createNotificationChannel(context);

        // 3. Set the notification's tap action - 创建一些点击事件，比如点击跳转页面
        // https://developer.android.google.cn/training/notify-user/build-notification.html#click


        // 4. Show the notification - 展示通知
        // https://developer.android.google.cn/training/notify-user/build-notification.html#notify
        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);

        // notificationId is a unique int for each notification that you must define
        notificationManager.notify((int) System.currentTimeMillis(), builder.build());
    }

    private static void createNotificationChannel(Context context) {
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = context.getString(R.string.app_name);
            String description = context.getString(R.string.app_name);
            int importance = NotificationManager.IMPORTANCE_HIGH;
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name, importance);
            channel.setDescription(description);
            // Register the channel with the system; you can't change the importance
            // or other notification behaviors after this
            NotificationManager notificationManager = context.getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }
}
