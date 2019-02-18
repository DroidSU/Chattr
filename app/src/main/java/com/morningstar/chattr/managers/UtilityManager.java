/*
 * Created by Sujoy Datta. Copyright (c) 2019. All rights reserved.
 *
 * To the person who is reading this..
 * When you finally understand how this works, please do explain it to me too at sujoydatta26@gmail.com
 * P.S.: In case you are planning to use this without mentioning me, you will be met with mean judgemental looks and sarcastic comments.
 */

package com.morningstar.chattr.managers;

import android.util.Patterns;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import androidx.annotation.NonNull;

public class UtilityManager {
    private static boolean result;

    public static boolean isEmailVaild(CharSequence email) {
        return Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }

    public static boolean isUserNameTaken(String userName) {
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().child(ConstantManager.FIREBASE_USERS_TABLE)
                .child(userName);

        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                result = dataSnapshot.exists();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        return result;
    }

    public static boolean isMobileNumberAlreadyRegistered(String number) {
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().child(ConstantManager.FIREBASE_PHONE_NUMBERS_TABLE).child(number);

        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                result = dataSnapshot.exists();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        return result;
    }
}
