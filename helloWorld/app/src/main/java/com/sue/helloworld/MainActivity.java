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
import com.mparticle.identity.MParticleUser;
import com.mparticle.identity.TaskFailureListener;
import com.mparticle.identity.TaskSuccessListener;
import com.mparticle.identity.BaseIdentityTask;
import com.mparticle.AttributionListener;
import com.mparticle.AttributionResult;
import com.mparticle.kits.AppsFlyerKit;
import com.mparticle.AttributionError;

import android.net.Uri;
import com.appboy.support.AppboyLogger;
import com.appsflyer.AppsFlyerLib;
import androidx.annotation.NonNull;
import org.json.JSONObject;

public class MainActivity extends AppCompatActivity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        final EditText et=(EditText)findViewById(R.id.email);
        final EditText tv=(EditText)findViewById(R.id.username);

        Button loginButton = findViewById(R.id.loginButton);
        loginButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                System.out.println("Logged In");

                Intent activity2Intent = new Intent(getApplicationContext(), HelloActivity.class);
                startActivity(activity2Intent);

                if(et.getText().toString().matches("") || tv.getText().toString().matches("")){
                    IdentityApiRequest identityRequest = IdentityApiRequest.withEmptyUser()
                            //the IdentityApiRequest provides several convenience methods for common identity types
                            .email("example@example.com")
                            .customerId("example")
                            .build();
                    MParticle.getInstance().Identity().login(identityRequest);
                } else {
                    IdentityApiRequest identityRequest = IdentityApiRequest.withEmptyUser()
                            //the IdentityApiRequest provides several convenience methods for common identity types
                            .email(et.getText().toString())
                            .customerId(tv.getText().toString())
                            .build();
                    MParticle.getInstance().Identity().login(identityRequest);
                }
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

        //Appsflyer kit attribution listener
        AttributionListener myListener = new AttributionListener() {
            @Override
            public void onResult(@NonNull AttributionResult attributionResult) {
                Log.d("mparticle-attr"," onResult = $attributionResult");
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

        //Braze Verbose Logging
        //AppboyLogger.setLogLevel(Log.VERBOSE);

        MParticleOptions options = MParticleOptions.builder(this)
                .credentials("APPKEY", "APPSECRET")
                .environment(MParticle.Environment.Development)
                .logLevel(MParticle.LogLevel.VERBOSE)
                .attributionListener(myListener)
                .build();
        MParticle.start(options);

    }

}