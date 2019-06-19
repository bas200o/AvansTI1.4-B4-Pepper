package com.b4.pepper;

import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

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
import com.b4.pepper.model.ThreadingHelper;
import com.b4.pepper.model.mqtt.TableManager;
import com.b4.pepper.model.speech.ConceptLibrary;
import com.b4.pepper.model.speech.ConversationState;
import com.b4.pepper.model.speech.ISpeechToTextReceiver;
import com.b4.pepper.model.speech.SpeechModel;
import com.b4.pepper.ui.NonSwipeableViewPager;
import com.b4.pepper.ui.main.SectionsPagerAdapter;

import java.util.Locale;

public class AppActivity extends RobotActivity implements RobotLifecycleCallbacks, ISpeechToTextReceiver {

    private NonSwipeableViewPager viewPager;
    private TabLayout tabLayout;
    private QiContext qiContext;
    private ConversationState conversationState;
    private static AppActivity context;

    public static AppActivity getContext(){
        return context;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        AppActivity.context = this;
        setContentView(R.layout.activity_app);
        SectionsPagerAdapter sectionsPagerAdapter = new SectionsPagerAdapter(this, getSupportFragmentManager());

        QiSDK.register(this,this);

        this.viewPager = findViewById(R.id.view_pager);
        this.viewPager.setAdapter(sectionsPagerAdapter);
        this.tabLayout = findViewById(R.id.tabs);
        this.tabLayout.setupWithViewPager(this.viewPager);

//        FloatingActionButton fab = findViewById(R.id.fab);
//
//        fab.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
//                        .setAction("Action", null).show();
//            }
//        });

        LinearLayout tabStrip = ((LinearLayout)tabLayout.getChildAt(0));

        for(int i = 0; i < tabStrip.getChildCount(); i++)
            tabStrip.getChildAt(i).setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    return true;
                }
            });
    }

    protected void onDestroy() {

        QiSDK.unregister(this, this);
        super.onDestroy();
    }

    private Chat getChatBot(final int chatResource){

        final Chat[] chat = new Chat[1];

        ThreadingHelper.runOffMainThreadSynchronous(new Runnable() {
            @Override
            public void run() {

                Topic topic = TopicBuilder.with(qiContext).withResource(chatResource).build();
                QiChatbot qiChatbot = QiChatbotBuilder.with(qiContext).withTopic(topic).build();
                chat[0] = ChatBuilder.with(qiContext).withChatbot(qiChatbot).build();
            }
        });

        return chat[0];
    }

    @Override
    public void onRobotFocusGained(QiContext qiContext) {

        Log.i("TEST", "focus gained");
        this.qiContext = qiContext;
        this.startNewConversation();
    }

    private void startNewConversation() {

        this.conversationState = ConversationState.Greeting;
        this.setNumberOfPeopleText(0);
        ThreadingHelper.setChat(getChatBot(R.raw.greetings));
        startChat(ThreadingHelper.getChat(), ConceptLibrary.greetings);
    }

    private void startNetConversationAsync(){

        this.setTab(0);
        new Thread(new Runnable() {
            @Override
            public void run() {
                AppActivity.this.startNewConversation();
            }
        }).start();
    }

    private void askNumberOfPeople(){

        this.setTab(1);
        new SpeechModel(qiContext).sayMessage("Met hoeveel mensen?");
        this.conversationState = ConversationState.AskingNumberOfPeople;
        ThreadingHelper.setChat(this.getChatBot(R.raw.met_hoeveel_mensen));
        startChat(ThreadingHelper.getChat(), ConceptLibrary.MetHoeveelMensen);
    }

    private void runLater(final int waitTime, final Runnable toRun) {

        new Thread(new Runnable() {
            @Override
            public void run() {

                try {

                    Thread.sleep(waitTime);
                } catch (InterruptedException e) {

                    e.printStackTrace();
                }

                toRun.run();
            }
        }).start();
    }

    @Override
    public void onSpeechRecognized(String phrase) {

        Log.d("Human input", phrase);
        switch (this.conversationState){

            case Greeting: {

                Log.d("Listening Greeting", phrase);

                if (phrase.matches(ConceptLibrary.greetingsPositive))
                    this.askNumberOfPeople();

                else {

                    this.setTab(0);
                    new SpeechModel(this.qiContext).sayMessage("Fijne dag nog");
                    this.startNetConversationAsync();
                }

                break;
            }
            case AskingNumberOfPeople: {

                Log.d("Listening AskingNumPeo", phrase);
                int numberOfPeople = Integer.parseInt(phrase);
                this.setNumberOfPeopleText(numberOfPeople);
                this.handleTableRequest(numberOfPeople);
                break;
            }
            case Finishing: {
                // Not used
                break;
            }
        }
    }

    private void startChat(final Chat chat, final String exitPhraseRegex){

        ThreadingHelper.setChatFuture(chat.async().run());
        ThreadingHelper.runOffMainThreadSynchronous(new Runnable() {
            @Override
            public void run() {
                chat.addOnHeardListener(new Chat.OnHeardListener() {
                    @Override
                    public void onHeard(Phrase heardPhrase) {

                        Log.d("chat onheard", heardPhrase.getText());

                        String phrase = heardPhrase.getText().toLowerCase();

                        if (phrase.matches(exitPhraseRegex)) {

                            Log.d("chat onheard", "input matches exit regex");
                            ThreadingHelper.stopChat();
                            AppActivity.this.onSpeechRecognized(phrase);
                        } else {

                            Log.d("chat onheard", "input does not match exit regex");
                        }
                    }
                });
            }
        });
    }

    @Override
    public void onRobotFocusLost() {

        this.qiContext = null;
    }

    @Override
    public void onRobotFocusRefused(String reason) {

    }

    @Override
    public void onAttachFragment(Fragment fragment) {

        super.onAttachFragment(fragment);
    }

    public void onWantsTableButton(View view) {

        onWantsTableButton();
    }

    public void onWantsTableButton() {

        try {
            ThreadingHelper.stopChat();
        } catch (Exception ignored) {}

        this.askNumberOfPeople();
    }

    private void setTab(final int index) {

        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                tabLayout.getTabAt(index).select();
            }
        });
    }

    private void setNumberOfPeopleText(final int number){

        runOnUiThread(new Runnable() {
            @Override
            public void run() {

                TextView personsCountText = AppActivity.this.findViewById(R.id.personsCount);
                personsCountText.setText(number + "");
            }
        });
    }

    public void onPersonsButtonClicked(View view) {

        TextView errorMessage = findViewById(R.id.errorMessage);
        String input = ((Button)view).getText().toString();
        EditText personCountText = findViewById(R.id.personsCount);
        String text = personCountText.getText().toString();

        try {

            String newText = text + input;
            int numberOfPersons = Integer.parseInt(newText);

            if(numberOfPersons != 0 || text.length() > 0)
                personCountText.setText(Integer.toString(numberOfPersons));

            checkPersonsAmount(numberOfPersons);
        } catch(Exception ignored) {}
    }

    private void handleTableRequest(final int numberOfPeople) {

        new Thread(new Runnable() {
            @Override
            public void run() {

                TableManager tableManager = new TableManager();
                tableManager.reserveTable(numberOfPeople);

                if (tableManager.getPickedTable().getId() != -1) {

                    AppActivity.this.setTab(2);
                    new SpeechModel(AppActivity.this.qiContext).sayMessage("U kunt gaan zitten een tafel waar een lamp brandt");

                    // TODO: kijk eens aan!
                    Log.i("TABLE_ID", "the tableID: " + tableManager.getPickedTable().getId());

                } else {

                    AppActivity.this.setTab(0);
                    new SpeechModel(AppActivity.this.qiContext).sayMessage("Sorry, er zijn geen tafels beschikbaar");
                }

                AppActivity.this.runLater(1000, new Runnable() {
                    @Override
                    public void run() {
                        AppActivity.this.startNetConversationAsync();
                    }
                });
            }
        }).start();
    }

    public void onTableReserve(View view) {

        try {

            ThreadingHelper.stopChat();
        } catch (NullPointerException ignored) {}

        EditText personCountText = findViewById(R.id.personsCount);
        String text = personCountText.getText().toString();
        int numberOfPersons = Integer.parseInt(text);
        this.handleTableRequest(numberOfPersons);
    }

    public void onPlusMinusClicked(View view) {

        EditText personCountText = findViewById(R.id.personsCount);
        String text = personCountText.getText().toString();

        String character = ((Button)view).getText().toString();

        try {

            int numberOfPersons = (text.isEmpty()) ? 0 : Integer.parseInt(text);

            if(character.equals("+"))
                numberOfPersons++;

            else if(character.equals("-"))
                if(numberOfPersons > 0)
                    numberOfPersons--;

            personCountText.setText(Integer.toString(numberOfPersons));
        } catch(Exception e) {

            e.printStackTrace();
        }
    }

    public void onBackspaceClicked(View view) {

        EditText personCountText = findViewById(R.id.personsCount);
        String text = personCountText.getText().toString();

        if(!text.isEmpty()) {

            String newText = text.substring(0, text.length() - 1);
            personCountText.setText(newText);

            try {

                checkPersonsAmount(Integer.parseInt(newText));
            } catch(Exception ignored) {}
        }
    }

    private void checkPersonsAmount(int numberOfPersons) {

        TextView errorMessage = findViewById(R.id.errorMessage);

        if(numberOfPersons > 40) {

            //errorMessage.setText("Te veel personen");
            errorMessage.setText(R.string.toManyPersonsMessageText);
            errorMessage.setVisibility(View.VISIBLE);
        } else {

            errorMessage.setText("");
            errorMessage.setVisibility(View.INVISIBLE);
        }
    }

    public void onChangeLanguageClicked(View view) {

        System.out.println("CHANGE LANGUAGE CLICKED!");

        if (Build.VERSION.SDK_INT >= 17)
            getResources().getConfiguration().setLocale(new Locale("nl"));

        else
            getResources().getConfiguration().locale = new Locale("nl");

        getResources().updateConfiguration(getResources().getConfiguration(), getResources().getDisplayMetrics());
    }
}