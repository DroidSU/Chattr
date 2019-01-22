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
        if (phoneNumber.length() > 10) {
            phoneNumber = RegexManager.removeCharacterFromNumber(phoneNumber);

            if (phoneNumber.length() == 12)
                phoneNumber = phoneNumber.substring(2);
            else if (phoneNumber.length() == 11)
                phoneNumber = phoneNumber.substring(1);
        }
        return phoneNumber;
    }

    private static String removeCharacterFromNumber(String number) {
        number = number.trim();
        StringBuilder stringBuilder = new StringBuilder();
        for (int i = 0; i < number.length(); i++) {
            if (Character.isDigit(number.charAt(i))) {
                stringBuilder.append(number.charAt(i));
            }
        }

        return stringBuilder.toString();
    }
}
