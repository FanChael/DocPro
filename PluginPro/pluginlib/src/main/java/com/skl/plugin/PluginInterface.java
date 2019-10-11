package com.skl.plugin;

import android.app.Activity;
import android.os.Bundle;

public interface PluginInterface {
    void onCreate(Bundle saveInstance);

    void attachContext(Activity context);

    void onStart();

    void onResume();

    void onPause();

    void onStop();

    void onRestart();

    void onDestroy();
}
