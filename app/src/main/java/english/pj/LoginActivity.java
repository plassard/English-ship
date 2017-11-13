package english.pj;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import com.vk.sdk.VKAccessToken;
import com.vk.sdk.VKCallback;
import com.vk.sdk.VKSdk;
import com.vk.sdk.api.VKApi;
import com.vk.sdk.api.VKApiConst;
import com.vk.sdk.api.VKError;
import com.vk.sdk.api.VKParameters;
import com.vk.sdk.api.VKRequest;
import com.vk.sdk.api.VKResponse;
import com.vk.sdk.VKScope;
import com.vk.sdk.api.model.VKApiUserFull;
import com.vk.sdk.api.model.VKList;

public class LoginActivity extends Activity {
    String login;
    String password;
    String country;
    String city;
    String avatar;
    String bdate;
    String id;
    EditText mEditLogin;
    EditText mEditPassword;
    Button mButtonRegister;
    AlertDialog alertDialog;
    SharedPreferences settings;
    private String[] vkscope = new String[]{VKScope.MESSAGES, VKScope.FRIENDS, VKScope.WALL};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        FontChangeCrawler fontChanger = new FontChangeCrawler(getAssets(), "fonts/Raleway-Regular.ttf");
        fontChanger.replaceFonts((ViewGroup)this.findViewById(android.R.id.content));
        TextView tvHeader = (TextView)findViewById(R.id.textViewLoginEng);
        tvHeader.setTypeface(Typeface.createFromAsset(getAssets(), "fonts/TitanOne-Regular.ttf"));
        TextView tvHeader2 = (TextView)findViewById(R.id.textViewLoginFC);
        tvHeader2.setTypeface(Typeface.createFromAsset(getAssets(), "fonts/TitanOne-Regular.ttf"));
        TextView tv3 = (TextView)findViewById(R.id.textView1Login);
        tv3.setTypeface(Typeface.createFromAsset(getAssets(), "fonts/Raleway-SemiBold.ttf"));

        mEditLogin = (EditText) findViewById(R.id.EditLogin);
        mEditPassword = (EditText)findViewById(R.id.EditPassword);
        mButtonRegister = (Button) findViewById(R.id.buttonRegister);
        mButtonRegister.setTypeface(Typeface.createFromAsset(getAssets(), "fonts/Raleway-SemiBold.ttf"));
        Button mButtonLogin = (Button) findViewById(R.id.buttonLoginGeneral);
        mButtonLogin.setTypeface(Typeface.createFromAsset(getAssets(), "fonts/Raleway-SemiBold.ttf"));
    }

    public void onClickLogin (View v) {
        AlertDialog alertDialog;
        String login = mEditLogin.getText().toString();
        String password = mEditPassword.getText().toString();
        if ("".equals(login) && "".equals(password)) {
            alertDialog = new AlertDialog.Builder(this).create();
            LayoutInflater inflater2 = (LayoutInflater) this.getSystemService( Context.LAYOUT_INFLATER_SERVICE );
            View viewInfoWindow =  inflater2.inflate( R.layout.info, null );
            alertDialog.setView(viewInfoWindow);
            alertDialog.show();
            TextView tvWhoInvite = (TextView)viewInfoWindow.findViewById(R.id.textViewInfo);
            tvWhoInvite.setText("Введите логин и пароль");

        } else if("".equals(login)) {
            alertDialog = new AlertDialog.Builder(this).create();
            LayoutInflater inflater2 = (LayoutInflater) this.getSystemService( Context.LAYOUT_INFLATER_SERVICE );
            View viewInfoWindow =  inflater2.inflate( R.layout.info, null );
            alertDialog.setView(viewInfoWindow);
            alertDialog.show();
            TextView tvWhoInvite = (TextView)viewInfoWindow.findViewById(R.id.textViewInfo);
            tvWhoInvite.setText("Введите логин");
        } else if( "".equals(password)) {
            alertDialog = new AlertDialog.Builder(this).create();
            LayoutInflater inflater2 = (LayoutInflater) this.getSystemService( Context.LAYOUT_INFLATER_SERVICE );
            View viewInfoWindow =  inflater2.inflate( R.layout.info, null );
            alertDialog.setView(viewInfoWindow);
            alertDialog.show();
            TextView tvWhoInvite = (TextView)viewInfoWindow.findViewById(R.id.textViewInfo);
            tvWhoInvite.setText("Введите пароль");
        } else {
            BWCheckIfUserIsInDB backgroundWorkerIfInDB = new BWCheckIfUserIsInDB(LoginActivity.this);
            backgroundWorkerIfInDB.execute(login, password);
        };
    }


    public void onClickRegister (View v) {
        Intent intent2 = new Intent(this, RegistrationActivity.class);
        intent2.putExtra("login", mEditLogin.getText().toString());
        startActivity(intent2);
    }


    // далее код логинства через ВК
    public void onVKLoginButtonClick (View v) {
        VKSdk.login(this, vkscope);
    }

    @Override
    protected void onActivityResult(final int requestCode, final int resultCode, final Intent data) {


        if (!VKSdk.onActivityResult(requestCode, resultCode, data, new VKCallback<VKAccessToken>() {
            @Override
            public void onResult(VKAccessToken res) {
                // Пользователь успешно авторизовался
                VKRequest request = VKApi.users().get(VKParameters.from(VKApiConst.FIELDS, "first_name,last_name,city,country,photo_max_orig,bdate"));
                request.executeWithListener(new VKRequest.VKRequestListener() {
                    @Override
                    public void onComplete(VKResponse response) {
                        super.onComplete(response);

                        VKList list = (VKList) response.parsedModel;

                        VKList<VKApiUserFull> list2 = (VKList<VKApiUserFull>) response.parsedModel;
                        VKApiUserFull user = list2.get(0);
                        try {
                            login = user.first_name + " " + user.last_name;
                        } catch (Exception e) {
                            login = user.nickname;
                        }
                        try {
                            country = user.country.title;
                        } catch (Exception e) {
                            country = "";
                        }

                        try {
                            city = user.city.title;
                        } catch (Exception e) {
                            city = "";
                        }
                        try {
                            avatar = user.photo_max_orig;
                        } catch (Exception e) {
                            avatar = "";
                        }
                        try {
                            bdate = user.bdate;
                        } catch (Exception e) {
                            bdate = "";
                        }


                        settings = getSharedPreferences("DATA", Context.MODE_PRIVATE);
                        SharedPreferences.Editor editor;
                        editor = settings.edit();
                        editor.putString("LOGIN", login);
                        editor.putString("CITY", city);
                        editor.putString("COUNTRY", country);
                        editor.putString("AVATAR", avatar);
                        editor.putString("BDATE", bdate);
                        editor.commit();

                        BWPutUserDataIntoDBviaVK backgroundWorkerPutUserData = new BWPutUserDataIntoDBviaVK(LoginActivity.this);
                        backgroundWorkerPutUserData.execute(login, avatar, city, country, bdate);
                    }
                });

            }

            @Override
            public void onError(VKError error) {
                // Произошла ошибка авторизации (например, пользователь запретил авторизацию)
                Toast.makeText(getApplicationContext(), "Error login in VK", Toast.LENGTH_LONG).show();
            }
        })) {

        }
    }

    public void onFBEnterClick(View v) {
        Intent intent = new Intent(this, FacebookLoginActivity.class);
        startActivity(intent);
    }

}
