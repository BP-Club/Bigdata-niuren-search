package com.lingtian.dsapi.controller;

import static java.lang.Math.pow;

/**
 * @author dailinwei
 * @version 1.0
 * @date 2020/5/27 6:32 下午
 */
public class sdasd {
    public static void main() {
        sdasd a = new sdasd();
        a.getNumber(2, 3);
    }

    public int repalce(int s) {
        if (s == 0)
            s = 1;
        else
            s = 0;
        return s;
    }


    public int getNumber(int num, double b) {
        if (num == 1 && b == 1)
            return 0;
        if (b > pow(2, num - 2))
            return repalce(getNumber(num - 1, b - pow(2, num - 2)));
        else
            return getNumber(num - 1, b);
    }
}
