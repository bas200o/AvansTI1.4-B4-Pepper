package com.b4.pepper;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.DragEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.aldebaran.qi.sdk.QiContext;
import com.aldebaran.qi.sdk.RobotLifecycleCallbacks;
import com.aldebaran.qi.sdk.design.activity.RobotActivity;
import com.aldebaran.qi.sdk.object.conversation.ListenResult;
import com.b4.pepper.model.ISpeechToTextReceiver;
import com.b4.pepper.model.SpeechRecognizer;
import com.b4.pepper.ui.main.SectionsPagerAdapter;

public class AppActivity extends RobotActivity implements RobotLifecycleCallbacks, ISpeechToTextReceiver
{
    private NonSwipeableViewPager viewPager;
    private TabLayout tabLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_app);
        SectionsPagerAdapter sectionsPagerAdapter = new SectionsPagerAdapter(this, getSupportFragmentManager());

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
        for(int i = 0; i < tabStrip.getChildCount(); i++) {
            tabStrip.getChildAt(i).setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    return true;
                }
            });
        }
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

    public void onWantsTableButton(View view)
    {
        this.tabLayout.getTabAt(1).select();
    }

    public void onPersonsButtonClicked(View view)
    {
        TextView errorMessage = findViewById(R.id.errorMessage);
        String input = ((Button)view).getText().toString();
        EditText personCountText = findViewById(R.id.personsCount);
        String text = personCountText.getText().toString();

        try
        {
            String newText = text + input;
            int numberOfPersons = Integer.parseInt(newText);

            if(numberOfPersons != 0 || text.length() > 0)
            {
                personCountText.setText(Integer.toString(numberOfPersons));
            }

            checkPersonsAmount(numberOfPersons);
        } catch(Exception e) { }
    }

    public void onTableReserve(View view)
    {
        TextView errorMessage = findViewById(R.id.errorMessage);
        EditText personCountText = findViewById(R.id.personsCount);
        String text = personCountText.getText().toString();

        try
        {
            int numberOfPersons = Integer.parseInt(text);

            if(numberOfPersons > 0 && numberOfPersons <= 40)
            {
                this.tabLayout.getTabAt(2).select();
            }
            else if(numberOfPersons == 0)
            {
                errorMessage.setText("Incorrecte invoer");
                errorMessage.setVisibility(View.VISIBLE);
            }
        }
        catch(Exception e)
        {
            errorMessage.setText("Incorrecte invoer");
            errorMessage.setVisibility(View.VISIBLE);
        }
    }

    public void onPlusMinusClicked(View view)
    {
        EditText personCountText = findViewById(R.id.personsCount);
        String text = personCountText.getText().toString();

        String character = ((Button)view).getText().toString();

        try
        {
            int numberOfPersons = (text.isEmpty()) ? 0 : Integer.parseInt(text);

            if(character.equals("+"))
            {
                numberOfPersons++;
            }
            else if(character.equals("-"))
            {
                if(numberOfPersons > 0)
                {
                    numberOfPersons--;
                }
            }
            personCountText.setText(Integer.toString(numberOfPersons));
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
    }

    public void onBackspaceClicked(View view)
    {
        EditText personCountText = findViewById(R.id.personsCount);
        String text = personCountText.getText().toString();

        if(!text.isEmpty())
        {
            String newText = text.substring(0, text.length() - 1);
            personCountText.setText(newText);
            try
            {
                checkPersonsAmount(Integer.parseInt(newText));
            }
            catch(Exception e){};
        }
    }

    private void checkPersonsAmount(int numberOfPersons)
    {
        TextView errorMessage = findViewById(R.id.errorMessage);

        if(numberOfPersons > 40)
        {
            errorMessage.setText("Te veel personen");
            errorMessage.setVisibility(View.VISIBLE);
        }
        else
        {
            errorMessage.setText("");
            errorMessage.setVisibility(View.INVISIBLE);
        }
    }
}