/*
 * Enable Viacam for Android, a camera based mouse emulator
 *
 * Copyright (C) 2015-17 Cesar Mauri Loba (CREA Software Systems)
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package com.crea_si.eviacam.common;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

/**
 * Manage uncaught exceptions
 *
 * TODO: disable/halt service after an error and stop a11y service for Android 7.0+
 */
class UncaughtExceptionHandler implements Thread.UncaughtExceptionHandler {

    private final Thread.UncaughtExceptionHandler mDefaultExceptionHandler;

    static public void init() {
        new UncaughtExceptionHandler();
    }

    /**
     * Private constructor
     *
     * Try to install a uncaught exception handler.
     */
    private UncaughtExceptionHandler() {
        Thread.UncaughtExceptionHandler defaultExceptionHandler=
                Thread.getDefaultUncaughtExceptionHandler();

        if (defaultExceptionHandler!= null &&
            defaultExceptionHandler.getClass().isAssignableFrom(UncaughtExceptionHandler.class)) {
            // Already installed
            EVIACAM.debug("UncaughtExceptionHandler already installed. Ignore request");
            mDefaultExceptionHandler= null;
        }
        else {
            mDefaultExceptionHandler = defaultExceptionHandler;
            Thread.setDefaultUncaughtExceptionHandler(this);
        }
    }


    @Override
    public void uncaughtException(@Nullable Thread thread, @NonNull Throwable ex) {
        Class<?> cls= ex.getClass();
        EVIACAM.debug("Uncaught exception:" + cls.getName());

        if (cls.isAssignableFrom(NullPointerException.class)) {
            EVIACAM.debug("Is a NullPointerException");
        }

        if (mDefaultExceptionHandler!= null) {
            mDefaultExceptionHandler.uncaughtException(thread, ex);
        }
    }
}