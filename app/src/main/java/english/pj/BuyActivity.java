package english.pj;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.Html;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;
import com.anjlab.android.iab.v3.BillingProcessor;
import com.anjlab.android.iab.v3.SkuDetails;
import com.anjlab.android.iab.v3.TransactionDetails;

import org.w3c.dom.Text;

import static android.text.Html.fromHtml;


public class BuyActivity extends Activity implements BillingProcessor.IBillingHandler {

    Context context;
    private static final String SUBSCRIPTION_ID = "premium";
    private static final String LICENSE_KEY = null; // PUT YOUR MERCHANT KEY HERE;
    // put your Google merchant id here (as stated in public profile of your Payments Merchant Center)
    // if filled library will provide protection against Freedom alike Play Market simulators
    private static final String MERCHANT_ID=null;

    BillingProcessor bp;
    private boolean readyToPurchase = false;

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_buy);
        context = this;
        BWCheckIfBillingBought bwCheckIfBillingBought = new BWCheckIfBillingBought(this);
        bwCheckIfBillingBought.execute();

        FontChangeCrawler fontChanger = new FontChangeCrawler(getAssets(), "fonts/Raleway-Bold.ttf");
        fontChanger.replaceFonts((ViewGroup)this.findViewById(android.R.id.content));

        TextView tvAllWords = (TextView)findViewById(R.id.textViewAllWords);

        if (android.os.Build.VERSION.SDK_INT >= 24) {
            tvAllWords.setText(Html.fromHtml("- тебе будет открыт доступ ко " + "<font color=\"#fac917\">" + "ВСЕМ " + "</font>" + "словам;",Html.FROM_HTML_MODE_LEGACY));
        } else {
            tvAllWords.setText(fromHtml("- тебе будет открыт доступ ко " + "<font color=\"#fac917\">" + "ВСЕМ " + "</font>" + "словам;"));
        }

        TextView tvCan = (TextView)findViewById(R.id.textViewCan);

        if (android.os.Build.VERSION.SDK_INT >= 24) {
            tvCan.setText(Html.fromHtml(
                    "- плата символическая, небольшая, как говорится - что мы, последние люди в этом мире? ты " + "<font color=\"#fac917\">" + "можешь" + "</font>" + " себе это позволить!",Html.FROM_HTML_MODE_LEGACY));
        } else {
            tvCan.setText(fromHtml("- плата символическая, небольшая, как говорится - что мы, последние люди в этом мире? ты " + "<font color=\"#fac917\">" + "можешь" + "</font>" + " себе это позволить!"));
        }


        String base64EncodedPublicKey =
                "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAtjJyOcjxXb5pIS9wAy0hQOGTnU+Vx5AE2asT/MfhgN494OXyZ1YQIcdE3XhGYDbk0aR+5vCI2D3EWEghdwxEYSeSTuH61hku+BLCmxmYUlsEym7Sr10/D7YCFQDK5FF+71kSR9gaE4QJQCDYhLoIRx9kufQfxQ/KcpY3gUG0K8XlzsmLMLuu2454swS3BMLEJtt9XadZ4Aeg7Uxz2qywogWJ9IZmD6pwyjzCNhqmfnHVUS7L0ZrJgSbow9EQ506kcCAzl+wCP8YcSLah56H3pxP9nWVH793yce93Cyk14bvAvTvMX7cEXfLWdJkfm2O+EbJB5tv0PR9LmTLHu41rkQIDAQAB";

        if(!BillingProcessor.isIabServiceAvailable(this)) {
            showToast("Чтобы делать покупик, обновите Android Market/Play до версии >= 3.9.16");
        }

        bp = BillingProcessor.newBillingProcessor(this, base64EncodedPublicKey, this); // doesn't bind
        bp.initialize(); // binds

        bp = new BillingProcessor(this, LICENSE_KEY, MERCHANT_ID, new BillingProcessor.IBillingHandler() {
            @Override
            public void onProductPurchased(@NonNull String productId, @Nullable TransactionDetails details) {
                //showToast("onProductPurchased: " + productId);
                SharedPreferences settings;
                settings = context.getSharedPreferences("DATA", Context.MODE_PRIVATE);
                SharedPreferences.Editor editor;
                editor = settings.edit();
                editor.putString("PREMIUM", "yes");
                editor.commit();
            }
            @Override
            public void onBillingError(int errorCode, @Nullable Throwable error) {
                showToast("Ошибка покупки №: " + Integer.toString(errorCode));
            }
            @Override
            public void onBillingInitialized() {
                //showToast("onBillingInitialized");
                readyToPurchase = true;

            }
            @Override
            public void onPurchaseHistoryRestored() {
                showToast("onPurchaseHistoryRestored");
                for(String sku : bp.listOwnedProducts())
                    Log.d("YYY", "Owned Managed Product: " + sku);
                for(String sku : bp.listOwnedSubscriptions())
                    Log.d("YYY", "Owned Subscription: " + sku);

            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (!bp.handleActivityResult(requestCode, resultCode, data)) {
            //showToast("Результат " + requestCode + requestCode);
            super.onActivityResult(requestCode, resultCode, data);

        }
    }


    public void onBuy(View v){
        if (!readyToPurchase) {
           // showToast("Billing not initialized.");
            return;
        }
        bp.subscribe(this,SUBSCRIPTION_ID);

        SkuDetails subs = bp.getSubscriptionListingDetails(SUBSCRIPTION_ID);
        showToast(subs != null ? subs.toString() : "Failed to load subscription details");
    }

    @Override
    public void onProductPurchased(@NonNull String productId, @Nullable TransactionDetails details) {
        SharedPreferences settings;
        settings = context.getSharedPreferences("DATA", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor;
        editor = settings.edit();
        editor.putString("PREMIUM", "yes");
        editor.commit();
    }

    @Override
    public void onPurchaseHistoryRestored() {

    }

    @Override
    public void onBillingError(int errorCode, @Nullable Throwable error) {

    }

    @Override
    public void onBillingInitialized() {
       //showToast("onBillingInitialized");
        readyToPurchase = true;
    }

    private void showToast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show();
    }

    @Override
    public void onDestroy() {
        if (bp != null) {
            bp.release();
        }
        super.onDestroy();
    }
}