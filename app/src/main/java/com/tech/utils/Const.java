package com.tech.utils;

import android.view.View;

public class Const {
    public static final int FULLSCREEN_SYS_UI =
            View.SYSTEM_UI_FLAG_HIDE_NAVIGATION |
                    View.SYSTEM_UI_FLAG_FULLSCREEN |
                    View.SYSTEM_UI_FLAG_LAYOUT_STABLE |
                    View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION |
                    View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN |
                    View.SYSTEM_UI_FLAG_IMMERSIVE;

    public static final String LOGIN_STATE = "login_state";
    public static final String PHONE_NUMBER = "phone_number";
    public static final String LOAD_BOOKMARK = "load_bookmark";
    public static final String LOAD_HISTORY = "load_history";
    public static final String INCOGNITO= "incognito";
    public static final String NIGHT_MODE = "night_mode";

}
