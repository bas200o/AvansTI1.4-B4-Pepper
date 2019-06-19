package com.b4.pepper.model;

import android.util.Log;

import com.aldebaran.qi.Future;
import com.aldebaran.qi.sdk.object.conversation.Chat;

public class ThreadingHelper {
    private static Future chatFuture;
    private static Chat chat;

    public static Future getChatFuture() {
        return chatFuture;
    }

    public static void setChatFuture(Future chatFuture) {
        ThreadingHelper.chatFuture = chatFuture;
    }

    public static Chat getChat() {
        return chat;
    }

    public static void setChat(Chat chat) {
        ThreadingHelper.chat = chat;
    }

    public static void runOffMainThreadSynchronous(Runnable toRun){
        Thread offMainThread = new Thread(toRun);
        offMainThread.start();
        try {
            offMainThread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public static void stopChat() throws NullPointerException {
        ThreadingHelper.runOffMainThreadSynchronous(
                new Runnable() {
                    @Override
                    public void run() {
                        if (chat == null) {
                            Log.d("Stopping chat",  "chat is null");
                        }
                        if (chat != null && chatFuture != null){
                            Log.d("Stopping chat",  "stopping...");
                            chatFuture.cancel(true);
                            chatFuture.requestCancellation();
                            while (!chat.getSaying().getText().equals("")) {
                                try {
                                    Thread.sleep(10);
                                } catch (InterruptedException e) {
                                    e.printStackTrace();
                                }
                            }
                        }
                    }
                }
        );
    }
}
