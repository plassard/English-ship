package english.pj;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.database.SQLException;
import android.os.Bundle;
import android.util.Log;
import android.content.pm.Signature;
import android.util.Base64;
import com.google.firebase.analytics.FirebaseAnalytics;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.io.IOException;

public class StartActivity extends Activity {
    private FirebaseAnalytics mFirebaseAnalytics;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);

       // printKeyHash(this);

        dbHelperForDBImport myDbHelper;
        myDbHelper = new dbHelperForDBImport(this);
        try {
            myDbHelper.createDataBase();
        } catch (IOException e) {   }

        try {
            myDbHelper.openDataBase();
        } catch(SQLException sqle){
            throw sqle;
        }
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        mFirebaseAnalytics = FirebaseAnalytics.getInstance(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        String login = "";
        try {
            SharedPreferences settings;
            settings = this.getSharedPreferences("DATA", Context.MODE_PRIVATE);
            login = mech.getLogin(this);
            if (login.equals("")) {
                Intent intent = new Intent(this, LoginActivity.class);
                startActivity(intent);
            } else {        }
        } catch (Exception e) {
                Intent intent = new Intent(this, LoginActivity.class);
                startActivity(intent);
        } catch (Error e) {
        }
        //try {


        startService(new Intent(this, ServiceBackround.class));

        BWCheckIfBillingBought bwCheckIfBillingBought = new BWCheckIfBillingBought(this);
        bwCheckIfBillingBought.execute();
    }

    public static String printKeyHash(Activity context) {
        PackageInfo packageInfo;
        String key = null;
        try {
            //getting application package name, as defined in manifest
            String packageName = context.getApplicationContext().getPackageName();

            //Retriving package info
            packageInfo = context.getPackageManager().getPackageInfo(packageName,
                    PackageManager.GET_SIGNATURES);

            Log.i("Package Name=", context.getApplicationContext().getPackageName());

            for (Signature signature : packageInfo.signatures) {
                MessageDigest md = MessageDigest.getInstance("SHA");
                md.update(signature.toByteArray());
                key = new String(Base64.encode(md.digest(), 0));

                // String key = new String(Base64.encodeBytes(md.digest()));
                Log.i("Key Hash=", key);
            }
        } catch (PackageManager.NameNotFoundException e1) {
            Log.i("Name not found", e1.toString());
        }
        catch (NoSuchAlgorithmException e) {
            Log.i("No such an algorithm", e.toString());
        } catch (Exception e) {
            Log.i("Exception", e.toString());
        }

        return key;
    }

}
