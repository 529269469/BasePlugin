package com.example.baseplugin.mvi;

import static java.lang.annotation.ElementType.PARAMETER;
import static java.lang.annotation.RetentionPolicy.SOURCE;

import androidx.annotation.IntDef;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

/**
 * Description : 设置toast显示方向
 */
@IntDef({ShowOrientation.START, ShowOrientation.TOP, ShowOrientation.END, ShowOrientation.BOTTOM})
@Target(PARAMETER)
@Retention(SOURCE)
public @interface ShowOrientation {
    int START = 0;
    int TOP = 1;
    int END = 2;
    int BOTTOM = 3;
}
