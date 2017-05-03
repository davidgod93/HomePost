/*
 * Copyright (c) 2015. El código pertenenciente a este programa es propiedad de David Zamora Villalobos bajo el seudónimo de davidgod93.
 * Con los derechos sobre el mismo reservados a dicha persona.
 */

package com.davidgod93.utils;

import android.util.Log;

/**
 * Creado por David a día 09/03/2015.
 */
public class Logger {
    private static final String LOG_TAG = "Logger-HomePost";

    public static void info(String msg) { Log.i(LOG_TAG, msg); }

    public static void warning(String msg) { Log.w(LOG_TAG, msg); }

    public static void debug(String msg) { Log.d(LOG_TAG, msg); }

    public static void error(String msg) { Log.e(LOG_TAG, msg); }

    public static void verbose(String msg) { Log.v(LOG_TAG, msg); }

    public static void maxPriority(String msg) { Log.println(Log.ASSERT, LOG_TAG, msg); }
}
