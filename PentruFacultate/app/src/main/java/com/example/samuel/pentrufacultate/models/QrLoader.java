package com.example.samuel.pentrufacultate.models;

import android.content.Intent;
import android.text.TextUtils;

import com.google.firebase.database.DatabaseReference;

public class QrLoader {
    public static boolean isValidQrData(Intent data) {
        return data != null && !TextUtils.isEmpty(data.getStringExtra(StringHelper.RESULT_QR_READER));
    }

    public static boolean isValidRecipe(RecipeModel recipe) {
        return false;
    }

    public static void loadRecipe(RecipeModel recipe, DatabaseReference mDataBase, String uidCurrentUser) {
        DatabaseReference mCurrentUserDatabaseProcedures = mDataBase.child("users_data").child("recipes").child(uidCurrentUser);
        mCurrentUserDatabaseProcedures.child(recipe.getTitle()).setValue(recipe.toJson());

    }

    public static String getRecipeUri(Intent data) {
        return data.getStringExtra(StringHelper.RESULT_QR_READER);
    }

//    public static DatabaseReference getReferenceForTheRecipe(DatabaseReference mDatabase, Intent data) {
//
//        return databaseReference;
//    }
}
