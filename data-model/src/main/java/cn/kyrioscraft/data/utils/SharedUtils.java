package cn.kyrioscraft.data.utils;

import java.util.Random;

/**
 * Created by kyrioscraft on 8/1/16.
 */
public class SharedUtils {

    public static Long randomNegativeId() {
        Random rand = new Random();
        return -1 * ((long) rand.nextInt(1000));
    }

}
