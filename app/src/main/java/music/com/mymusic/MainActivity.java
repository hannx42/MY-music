package music.com.mymusic;

import android.media.MediaMetadataRetriever;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SeekBar;
import android.widget.TextView;

import java.io.File;
import java.util.ArrayList;

import static android.R.attr.path;
import static music.com.mymusic.MusicPlay.PLAYER_IDLE;
import static music.com.mymusic.MusicPlay.PLAYER_PLAY;

public class MainActivity extends AppCompatActivity implements AdapterView.OnItemClickListener, View.OnClickListener, SeekBar.OnSeekBarChangeListener, MusicPlay.OnCompletionListener {
    private ListView lvPlayList;
    private TextView tvTitle;
    private TextView tvArtist;
    private TextView tvTimeProcess;
    private SeekBar sbProcess;
    private TextView tvTimeTotal;
    private ImageView ivShuffle;
    private ImageView ivPrevious;
    private ImageView ivPlay;
    private ImageView ivNext;
    private ImageView ivRepeat;
    private ArrayList<String> paths; //luu tat ca duong dan cua bai hat
    private int timeProcess;
    private int timeTotal;
    private PlayListADapter adapter;
    private MusicPlay musicPlayer;
    private boolean isRunning;
    private int UPDATE_TIME = 1;
    private int timeCurrent;
    private int position;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //anh xa
        initViews();
        //xet su kien cho nut click
        initListeners();
        //them noi dung
        initComponents();
    }

    private void initComponents() {
        initList();
        adapter = new PlayListADapter(App.getContext(),paths);
        lvPlayList.setAdapter(adapter);
        musicPlayer = new MusicPlay();
        musicPlayer.setOnCompletionListener(this);
    }

    private void initList() {
        paths = new ArrayList<>();
        String path= Environment.getExternalStorageDirectory().getAbsolutePath() + "/Download";
        File file = new File(path);
        File[] files = file.listFiles(); //lay tat ca cac file trong thu muc Download
        for(int i=0; i < files.length; i++) {
            //doc tat ca cac file co trong download
            String s = files[i].getName();
            if(s.endsWith(".mp3")){ //kiem tra co phai duoi mp3
                paths.add(files[i].getAbsolutePath());
            }
        }
    }

    private void initListeners() {
        lvPlayList.setOnItemClickListener(this);
        ivShuffle.setOnClickListener(this);
        ivPrevious.setOnClickListener(this);
        ivPlay.setOnClickListener(this);
        ivNext.setOnClickListener(this);
        ivRepeat.setOnClickListener(this);
        sbProcess.setOnSeekBarChangeListener(this);
    }

    private void initViews() {
        lvPlayList = (ListView)findViewById( R.id.lv_play_list );
        tvTitle = (TextView)findViewById( R.id.tv_title );
        tvArtist = (TextView)findViewById( R.id.tv_artist );
        tvTimeProcess = (TextView)findViewById( R.id.tv_time_process );
        sbProcess = (SeekBar)findViewById( R.id.sb_process );
        tvTimeTotal = (TextView)findViewById( R.id.tv_time_total );
        ivShuffle = (ImageView)findViewById( R.id.iv_shuffle );
        ivPrevious = (ImageView)findViewById( R.id.iv_previous );
        ivPlay = (ImageView)findViewById( R.id.iv_play );
        ivNext = (ImageView)findViewById( R.id.iv_next );
        ivRepeat = (ImageView)findViewById( R.id.iv_repeat );
    }

    private Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if(msg.what == UPDATE_TIME){
                timeCurrent=musicPlayer.getTimeCurrent();
                tvTimeProcess.setText(getTimeFormat(timeCurrent));
                sbProcess.setProgress(timeCurrent);
            }
        }
    };

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        this.position = position;
        String path = paths.get(position);
        playMusic(path);
    }

    private void playMusic(String path) {
        if(musicPlayer.getState() == + PLAYER_PLAY){
            musicPlayer.stop();
        }
        musicPlayer.setup(path);
        musicPlayer.play();
        ivPlay.setImageResource(R.drawable.ic_pause_black_24dp);


        //ten bai hat + ca si
        MediaMetadataRetriever mmr = new MediaMetadataRetriever();
        mmr.setDataSource(paths.get(position));
        String artist = mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_ARTIST);
        String title = mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_TITLE);
        tvArtist.setText(artist);
        tvTitle.setText(title);
        isRunning = true;

        //time
        //total
        tvTimeTotal.setText(getTimeFormat(musicPlayer.getTimeTotal()));
        //process //seekbar
        sbProcess.setMax(musicPlayer.getTimeTotal());
        new Thread(new Runnable() {
            @Override
            public void run() {
                while (isRunning){
                    Message message = new Message();
                    message.what = UPDATE_TIME;
                    handler.sendMessage(message);
                    try {
                        Thread.sleep(100);
                    } catch (InterruptedException e) {
                    }
                }
            }
        }).start();
    }

    private String getTimeFormat (long time){
        String tm = "";
        int s;
        int m;
        int h;
        //giay
        s = (int) (time % 60);
        m = (int) ((time - s) / 60);
        if (m >= 60){
            h = m/60;
            m = m%60;
            if (h>0){
                if(h<10)
                    tm += "0" + h + ":";
                else
                    tm += h + ":";
            }
        }
        if (m<10)
            tm += "0" + m + ":";
        else
            tm += m + ":";
        if (s<10)
            tm += "0" + s;
        else
            tm += s + "";
        return tm;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.iv_next:
                nextMusic();
                break;

            case R.id.iv_play:
                if(musicPlayer.getState() == PLAYER_PLAY){//setState ở đâu ấy nhỉ
                    ivPlay.setImageResource(R.drawable.ic_play_arrow_black_24dp);
                    musicPlayer.pause();
                }else {
                    ivPlay.setImageResource(R.drawable.ic_pause_black_24dp);
                    musicPlayer.play();
                }
                break;

            case R.id.iv_previous:
                previousMusic();
                break;


            default:
                break;
        }
    }

    private void RepeatMusic() {

    }

    private void previousMusic() {
        position--;
        if(position <0) {
            position = paths.size()-1;
        }
        String path = paths.get(position);
        playMusic(path);
    }


    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
        if (timeCurrent != progress && timeCurrent != 0)
            musicPlayer.seek(sbProcess.getProgress() * 1000);
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {

    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {

    }

    @Override
    public void OnEndMusic() {
        //ket thuc bai hat
        nextMusic();
        //ket thuc bai hat se phat tiep
    }

    private void nextMusic() {
        position++;
        if(position >= paths.size()) {
            position = 0;
        }
        String path = paths.get(position);
        playMusic(path);
    }
}
