package english.pj;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;


public class BWGetChatMessages extends AsyncTask<Void, Void, String> {
    Context context;
    String round;
    JSONObject jsonObject;
    static JSONArray json_data;
    String unseenMessageUserName, unseenMessageText;

    BWGetChatMessages(Context ctx) {
        context = ctx;
    }
    @Override
    protected String doInBackground(Void... params) {
        String putInWaitURL = "http://q99710ny.bget.ru/get_chat_messages.php";
       // ServiceBackround.unseenMessageUserName = "";

        try {
            URL url = new URL(putInWaitURL);

            HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
            httpURLConnection.setRequestMethod("POST");
            httpURLConnection.setDoOutput(true);
            httpURLConnection.setDoInput(true);
            OutputStream outputStream = httpURLConnection.getOutputStream();
            BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(outputStream, "UTF-8"));
            String post_data = URLEncoder.encode("name","UTF-8")+"="+URLEncoder.encode(mech.getLogin(context),"UTF-8");

            bufferedWriter.write(post_data);
            bufferedWriter.flush();
            bufferedWriter.close();
            outputStream.close();
            InputStream inputStream = httpURLConnection.getInputStream();
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream,"UTF-8"));
            String result="";
            String line="";
            while((line = bufferedReader.readLine())!= null) {
                result += line;
            }
            bufferedReader.close();
            inputStream.close();
            httpURLConnection.disconnect();

            return result;
        } catch (MalformedURLException e) {
        } catch (Exception e) {
        }
        return null;
    }


    @Override
    protected void onPreExecute() {

    }


    @Override
    protected void onPostExecute(String result) {

        try {
            json_data = new JSONArray(result);
            int i = 0;

            while (i < BWGetChatMessages.json_data.length()) {
                try {
                    jsonObject = new JSONObject(BWGetChatMessages.json_data.getString(i));
                    if (!(jsonObject.getString("wasseen").equals("1")) && (jsonObject.getString("client").equals(mech.getLogin(context)))) {
                        unseenMessageUserName = jsonObject.getString("author");
                        unseenMessageText = jsonObject.getString("text");

                        BWMakeChatMesRead bwMakeChatMesRead = new BWMakeChatMesRead(context);
                        bwMakeChatMesRead.execute();
                        if(ChatActivity.isChatActive) {
                            ChatActivity.isNewMessage = true;
                        } else {
                            sendNotification();
                        }
                        break;
                    }
                } catch (Exception e) {
                }
                i++;

            }

            if(ChatActivity.isChatActive) {
                BWMakeChatMesRead bwMakeChatMesRead = new BWMakeChatMesRead(context);
                bwMakeChatMesRead.execute();
                ChatActivity.onChatUpdate(context);
            }

        } catch (Exception e) {   }

    }
    @Override
    protected void onProgressUpdate(Void... values) {
        super.onProgressUpdate(values);
    }


    void sendNotification(){
        Intent notificationIntent = new Intent(context, ChatActivity.class);
        ChatActivity.anotherPartnerInChat = unseenMessageUserName;
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