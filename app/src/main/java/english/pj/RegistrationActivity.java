package english.pj;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

public class RegistrationActivity extends Activity {

EditText etLogin;
    EditText etPass;
    EditText etBDate;
    Context context;
    String login;
    String pass;
    String city;
    String country;
    String bdate;
    AlertDialog alertDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);
        etLogin = (EditText)findViewById(R.id.EditLogin);
        etPass = (EditText)findViewById(R.id.EditPassword);
        etBDate = (EditText)findViewById(R.id.editBDate);
        bdate = "";
        country = "Украина";
        FontChangeCrawler fontChanger = new FontChangeCrawler(getAssets(), "fonts/Raleway-Regular.ttf");
        fontChanger.replaceFonts((ViewGroup)this.findViewById(android.R.id.content));
        TextView tvHeader = (TextView)findViewById(R.id.textViewRegEng);
        tvHeader.setTypeface(Typeface.createFromAsset(getAssets(), "fonts/TitanOne-Regular.ttf"));
        TextView tvHeader2 = (TextView)findViewById(R.id.textViewRegFC);
        tvHeader2.setTypeface(Typeface.createFromAsset(getAssets(), "fonts/TitanOne-Regular.ttf"));

        final Spinner spinner = (Spinner) findViewById(R.id.spinnerCountryRegistration);

        fontChanger.replaceFonts((ViewGroup)spinner);
        // заголовок
        spinner.setPrompt("Выберите страну");

        // устанавливаем обработчик нажатия
        spinner.setOnItemSelectedListener(new OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view,
                                       int position, long id) {
                country = spinner.getSelectedItem().toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }

        });

      /*  etPass.setOnKeyListener(new View.OnKeyListener() {
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                // If the event is a key-down event on the "enter" button
                if ((event.getAction() == KeyEvent.ACTION_DOWN) &&
                        (keyCode == KeyEvent.KEYCODE_ENTER)) {
                    // Perform action on key press
                    //Toast.makeText(HelloFormStuff.this, edittext.getText(), Toast.LENGTH_SHORT).show();
                    return true;
                }
                return false;
            }
        });*/
    }

    @Override
    protected void onResume() {
        super.onResume();
        Intent intent = getIntent();
        etLogin.setText(intent.getStringExtra("login"));
    }

    public void onRegisterButton (View v) {

        login = etLogin.getText().toString();
        pass = etPass.getText().toString();
        bdate = etBDate.getText().toString();
        city = "";

        try{ if(login.equals(null)){ login = "";} } catch (Exception e) { login = "";}
        try{ if(pass.equals(null)){ pass = "";} } catch (Exception e) { pass = "";}
        try{ if(bdate.equals(null)){ bdate = "";} } catch (Exception e) { bdate = "";}


        if ("".equals(login) && "".equals(pass)) {
            alertDialog = new AlertDialog.Builder(this).create();
            alertDialog.setTitle("Введите логин и пароль");
            alertDialog.show();
        } else if("".equals(login)) {
            alertDialog = new AlertDialog.Builder(this).create();
            alertDialog.setTitle("Введите логин");
            alertDialog.show();
        } else if( "".equals(pass)) {
            alertDialog = new AlertDialog.Builder(this).create();
            alertDialog.setTitle("Введите пароль");
            alertDialog.show();
        } else {
            BWRegisterNewUser bwRegisterNewUser = new BWRegisterNewUser(this);
            bwRegisterNewUser.execute(login, pass, country, bdate);
        }

    }

    // отображаем диалоговое окно для выбора даты
    public void setDate(View v) {
        new DatePickerDialog(RegistrationActivity.this, d,
                1980,
                0,
                1)
                .show();
    }
    // установка обработчика выбора даты
    DatePickerDialog.OnDateSetListener d=new DatePickerDialog.OnDateSetListener() {
        public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
            bdate = String.valueOf(dayOfMonth) + "." + String.valueOf(monthOfYear + 1) + "." + String.valueOf(year);
            etBDate.setText(bdate);
        }
    };
}
