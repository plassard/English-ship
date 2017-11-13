package english.pj;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
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

public class BWRegisterNewUser extends AsyncTask<String, Void, String> {
    Context context;
    AlertDialog alertDialog;
    String login;
    String city;
    String country;
    String bdate;
    String pass;
    public SharedPreferences settings;

    BWRegisterNewUser(Context ctx) {
        context = ctx;
    }
    @Override
    protected String doInBackground(String... params) {
        String putInWaitURL = "http://q99710ny.bget.ru/register_new_user.php";
        login = params[0];
        pass = params[1];
        country = params[2];
        bdate = params[3];

        try {
            URL url = new URL(putInWaitURL);

            HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
            httpURLConnection.setRequestMethod("POST");
            httpURLConnection.setDoOutput(true);
            httpURLConnection.setDoInput(true);
            OutputStream outputStream = httpURLConnection.getOutputStream();
            BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(outputStream, "UTF-8"));
            String post_data = URLEncoder.encode("login", "UTF-8")+"="+URLEncoder.encode(login,"UTF-8")+"&"+
                    URLEncoder.encode("country","UTF-8")+"="+URLEncoder.encode(country,"UTF-8")+"&"+
                    URLEncoder.encode("bdate","UTF-8")+"="+URLEncoder.encode(bdate,"UTF-8")+"&"+
                    URLEncoder.encode("pass","UTF-8")+"="+URLEncoder.encode(pass,"UTF-8");
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
        try {
            if (result.equals("success")) {
                settings = context.getSharedPreferences("DATA", Context.MODE_PRIVATE);
                SharedPreferences.Editor editor;
                editor = settings.edit();
                editor.putString("LOGIN", login);
                editor.putString("CITY", city);
                editor.putString("COUNTRY", country);
                editor.putString("BDATE", bdate);
                editor.commit();

                BWUserStatistics bwGetUserStatistics = new BWUserStatistics(context);
                bwGetUserStatistics.execute(login);
                BWGetPositionInRankingTable.wheretogo = "main_activity";

            } else {
                alertDialog = new AlertDialog.Builder(context).create();
                alertDialog.setTitle("такой пользователь уже зарегистрирован");
                alertDialog.show();
            }
        }catch (Exception e) {
            Intent intent = new Intent(context, StartActivity.class);
            context.startActivity(intent);
        }
    }

    @Override
    protected void onProgressUpdate(Void... values) {
        super.onProgressUpdate(values);
    }


}