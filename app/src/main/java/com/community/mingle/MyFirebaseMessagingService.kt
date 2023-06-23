package com.community.mingle

import android.annotation.SuppressLint
import android.app.*
import android.app.PendingIntent.FLAG_IMMUTABLE
import android.app.PendingIntent.FLAG_UPDATE_CURRENT
import android.content.Context
import android.content.Intent
import android.media.RingtoneManager
import android.os.Build
import android.os.PowerManager
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.ProcessLifecycleOwner
import androidx.work.OneTimeWorkRequest
import androidx.work.WorkManager
import com.community.mingle.views.SplashActivity
import com.community.mingle.views.ui.board.PostActivity
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.google.firebase.quickstart.fcm.kotlin.MyWorker

class MyFirebaseMessagingService : FirebaseMessagingService() {
    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        super.onMessageReceived(remoteMessage)
        Log.d(TAG, "From: ${remoteMessage.from}")

        val pm =
            getSystemService(Context.POWER_SERVICE) as PowerManager
        @SuppressLint("InvalidWakeLockTag") val wakeLock =
            pm.newWakeLock(
                PowerManager.SCREEN_DIM_WAKE_LOCK
                        or PowerManager.ACQUIRE_CAUSES_WAKEUP, "TAG"
            )
        wakeLock.acquire(3000)
        wakeLock.release()

        // Check if message contains a data payload.
        if (remoteMessage.data.isNotEmpty()) {
            Log.d(TAG, "Message data payload: ${remoteMessage.data}")
            if (true) {
                // For long-running tasks (10 seconds or more) use WorkManager.
                scheduleJob()
            } else {
                // Handle message within 10 seconds
                handleNow()
            }
        }

        val body = remoteMessage.notification?.body
        val title = remoteMessage.notification?.title
        val boardType = remoteMessage.data["tableId"]
        val postId = remoteMessage.data["postId"]

        Log.d(TAG, "title : $title, body : $body, boardtype : $boardType, postid : $postId")

        sendNotification(body!!, title!!,boardType!!, postId)

//        // Check if message contains a notification payload.
//        remoteMessage.notification?.let {
//            Log.d(TAG, "Message Notification Body: ${it}")
//            it?.let { it -> sendNotification(it.body!!,it.title!!, boardType, postId) }
//        }
    }
    // [END receive_message]

    // [START on_new_token]
    /**
     * Called if the FCM registration token is updated. This may occur if the security of
     * the previous token had been compromised. Note that this is called when the
     * FCM registration token is initially generated so this is where you would retrieve the token.
     */
    override fun onNewToken(token: String) {
        super.onNewToken(token)
        Log.d(TAG, "Refreshed token: $token")
        MingleApplication.pref.fcmToken = token

        // If you want to send messages to this application instance or
        // manage this apps subscriptions on the server side, send the
        // FCM registration token to your app server.
        sendRegistrationToServer(token)
    }
// [END on_new_token]

    /**
     * Schedule async work using WorkManager.
     */
    private fun scheduleJob() {
        // [START dispatch_job]
        val work = OneTimeWorkRequest.Builder(MyWorker::class.java).build()
        WorkManager.getInstance(this).beginWith(work).enqueue()
        // [END dispatch_job]
    }

    /**
     * Handle time allotted to BroadcastReceivers.
     */
    private fun handleNow() {
        Log.d(TAG, "Short lived task is done.")
    }

    /**
     * Persist token to third-party servers.
     *
     * Modify this method to associate the user's FCM registration token with any server-side account
     * maintained by your application.
     *
     * @param token The new token.
     */
    private fun sendRegistrationToServer(token: String?) {
        // TODO: Implement this method to send token to your app server.

        Log.d(TAG, "sendRegistrationTokenToServer($token)")

    }

    /**
     * Create and show a simple notification containing the received FCM message.
     *
     * @param messageBody FCM message body received.
     */
    private fun sendNotification(
        body: String,title:String, boardType : String, postId: String?
    ) {
        val notificationManager =
            getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        Log.d("boardType",boardType.toString())

        val channelId = getString(R.string.default_notification_channel_id)
        val channelName = getString(R.string.default_notification_channel_name)
        //Oreo(26) 이상 버전에는 channel 필요
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                channelId,
                channelName,
                NotificationManager.IMPORTANCE_DEFAULT
            )
            notificationManager.createNotificationChannel(channel)
        }
        //알림 생성

        NotificationManagerCompat.from(this)
            .notify((System.currentTimeMillis()/100).toInt(), createNotification(body, title, boardType, postId))  //알림이 여러개 표시되도록 requestCode 를 추가
    }

    fun isAppOnForeground(): Boolean {
        return ProcessLifecycleOwner.get().getLifecycle().getCurrentState()
            .isAtLeast(Lifecycle.State.STARTED);
    }

    /* 알림 설정 메서드 */
    private fun createNotification(
        body: String,title:String, boardType : String, postId: String?
    ): Notification {

        var intent = Intent(this, SplashActivity::class.java).apply {
            Log.d("alarm ",boardType+" "+postId.toString())
            putExtra("tableId",boardType)
            putExtra("postId", postId)
            flags = Intent.FLAG_ACTIVITY_SINGLE_TOP or Intent.FLAG_ACTIVITY_CLEAR_TOP
            //flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
        }

        if (isAppOnForeground()) {
            intent = Intent(this, PostActivity::class.java).apply {
                putExtra("type",boardType)
                putExtra("postId", postId?.toInt())
                flags = Intent.FLAG_ACTIVITY_SINGLE_TOP or Intent.FLAG_ACTIVITY_CLEAR_TOP
                //flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
            }
        }

        val pendingIntent = PendingIntent.getActivity(this, 0, intent, FLAG_IMMUTABLE or FLAG_UPDATE_CURRENT)  //알림이 여러개 표시되도록 requestCode 를 추가

        val channelId = getString(R.string.default_notification_channel_id)
        val defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
        val notificationBuilder = NotificationCompat.Builder(this, channelId)
            .setSmallIcon(R.drawable.ic_notification)
            .setContentTitle(title)
            .setContentText(body)
            .setStyle(NotificationCompat.BigTextStyle()
                .bigText(body))
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setSound(defaultSoundUri)
            .setVisibility(NotificationCompat.VISIBILITY_PRIVATE)
            .setContentIntent(pendingIntent)  //알림 눌렀을 때 실행할 Intent 설정
            .setAutoCancel(true)  //클릭 시 자동으로 삭제되도록 설정

        Log.d("alarm ",boardType+" "+postId.toString())

        return notificationBuilder.build()
    }

//    private fun sendNotification(body: String,title:String, boardType : String?, postId: Int?) {
//            val intent = Intent(this, MainActivity::class.java).apply {
//                putExtra("boardType",boardType)
//                putExtra("postId",postId)
//                flags = Intent.FLAG_ACTIVITY_CLEAR_TOP and Intent.FLAG_ACTIVITY_NEW_TASK
//            }
//           val pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_MUTABLE)
//
//
//
//        // Create an explicit intent for an Activity in your app
//
//        val channelId = getString(R.string.default_notification_channel_id)
//        val channelName = getString(R.string.default_notification_channel_name)
//        val defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
//
//        val notificationBuilder = NotificationCompat.Builder(this, channelId)
//            .setSmallIcon(R.drawable.mingkki)
//            .setContentTitle(title)
//            .setContentText(body)
//            .setStyle(NotificationCompat.BigTextStyle()
//                .bigText(body))
//            .setAutoCancel(true)
//            .setSound(defaultSoundUri)
//            .setContentIntent(pendingIntent)
//            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
//
//        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
//
//        // Since android Oreo notification channel is needed.
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
//            val channel = NotificationChannel(channelId,
//                "Channel human readable title",
//                NotificationManager.IMPORTANCE_DEFAULT)
//            notificationManager.createNotificationChannel(channel)
//        }
//
//        notificationManager.notify(0 /* ID of notification */, notificationBuilder.build())
//    }

    companion object {

        private const val TAG = "MyFirebaseMsgService"
    }
}