package com.like.blebluetoothdemoapplication;

import java.util.Arrays;

/**
 * 作者: Li_ke
 * 日期: 2018/12/17 0017 15:30
 * 作用:
 */
public class JavaDo {
    public void start() {

        int b = Integer.valueOf("9", 16);
        int s = Integer.valueOf("FB", 16);
        int g = Integer.valueOf("F1", 16);
        System.out.println(b + "," + s + "," + g);

        byte[] bytes = stepParseIntToBLE(123456);
        System.out.println(Arrays.toString(bytes));
    }

    private byte[] stepParseIntToBLE(int step) {//123456

        String stepStr = Integer.toHexString(step);//1E240
        int len = (int) Math.ceil(((float) stepStr.length()) / 2);//长度 万 = 5,向上取整 len=3

        byte[] stepByte = new byte[len];//[3]
        int iStep = step;
        for (int i = len - 1; i >= 0; i--) { //1->40; 0->E2
            stepByte[i] = (byte) (iStep & 0xFF);
            iStep >>= 8;
            System.out.println(Byte.toUnsignedInt(stepByte[i]));
        }

        byte[] result = new byte[len + 1];//[1,E2,40]
        System.arraycopy(stepByte, 0, result, 1, stepByte.length);
        result[0] = 0x01;
        return result;
    }

    private int stepParseBLEToInt(byte[] data) {
        //data -> step
        byte[] step = new byte[data.length - 1];
        System.arraycopy(data, 1, step, 0, data.length - 1);

        //step -> int
        int result = 0;
        for (int i = 0; i < step.length; i++) {//3
            int number = Byte.toUnsignedInt(step[i]);
            for (int i1 = 0; i1 < (step.length - i - 1); i1++) {//0->2; 1->1; 2->0;
                number <<= 8;
            }
            result += number;
        }
        return result;
    }
}
