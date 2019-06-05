package com.b4.pepper;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import com.aldebaran.qi.sdk.QiContext;
import com.aldebaran.qi.sdk.RobotLifecycleCallbacks;
import com.aldebaran.qi.sdk.design.activity.RobotActivity;
import com.aldebaran.qi.sdk.object.conversation.ListenResult;
import com.b4.pepper.model.ISpeechToTextReceiver;
import com.b4.pepper.model.SpeechRecognizer;
import com.b4.pepper.ui.main.SectionsPagerAdapter;

public class AppActivity extends RobotActivity implements RobotLifecycleCallbacks, ISpeechToTextReceiver
{
    private ViewPager viewPager;
    private TabLayout tabs;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_app);
        SectionsPagerAdapter sectionsPagerAdapter = new SectionsPagerAdapter(this, getSupportFragmentManager());

        this.viewPager = findViewById(R.id.view_pager);
        this.viewPager.setAdapter(sectionsPagerAdapter);
        this.tabs = findViewById(R.id.tabs);
        this.tabs.setupWithViewPager(this.viewPager);

//        FloatingActionButton fab = findViewById(R.id.fab);
//
//        fab.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
//                        .setAction("Action", null).show();
//            }
//        });
    }

    @Override
    public void onSpeechRecognized(ListenResult listenResult)
    {
        String recognizedSpeech = listenResult.getHeardPhrase().getText();

        //TODO HANDLE TEXT FROM SPEECH RECOGNIZER
    }

    @Override
    public void onRobotFocusGained(QiContext qiContext)
    {
        SpeechRecognizer speechRecognizer = SpeechRecognizer.getInstance(qiContext);
        speechRecognizer.listen();
        System.out.println("LISTENING!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
    }

    @Override
    public void onRobotFocusLost()
    {

    }

    @Override
    public void onRobotFocusRefused(String reason)
    {

    }

    @Override
    public void onAttachFragment(Fragment fragment)
    {
        super.onAttachFragment(fragment);
    }
}