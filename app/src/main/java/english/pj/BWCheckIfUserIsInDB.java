package english.pj;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

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

/**
 * Created by ASUS on 07.06.2017.
 */

public class BWCheckIfUserIsInDB extends AsyncTask<String, Void, String> {
    Context context;
    AlertDialog alertDialog;
    String login;
    String password;
    public SharedPreferences settings;

    BWCheckIfUserIsInDB(Context ctx) {
        context = ctx;
    }
    @Override
    protected String doInBackground(String... params) {
        String putInWaitURL = "http://q99710ny.bget.ru/Check_if_user_is_in_DB.php";
        login = params[0];
        password = params[1];

        try {
            URL url = new URL(putInWaitURL);

            HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
            httpURLConnection.setRequestMethod("POST");
            httpURLConnection.setDoOutput(true);
            httpURLConnection.setDoInput(true);
            OutputStream outputStream = httpURLConnection.getOutputStream();
            BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(outputStream, "UTF-8"));
            String post_data = URLEncoder.encode("login", "UTF-8")+"="+URLEncoder.encode(login,"UTF-8")+"&"+
                    URLEncoder.encode("password","UTF-8")+"="+URLEncoder.encode(password,"UTF-8");
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

        } catch (IOException e) {

        }
        return null;
    }

    @Override
    protected void onPreExecute() {

    }

    @Override
    protected void onPostExecute(String result) {

        if ("notregistered".equals(result)) {
            //say not registered
            alertDialog = new AlertDialog.Builder(context).create();
            LayoutInflater inflater2 = (LayoutInflater) context.getSystemService( Context.LAYOUT_INFLATER_SERVICE );
            View viewInfoWindow =  inflater2.inflate( R.layout.info, null );
            alertDialog.setView(viewInfoWindow);
            alertDialog.show();
            TextView tvWhoInvite = (TextView)viewInfoWindow.findViewById(R.id.textViewInfo);
            tvWhoInvite.setText("Такой логин не зарегистрирован");

        } else if ("passincorrect".equals(result)) {
            alertDialog = new AlertDialog.Builder(context).create();
            LayoutInflater inflater2 = (LayoutInflater) context.getSystemService( Context.LAYOUT_INFLATER_SERVICE );
            View viewInfoWindow =  inflater2.inflate( R.layout.info, null );
            alertDialog.setView(viewInfoWindow);
            alertDialog.show();
            TextView tvWhoInvite = (TextView)viewInfoWindow.findViewById(R.id.textViewInfo);
            tvWhoInvite.setText("Неверный пароль, попробуйте ввести пароль еще раз");
        } else {
            try {
                JSONObject json_data = new JSONObject(result);
                //String loginDB = json_data.getString("login");
                String passDB = json_data.getString("password");
                String cityDB = json_data.getString("city");
                String countryDB = json_data.getString("country");
                String avatarDB = json_data.getString("avatar");
                String bdateDB = json_data.getString("bdate");

                if ("vkpass".equals(passDB)) {
                    alertDialog = new AlertDialog.Builder(context).create();
                    LayoutInflater inflater2 = (LayoutInflater) context.getSystemService( Context.LAYOUT_INFLATER_SERVICE );
                    View viewInfoWindow =  inflater2.inflate( R.layout.info, null );
                    alertDialog.setView(viewInfoWindow);
                    alertDialog.show();
                    TextView tvWhoInvite = (TextView)viewInfoWindow.findViewById(R.id.textViewInfo);
                    tvWhoInvite.setText("Пользователь " + login + " регистрировался используя вход через профиль ВКонтакте. Пожалуйста, войдите используя свой профиль ВКонтакте");

                } else if ("fbpass".equals(passDB)) {
                    alertDialog = new AlertDialog.Builder(context).create();
                    LayoutInflater inflater2 = (LayoutInflater) context.getSystemService( Context.LAYOUT_INFLATER_SERVICE );
                    View viewInfoWindow =  inflater2.inflate( R.layout.info, null );
                    alertDialog.setView(viewInfoWindow);
                    alertDialog.show();
                    TextView tvWhoInvite = (TextView)viewInfoWindow.findViewById(R.id.textViewInfo);
                    tvWhoInvite.setText("Пользователь " + login + " регистрировался используя вход через профиль Facebook. Пожалуйста, войдите используя свой профиль Facebook");
                } else {

                    if (password.equals(passDB)) {
                        settings = context.getSharedPreferences("DATA", Context.MODE_PRIVATE);
                        SharedPreferences.Editor editor;
                        editor = settings.edit();
                        editor.putString("LOGIN", login);
                        editor.putString("CITY", cityDB);
                        editor.putString("COUNTRY", countryDB);
                        editor.putString("AVATAR", avatarDB);
                        editor.putString("BDATE", bdateDB);
                        editor.commit();

                        Intent intent = new Intent(context, MainActivity.class);
                        context.startActivity(intent);
                        /*BWGetUserStatistics bwGetUserStatistics = new BWGetUserStatistics(context);
                        bwGetUserStatistics.execute(login);
                        BWGetPositionInRankingTable.wheretogo = "main_activity";*/

                    } else {
                        alertDialog = new AlertDialog.Builder(context).create();
                        LayoutInflater inflater2 = (LayoutInflater) context.getSystemService( Context.LAYOUT_INFLATER_SERVICE );
                        View viewInfoWindow =  inflater2.inflate( R.layout.info, null );
                        alertDialog.setView(viewInfoWindow);
                        alertDialog.show();
                        TextView tvWhoInvite = (TextView)viewInfoWindow.findViewById(R.id.textViewInfo);
                        tvWhoInvite.setText("Неверный пароль, попробуйте ввести пароль еще раз");
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
                alertDialog = new AlertDialog.Builder(context).create();
                LayoutInflater inflater2 = (LayoutInflater) context.getSystemService( Context.LAYOUT_INFLATER_SERVICE );
                View viewInfoWindow =  inflater2.inflate( R.layout.info, null );
                alertDialog.setView(viewInfoWindow);
                alertDialog.show();
                TextView tvWhoInvite = (TextView)viewInfoWindow.findViewById(R.id.textViewInfo);
                tvWhoInvite.setText("Что-то не так на сервере");
            }
        };
    }


    @Override
    protected void onProgressUpdate(Void... values) {
        super.onProgressUpdate(values);
    }


}
