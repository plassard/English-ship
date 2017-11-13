package english.pj;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.util.Log;

import org.json.JSONException;
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
import java.util.ArrayList;
import java.util.StringTokenizer;


public class BWUserStatistics extends AsyncTask<String, Void, String> {
    Context context;
    String id;
    static JSONObject json_row;
    String allFriendsInRow;
    static ArrayList<String> friendsList = new ArrayList<String>();
    public SharedPreferences settings;
    boolean ErrorInt = false;

    BWUserStatistics(Context ctx) {
        context = ctx;
    }
    @Override
    protected String doInBackground(String... params) {
        String putInWaitURL = "http://q99710ny.bget.ru/get_user_stat.php";

        try {
                if(mech.getLogin(context) != null) {
                URL url = new URL(putInWaitURL);

                HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
                httpURLConnection.setRequestMethod("POST");
                httpURLConnection.setDoOutput(true);
                httpURLConnection.setDoInput(true);
                OutputStream outputStream = httpURLConnection.getOutputStream();
                BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(outputStream, "UTF-8"));
                String post_data = URLEncoder.encode("login", "UTF-8") + "=" + URLEncoder.encode(mech.getLogin(context), "UTF-8");
                bufferedWriter.write(post_data);
                bufferedWriter.flush();
                bufferedWriter.close();
                outputStream.close();
                InputStream inputStream = httpURLConnection.getInputStream();
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream, "UTF-8"));
                String result = "";
                String line = "";
                while ((line = bufferedReader.readLine()) != null) {
                    result += line;
                }
                bufferedReader.close();
                inputStream.close();
                httpURLConnection.disconnect();
                return result;
            }
            } catch (MalformedURLException e) {

            } catch (Exception e) {
                ErrorInt = true;

            }

        return null;
    }


    @Override
    protected void onPreExecute() {

    }


    @Override
    protected void onPostExecute(String result) {

        try {
                json_row = new JSONObject(result);

                //make update somewhere
                id = json_row.getString("No.");
                settings = context.getSharedPreferences("DATA", Context.MODE_PRIVATE);
                SharedPreferences.Editor editor;
                editor = settings.edit();
                editor.putString("LOGIN", mech.getLogin(context));
                editor.putString("ID", id);
                editor.commit();
                //ChangeProfileActivity.id = id;

                try {
                    allFriendsInRow = BWUserStatistics.json_row.getString("friends");
                    StringTokenizer stringTokenizer = new StringTokenizer(allFriendsInRow, ",");
                    friendsList.clear();
                    String interim;
                    while (stringTokenizer.hasMoreTokens()) {
                        interim = stringTokenizer.nextToken();
                        if (interim.equals(null) || interim.equals("")) {
                        } else {
                            friendsList.add(interim);
                        }
                    }
                } catch (JSONException e) {
                }
                //rank = json_row.getString("correctanswers");

                if(json_row.getString("token").equals("")){

                    /*String token = FirebaseInstanceId.getInstance().getToken();
                    if(token != null) {
                        BWSendRegTokenToServer bwSendRegTokenToServer = new BWSendRegTokenToServer(context);
                        bwSendRegTokenToServer.execute(token);
                    }*/
                }

            } catch (Exception e) {
            }

            BWGetPositionInRankingTable bwGetPositionInRankingTable = new BWGetPositionInRankingTable(context);
            String points;
            points = "";
            try {
                points = json_row.getString("points");
            } catch (Exception e) {
            }
            bwGetPositionInRankingTable.execute(points);
    }
    @Override
    protected void onProgressUpdate(Void... values) {
        super.onProgressUpdate(values);
    }


}