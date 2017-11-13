package english.pj;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

public class SettingsActivity extends Activity {
    AlertDialog.Builder adb;
    AlertDialog dialogExit;
    String sound;
    ImageView ivSound, ivAvaSettings;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        FontChangeCrawler fontChanger = new FontChangeCrawler(getAssets(), "fonts/Raleway-Bold.ttf");
        fontChanger.replaceFonts((ViewGroup)this.findViewById(android.R.id.content));
        TextView tvHeader = (TextView)findViewById(R.id.textViewNickSettings);
        tvHeader.setTypeface(Typeface.createFromAsset(getAssets(), "fonts/Raleway-Black.ttf"));

        ivSound = (ImageView)findViewById(R.id.imageViewSettingsSound);
        ivAvaSettings = (ImageView)findViewById(R.id.imageViewSettingsAvatar);
        if(mech.isSoundOn(this)){
            ivSound.setBackgroundResource(R.drawable.speakers_on);
        } else {
            ivSound.setBackgroundResource(R.drawable.speakers_off);
        }
        try{
            Picasso.with(this).load(mech.getAvatar(this))
                    .resize(720, 0).onlyScaleDown()
                    .transform(new CircleTransform()).into(ivAvaSettings);
        } catch (Exception e) {
            Picasso.with(this).load(R.drawable.ava)
                    .transform(new CircleTransform()).into(ivAvaSettings);
        }

        TextView tvNick = (TextView)findViewById(R.id.textViewNickSettings);
        tvNick.setText(mech.getLogin(this));
        String city, country;
        SharedPreferences settings = getSharedPreferences("DATA", Context.MODE_PRIVATE);
        city = settings.getString("CITY", null);
        country = settings.getString("COUNTRY", null);
        try{if(city.equals(null)) { city = ""; } } catch (Exception e) { city = "";}
        try{if(country.equals(null)) { country = "";}} catch (Exception e) { country = "";}
        TextView tvLocation = (TextView)findViewById(R.id.textViewLocationSettings);
        String text = "";
        if(city.equals("")){ text = country; }
        else if(country.equals("")){ text = city; }
        else {text = city + ", " + country; }
        tvLocation.setText(text);

        TextView textViewPrem = (TextView)findViewById(R.id.textViewPremSettings);
        ImageView ivPrem = (ImageView)findViewById(R.id.imageViewSettingsPrem);
        if(mech.isPremium(this)){
            textViewPrem.setVisibility(View.GONE);
            ivPrem.setVisibility(View.GONE);
        } else {
            textViewPrem.setVisibility(View.VISIBLE);
            ivPrem.setVisibility(View.VISIBLE);
        }

    }


    Dialog dialog;
    public void onExitAccount (View v) {

        dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.exit_dialog);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.setCanceledOnTouchOutside(true);
        dialog.getWindow().setLayout(WindowManager.LayoutParams.WRAP_CONTENT, WindowManager.LayoutParams.WRAP_CONTENT);
        dialog.show();

        Button btnYes = (Button) dialog.findViewById(R.id.buttonExitYes);
        Button btnNo= (Button) dialog.findViewById(R.id.buttonExitNo);

        btnYes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getApplicationContext().getSharedPreferences("DATA", 0).edit().clear().apply();
                Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
                startActivity(intent);
            }
        });


        btnNo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
    }
    public void ExitAccount() {
        this.getSharedPreferences("DATA", 0).edit().clear().apply();
        Intent intent = new Intent(this, StartActivity.class);
        startActivity(intent);
    }

    public void onChangeProfile (View v) {
            Intent intent = new Intent(this, ChangeProfileActivity.class);
            startActivity(intent);

    }

    public void onHelp (View v) {
            Intent intent = new Intent(this, HelpActivity.class);
            startActivity(intent);
    }

    public void onPremium (View v) {
        Intent intent = new Intent(this, BuyActivity.class);
        startActivity(intent);
    }



    public void onAbout (View v) {
            Intent intent = new Intent(this, AboutActivity.class);
            startActivity(intent);
    }

    public void onSound(View v) {
        SharedPreferences settings = this.getSharedPreferences("DATA", this.MODE_PRIVATE);
        SharedPreferences.Editor editor;
        editor = settings.edit();
        if(mech.isSoundOn(this)){
            sound = "off";
            ivSound.setBackgroundResource(R.drawable.speakers_off);
        } else {
            sound = "on";
            ivSound.setBackgroundResource(R.drawable.speakers_on);
        }
        editor.putString("SOUND", sound);
        editor.commit();
    }
    public void onRate (View v) {
        Uri uri = Uri.parse("https://https://play.google.com/store/apps/details?id=english.pj");
        Intent intentLink = new Intent(Intent.ACTION_VIEW, uri);
        startActivity(intentLink);
    }
    public void onBackIm(View v){
        this.finish();
    }
}
