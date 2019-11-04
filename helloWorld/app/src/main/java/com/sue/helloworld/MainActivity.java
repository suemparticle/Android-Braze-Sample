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


public class MainActivity extends AppCompatActivity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //AppboyLogger.setLogLevel(Log.VERBOSE);
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

        MParticleOptions options = MParticleOptions.builder(this)
                .credentials("APPKEY", "SECRETKEY")
                .environment(MParticle.Environment.Development)
                //.identify(identityRequest)
                .logLevel(MParticle.LogLevel.VERBOSE)
                .build();
        MParticle.start(options);

    }

//    private void refreshFeed {
//        if (MParticle.getInstance().isKitActive(ServiceProviders.APPBOY)) {
//            Appboy.getInstance(this).requestFeedRefresh();
//        }
//    }
//

}

//public class ExampleReceiver extends BroadcastReceiver {
//    @Override
//    public void onReceive(Context context, Intent intent) {
//        //process the Intent/send to other receivers as desired, and
//        //send the Context and Intent into mParticle's BroadcastReceiver
//        new com.mparticle.ReferrerReceiver().onReceive(context, intent);
//    }
//}
