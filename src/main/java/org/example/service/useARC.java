package org.example.service;

import java.util.List;

public class useARC {
    private static Integer arc_len = 0;

    public static void saveToARC(String newRec) {
        /*
         *  将newRec保存进ARC缓存里
         */
    }

    public static List<String> get_ARC_list() {
        /*
         *  返回当前保存在当前ARC缓存中的成员
         */
    }

    public static void clear() {
        /*
         *  将缓存的大小置0,缓存置为空
         */
    }

    public static void setArc_len(Integer len) {
        arc_len = len;
    }
}
