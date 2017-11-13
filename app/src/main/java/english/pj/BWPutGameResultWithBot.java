package english.pj;

import android.content.Context;
import android.os.AsyncTask;

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


public class BWPutGameResultWithBot extends AsyncTask<String, Void, String> {
    Context context;
    String result;
    String pl2, vocab;

    BWPutGameResultWithBot(Context ctx) {
        context = ctx;
    }
    @Override
    protected String doInBackground(String... params) {
        String putInWaitURL = "http://q99710ny.bget.ru/put_bot_game_result.php";
        result = params[0];
        vocab = params[1];

        try {
            URL url = new URL(putInWaitURL);

            HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
            httpURLConnection.setRequestMethod("POST");
            httpURLConnection.setDoOutput(true);
            httpURLConnection.setDoInput(true);
            OutputStream outputStream = httpURLConnection.getOutputStream();
            BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(outputStream, "UTF-8"));
            String post_data = URLEncoder.encode("login", "UTF-8")+"="+URLEncoder.encode(mech.getLogin(context),"UTF-8")+"&"+
                    URLEncoder.encode("result","UTF-8")+"="+URLEncoder.encode(result,"UTF-8")+"&"+
                    URLEncoder.encode("vocab","UTF-8")+"="+URLEncoder.encode(vocab,"UTF-8");
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
        BWUserStatistics bwUserStatistics = new BWUserStatistics(context);
        bwUserStatistics.execute();
    }

    @Override
    protected void onProgressUpdate(Void... values) {
        super.onProgressUpdate(values);
    }


}