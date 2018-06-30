package com.ank30.audiovisualiser;

import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.media.audiofx.Visualizer;
import android.os.Environment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import java.io.IOException;

public class MainActivity extends AppCompatActivity {
    private MediaRecorder audioRecorder;
    private boolean mIsRecording;
    private boolean isRecording;
    private boolean isPlaying;
    private MediaPlayer player;
    private Visualizer mVisualizer;
    private MediaPlayer.OnCompletionListener completeListener;
    private ViewPager visualizerViewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        final Button record = (Button) findViewById(R.id.btntoggelRec);

        final Button playBack = (Button) findViewById(R.id.btnPlayRec);
        visualizerViewPager = (ViewPager) findViewById(R.id.visualizerViewPager);

        FragmentPagerAdapter fragmentPagerAdapter = new FragmentPagerAdapter(getSupportFragmentManager()) {
            @Override
            public Fragment getItem(int position) {
                return MyVisualizer.instantiate(MainActivity.this, "MyVisualizer");
            }

            @Override
            public int getCount() {
                return 0;
            }
        };

        visualizerViewPager.setAdapter(fragmentPagerAdapter);

//	set recording boolean flag to false
        isRecording = false;

//	set playing flag to false

        isPlaying = false;

//get the output file where you want the recording to be stored

        final String outputFile = Environment.getExternalStorageDirectory().getAbsolutePath() + "/ recording.3gp";

// step 1 : setup MediaRecorder
        audioRecorder = new MediaRecorder();
        audioRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        audioRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP );
        audioRecorder.setAudioEncoder(MediaRecorder.OutputFormat.AMR_NB);
        audioRecorder.setOutputFile(outputFile);

// End step 1

//step 2 : setup recording start/stop
        record.setOnClickListener(new View.OnClickListener() {

            @Override

            public void onClick(View v) {
                if (isRecording) {
                    Toast.makeText(MainActivity.this, "Recording Stoped", Toast.LENGTH_SHORT).show();
                    audioRecorder.stop();
                    audioRecorder.release();
                    isRecording = false;
                    playBack.setEnabled(true);
                    ((Button) v).setText("Record");
                } else {
                    try {
                        Toast.makeText(MainActivity.this, "Recording Started", Toast.LENGTH_SHORT).show(); audioRecorder.prepare(); audioRecorder.start(); isRecording = true;
                        ((Button) v).setText("Stop");
                        playBack.setEnabled(false);
                    }
                    catch  (Exception ex) {
                        ex.printStackTrace();
                        Toast.makeText(MainActivity.this, "Recording Error", Toast.LENGTH_SHORT).show();
                    }

                }

            }

        });

//	end of step 2

//	step 3 : setup playback start/Stop

        playBack.setOnClickListener(new View.OnClickListener() {
            @Override

            public void onClick(View v) {
                if (isPlaying) {
                    player.stop();
                    player.release();
                    isPlaying = false;
                    record.setEnabled(true);
                    mVisualizer.setEnabled(false);
                    ((Button) v).setText("Play");

                } else {

                    try {
                        record.setEnabled(false);
                        player = new MediaPlayer();
                        player.setDataSource(outputFile);

                        player.setOnCompletionListener(completeListener);
                        player.prepare();
                        setupVisualizerFxAndUI();
                        player.start();
                        mVisualizer.setEnabled(true);
                        isPlaying = true;
                        ((Button) v).setText("Stop");
                        Toast.makeText(MainActivity.this, "PlayBack Started", Toast.LENGTH_SHORT).show();
                    } catch (IOException e) {
                        e.printStackTrace(); Toast.makeText(MainActivity.this, "No recording to play please record first", Toast.LENGTH_SHORT).show();
                    }

                }

            }

        });

    }
    private void setupVisualizerFxAndUI() {

// Create the Visualizer object and attach it to our mediaplayer.

        mVisualizer = new Visualizer(player.getAudioSessionId());
        mVisualizer.setCaptureSize(Visualizer.getCaptureSizeRange()[1]);
        mVisualizer.setDataCaptureListener(
                new Visualizer.OnDataCaptureListener() {
                    public void onWaveFormDataCapture(Visualizer visualizer, byte[] bytes, int samplingRate) {
                        MyVisualizer.updateVisualizer(bytes);
                    }
                    public void onFftDataCapture(Visualizer visualizer, byte[] bytes, int samplingRate) {

                    }

                },Visualizer.getMaxCaptureRate() / 2, true, false);

    }

}
