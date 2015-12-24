package technology.xor.notes.views;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.SystemClock;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.github.clans.fab.FloatingActionButton;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

import technology.xor.notes.database.CipherDatabaseHelper;
import technology.xor.notes.database.CipherRecording;
import technology.xor.notes.support.LocationProvider;
import technology.xor.photolibrary.R;

public class NewRecording extends Activity implements LocationProvider.LocationCallback{

    private static final String LOG_TAG = "AudioRecordTest";
    private static String mFileName = null;

    private MediaRecorder mRecorder = null;

    private PlayButton mPlayButton = null;
    private MediaPlayer mPlayer = null;

    private FloatingActionButton btnRecord;
    private TextView tvTimer;

    boolean mStartRecording;
    private long startTime = 0L;

    private Handler customHandler = new Handler();

    private long timeInMilliseconds = 0L;
    private long timeSwapBuff = 0L;
    private long updatedTime = 0L;

    private LocationProvider mLocationProvider;
    private double latitude;
    private double longitude;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recording);

        mLocationProvider = new LocationProvider(this, this);
        latitude = 0.0;
        longitude = 0.0;

        btnRecord = (FloatingActionButton) findViewById(R.id.fb_record);
        tvTimer = (TextView) findViewById(R.id.tv_timer);

        mStartRecording  = false;

        btnRecord.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (mStartRecording)
                    mStartRecording = false;
                else if (!mStartRecording)
                    mStartRecording = true;

                if (mStartRecording) {
                    tvTimer.setVisibility(View.VISIBLE);
                    btnRecord.setColorNormalResId(R.color.pressed_record);
                    //btnRecord.setColorNormal(R.color.primary_record);
                    startTime = SystemClock.uptimeMillis();
                    customHandler.postDelayed(updateTimerThread, 0);

                    onRecord(mStartRecording);
                } else {
                    tvTimer.setVisibility(View.GONE);

                    customHandler.removeCallbacks(updateTimerThread);

                    onRecord(mStartRecording);
                }
            }
        });
    }

    private void onRecord(boolean start) {
        if (start) {
            startRecording();
        } else {
            stopRecording();
        }
    }

    private void onPlay(boolean start) {
        if (start) {
            startPlaying();
        } else {
            stopPlaying();
        }
    }

    private void startPlaying() {
        mPlayer = new MediaPlayer();
        try {
            mPlayer.setDataSource(mFileName);
            mPlayer.prepare();
            mPlayer.start();
        } catch (IOException e) {
            Log.e(LOG_TAG, "prepare() failed");
        }
    }

    private void stopPlaying() {
        mPlayer.release();
        mPlayer = null;
    }

    private void startRecording() {
        mRecorder = new MediaRecorder();
        mRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        mRecorder.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4);
        mRecorder.setOutputFile(mFileName);
        mRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AAC);

        try {
            mRecorder.prepare();
        } catch (IOException e) {
            Log.e(LOG_TAG, "prepare() failed");
        }

        mRecorder.start();
    }

    private void stopRecording() {
        mRecorder.stop();
        mRecorder.release();
        mRecorder = null;

        byte[] soundBytes;

        try {
            InputStream inputStream = getContentResolver().openInputStream(Uri.fromFile(new File(mFileName)));
            soundBytes = new byte[inputStream.available()];
            soundBytes = toByteArray(inputStream);

            CipherDatabaseHelper dbHelper = new CipherDatabaseHelper(NewRecording.this);
            CipherRecording cRecording = new CipherRecording(GetDate(), GetTime(), String.valueOf(latitude) + "," +
                    String.valueOf(longitude), soundBytes, String.valueOf(timeInMilliseconds), "Test Recording");

            dbHelper.AddRecording(cRecording);
            dbHelper.close();

            Intent returnIntent = new Intent();
            setResult(RESULT_OK, returnIntent);
            finish();
        }catch(Exception e){
            e.printStackTrace();
        }
    }

    @Override
    public void handleNewLocation(Location location) {
        latitude = location.getLatitude();
        longitude = location.getLongitude();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent returnIntent = new Intent();
        setResult(RESULT_CANCELED, returnIntent);
        finish();
    }

    private String GetDate() {
        Calendar cal = Calendar.getInstance();
        //SimpleDateFormat sdf = new SimpleDateFormat("MMMM dd, yyyy HH:mm");
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd", Locale.US);
        return sdf.format(cal.getTime());
    }

    private String GetTime() {
        Calendar cal = Calendar.getInstance();
        //SimpleDateFormat sdf = new SimpleDateFormat("MMMM dd, yyyy HH:mm");
        SimpleDateFormat sdf = new SimpleDateFormat("k:m zzz", Locale.US);
        return sdf.format(cal.getTime());
    }

    public byte[] toByteArray(InputStream in) throws IOException {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        int read = 0;
        byte[] buffer = new byte[1024];
        while (read != -1) {
            read = in.read(buffer);
            if (read != -1)
                out.write(buffer,0,read);
        }
        out.close();
        return out.toByteArray();
    }

    class PlayButton extends Button {
        boolean mStartPlaying = true;

        OnClickListener clicker = new OnClickListener() {
            public void onClick(View v) {
                onPlay(mStartPlaying);
                if (mStartPlaying) {
                    setText("Stop playing");
                } else {
                    setText("Start playing");
                }
                mStartPlaying = !mStartPlaying;
            }
        };

        public PlayButton(Context ctx) {
            super(ctx);
            setText("Start playing");
            setOnClickListener(clicker);
        }
    }

    public NewRecording() {
        mFileName = Environment.getExternalStorageDirectory().getAbsolutePath();
        mFileName += "/audiorecording.3gp";
    }

    @Override
    public void onPause() {
        super.onPause();
        if (mRecorder != null) {
            mRecorder.release();
            mRecorder = null;
        }

        if (mPlayer != null) {
            mPlayer.release();
            mPlayer = null;
        }
    }

    private Runnable updateTimerThread = new Runnable() {

        public void run() {

            timeInMilliseconds = SystemClock.uptimeMillis() - startTime;

            updatedTime = timeSwapBuff + timeInMilliseconds;

            int secs = (int) (updatedTime / 1000);
            int mins = secs / 60;
            secs = secs % 60;
            int milliseconds = (int) (updatedTime % 1000);
            tvTimer.setText("" + mins + ":"
                    + String.format("%02d", secs) + ":"
                    + String.format("%03d", milliseconds));
            customHandler.postDelayed(this, 0);
        }

    };
}
