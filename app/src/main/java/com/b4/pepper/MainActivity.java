package com.example.b4_pepper;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import com.aldebaran.qi.sdk.QiContext;
import com.aldebaran.qi.sdk.QiSDK;
import com.aldebaran.qi.sdk.RobotLifecycleCallbacks;
import com.aldebaran.qi.sdk.builder.SayBuilder;
import com.aldebaran.qi.sdk.design.activity.RobotActivity;
import com.aldebaran.qi.sdk.object.conversation.Say;

public class MainActivity extends RobotActivity implements RobotLifecycleCallbacks {

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        QiSDK.register(this,this);

        setContentView(R.layout.activity_main);
    }

    @Override
    protected void onDestroy() {
        QiSDK.unregister(this, this);
        super.onDestroy();
    }

    @Override
    public void onRobotFocusGained(QiContext qiContext) {
        Speech speech = new Speech();

        speech.englishGreet(qiContext);
        speech.englishGroupSize(qiContext);
        speech.englishGuideTable(qiContext);
    }

    @Override
    public void onRobotFocusLost() {
        System.out.println("focus loss");
    }

    @Override
    public void onRobotFocusRefused(String reason) {
        System.out.println("refocus");
    }
}
