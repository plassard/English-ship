package english.pj;

import android.app.ActivityManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;

import java.util.ArrayList;
import java.util.concurrent.ExecutionException;

public class ServiceBackround extends Service {
    public ServiceBackround() {
    }
    Handler handler = new Handler();
    Context context;
    String unseenMessageUserName;
    String unseenMessageText;
    ArrayList<String> listOfOnYourTurnNotified = new ArrayList<String>();
    boolean wasInvitationShown;
    NotificationManager notifManager;

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        context = this;
        wasInvitationShown = false;

        handler.post(RunnableAllGamesWithMe);
        handler.post(RunnableChat); //запускаем первый раз

        return super.onStartCommand(intent, flags, startId);
    }

    final Runnable RunnableChat = new Runnable(){ //создаем второй поток и все это в этом отдельном потоке происходит
        public void run(){
            if(!(unseenMessageUserName.equals(""))){
                ChatActivity.anotherPartnerInChat = unseenMessageUserName;
                if(!(ChatActivity.isChatActive)) {
                    sendNotification();
                }
            }

            BWGetChatMessages bwGetChatMessages = new BWGetChatMessages(context);
            bwGetChatMessages.execute();

            handler.postDelayed(this, 21600000); // запускаем повторно вновь и вновь
        }
    };
    boolean isInForeground(){
        boolean isInForeground;
        ActivityManager.RunningAppProcessInfo myProcess = new ActivityManager.RunningAppProcessInfo();
        ActivityManager.getMyMemoryState(myProcess);
        isInForeground = myProcess.importance == ActivityManager.RunningAppProcessInfo.IMPORTANCE_FOREGROUND;
        return isInForeground;
    }
    final Runnable RunnableAllGamesWithMe = new Runnable(){ //создаем второй поток и все это в этом отдельном потоке происходит
        public void run(){

            if(isInForeground() == false) {
                try{
                //уведомление о приглашении играть
                if (!BWAllGamesWithMe.invitation.equals("")) {
                    if (wasInvitationShown == false) {
                        sendNotificationOnInvitation();
                    }
                } }catch (Exception e){}
            }
            BWAllGamesWithMe backgroundWorkerAllGames = new BWAllGamesWithMe(context);
            backgroundWorkerAllGames.execute("");

            handler.postDelayed(this, 21600000); // запускаем повторно вновь и вновь почти раз в сутки 81400000
        }
    };

    void sendNotificationOnYourTurn(int i){
        Intent notificationIntent = new Intent(this, GamesListActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, notificationIntent, PendingIntent.FLAG_CANCEL_CURRENT);
        Notification.Builder builder = new Notification.Builder(this);
        builder.setContentIntent(pendingIntent)
                .setAutoCancel(true)
                .setContentTitle("Твой ход")
                .setTicker("Твой ход против " + BWAllGamesWithMe.invitation)
                .setContentText(BWAllGamesWithMe.invitation + " выбрал категорию")
                .setSmallIcon(R.drawable.icon)
                .setContentIntent(pendingIntent);
        Notification notification = builder.build();
        NotificationManager notificationManager = (NotificationManager) this
                .getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(1, notification);
        Uri sound= RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        Ringtone r = RingtoneManager.getRingtone(getApplicationContext(), sound);
        r.play();
    }

    void sendNotificationOnInvitation(){
        Log.i("LLS2", "invitation");
        Intent notificationIntent = new Intent(context, GamesListActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(context,
                0, notificationIntent,
                PendingIntent.FLAG_CANCEL_CURRENT);

        Notification.Builder builder = new Notification.Builder(context);

        builder.setContentIntent(pendingIntent)
                .setAutoCancel(true)
                .setContentTitle("Приглашение на бой")
                .setTicker(BWAllGamesWithMe.invitation + " предлагает сразиться")
                .setContentText(BWAllGamesWithMe.invitation + " предлагает сразиться")
                .setSmallIcon(R.drawable.icon)
                .setContentIntent(pendingIntent); // Текст уведомления

        Notification notification = builder.build();

        NotificationManager notificationManager = (NotificationManager) context
                .getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(1, notification);
        wasInvitationShown = true;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        notifManager = (NotificationManager)getSystemService(NOTIFICATION_SERVICE);
        unseenMessageUserName = "";
    }
    void sendNotification(){
        Intent notificationIntent = new Intent(context, ChatActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(context,
                0, notificationIntent,
                PendingIntent.FLAG_CANCEL_CURRENT);

        Notification.Builder builder = new Notification.Builder(context);

        builder.setContentIntent(pendingIntent)
                .setSmallIcon(R.drawable.icon)
                .setTicker("Новое сообщение от " + unseenMessageUserName)
                .setWhen(System.currentTimeMillis())
                .setAutoCancel(true)
                .setContentTitle("Сообщение от " + unseenMessageUserName)
                .setContentText(unseenMessageText); // Текст уведомления

        Notification notification = builder.build();

        NotificationManager notificationManager = (NotificationManager) context
                .getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(1, notification);
        unseenMessageUserName = "";
    }
}
