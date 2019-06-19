package com.b4.pepper.model.speech;

import com.aldebaran.qi.sdk.object.conversation.ListenResult;
import com.aldebaran.qi.sdk.object.conversation.Phrase;

public interface ISpeechToTextReceiver
{
    void onSpeechRecognized(String phrase);
}
