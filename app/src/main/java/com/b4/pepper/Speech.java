package com.example.b4_pepper;

import com.aldebaran.qi.sdk.QiContext;
import com.aldebaran.qi.sdk.builder.SayBuilder;
import com.aldebaran.qi.sdk.object.conversation.Say;

public class Speech {

    public void englishGreet(QiContext qiContext){
        Say say = SayBuilder.with(qiContext)
                            .withText("Hello! Do you need a table?")
                            .build();
        say.run();
    }

    public void englishGroupSize(QiContext qiContext){
        Say say = SayBuilder.with(qiContext)
                            .withText("How many seats do you need?")
                            .build();
        say.run();
    }

    public void englishGuideTable(QiContext qiContext){
        Say say = SayBuilder.with(qiContext)
                            .withText("There is your table, have a nice day!")
                            .build();
        say.run();
    }

    public void dutchGreet(QiContext qiContext){
        Say say = SayBuilder.with(qiContext)
                            .withText("Hallo, wilt u een tafel?")
                            .build();
        say.run();
    }

    public void dutchGroupSize(QiContext qiContext){
        Say say = SayBuilder.with(qiContext)
                            .withText("Hoeveel stoelen heeft u nodig?")
                            .build();
        say.run();
    }

    public void dutchGuideTable(QiContext qiContext){
        Say say = SayBuilder.with(qiContext)
                            .withText("Daar is uw tafel, fijne dag verder")
                            .build();
        say.run();
    }
}
