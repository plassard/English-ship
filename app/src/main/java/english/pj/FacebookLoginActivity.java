package english.pj;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.appevents.AppEventsLogger;
import com.facebook.internal.ImageRequest;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;

import org.json.JSONObject;

import java.util.Arrays;

public class FacebookLoginActivity extends Activity {

    CallbackManager callbackManager;
    String email = "";
    String gender = "";
    String firstName = "";
    String lastName = "";
    String bday = "";
    String cityFB = "";
    String countryFB = "";
    SharedPreferences settings;

    String profURL;
    Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_facebook_login);
        context = this;
        FacebookSdk.sdkInitialize(getApplicationContext());
        AppEventsLogger.activateApp(this);

        // тут фейсбук обещает, если я это добавлю то будет классно и я смогу смотреть статистику
        // использования приложения - кто сколько его запускал, как долго и т.д.
        callbackManager = CallbackManager.Factory.create();


        LoginManager.getInstance().logInWithReadPermissions(
                this,
                Arrays.asList("user_friends", "email", "public_profile"));

        LoginManager.getInstance().registerCallback(callbackManager,
                new FacebookCallback<LoginResult>() {
                    @Override
                    public void onSuccess(LoginResult loginResult) {
                        setFacebookData(loginResult);
                    }

                    @Override
                    public void onCancel() {
                    }

                    @Override
                    public void onError(FacebookException exception) {

                    }
                });

    }

    private void setFacebookData(final LoginResult loginResult)
    {
        GraphRequest request = GraphRequest.newMeRequest(
                loginResult.getAccessToken(),
                new GraphRequest.GraphJSONObjectCallback() {
                    @Override
                    public void onCompleted(JSONObject object, GraphResponse response) {
                        // Application code
                        try {
                            importFbProfilePhoto();
                        } catch (Exception e) {
                        }
                        try {
                            firstName = response.getJSONObject().getString("first_name");
                            lastName = response.getJSONObject().getString("last_name");
                            email = response.getJSONObject().getString("email");
                            gender = response.getJSONObject().getString("gender");
                            bday = cityFB = countryFB = "";
                           /* bday= response.getJSONObject().getString("birthday");
                            JSONObject jsb = object.getJSONObject("location");

                            cityFB = jsb.getJSONObject("location").getString("city");
                            countryFB = jsb.getJSONObject("location").getString("country");*/
                        } catch (Exception e) {
                            Intent intent = new Intent(context, StartActivity.class);
                            context.startActivity(intent);
                        }

                    }
                });
        Bundle parameters = new Bundle();
        parameters.putString("fields", "email,first_name,last_name,gender");
        //parameters.putString("fields", "id,email,first_name,last_name,gender, birthday, location{location}");
        request.setParameters(parameters);
        request.executeAsync();

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        callbackManager.onActivityResult(requestCode, resultCode, data);
    }

    private void importFbProfilePhoto() {

        if (AccessToken.getCurrentAccessToken() != null) {

            GraphRequest request = GraphRequest.newMeRequest(
                    AccessToken.getCurrentAccessToken(), new GraphRequest.GraphJSONObjectCallback() {
                        @Override
                        public void onCompleted(JSONObject me, GraphResponse response) {

                            if (AccessToken.getCurrentAccessToken() != null) {

                                if (me != null) {

                                    String profileImageUrl = ImageRequest.getProfilePictureUri(me.optString("id"), 500, 500).toString();
                                    profURL = profileImageUrl;
                                }
                            }

                            BWPutUserDataIntoDBviaFacebook backgroundWorkerPutUserData = new BWPutUserDataIntoDBviaFacebook(FacebookLoginActivity.this);
                            String login = firstName + " " + lastName;
                            backgroundWorkerPutUserData.execute(login, profURL, cityFB, countryFB, bday);

                            settings = getSharedPreferences("DATA", Context.MODE_PRIVATE);
                            SharedPreferences.Editor editor;
                            editor = settings.edit();
                            editor.putString("LOGIN", login);
                            //editor.putString("CITY", cityFB);
                            //editor.putString("COUNTRY", countryFB);
                            editor.putString("AVATAR", profURL);
                            //editor.putString("BDATE", bday);
                            editor.commit();

                        }
                    });
            GraphRequest.executeBatchAsync(request);
        }
    }

}
