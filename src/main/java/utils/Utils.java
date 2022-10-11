package utils;

import java.text.SimpleDateFormat;
import java.util.Date;

public class Utils {
    public static String formatDateDDMMYY(Date date){
        if(date == null){
            return "inconnu";
        }
        SimpleDateFormat formater = new SimpleDateFormat("dd/MM/yy HH:mm");
        return formater.format(date);
    }
}
