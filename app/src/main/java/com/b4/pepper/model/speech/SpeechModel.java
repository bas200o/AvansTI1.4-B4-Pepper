package com.b4.pepper.model.speech;

import android.os.NetworkOnMainThreadException;

import com.aldebaran.qi.sdk.QiContext;
import com.aldebaran.qi.sdk.builder.SayBuilder;
import com.aldebaran.qi.sdk.object.conversation.Say;
import com.b4.pepper.model.ThreadingHelper;

import java.util.ArrayList;

public class SpeechModel {

    private QiContext qiContext;

    public SpeechModel(QiContext qiContext) {

        this.qiContext = qiContext;
    }

    public String getRandomMessageFromList(ArrayList<String> list) {

        return list.get((int) (Math.random() * list.size()));
    }

    public void sayMessage(final String message) {
        ThreadingHelper.runOffMainThreadSynchronous(
                new Runnable() {
                    @Override
                    public void run() {
                        try {
                            ThreadingHelper.stopChat();
                            Say say = SayBuilder.with(qiContext).withText(message).build();
                            say.run();
                        } catch (Exception ex) {
                            ex.printStackTrace();
                        }
                    }
                }
        );
    }
}
