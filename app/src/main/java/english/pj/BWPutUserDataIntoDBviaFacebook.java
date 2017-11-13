package english.pj;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
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

public class BWPutUserDataIntoDBviaFacebook extends AsyncTask<String, Void, String> {
    Context context;
    AlertDialog alertDialog;
    String login;
    String avatar;
    String city;
    String country;
    String bdate;

    BWPutUserDataIntoDBviaFacebook(Context ctx) {
        context = ctx;
    }
    @Override
    protected String doInBackground(String... params) {
        String putInWaitURL = "http://q99710ny.bget.ru/put_user_from_FB.php";
        login = params[0];
        avatar = params[1];
        city = params[2];
        country = params[3];
        bdate = params[4];
        if(country.equals("Ukraine")) { country = "Украина"; }
        if(country.equals("Russian Federation")) { country = "Россия"; }
        if(country.equals("Belarus")) { country = "Беларусь"; }

        try {
            URL url = new URL(putInWaitURL);

            HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
            httpURLConnection.setRequestMethod("POST");
            httpURLConnection.setDoOutput(true);
            httpURLConnection.setDoInput(true);
            OutputStream outputStream = httpURLConnection.getOutputStream();
            BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(outputStream, "UTF-8"));
            String post_data = URLEncoder.encode("login", "UTF-8")+"="+URLEncoder.encode(login,"UTF-8")+"&"+
                    URLEncoder.encode("avatar","UTF-8")+"="+URLEncoder.encode(avatar,"UTF-8")+"&"+
                    URLEncoder.encode("city","UTF-8")+"="+URLEncoder.encode(city,"UTF-8")+"&"+
                    URLEncoder.encode("country","UTF-8")+"="+URLEncoder.encode(country,"UTF-8")+"&"+
                    URLEncoder.encode("bdate","UTF-8")+"="+URLEncoder.encode(bdate,"UTF-8");
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
        Intent intent = new Intent(context, MainActivity.class);
        context.startActivity(intent);
    }

    @Override
    protected void onProgressUpdate(Void... values) {
        super.onProgressUpdate(values);
    }


}