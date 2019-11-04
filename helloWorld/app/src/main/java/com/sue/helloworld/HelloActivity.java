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

import com.mparticle.MParticle;
import com.mparticle.MParticleOptions;
import com.mparticle.identity.IdentityApiRequest;
import com.mparticle.identity.MParticleUser;
import com.mparticle.internal.MPUtility;
import com.mparticle.MPEvent;
import com.mparticle.MParticle.EventType;
import com.mparticle.commerce.Cart;
import com.mparticle.commerce.CommerceApi;
import com.mparticle.commerce.CommerceEvent;
import com.mparticle.commerce.Product;

import com.mparticle.media.MPMediaAPI;
import com.mparticle.media.MediaCallbacks;
import com.mparticle.messaging.MPMessagingAPI;
import com.mparticle.messaging.ProviderCloudMessage;
import com.mparticle.segmentation.SegmentListener;

import android.content.Context;

public class HelloActivity extends AppCompatActivity {

    private Context mApplicationContext;
    private WebView myWebView;

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

        Button trackEvent = findViewById(R.id.trackEvent);
        trackEvent.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                System.out.println("bought iPhone");

                // Note: may return null if the SDK has yet to acquire a user via IDSync!
                MParticleUser currentUser = MParticle.getInstance().Identity().getCurrentUser();

                Map<String, String> customAttributes = new HashMap<String, String>();
                customAttributes.put("color", "rose gold");
                customAttributes.put("storage", "64GB");

                MPEvent event = new MPEvent.Builder("Purchased iPhone", EventType.Navigation)
                        .customAttributes(customAttributes)
                        .build();

                MParticle.getInstance().logEvent(event);

                TextView tv = (TextView)findViewById(R.id.status);
                tv.setText("Tracked Event");

            }
        });

        Button setAttribute = findViewById(R.id.setAttribute);
        setAttribute.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                System.out.println("set attribute");

                // Note: may return null if the SDK has yet to acquire a user via IDSync!
                MParticleUser currentUser = MParticle.getInstance().Identity().getCurrentUser();

                // Set user attributes associated with the user
                currentUser.setUserAttribute("$City","New York");

                TextView tv = (TextView)findViewById(R.id.status);
                tv.setText("Set User Attribute");
            }
        });


        Button sendPush = findViewById(R.id.sendPush);
        sendPush.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                System.out.println("sent push");

                // Note: may return null if the SDK has yet to acquire a user via IDSync!
                MParticleUser currentUser = MParticle.getInstance().Identity().getCurrentUser();

                TextView tv = (TextView)findViewById(R.id.status);
                tv.setText("Sent Push");

                final WebView myWebView = (WebView) findViewById(R.id.webview);
                myWebView.loadUrl("https://www.sueyoungchung.com");

                WebSettings webSettings = myWebView.getSettings();
                myWebView.setWebViewClient(new WebViewClient());
                webSettings.setJavaScriptEnabled(true);

                myWebView.setWebViewClient(new WebViewClient(){
                    public void onPageFinished(WebView view, String weburl){
                        myWebView.loadUrl("javascript:testconsole()");
                    }
                });


            }
        });




        }

}
