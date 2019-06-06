package com.b4.pepper.model.Speech;

import com.aldebaran.qi.sdk.object.conversation.ListenResult;

public interface ISpeechToTextReceiver
{
    void onSpeechRecognized(ListenResult listenResult);
}
