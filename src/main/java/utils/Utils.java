package utils;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Random;

public class Utils {

    private static final Random random = new Random();

    public static String formatDateDDMMYY(Date date){
        if(date == null){
            return "inconnu";
        }
        SimpleDateFormat formater = new SimpleDateFormat("dd/MM/yy HH:mm");
        return formater.format(date);
    }

    public static int getRandomNumber(int min, int max) {
        return (int) ((Math.random() * (max - min)) + min);
    }

    //en %
    public static boolean randomTest(int seuil) {
        if (seuil >= 100) {
            return true;
        }
        return random.nextInt(101) <= seuil;
    }

    public static boolean randomTest(double seuil) {
        if (seuil >= 100) {
            return true;
        }
        return (random.nextDouble() * 100) <= seuil;
    }

    public static Random getRandom() {
        return random;
    }
}
