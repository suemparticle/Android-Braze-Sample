package com.sue.helloworld;

import androidx.appcompat.app.AppCompatActivity;
import android.widget.Button;
import android.content.Intent;
import android.view.View;
import android.view.View.OnClickListener;
import android.os.Bundle;
import java.util.HashMap;
import java.util.Map;
import android.widget.TextView;
import android.webkit.WebView;
import android.webkit.WebSettings;
import android.webkit.WebViewClient;
import android.util.Log;

import com.mparticle.MParticle;
import com.mparticle.MParticleOptions;
import com.mparticle.identity.IdentityApiRequest;
import com.mparticle.identity.MParticleUser;
import com.mparticle.internal.MPUtility;
import com.mparticle.MPEvent;
import com.mparticle.MParticle.EventType;
import com.mparticle.commerce.Product;
import com.mparticle.commerce.CommerceApi;
import com.mparticle.commerce.CommerceEvent;
import com.mparticle.commerce.TransactionAttributes;

import com.appboy.Appboy;
import androidx.fragment.app.FragmentActivity;
import com.google.firebase.iid.FirebaseInstanceId;

import android.content.Context;

public class HelloActivity extends AppCompatActivity {

    private Context mApplicationContext;
    private WebView myWebView;
    private static final String TAG = "MainActivity";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hello);

        Button logoutButton = findViewById(R.id.logoutButton);
        logoutButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                System.out.println("Logged Out");

                Intent activity1Intent = new Intent(getApplicationContext(), MainActivity.class);
                startActivity(activity1Intent);

                MParticle.getInstance().Identity().logout(IdentityApiRequest.withEmptyUser().build());
            }
        });

        //Tracking events
        Button trackEvent = findViewById(R.id.trackEvent);
        trackEvent.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                System.out.println("Event tracked");

                // Note: may return null if the SDK has yet to acquire a user via IDSync!
                MParticleUser currentUser = MParticle.getInstance().Identity().getCurrentUser();

                Map<String, String> customAttributes = new HashMap<String, String>();
                customAttributes.put("eventAttribute", "eventValue");

                MPEvent event = new MPEvent.Builder("EventB", EventType.Navigation)
                        .customAttributes(customAttributes)
                        .build();

                MParticle.getInstance().logEvent(event);

                TextView tv = (TextView)findViewById(R.id.status);
                tv.setText("Tracked Event");

            }
        });

        //Track purchases
        Button purchase = findViewById(R.id.purchaseButton);
        purchase.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                System.out.println("Purchased item");

                // Note: may return null if the SDK has yet to acquire a user via IDSync!
                MParticleUser currentUser = MParticle.getInstance().Identity().getCurrentUser();

                // 1. Create the products
                Product product = new Product.Builder("ProductX", "sku-567", 100.00)
                        .quantity(4.0)
                        .build();

                // 2. Summarize the transaction
                TransactionAttributes attributes = new TransactionAttributes("transaction-5678")
                        .setRevenue(430.00)
                        .setTax(30.00);

                // 3. Log the purchase event
                CommerceEvent event = new CommerceEvent.Builder(Product.PURCHASE, product)
                        .transactionAttributes(attributes)
                        .build();
                MParticle.getInstance().logEvent(event);

                TextView tv = (TextView)findViewById(R.id.status);
                tv.setText("Purchased Item");
            }
        });

        //Set user attribute
        Button setAttribute = findViewById(R.id.setAttribute);
        setAttribute.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                System.out.println("set attribute");

                // Note: may return null if the SDK has yet to acquire a user via IDSync!
                MParticleUser currentUser = MParticle.getInstance().Identity().getCurrentUser();

                // Set user attributes associated with the user
                currentUser.setUserAttribute("$City","Chicago");

                TextView tv = (TextView)findViewById(R.id.status);
                tv.setText("Set User Attribute");
            }
        });

        MParticle.getInstance().Messaging().enablePushNotifications("709599249964");

        //Send push with Braze
        Button sendPush = findViewById(R.id.sendPush);
        sendPush.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                System.out.println("sent push");

                // Note: may return null if the SDK has yet to acquire a user via IDSync!
                MParticleUser currentUser = MParticle.getInstance().Identity().getCurrentUser();

                TextView tv = (TextView)findViewById(R.id.status);
                tv.setText("Sent Push");

                MPEvent event = new MPEvent.Builder("pushTrigger", EventType.Navigation)
                        .build();

                MParticle.getInstance().logEvent(event);
                
                MParticle.getInstance().Messaging().displayPushNotificationByDefault(true);

                MParticle.getInstance().logEvent(event);

            }
        });



        // Firebase push tokens cannot be obtained on the main thread.
        final Context applicationContext = this;
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    String token = FirebaseInstanceId.getInstance().getToken("709599249964", "FCM");
                    Appboy.getInstance(applicationContext).registerAppboyPushMessages(token);
                } catch (Exception e) {
                    Log.e(TAG, "Exception while registering Firebase token with Braze.", e);
                }
            }
        }).start();


        }

}
