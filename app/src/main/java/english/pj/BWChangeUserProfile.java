package english.pj;

import android.app.AlertDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.util.Log;

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

public class BWChangeUserProfile extends AsyncTask<String, Void, String> {
    Context context;
    String login;
    String avatar;
    String city;
    String country;
    String bdate;
    SharedPreferences settings;

    BWChangeUserProfile(Context ctx) {
        context = ctx;
    }
    @Override
    protected String doInBackground(String... params) {
        String putInWaitURL = "http://q99710ny.bget.ru/change_user_data.php";
        login = params[0];
        avatar = params[1];
        city = params[2];
        country = params[3];
        bdate = params[4];
Log.i("POI", "ava");
        Log.i("POI", "ava" + avatar);
        Log.i("POI", "log" + login);

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
        settings = context.getSharedPreferences("DATA", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor;
        editor = settings.edit();
        editor.putString("LOGIN", login);
        editor.putString("CITY", city);
        editor.putString("COUNTRY", country);
        editor.putString("AVATAR", avatar);
        editor.putString("BDATE", bdate);
        editor.commit();

        //Intent intent3 = new Intent(context, MainActivity.class);
        //context.startActivity(intent3);

      /*  alertDialog = new AlertDialog.Builder(context).create();
        alertDialog.setTitle(result);
        alertDialog.show();*/
    }

    @Override
    protected void onProgressUpdate(Void... values) {
        super.onProgressUpdate(values);
    }


}