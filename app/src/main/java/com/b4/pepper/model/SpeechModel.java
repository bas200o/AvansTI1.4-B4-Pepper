package com.b4.pepper.model;

import com.aldebaran.qi.sdk.Qi;
import com.aldebaran.qi.sdk.QiContext;
import com.aldebaran.qi.sdk.builder.SayBuilder;
import com.aldebaran.qi.sdk.object.conversation.Say;

import java.util.ArrayList;

public class SpeechModel {

    private QiContext qiContext;

    public SpeechModel(QiContext qiContext) {

        this.qiContext = qiContext;
    }

    public String getRandomMessageFromList(ArrayList<String> list) {

        return list.get((int) (Math.random() * list.size()));
    }

    public void sayMessage(String message) {

        Say say = SayBuilder.with(this.qiContext).withText(message).build();
        say.run();
    }
}