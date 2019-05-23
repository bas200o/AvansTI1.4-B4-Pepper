package com.b4.pepper;

import android.os.Bundle;
import android.util.Log;

import com.aldebaran.qi.sdk.QiContext;
import com.aldebaran.qi.sdk.QiSDK;
import com.aldebaran.qi.sdk.RobotLifecycleCallbacks;
import com.aldebaran.qi.sdk.design.activity.RobotActivity;
import com.b4.pepper.model.SpeechList;
import com.b4.pepper.model.SpeechModel;

public class MainActivity extends RobotActivity implements RobotLifecycleCallbacks {

    private QiContext qiContext;

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

        Log.i("TEST", "focus gained");

        this.qiContext = qiContext;

        SpeechModel speechModel = new SpeechModel(this.qiContext);

        speechModel.sayMessage(speechModel.getRandomMessageFromList(SpeechList.HELLO));
    }

    @Override
    public void onRobotFocusLost() {

        Log.i("TEST", "focus lost");

        this.qiContext = null;
    }

    @Override
    public void onRobotFocusRefused(String reason) {
        System.out.println("refocus");
    }
}
