package english.pj;

import android.app.Activity;
import android.content.Context;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

public class ChatActivity extends Activity {
    static LinearLayout linLayoutChat;
    EditText etChat;
    Context context = this;
    static ScrollView scrollView;
    static boolean isChatActive = false;
    static String anotherPartnerInChat;
    static boolean isNewMessage;
    static JSONObject jsonObject;
    static LayoutInflater inflater;
    static View view;
    static boolean justResumedFirstTime;
    TextView tvHeader;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        FontChangeCrawler fontChanger = new FontChangeCrawler(getAssets(), "fonts/Raleway-Regular.ttf");
        fontChanger.replaceFonts((ViewGroup)this.findViewById(android.R.id.content));
        tvHeader = (TextView)findViewById(R.id.textViewChatPartner);
        tvHeader.setTypeface(Typeface.createFromAsset(getAssets(), "fonts/Raleway-Black.ttf"));

        ImageView ivAvaChat = (ImageView)findViewById(R.id.imageViewChatAva);
        String ava = "";
        ava = getIntent().getStringExtra("ava");

        try{
            if (ava.equals("")) {
                Picasso.with(this).load(R.drawable.ava).resize(720, 0).onlyScaleDown().transform(new CircleTransform()).into(ivAvaChat);
            } else {
                Picasso.with(this).load(ava).resize(720, 0).onlyScaleDown().transform(new CircleTransform()).into(ivAvaChat);
            }
        } catch (Exception e) {
            Picasso.with(this).load(R.drawable.ava).resize(720, 0).onlyScaleDown().transform(new CircleTransform()).into(ivAvaChat);
        }
        linLayoutChat = (LinearLayout) findViewById(R.id.ChatLinLayout);
        etChat = (EditText) findViewById(R.id.editTextChat);
        scrollView = (ScrollView) ChatActivity.this.findViewById(R.id.scrollChat);
        //tvHeader = (TextView)findViewById(R.id.textViewChatPartner);
        isNewMessage = false;
    }
    CountDownTimer countDownTimer;

    @Override
    protected void onResume() {
        super.onResume();
        BWGetChatMessages bwGetChatMessages = new BWGetChatMessages(this);
        bwGetChatMessages.execute();
        isChatActive = true;
        //ServiceBackround.howMuchChatGetMessagesWaits = 2000;
        justResumedFirstTime = true;
        tvHeader.setText("Чат с  " +
                anotherPartnerInChat);
        countDownTimer = new CountDownTimer(18000000, 2500) {
            @Override
            public void onTick(long millisUntilFinished) {
                startGettingChatMessages();
            }
            @Override
            public void onFinish() {

            }
        }.start();
    }

    void startGettingChatMessages(){
        BWGetChatMessages bwGetChatMessages = new BWGetChatMessages(this);
        bwGetChatMessages.execute();
    }
    @Override
    protected void onStop() {
        isChatActive = false;
        //ServiceBackround.howMuchChatGetMessagesWaits = 15000;


        justResumedFirstTime = false;

        super.onStop();
    }

    public void onChatMesSend(View v) {
        if(!(etChat.getText().equals(""))) {
            try {
                BWSendChatMessage bwSendChatMessage = new BWSendChatMessage(this);
                bwSendChatMessage.execute(anotherPartnerInChat, etChat.getText().toString());
                view = inflater.inflate( R.layout.chat_row, null );
                Button myButton = (Button) view.findViewById( R.id.buttonRowInChat );
                myButton.setTransformationMethod(null);
                myButton.setText(jsonObject.getString("text"));
                linLayoutChat.addView(view);

            } catch (Exception e) {
            }

            //BWSendPushNotification bwSendPushNotification = new BWSendPushNotification(this);
            //bwSendPushNotification.execute("chat", anotherPartnerInChat, etChat.getText().toString());

            etChat.setText("");
        }
    }

    static public void onChatUpdate(Context context) {

        inflater = LayoutInflater.from(context);
        linLayoutChat.removeAllViews();

        int i = 0;
        while (i < BWGetChatMessages.json_data.length()) {
            try {
                jsonObject = new JSONObject(BWGetChatMessages.json_data.getString(i));
                if (jsonObject.getString("author").equals(mech.getLogin(context)) && jsonObject.getString("client").equals(anotherPartnerInChat)) {
                    view = inflater.inflate( R.layout.chat_row, null );
                    Button myButton = (Button) view.findViewById( R.id.buttonRowInChat );
                    myButton.setTransformationMethod(null);
                    myButton.setText(jsonObject.getString("text"));
                    linLayoutChat.addView(view);
                } else if(jsonObject.getString("author").equals(anotherPartnerInChat) && jsonObject.getString("client").equals(mech.getLogin(context))) {
                    view = inflater.inflate( R.layout.chat_row_right, null );
                    Button myButton = (Button) view.findViewById( R.id.buttonRowInChat );
                    myButton.setTransformationMethod(null);
                    myButton.setText(jsonObject.getString("text"));
                    linLayoutChat.addView(view);
                }

            } catch (JSONException e) {  }
            i++;

        }
        new CountDownTimer(2000, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {  /* do something after 1s*/ }

            @Override
            public void onFinish() {
                if (isNewMessage) {
                    scrollView.fullScroll(ScrollView.FOCUS_DOWN);
                }
                if(justResumedFirstTime) {
                    scrollView.fullScroll(ScrollView.FOCUS_DOWN);
                    justResumedFirstTime = false;
                };
            }
        }.start();

    }

    public void onButtonBackChat(View v) {
        finish();
    }
}


