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

    public static Random getRandom() {
        return random;
    }
}
