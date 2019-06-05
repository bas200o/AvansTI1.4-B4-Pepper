package com.b4.pepper;

import android.os.Bundle;
import android.util.Log;

import com.aldebaran.qi.Consumer;
import com.aldebaran.qi.Future;
import com.aldebaran.qi.sdk.QiContext;
import com.aldebaran.qi.sdk.QiSDK;
import com.aldebaran.qi.sdk.RobotLifecycleCallbacks;
import com.aldebaran.qi.sdk.builder.ChatBuilder;
import com.aldebaran.qi.sdk.builder.QiChatbotBuilder;
import com.aldebaran.qi.sdk.builder.TopicBuilder;
import com.aldebaran.qi.sdk.design.activity.RobotActivity;
import com.aldebaran.qi.sdk.object.conversation.Chat;
import com.aldebaran.qi.sdk.object.conversation.Phrase;
import com.aldebaran.qi.sdk.object.conversation.QiChatbot;
import com.aldebaran.qi.sdk.object.conversation.Topic;
import com.b4.pepper.model.SpeechList;
import com.b4.pepper.model.SpeechModel;

public class MainActivity extends RobotActivity implements RobotLifecycleCallbacks {

    private QiContext qiContext;
    private Chat chat;


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

        Topic topic = TopicBuilder.with(qiContext).withResource(R.raw.greetings).build();
        QiChatbot qiChatbot = QiChatbotBuilder.with(qiContext).withTopic(topic).build();
        this.chat = ChatBuilder.with(qiContext).withChatbot(qiChatbot).build();

        this.chat.addOnStartedListener(new Chat.OnStartedListener() {
            @Override
            public void onStarted() {
                Log.i("Speech", "Discussion started.");
            }
        });

        this.chat.addOnHeardListener(new Chat.OnHeardListener() {
            @Override
            public void onHeard(Phrase heardPhrase) {
                heardPhrase.getText();
            }
        });

        if (this.chat != null)
        {
            this.chat.removeAllOnStartedListeners();
        }

        Future<Void> chatFuture = chat.async().run();

        chatFuture.thenConsume(new Consumer<Future<Void>>() {
            @Override
            public void consume(Future<Void> voidFuture) throws Throwable {
                if (voidFuture.hasError()) {
                    Log.e("Speech", "Discussion finished with error.", voidFuture.getError());
                }
            }
        });
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
