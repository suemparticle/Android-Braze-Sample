package com.sue.helloworld;

import androidx.appcompat.app.AppCompatActivity;
import android.widget.Button;
import android.content.Intent;
import android.view.View;
import android.view.View.OnClickListener;
import android.os.Bundle;
import android.util.Log;
import android.widget.EditText;

import com.mparticle.MParticle;
import com.mparticle.MParticleOptions;
import com.mparticle.MParticle.LogLevel;
import com.mparticle.identity.IdentityApi;
import com.mparticle.identity.IdentityApiRequest;
import com.mparticle.identity.IdentityApiResult;
import com.mparticle.identity.IdentityHttpResponse;
import com.mparticle.identity.IdentityStateListener;
import com.mparticle.identity.MParticleUser;
import com.mparticle.identity.TaskFailureListener;
import com.mparticle.identity.TaskSuccessListener;
import com.mparticle.identity.BaseIdentityTask;
import com.mparticle.AttributionListener;
import com.mparticle.AttributionResult;
import com.mparticle.kits.AppsFlyerKit;
import com.mparticle.AttributionError;
import com.appsflyer.AppsFlyerConversionListener;

import android.net.Uri;
import com.appboy.support.AppboyLogger;
import com.appsflyer.AppsFlyerLib;
import androidx.annotation.NonNull;
import org.json.JSONObject;
//import com.appboy.AppboyLifecycleCallbackListener;


public class MainActivity extends AppCompatActivity {

    //private AttributionListener myListener;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //registerActivityLifecycleCallbacks(new AppboyLifecycleCallbackListener(true, true));

        AppboyLogger.setLogLevel(Log.VERBOSE);
        //configureAppboyAtRuntime();
        //registerActivityLifecycleCallbacks(new AppboyLifecycleCallbackListener());

        final EditText et=(EditText)findViewById(R.id.email);
        final EditText tv=(EditText)findViewById(R.id.username);

        Button loginButton = findViewById(R.id.loginButton);
        loginButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                System.out.println("Button Clicked");

                Intent activity2Intent = new Intent(getApplicationContext(), HelloActivity.class);
                startActivity(activity2Intent);

                IdentityApiRequest identityRequest = IdentityApiRequest.withEmptyUser()
                        //the IdentityApiRequest provides several convenience methods for common identity types
                        .email(et.getText().toString())
                        .customerId(tv.getText().toString())
                        .build();
                MParticle.getInstance().Identity().login(identityRequest);

            }

        });

        BaseIdentityTask identifyTask = new BaseIdentityTask()
                .addFailureListener(new TaskFailureListener() {
                    @Override
                    public void onFailure(IdentityHttpResponse identityHttpResponse) {
                        //handle failure - see below
                    }
                }).addSuccessListener(new TaskSuccessListener() {
                    @Override
                    public void onSuccess(IdentityApiResult identityApiResult) {
                        MParticleUser user = identityApiResult.getUser();
                    }
                });


        // DEEPLINKING

        Intent intent = getIntent();
        String action = intent.getAction();
        Uri data = intent.getData();

        //AppsFlyerLib.getInstance().sendDeepLinkData(this);



        AttributionListener myListener = new AttributionListener() {
            @Override
            public void onResult(@NonNull AttributionResult attributionResult) {
                Log.d("blim-mparticle"," onResult = $attributionResult");
                if (attributionResult.getServiceProviderId() == MParticle.ServiceProviders.APPSFLYER) {
                    JSONObject attributionParams = attributionResult.getParameters();
                    if (attributionParams != null && attributionParams.has(AppsFlyerKit.INSTALL_CONVERSION_RESULT)) {
                        Log.d("Conversion result", attributionParams.toString());
                    } else if (attributionParams != null && attributionParams.has(AppsFlyerKit.APP_OPEN_ATTRIBUTION_RESULT)) {
                        Log.d("App open result", attributionParams.toString());
                    }
                }
            }

            @Override
            public void onError(@NonNull AttributionError attributionError) {
                Log.d("Attribution Data Error", attributionError.toString());
            }
        };


        MParticleOptions options = MParticleOptions.builder(this)
                .credentials("APPKEY", "APPSECRET")
                .environment(MParticle.Environment.Development)
                //.identify(identityRequest)
                .logLevel(MParticle.LogLevel.VERBOSE)
                .attributionListener(myListener)
                .build();
        MParticle.start(options);

    }

}