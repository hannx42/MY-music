package example.demo.service;


import android.app.Service;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.IBinder;
import android.widget.Toast;

public class AudioService extends Service {
	private static final String TAG = null;
	MediaPlayer player;
	public IBinder onBind(Intent arg0) {

		return null;
	}
	@Override
	public void onCreate() {
		super.onCreate();
		player = MediaPlayer.create(this, R.raw.motherfucker);
		player.setLooping(true);
		player.setVolume(100,100);

	}
	public int onStartCommand(Intent intent, int flags, int startId) {
		player.start();
		Toast.makeText(this,intent.getAction(), Toast.LENGTH_LONG).show();
		return 10;
	}

	public IBinder onUnBind(Intent arg0) {
		// TO DO Auto-generated method
		return null;
	}

	public void onStop() {

	}
	public void onPause() {

	}
	@Override
	public void onDestroy() {
		player.stop();
		player.release();
	}

	@Override
	public void onLowMemory() {

	}
}
