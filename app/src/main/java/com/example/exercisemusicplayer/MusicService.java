package com.example.exercisemusicplayer;

import android.app.Service;
import android.content.ContentUris;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Binder;
import android.os.IBinder;
import android.os.PowerManager;
import android.util.Log;

import java.util.ArrayList;


public class MusicService extends Service implements
        MediaPlayer.OnPreparedListener,
        MediaPlayer.OnErrorListener,
        MediaPlayer.OnCompletionListener {

    @Override
    public IBinder onBind(Intent intent) {
        return musicBind;
    }

    @Override
    public void onCompletion(MediaPlayer mediaPlayer) {

    }

    @Override
    public boolean onError(MediaPlayer mediaPlayer, int i, int i1) {
        return false;
    }

    @Override
    public void onPrepared(MediaPlayer mediaPlayer) {
        //start playback
        mediaPlayer.start();
    }

    public void setSong(int songIndex){
        songPosn=songIndex;
    }

    private MediaPlayer player;
    private ArrayList<Song> songs;
    private int songPosn;

    public void onCreate(){
        super.onCreate();
        songPosn=0;
        player = new MediaPlayer();
        initMusicPlayer();
    }
    public void initMusicPlayer(){
        player.setWakeMode(getApplicationContext(), PowerManager.PARTIAL_WAKE_LOCK);
        player.setAudioStreamType(AudioManager.STREAM_MUSIC);
        player.setOnPreparedListener(this);
        player.setOnCompletionListener(this);
        player.setOnErrorListener(this);

    }
    public void setList(ArrayList<Song> theSongs){
        songs=theSongs;
    }
    public class MusicBinder extends Binder {
        MusicService getService() {
            return MusicService.this;
        }
    }
    private final IBinder musicBind = new MusicBinder();
    @Override
    public boolean onUnbind(Intent intent){
        player.stop();
        player.release();
        return false;
    }
    public void playSong(){
        player.reset();
        Song playSong = songs.get(songPosn);
        long currSong = playSong.getID();
        Uri trackUri = ContentUris.withAppendedId(
                android.provider.MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
                currSong);
        try{
            player.setDataSource(getApplicationContext(), trackUri);
        }
        catch(Exception e){
            Log.e("MUSIC SERVICE", "Error setting data source", e);
        }
        player.prepareAsync();
    }

}
