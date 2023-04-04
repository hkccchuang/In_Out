package com.example.groupproject;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.AudioManager;
import android.media.SoundPool;
import android.os.Bundle;
import android.os.Looper;
import android.text.InputFilter;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Timer;
import java.util.TimerTask;

public class UserRegister extends AppCompatActivity implements View.OnClickListener,Animation.AnimationListener{

    Intent intent;
    ImageView image;
    Button cancel,cancel2,confirm;
    EditText editUserName;
    Timer timer=new Timer();//Timer for show animation
    public   SoundPool sp;//sound effect
    int soundEffect;

    String username;

    private static long lastClickTime = 0;

    Animation toLeft,toRight,fadeOut;

    Boolean isNext = false;

    SharedPreferences userInfo;//user information storage
    SharedPreferences.Editor editor;

    boolean isDelete=false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_register);
        getSupportActionBar().hide();
        loadUI();
        loadSound();
        loadAnimation();
        isDelete=false;
    }


    private void loadUI(){
        cancel=findViewById(R.id.btnCancel);
        cancel2=findViewById(R.id.btnCancel2);
        confirm=findViewById(R.id.btnConfirm);
        editUserName=findViewById(R.id.editUserName);
        image=findViewById(R.id.imageRegister);


        cancel.setOnClickListener(this);
        cancel2.setOnClickListener(this);
        confirm.setOnClickListener(this);

        userInfo=getSharedPreferences("userName",MODE_PRIVATE);
        editor=userInfo.edit();


    }

    private void loadSound(){
        sp= new SoundPool(10, AudioManager.STREAM_SYSTEM, 5);
        soundEffect = sp.load(this, R.raw.confirm, 1);//confirm.mp3
    }

    private void loadAnimation(){
        toRight= AnimationUtils.loadAnimation(this,R.anim.left_right);
        toRight.setAnimationListener(this);
        toLeft= AnimationUtils.loadAnimation(this,R.anim.right_left);
        toLeft.setAnimationListener(this);
        fadeOut=AnimationUtils.loadAnimation(this,R.anim.fade_out);
        fadeOut.setAnimationListener(this);
    }

    @Override
    public void onClick(View view) {

        if(!isFastDoubleClick()){

        sp.play(soundEffect, 0.3f, 0.3f, 0, 0, 1);//confirm.mp3

        if (view.getId() == R.id.btnCancel||view.getId()==R.id.btnCancel2)
        {image.startAnimation(toRight);editUserName.startAnimation(toRight);}
        if(view.getId()==R.id.btnConfirm&&
                editUserName.getText().toString().isEmpty()!=true)
        {image.startAnimation(toLeft);editUserName.startAnimation(toLeft);}//animation

        TimerTask task = new TimerTask() {
            public void run() {

                if(view.getId()==R.id.btnCancel||view.getId()==R.id.btnCancel2){

                    finish();//Go back to main menu

                }
                else if(view.getId()==R.id.btnConfirm&&editUserName.getText().toString().isEmpty()!=true
                        &&userInfo.getString("name","0")!="0"&&!isDelete){
                     //clean the before user data
                    editor.clear();
                    editUserName.setFilters(new InputFilter[]{new InputFilter.LengthFilter(20)});//Change the max for edittext
                    username=editUserName.getText().toString();//store the name for temporary
                    editUserName.setText("Delete Data?"); //case already have a user
                    isDelete=true;//if already have user,clean all the data
                    editUserName.setEnabled(false);
                }
                else if(editUserName.getText().toString().isEmpty()){
                    editUserName.setHint("Invalid");// Fail case. Hint for invalid name
                }
                else if(view.getId()==R.id.btnConfirm&&userInfo.getString("name","0")=="0"){
                    intent =new Intent(UserRegister.this,StageSelection.class);

                    username=editUserName.getText().toString();           //store the new username

                    editor.putString("name",username);

                    editor.commit();
                    finish();
                    startActivity(intent);// Successful case.User name not null but this is the first user then go to stage selection
                }
                else if(view.getId()==R.id.btnConfirm&&userInfo.getString("name","0")!="0"&&isDelete){

                    intent =new Intent(UserRegister.this,StageSelection.class);

                    editor.putString("name",username);

                    editor.commit();

                    finish();
                    startActivity(intent);
                    // Second Successful case.User name not null and already have a user then go to stage selection


                }

            }
        };
        timer.schedule(task, 200);}
    }

    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        if (event.getKeyCode() == KeyEvent.KEYCODE_BACK ) {
            //do something.
            return true;
        } else {
            return super.dispatchKeyEvent(event);
        }
    }//Ban back

    @Override
    public void onAnimationStart(Animation animation) {

    }

    @Override
    public void onAnimationEnd(Animation animation) {

    }

    @Override
    public void onAnimationRepeat(Animation animation) {

    }
    public boolean isFastDoubleClick() {
        long time = System.currentTimeMillis();
        long timeD = time - lastClickTime;
        if (0 < timeD && timeD < 800) {
            return true;
        }
        lastClickTime = time;
        return false;
    }// A function detected fast clicking,

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        InputMethodManager im = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
        im.hideSoftInputFromWindow(getCurrentFocus().getApplicationWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
        return super.onTouchEvent(event);
    }//hide the keyboard when touch the screen

    protected void onPause(){
        super.onPause();
        if(!isNext){
            MainActivity.mMediaPlayer.pause();
            //If user go to other intent,music will not be pause
        }
    }

    protected void onResume(){
        super.onResume();
        MainActivity.mMediaPlayer.start();
        isNext=false;

    }

    protected void onDestroy(){
        super.onDestroy();
        sp.release();//release sound pool
    }

}