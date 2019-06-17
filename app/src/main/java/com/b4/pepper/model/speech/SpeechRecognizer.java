package com.b4.pepper.model.speech;

import com.aldebaran.qi.sdk.QiContext;
import com.aldebaran.qi.sdk.builder.ListenBuilder;
import com.aldebaran.qi.sdk.object.conversation.BodyLanguageOption;
import com.aldebaran.qi.sdk.object.conversation.Listen;
import com.aldebaran.qi.sdk.object.conversation.PhraseSet;
import com.aldebaran.qi.sdk.object.locale.Language;
import com.aldebaran.qi.sdk.object.locale.Locale;
import com.aldebaran.qi.sdk.object.locale.Region;

public class SpeechRecognizer
{
    private static SpeechRecognizer instance;
    //Receiver for recognized speech
    private ISpeechToTextReceiver speechToTextReceiver;

    //Current context used
    private QiContext qiContext;

    //Sets the language that will be recognized
    private Locale locale;
    //Set of phrases that can be recognized
    private PhraseSet phraseSet;
    //Listens for speech
    private Listen listener;

    private SpeechRecognizer(QiContext qiContext)
    {
        this.qiContext = qiContext;
        this.locale = new Locale(Language.DUTCH, Region.NETHERLANDS);
        setListener();
    }

    public void listen()
    {
        if(this.speechToTextReceiver != null)
        {
//            Future<ListenResult> listenFuture = this.listener.async().run();
//            this.speechToTextReceiver.onSpeechRecognized(listenFuture.getValue());
            //this.speechToTextReceiver.onSpeechRecognized(this.listener.run());
        }
    }

    public static SpeechRecognizer getInstance(QiContext qiContext)
    {
        if(instance == null)
        {
            instance = new SpeechRecognizer(qiContext);
        }
        else
        {
            instance.setQiContext(qiContext);
        }
        return instance;
    }

    public void setSpeechToTextReceiver(ISpeechToTextReceiver speechToTextReceiver)
    {
        this.speechToTextReceiver = speechToTextReceiver;
    }

    public void setQiContext(QiContext qiContext)
    {
        if(qiContext != null)
        {
            this.qiContext = qiContext;
            setListener();
        }
    }

    public void setLocale(Locale locale)
    {
        if(locale != null)
        {
            this.locale = locale;
        }
    }

    public void setPhraseSet(PhraseSet phraseSet)
    {
        this.phraseSet = phraseSet;
    }

    private void setListener()
    {
        if(this.phraseSet != null)
        {
            this.listener = ListenBuilder.with(this.qiContext).withPhraseSet(this.phraseSet).withBodyLanguageOption(BodyLanguageOption.DISABLED).withLocale(this.locale).build();
        }
        else
        {
            this.listener = ListenBuilder.with(this.qiContext).withBodyLanguageOption(BodyLanguageOption.DISABLED).withLocale(this.locale).build();
        }
    }
}
