package com.rk.keyboardplayground;

import com.google.android.play.core.splitcompat.SplitCompatApplication;
import com.touchtalent.bobblekeyboard.dynamicsupport.BobbleImeSDK;

public class KeyboardPlayGroundApp extends SplitCompatApplication {

    @Override
    public void onCreate() {
        super.onCreate();
        BobbleImeSDK.initialise(this,"bobblekeyboard");
    }
}
