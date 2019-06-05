package com.example.samuel.pentrufacultate.network;

import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class LoginHelper {
    private final static FirebaseAuth auth=FirebaseAuth.getInstance();

    private static boolean logged;


    public static boolean isLogged() {
        return logged;
    }
    public static void logOut(){
        auth.signOut();
        logged=false;
    }
    public static void logedIn(){
        logged=true;

    }
}
