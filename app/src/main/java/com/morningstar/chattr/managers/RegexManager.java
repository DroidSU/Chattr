/*
 * Created by Sujoy Datta. Copyright (c) 2019. All rights reserved.
 *
 * To the person who is reading this..
 * When you finally understand how this works, please do explain it to me too at sujoydatta26@gmail.com
 * P.S.: In case you are planning to use this without mentioning me, you will be met with mean judgemental looks and sarcastic comments.
 */

package com.morningstar.chattr.managers;

public class RegexManager {

    public static String removeCountryCode(String phoneNumber) {
        if (phoneNumber.contains("+") && phoneNumber.length() > 11) {
            phoneNumber = phoneNumber.substring(3);
        } else {
            phoneNumber = phoneNumber.substring(1);
        }

        if (phoneNumber.length() > 10 && phoneNumber.contains(" ")) {
            return RegexManager.removeSpaceFromNumber(phoneNumber);
        }
        return phoneNumber;
    }

    private static String removeSpaceFromNumber(String phoneNumber) {
        while (phoneNumber.contains(" ")) {
            int index = phoneNumber.indexOf(" ");
            String spliString1 = phoneNumber.split(" ")[0];
            String spliString2 = phoneNumber.split(" ")[1];
            phoneNumber = spliString1 + spliString2;
        }
        return phoneNumber;
    }
}
