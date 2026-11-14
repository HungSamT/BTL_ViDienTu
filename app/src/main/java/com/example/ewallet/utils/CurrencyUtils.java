package com.example.ewallet.utils;

import com.example.ewallet.model.Transaction;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class CurrencyUtils {

    public static String formatVND(long amount) {
        DecimalFormatSymbols symbols = new DecimalFormatSymbols(Locale.getDefault());
        symbols.setGroupingSeparator('.');
        symbols.setDecimalSeparator(',');
        DecimalFormat formatter = new DecimalFormat("#,##0", symbols);
        return formatter.format(amount) + " đ";
    }

    public static String formatSignedVND(String type, long amount) {
        String formatted = formatVND(amount);
        if (Transaction.TYPE_TOP_UP.equals(type) || Transaction.TYPE_TRANSFER_IN.equals(type)) {
            return "+ " + formatted;
        } else {
            return "- " + formatted;
        }
    }

    public static String getCurrentDateTime() {
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss", Locale.getDefault());
        return sdf.format(new Date());
    }
}
