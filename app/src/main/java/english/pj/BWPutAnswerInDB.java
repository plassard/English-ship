package english.pj;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.util.Log;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;

public class BWPutAnswerInDB extends AsyncTask<String, Void, String> {
    Context context;
    String _id;
    String currentAnswer;
    String qnumber;
    String isLastQ;

    BWPutAnswerInDB (Context ctx) {
        context = ctx;
    }
    @Override
    protected String doInBackground(String... params) {
        String putInWaitURL = "http://q99710ny.bget.ru/put_current_answer.php";
        _id = params[0];
        currentAnswer = params[1];
        qnumber = params[2];
        isLastQ = params[3];

        try {
            URL url = new URL(putInWaitURL);

            HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
            httpURLConnection.setRequestMethod("POST");
            httpURLConnection.setDoOutput(true);
            httpURLConnection.setDoInput(true);
            OutputStream outputStream = httpURLConnection.getOutputStream();
            BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(outputStream, "UTF-8"));
            String post_data = URLEncoder.encode("_id", "UTF-8")+"="+URLEncoder.encode(_id,"UTF-8")+"&"+
                    URLEncoder.encode("ca","UTF-8")+"="+URLEncoder.encode(currentAnswer,"UTF-8")+"&"+
                    URLEncoder.encode("qnumber","UTF-8")+"="+URLEncoder.encode(qnumber,"UTF-8")+"&"+
                    URLEncoder.encode("last","UTF-8")+"="+URLEncoder.encode(isLastQ,"UTF-8")+"&"+
                    URLEncoder.encode("login","UTF-8")+"="+URLEncoder.encode(mech.getLogin(context),"UTF-8");
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
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    protected void onPreExecute() {
    }

    @Override
    protected void onPostExecute(String result) {

        if(isLastQ.equals("last")){
            BWAllGamesWithMe.resultAllGames = result;
            //getNewJSON and go to PreGame;

            Intent intent = new Intent(context, PreGameActivity.class);
            intent.putExtra("bot", 0);
            intent.putExtra("gameid", Integer.valueOf(_id));
            context.startActivity(intent);
        }
    }

    @Override
    protected void onProgressUpdate(Void... values) {
        super.onProgressUpdate(values);
    }


}