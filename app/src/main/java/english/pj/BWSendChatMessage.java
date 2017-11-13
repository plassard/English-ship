package english.pj;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;


public class BWSendChatMessage extends AsyncTask<String, Void, String> {
    Context context;

    String client = "aa";
    String text = "bb";

    BWSendChatMessage(Context ctx) {
        context = ctx;
    }
    @Override
    protected String doInBackground(String... params) {

        String putInWaitURL = "http://q99710ny.bget.ru/send_chat_message.php";
        client = params[0];
        text = params[1];

        try {
            URL url = new URL(putInWaitURL);

            HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
            httpURLConnection.setRequestMethod("POST");
            httpURLConnection.setDoOutput(true);
            httpURLConnection.setDoInput(true);
            OutputStream outputStream = httpURLConnection.getOutputStream();
            BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(outputStream, "UTF-8"));
            String post_data = URLEncoder.encode("author","UTF-8")+"="+URLEncoder.encode(mech.getLogin(context),"UTF-8")+"&"+
                    URLEncoder.encode("client","UTF-8")+"="+URLEncoder.encode(client,"UTF-8")+"&"+
                    URLEncoder.encode("text","UTF-8")+"="+URLEncoder.encode(text,"UTF-8");

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
        }  catch (Exception e) {
            Intent intent = new Intent(context, PreGameActivity.class);
            context.startActivity(intent);
        }
        return null;
    }


    @Override
    protected void onPreExecute() {

    }


    @Override
    protected void onPostExecute(String result) {
        BWGetChatMessages bwGetChatMessages = new BWGetChatMessages(context);
        bwGetChatMessages.execute();
    }
    @Override
    protected void onProgressUpdate(Void... values) {
        super.onProgressUpdate(values);
    }


}