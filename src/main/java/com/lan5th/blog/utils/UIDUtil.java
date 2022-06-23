package com.lan5th.blog.utils;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * @author lan5th
 * @date 2022/6/23 21:40
 * 生成long型UID
 */
public class UIDUtil {
    private static long tmpID = 0L;

    public static long getNewId()
    {
        long newId = Long.valueOf(new SimpleDateFormat("yyMMddhhmmssSSS").format(new Date())) * 10000;
        synchronized (UIDUtil.class) {
            if (newId > tmpID)
                tmpID = newId;
            else {
                tmpID++;
                newId = tmpID;
            }
        }
        return newId;
    }
}
