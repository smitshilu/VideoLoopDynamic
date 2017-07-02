package in.smitshilu.videoloop;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Environment;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.MediaController;
import android.widget.Toast;
import android.widget.VideoView;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.util.TimeZone;

/**
 * An example full-screen activity that shows and hides the system UI (i.e.
 * status bar and navigation/system bar) with user interaction.
 */
public class FullscreenActivity extends AppCompatActivity {

    private static String TAG = "FullscreenActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fullscreen);

        Intent intent = getIntent();

        // Declaring final as we will use it in OnCompleteListener()
        final VideoView videoView = (VideoView) findViewById(R.id.videoView);
        final String videoPath = intent.getStringExtra("videoPath");
        videoView.setVideoPath(videoPath);
        videoView.setMediaController(new MediaController(this));
        videoView.start();

        // Repeat video again after completion
        videoView.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                try {
                    Toast.makeText(getApplicationContext(), getTime(), Toast.LENGTH_LONG).show();
                    videoView.start();
                } catch (Exception e) {
                    Toast.makeText(getApplicationContext(), "Sorry couldn't play it again", Toast.LENGTH_LONG).show();
                    Log.e(TAG, "Error: " + e);
                }
            }
        });
    }

    // Get UTC time from API ("http://tycho.usno.navy.mil/timer.html")
    protected String getTime() {
        final StringBuilder time = new StringBuilder();
        Thread t = new Thread() {
            @Override
            public void run() {
                try {
                    URL url = new URL("http://tycho.usno.navy.mil/timer.html");
                    HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
                    if(httpURLConnection.getResponseCode() == 200) {
                        BufferedReader br = new BufferedReader(new InputStreamReader(httpURLConnection.getInputStream()));
                        String temp = "";
                        while ((temp = br.readLine()) != null) {
                            // Selecting UTC time only
                            if(temp.contains("UTC")) {
                                time.append(temp.substring(temp.indexOf(',')+2, temp.indexOf(',')+14));
                            }
                        }
                    }
                }
                catch (Exception e) {
                    Log.e(TAG, "Error: " + e);
                    Toast.makeText(getApplicationContext(), "Error fetching time", Toast.LENGTH_LONG).show();
                }
            }
        };
        t.start();
        try {
            t.join();
        } catch (Exception e) {
            Log.e(TAG, "Error: " + e);
            Toast.makeText(getApplicationContext(), "Error Joining Thread", Toast.LENGTH_LONG).show();
        }
        return time.toString().trim();
    }
}
