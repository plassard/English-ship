package english.pj;

import android.app.AlertDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.widget.Toast;

import com.anjlab.android.iab.v3.BillingProcessor;
import com.anjlab.android.iab.v3.PurchaseInfo;
import com.anjlab.android.iab.v3.SkuDetails;
import com.anjlab.android.iab.v3.TransactionDetails;



public class BWCheckIfBillingBought extends AsyncTask<String, Void, String> implements BillingProcessor.IBillingHandler{
    Context context;


    BillingProcessor bp;
    String base64EncodedPublicKey =
            "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAtjJyOcjxXb5pIS9wAy0hQOGTnU+Vx5AE2asT/MfhgN494OXyZ1YQIcdE3XhGYDbk0aR+5vCI2D3EWEghdwxEYSeSTuH61hku+BLCmxmYUlsEym7Sr10/D7YCFQDK5FF+71kSR9gaE4QJQCDYhLoIRx9kufQfxQ/KcpY3gUG0K8XlzsmLMLuu2454swS3BMLEJtt9XadZ4Aeg7Uxz2qywogWJ9IZmD6pwyjzCNhqmfnHVUS7L0ZrJgSbow9EQ506kcCAzl+wCP8YcSLah56H3pxP9nWVH793yce93Cyk14bvAvTvMX7cEXfLWdJkfm2O+EbJB5tv0PR9LmTLHu41rkQIDAQAB";

    BWCheckIfBillingBought(Context ctx) {
        context = ctx;
    }
    @Override
    protected String doInBackground(String... params) {
        try {
            bp = BillingProcessor.newBillingProcessor(context, base64EncodedPublicKey, this); // doesn't bind
            // binds
            try {
                bp.initialize();
            } catch (Error e) {
                // Toast.makeText(context, "err0" + e.toString(), Toast.LENGTH_LONG).show();
            } catch (Exception e) {
                // Toast.makeText(context, "exc0" + e.toString(), Toast.LENGTH_LONG).show();
            }



        }catch (Exception e){  }
        return null;
    }


    @Override
    protected void onPreExecute() {

    }


    @Override
    protected void onPostExecute(String result) {
        try {
            bp.loadOwnedPurchasesFromGoogle();
        } catch (Error e){
          //  Toast.makeText(context, "err" + e.toString(), Toast.LENGTH_LONG).show();
        }catch (Exception e){
           // Toast.makeText(context, "exc" + e.toString(), Toast.LENGTH_LONG).show();
        }

        SharedPreferences settings;
        settings = context.getSharedPreferences("DATA", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor;
        editor = settings.edit();
        boolean subscribed= false;
    try {
    SkuDetails subs = bp.getSubscriptionListingDetails("premium");
        if(subs.isSubscription){
            subscribed = true;
            if(bp.getSubscriptionTransactionDetails("premium").purchaseInfo.purchaseData.autoRenewing) {
                subscribed = true;
                Toast.makeText(context, "subscribed" + subscribed, Toast.LENGTH_LONG).show();
                editor.putString("PREMIUM", "yes");
            } else {
                subscribed= false;
                Toast.makeText(context, "p no longer" + subscribed, Toast.LENGTH_LONG).show();
                editor.putString("PREMIUM", "no");
            }
            editor.commit();
        }
    } catch (Exception e){
        Toast.makeText(context, "exc2" + e.toString() + subscribed, Toast.LENGTH_LONG).show();
    }

        try{
            if (bp != null){
                bp.release();}
        } catch (Error e){
            //Toast.makeText(context, "err2" + e.toString(), Toast.LENGTH_LONG).show();
        }catch (Exception e){
           // Toast.makeText(context, "exc2" + e.toString(), Toast.LENGTH_LONG).show();
        }
    }
    @Override
    protected void onProgressUpdate(Void... values) {
        super.onProgressUpdate(values);
    }


    @Override
    public void onProductPurchased(@NonNull String productId, @Nullable TransactionDetails details) {

    }

    @Override
    public void onPurchaseHistoryRestored() {

    }

    @Override
    public void onBillingError(int errorCode, @Nullable Throwable error) {

    }

    @Override
    public void onBillingInitialized() {

    }
}