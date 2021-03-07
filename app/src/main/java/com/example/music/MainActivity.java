package com.example.music;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SeekBar;
import android.widget.TextView;

import java.io.IOException;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements View.OnClickListener, MediaPlayer.OnCompletionListener, SeekBar.OnSeekBarChangeListener{

    private Button btnPlay;
    private Button btnStop;
    private Button btnPaused;
    private SeekBar seekVolume;
    private SeekBar seekProgress;

    private MediaPlayer player;
    private MediaPlayer player1;
    private MediaPlayer player2;

    private AudioManager audioManager;

    private TextView txtProcess;
    private TextView txtFinish;

    private String timeFinish = null;
    private String timeProgress = null;


    private ListView song_list;
    //private ArrayList arrayList;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        getSongList();


        initViews();

        initVar();

        audioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);

        //Прчитаем какая макс громкость на устройстве разрешена для аудиопотока
        seekVolume.setMax(audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC));
        seekVolume.setProgress(audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC));


        seekVolume.setOnSeekBarChangeListener(this);



    }


    private void initVar() {


    }

    public void getSongList() {
        //retrieve song info
        //ArrayList<MediaPlayer> songList = new ArrayList<>();

      player = MediaPlayer.create(this,R.raw.song);
      //player = MediaPlayer.create(this,songList);
    }

    private void initViews() {


        btnPlay = findViewById(R.id.btnPlay);
        btnPaused = findViewById(R.id.btnPaused);
        btnStop = findViewById(R.id.btnStop);

        txtProcess = findViewById(R.id.txtProcess);
        txtFinish = findViewById(R.id.txtFinish);

        seekVolume = findViewById(R.id.seekVolume);
        seekProgress = findViewById(R.id.seekProgress);

        btnPlay.setOnClickListener(this);
        btnPaused.setOnClickListener(this);
        btnStop.setOnClickListener(this);

        player.setOnCompletionListener(this);

        song_list = findViewById(R.id.song_list);

    }

    @Override
    public void onClick(View v) {

        switch (v.getId()){

            case R.id.btnPlay: {
                play();
                break;
            }
            case R.id.btnPaused: {
                paused();
                break;
            }
            case R.id.btnStop: {
                stop();
                break;
            }
        }
    }


    private void play() {

        player.start();

        seekProgress.setMax(player.getDuration());


        new ProgressTracker().execute();

        seekProgress.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {

        @Override
        public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

            timeFinish = String.format("%.2f",((double) player.getDuration()/60000));

            txtProcess.setText(String.format("%.2f",(double) progress/60000));
       }

       @Override
       public void onStartTrackingTouch(SeekBar seekBar) {

       }

       @Override
       public void onStopTrackingTouch(SeekBar seekBar) {

       }
       });
        txtFinish.setText(timeFinish);

         }

    private void paused() {

        player.pause();

    }

    private void stop() {

        player.stop();


        try {
            player.prepare();

            //Перемотаем на начало песни после остановки
            player.seekTo(0);

        }
        catch (IOException e){
            e.printStackTrace();
        }
        }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        if (player.isPlaying()) stop();

    }


    @Override
    public void onCompletion(MediaPlayer mp) {

        stop();
    }



    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

        audioManager.setStreamVolume(AudioManager.STREAM_MUSIC,progress,0);

    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {


    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {

    }


    public class ProgressTracker extends AsyncTask<Void,Void,Void>{

        @Override
        protected void onProgressUpdate(Void... values) {

            super.onProgressUpdate(values);

            Log.d("myApp", String.valueOf(player.getCurrentPosition()));

           seekProgress.setProgress(player.getCurrentPosition());

        }

        @Override
        protected Void doInBackground(Void... voids) {


           while (player.isPlaying()){

               try {
                   //Проверяем прогресс с частотой 1 раз в секунду
                   Thread.sleep(1000);
               }catch (InterruptedException e){
                   e.printStackTrace();
               }

               onProgressUpdate();
           }
            return null;

        }
    }

}