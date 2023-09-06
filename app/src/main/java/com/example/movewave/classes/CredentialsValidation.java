package com.example.movewave.classes;

import java.util.regex.Pattern;

public class CredentialsValidation {
    public static boolean isEmailValid(String email) {
        String emailRegex = "^[a-zA-Z0-9_+&*-]+(?:\\." +
                "[a-zA-Z0-9_+&*-]+)*@" +
                "(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,7}$";
        Pattern patternEmail = Pattern.compile(emailRegex);
        return patternEmail.matcher(email).matches();
    }

    public static boolean isPasswordValid(String password) {
        String passwordRegex = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]{7,}$";
        Pattern patternPwd = Pattern.compile(passwordRegex);
        return patternPwd.matcher(password).matches();
    }

}
