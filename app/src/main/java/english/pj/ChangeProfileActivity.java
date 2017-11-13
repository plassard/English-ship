package english.pj;

import android.Manifest;
import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.support.annotation.RequiresApi;
import android.text.TextUtils;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.MemoryPolicy;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.regex.Pattern;


public class ChangeProfileActivity extends Activity {

    String login, city, country, bdate, avatar;
    EditText etCity, etBDate;
    private ImageView ivAvatar;
    static String id;
    TextView tvLogin;
    private Bitmap bitmap;
    boolean isKitKat = Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT;
    String strImagePath = "no image selected";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_profile);
        FontChangeCrawler fontChanger = new FontChangeCrawler(getAssets(), "fonts/Raleway-Bold.ttf");
        fontChanger.replaceFonts((ViewGroup)this.findViewById(android.R.id.content));
        TextView tvHeader = (TextView)findViewById(R.id.textViewNick);
        tvHeader.setTypeface(Typeface.createFromAsset(getAssets(), "fonts/Raleway-Black.ttf"));

        tvLogin = (TextView) findViewById(R.id.textViewStatHeader);
        etCity = (EditText) findViewById(R.id.editTextCity);
        etBDate = (EditText) findViewById(R.id.editTextDate);
        ivAvatar = (ImageView) findViewById(R.id.imageViewAvatar);

        avatar = "";
        final Spinner spinnerCountries = (Spinner) findViewById(R.id.spinnerCountries);
        spinnerCountries.setOnItemSelectedListener(new OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view,
                                       int position, long id) {
                country = spinnerCountries.getSelectedItem().toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });



    }

    @Override
    protected void onResume () {
        super.onResume();
        SharedPreferences settings = getSharedPreferences("DATA", Context.MODE_PRIVATE);
        login = settings.getString("LOGIN", null);
        city = settings.getString("CITY", null);
        country = settings.getString("COUNTRY", null);
        bdate = settings.getString("BDATE", null);
        avatar = settings.getString("AVATAR", null);
        tvLogin.setText(login);
        etCity.setText(city);
        etBDate.setText(bdate);
        BWUserStatistics bwGetUserStatistics = new BWUserStatistics(this);
        bwGetUserStatistics.execute(login);

        try {
            if(!avatar.equals(null) & !avatar.equals("")) {
                Picasso.with(this).load(avatar)
                        .memoryPolicy(MemoryPolicy.NO_CACHE )
                        .networkPolicy(NetworkPolicy.NO_CACHE)
                        .resize(720, 0).onlyScaleDown()
                        .transform(new CircleTransform())
                        .into((ImageView) findViewById(R.id.imageViewAvatar));
            } else {
                Picasso.with(this).load(R.drawable.ava).transform(new CircleTransform()).into((ImageView)findViewById(R.id.imageViewAvatar));
            }
        } catch (Exception e) {
            Picasso.with(this).load(R.drawable.ava).transform(new CircleTransform()).into((ImageView) findViewById(R.id.imageViewAvatar));
        }
    }

    public void onApplyButton (View v) {
        city = etCity.getText().toString();
        bdate = etBDate.getText().toString();
        try{ if(avatar.equals(null)){
            avatar = "";
        } } catch (Exception e) { avatar = ""; }
        try{ if(country.equals(null)){
            country = "";
        } } catch (Exception e) { country = ""; }
        BWChangeUserProfile bwChangeUserProfile = new BWChangeUserProfile(this);
        bwChangeUserProfile.execute(login, avatar, city, country, bdate);

        Picasso.with(this).invalidate(avatar);

        try {
        //    Picasso.with(this).load(new File(avatar)).skipMemoryCache()
           //         .placeholder(R.drawable.ava).stableKey(avatar).into((ImageView) findViewById(R.id.imageViewAvatar));

            Picasso.with(this).invalidate(avatar);

            Picasso.with(this).load(avatar)
                    .memoryPolicy(MemoryPolicy.NO_CACHE )
                    .networkPolicy(NetworkPolicy.NO_CACHE)
                    .transform(new CircleTransform())
                    .into((ImageView) findViewById(R.id.imageViewAvatar));
            Picasso.with(this).invalidate(avatar);
        } catch (Exception e) {
            avatar = "";
            Picasso.with(this).load(R.drawable.ava)
                    .resize(720, 0).onlyScaleDown()
                    .transform(new CircleTransform()).into((ImageView) findViewById(R.id.imageViewAvatar));
        }


    }

    // отображаем диалоговое окно для выбора даты
    public void setDateChangeProfile(View v) {
        new DatePickerDialog(ChangeProfileActivity.this, d,
                1970,
                0,
                1)
                .show();
    }
    // установка обработчика выбора даты
    DatePickerDialog.OnDateSetListener d=new DatePickerDialog.OnDateSetListener() {
        public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
            etBDate.setText(String.valueOf(dayOfMonth) + "." + String.valueOf(monthOfYear + 1) + "." + String.valueOf(year));
        }
    };


    public void onUploadClick(View v) {

        // For API >= 23 we need to check specifically that we have permissions to read external storage,
        // but we don't know if we need to for the URI so the simplest is to try open the stream and see if we get error.
        boolean requirePermissions = false;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {

            // request permissions and handle the result in onRequestPermissionsResult()
            requirePermissions = true;
            requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 0);
        }
        if (!requirePermissions) {
            Intent intent = new Intent();
            intent.setType("image/*");
            intent.setAction(Intent.ACTION_PICK);
            startActivityForResult(Intent.createChooser(intent, "Select Picture"), 1);
        }
    }


    private Uri mCropImageUri;

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            Intent intent = new Intent();
            intent.setType("image/*");
            intent.setAction(Intent.ACTION_PICK);
            startActivityForResult(Intent.createChooser(intent, "Select Picture"), 1);
        } else {
            Toast.makeText(this, "Необходимые разрешения не предоставлены", Toast.LENGTH_LONG).show();
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (data != null && data.getData() != null && resultCode == RESULT_OK) {

            boolean isImageFromGoogleDrive = false;
            Uri uri = data.getData();

            if (isKitKat && DocumentsContract.isDocumentUri(ChangeProfileActivity.this, uri)) {

                if ("com.android.externalstorage.documents".equals(uri.getAuthority())) {
                    String docId = DocumentsContract.getDocumentId(uri);
                    String[] split = docId.split(":");
                    String type = split[0];

                    if ("primary".equalsIgnoreCase(type)) {
                        strImagePath = Environment.getExternalStorageDirectory() + "/" + split[1];
                    }
                    else {
                        Pattern DIR_SEPORATOR = Pattern.compile("/");
                        Set<String> rv = new HashSet<>();
                        String rawExternalStorage = System.getenv("EXTERNAL_STORAGE");
                        String rawSecondaryStoragesStr = System.getenv("SECONDARY_STORAGE");
                        String rawEmulatedStorageTarget = System.getenv("EMULATED_STORAGE_TARGET");
                        if(TextUtils.isEmpty(rawEmulatedStorageTarget))
                        {
                            if(TextUtils.isEmpty(rawExternalStorage))
                            {
                                rv.add("/storage/sdcard0");
                            }
                            else
                            {
                                rv.add(rawExternalStorage);
                            }
                        }
                        else
                        {
                            String rawUserId;
                            if(Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN_MR1)
                            {
                                rawUserId = "";
                            }
                            else
                            {
                                String path = Environment.getExternalStorageDirectory().getAbsolutePath();
                                String[] folders = DIR_SEPORATOR.split(path);
                                String lastFolder = folders[folders.length - 1];
                                boolean isDigit = false;
                                try
                                {
                                    Integer.valueOf(lastFolder);
                                    isDigit = true;
                                }
                                catch(NumberFormatException ignored)
                                {
                                }
                                rawUserId = isDigit ? lastFolder : "";
                            }
                            if(TextUtils.isEmpty(rawUserId))
                            {
                                rv.add(rawEmulatedStorageTarget);
                            }
                            else
                            {
                                rv.add(rawEmulatedStorageTarget + File.separator + rawUserId);
                            }
                        }
                        if(!TextUtils.isEmpty(rawSecondaryStoragesStr))
                        {
                            String[] rawSecondaryStorages = rawSecondaryStoragesStr.split(File.pathSeparator);
                            Collections.addAll(rv, rawSecondaryStorages);
                        }
                        String[] temp = rv.toArray(new String[rv.size()]);

                        for (int i = 0; i < temp.length; i++)   {
                            File tempf = new File(temp[i] + "/" + split[1]);
                            if(tempf.exists()) {
                                strImagePath = temp[i] + "/" + split[1];
                            }
                        }
                    }
                }
                else if ("com.android.providers.downloads.documents".equals(uri.getAuthority())) {
                    String id = DocumentsContract.getDocumentId(uri);
                    Uri contentUri = ContentUris.withAppendedId(
                            Uri.parse("content://downloads/public_downloads"), Long.valueOf(id));

                    Cursor cursor = null;
                    String column = "_data";
                    String[] projection = {
                            column
                    };

                    try {
                        cursor = getContentResolver().query(contentUri, projection, null, null,
                                null);
                        if (cursor != null && cursor.moveToFirst()) {
                            final int column_index = cursor.getColumnIndexOrThrow(column);
                            strImagePath = cursor.getString(column_index);
                        }
                    } finally {
                        if (cursor != null)
                            cursor.close();
                    }
                }
                else if ("com.android.providers.media.documents".equals(uri.getAuthority())) {
                    String docId = DocumentsContract.getDocumentId(uri);
                    String[] split = docId.split(":");
                    String type = split[0];

                    Uri contentUri = null;
                    if ("image".equals(type)) {
                        contentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
                    } else if ("video".equals(type)) {
                        contentUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
                    } else if ("audio".equals(type)) {
                        contentUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
                    }

                    String selection = "_id=?";
                    String[] selectionArgs = new String[]{
                            split[1]
                    };

                    Cursor cursor = null;
                    String column = "_data";
                    String[] projection = {
                            column
                    };

                    try {
                        cursor = getContentResolver().query(contentUri, projection, selection, selectionArgs,
                                null);
                        if (cursor != null && cursor.moveToFirst()) {
                            int column_index = cursor.getColumnIndexOrThrow(column);
                            strImagePath = cursor.getString(column_index);
                        }
                    } finally {
                        if (cursor != null)
                            cursor.close();
                    }
                }
                else if("com.google.android.apps.docs.storage".equals(uri.getAuthority()))   {
                    isImageFromGoogleDrive = true;
                }
            }
            else if ("content".equalsIgnoreCase(uri.getScheme())) {
                Cursor cursor = null;
                String column = "_data";
                String[] projection = {
                        column
                };

                try {
                    cursor = getContentResolver().query(uri, projection, null, null,
                            null);
                    if (cursor != null && cursor.moveToFirst()) {
                        int column_index = cursor.getColumnIndexOrThrow(column);
                        strImagePath = cursor.getString(column_index);
                    }
                } finally {
                    if (cursor != null)
                        cursor.close();
                }
            }
            else if ("file".equalsIgnoreCase(uri.getScheme())) {
                strImagePath = uri.getPath();
            }


            if(isImageFromGoogleDrive)  {
                try {
                    Bitmap bitmap = BitmapFactory.decodeStream(getContentResolver().openInputStream(uri));
                    ivAvatar.setImageBitmap(bitmap);
                }
                catch (Exception e) {
                    e.printStackTrace();
                }
            }
            else    {
                File f = new File(strImagePath);
                BitmapFactory.Options bmOptions = new BitmapFactory.Options();
                Bitmap bitmap = BitmapFactory.decodeFile(f.getAbsolutePath(),bmOptions);
                ivAvatar.setImageBitmap(bitmap);
            }
            decodeFile(strImagePath);
        }
        super.onActivityResult(requestCode, resultCode, data);

    }

    public void decodeFile(String filePath) {
        // Decode image size
        BitmapFactory.Options o = new BitmapFactory.Options();
        o.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(filePath, o);

        // The new size we want to scale to
        final int REQUIRED_SIZE = 1280;

        // Find the correct scale value. It should be the power of 2.
        int width_tmp = o.outWidth, height_tmp = o.outHeight;
        int scale = 1;
        while (true) {
            if (width_tmp < REQUIRED_SIZE && height_tmp < REQUIRED_SIZE)
                break;
            width_tmp /= 2;
            height_tmp /= 2;
            scale *= 2;
        }

        // Decode with inSampleSize
        BitmapFactory.Options o2 = new BitmapFactory.Options();
        o2.inSampleSize = scale;
        bitmap = BitmapFactory.decodeFile(filePath, o2);

        avatar = "http://q99710ny.bget.ru/uploads/" + mech.getLogin(this) + ".jpg";

        SharedPreferences settings;
        settings = ChangeProfileActivity.this.getSharedPreferences("DATA", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor;
        editor = settings.edit();
        editor.putString("AVATAR", avatar);
        editor.commit();

       /* dialog = ProgressDialog.show(ChangeProfileActivity.this, "Uploading",
                "Please wait...", true);*/
        new ImageGalleryTask().execute();
    }

    class ImageGalleryTask extends AsyncTask<Void, Void, String> {
        //@SuppressWarnings("unused")
        @Override
        protected String doInBackground(Void... unsued) {
            InputStream is;
            BitmapFactory.Options bfo;
            Bitmap bitmapOrg;
            ByteArrayOutputStream bao ;
            Context context;
            context = ChangeProfileActivity.this;
            bfo = new BitmapFactory.Options();
            bfo.inSampleSize = 2;
            bitmapOrg = BitmapFactory.decodeFile(Environment.getExternalStorageDirectory() + "/" + mech.getLogin(context), bfo);

            bao = new ByteArrayOutputStream();

            try{
                bitmap.compress(Bitmap.CompressFormat.JPEG, 90, bao);
            } catch (Exception e) {
            }

            byte [] ba = bao.toByteArray();
            String ba1 = Base64.encodeToString(ba, Base64.DEFAULT);
            ArrayList<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
            nameValuePairs.add(new BasicNameValuePair("image",ba1));
            nameValuePairs.add(new BasicNameValuePair("cmd",mech.getLogin(context) + ".jpg"));
            Log.v("log_tag", System.currentTimeMillis()+".jpg");
            try{
                HttpClient httpclient = new DefaultHttpClient();
                HttpPost httppost = new
                        //  Here you need to put your server file address
                        HttpPost("http://q99710ny.bget.ru/upload_photo.php");
                httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
                HttpResponse response = httpclient.execute(httppost);
                HttpEntity entity = response.getEntity();
                is = entity.getContent();
                Log.v("log_tag", "In the try Loop" );
            }catch(Exception e){
                Log.v("log_tag", "Error in http connection "+e.toString());
            }
            return "Success";
        }

        @Override
        protected void onProgressUpdate(Void... unsued) {

        }

        @Override
        protected void onPostExecute(String sResponse) {

            try {
                Picasso.with(ChangeProfileActivity.this).load(avatar)
                        .memoryPolicy(MemoryPolicy.NO_CACHE )
                        .networkPolicy(NetworkPolicy.NO_CACHE)
                        .resize(720, 0)
                        .onlyScaleDown()
                        .transform(new CircleTransform())
                        .into((ImageView) findViewById(R.id.imageViewAvatar));
            } catch (Exception e) {
                Picasso.with(ChangeProfileActivity.this).load(R.drawable.ava).into((ImageView) findViewById(R.id.imageViewAvatar));
            }

        }

    }

}
