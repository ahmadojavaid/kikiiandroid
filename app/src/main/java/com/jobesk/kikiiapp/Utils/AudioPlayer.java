package com.jobesk.kikiiapp.Utils;

import android.app.Activity;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Handler;
import android.view.View;
import android.widget.ImageView;
import android.widget.SeekBar;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

public class AudioPlayer implements MediaPlayer.OnPreparedListener, MediaPlayer.OnErrorListener {
    private boolean isPLAYING = false;

    private MediaPlayer mp = null;
    private SeekBar seekbar = null;

    public AudioPlayer(boolean isPLAYING) {
        this.isPLAYING = isPLAYING;
    }

    public void startPlayer(Activity context, String path, SeekBar seek_audio, final ImageView img_play_audio, final ImageView img_pause_audio, Long recordingTime) {
        mp=new MediaPlayer();
        seekbar=seek_audio;
        mp.setAudioStreamType(AudioManager.STREAM_MUSIC);
        if (!isPLAYING) {
            isPLAYING = true;
            try {
                mp.setDataSource(context, Uri.parse(path));
            } catch (IOException e) {
                e.printStackTrace();
            }
            mp.setOnPreparedListener(this);
            mp.prepareAsync();
            mp.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer mp) {

                    seekbar.setProgress(0);
                    img_play_audio.setVisibility(View.VISIBLE);
                    img_pause_audio.setVisibility(View.GONE);
                    isPLAYING=false;
                    stopPlaying();
                }
            });
            /*MediaMetadataRetriever retriever = new MediaMetadataRetriever();
            if (Build.VERSION.SDK_INT >= 14)
                retriever.setDataSource(path, new HashMap<String, String>());
            else
                retriever.setDataSource(path);
            String time = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION);
            Long timeInmillisec = Long.parseLong(time);*/
            seekbar.setMax(recordingTime.intValue()/1000);
            /*img_play_audio.setVisibility(View.GONE);
            img_pause_audio.setVisibility(View.VISIBLE);
*/
            final Handler mHandler = new Handler();
            context.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if (mp != null) {
                        int mCurrentPosition = mp.getCurrentPosition() / 1000;
                        seekbar.setProgress(mCurrentPosition);
                    }
                    mHandler.postDelayed(this, 1000);
                }
            });
            seekbar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                @Override
                public void onStopTrackingTouch(SeekBar seekBar) {
                }

                @Override
                public void onStartTrackingTouch(SeekBar seekBar) {

                }

                @Override
                public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                    if (mp != null && fromUser) {
                        progress=progress*1000;
                        mp.seekTo(progress);
                    }
                }
            });
        }
    }
    public void stopPlayer(SeekBar seek_audio, ImageView img_play_audio, ImageView img_pause_audio) {
        if (isPLAYING) {
            seek_audio.setProgress(0);
//            img_play_audio.setVisibility(View.VISIBLE);
//            img_pause_audio.setVisibility(View.GONE);
            isPLAYING = false;
            stopPlaying();
        }
    }

    private void stopPlaying() {
        mp.reset();
        mp = null;
        seekbar=null;
    }

//    public String getAudioDuration(String path, Context context) {
    public String getAudioDuration(long timeInmillisec) {
       /* MediaMetadataRetriever retriever = new MediaMetadataRetriever();
        if (Build.VERSION.SDK_INT >= 14)
            retriever.setDataSource(path, new HashMap<String, String>());
        else
            retriever.setDataSource(path);
        String time = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION);
        long timeInmillisec = Long.parseLong(time);*/
        String time2 = String.format("%02d:%02d",
                TimeUnit.MILLISECONDS.toMinutes(timeInmillisec),
                TimeUnit.MILLISECONDS.toSeconds(timeInmillisec) -
                        TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(timeInmillisec))
        );
        return time2;
       /* long duration = timeInmillisec / 1000;
        long hours = duration / 3600;
        long minutes = (duration - hours * 3600) / 60;
        long seconds = duration - (hours * 3600 + minutes * 60);
        return minutes + ":" + seconds;*/
    }

    @Override
    public boolean onError(MediaPlayer mediaPlayer, int i, int i1) {
        return false;
    }

    @Override
    public void onPrepared(MediaPlayer mediaPlayer) {
        mediaPlayer.start();
    }
}
