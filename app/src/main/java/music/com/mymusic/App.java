package music.com.mymusic;

import android.app.Activity;
import android.app.Application;
import android.content.Context;

/**
 * Created by Admin on 3/04/2017.
 */

public class App extends Application{
    private static Context context;
    @Override

    public void onCreate(){
        super.onCreate();
        context=this;
    }

    public static Context getContext() {
        return context;
    }
}
