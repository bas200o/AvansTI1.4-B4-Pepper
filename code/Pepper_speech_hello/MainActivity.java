package com.b4.b4pepper;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import com.aldebaran.qi.sdk.QiContext;
import com.aldebaran.qi.sdk.QiSDK;
import com.aldebaran.qi.sdk.RobotLifecycleCallbacks;
import com.aldebaran.qi.sdk.builder.SayBuilder;

public class MainActivity extends AppCompatActivity implements RobotLifecycleCallbacks {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        QiSDK.register(this, this);
    }

    @Override
    protected void onDestroy() {
        // Unregister the RobotLifecycleCallbacks for this Activity.
        QiSDK.unregister(this, this);
        super.onDestroy();
    }

    @Override
    public void onRobotFocusGained(QiContext qiContext) {
        Log.d("onRobotFocusGained", "onRobotFocusGained");
        // Create a new say action.
        say("Hello Human!", qiContext);
    }

    @Override
    public void onRobotFocusLost() {
        Log.d("onRobotFocusLost", "onRobotFocusLost");
    }

    @Override
    public void onRobotFocusRefused(String reason) {
        Log.d("onRobotFocusRefused", "onRobotFocusRefused");
    }

    private void say(String text, QiContext qiContext){
        Log.d("RobotTalk", text);
        SayBuilder.with(qiContext).withText(text).build().run();
    }
}
