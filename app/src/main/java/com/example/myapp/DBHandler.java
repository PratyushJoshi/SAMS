package com.example.myapp;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DBHandler extends SQLiteOpenHelper {
    private static final String DB_NAME = "emsdb";
    private static final int DB_VERSION = 2;
    private static final String TABLE_NAME = "USERS";
    private static final String ID_COL = "id";
    private static final String USERNAME_COL = "username";
    private static final String PASSWORD_COL = "password";
    private static final String CONFIRMPASSWORD_COL = "confirmpassword";
    private static final String BIOMETRIC_COL = "biometric";
    private static final String EMAIL_COL = "email"; // Added column for email
    private static final String LAST_LOGIN_COL = "last_login"; // Added column for last login timestamp

    public DBHandler(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String query = "CREATE TABLE " + TABLE_NAME + " ("
                + ID_COL + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + USERNAME_COL + " TEXT UNIQUE,"
                + PASSWORD_COL + " VARCHAR,"
                + CONFIRMPASSWORD_COL + " VARCHAR,"
                + EMAIL_COL + " TEXT,"
                + BIOMETRIC_COL + " INTEGER DEFAULT 0,"
                + LAST_LOGIN_COL + "TIMESTAMP DEFAULT CURRENT_TIMESTAMP)"; // Adding the last login timestamp column
        db.execSQL(query);

        // Create a trigger to update the last login timestamp on login
        String triggerQuery = "CREATE TRIGGER update_last_login_trigger AFTER UPDATE OF " + BIOMETRIC_COL +
                " ON " + TABLE_NAME + " WHEN NEW." + BIOMETRIC_COL + "=1 BEGIN " +
                "UPDATE " + TABLE_NAME + " SET " + LAST_LOGIN_COL + "=CURRENT_TIMESTAMP WHERE " + USERNAME_COL + "=NEW." + USERNAME_COL + ";" +
                "END;";
        db.execSQL(triggerQuery);
    }

    // Method to check if user exists
    public boolean isUserExists(String username) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = null;
        boolean exists = false;

        try {
            String query = "SELECT * FROM " + TABLE_NAME + " WHERE " + USERNAME_COL + "=?";
            cursor = db.rawQuery(query, new String[]{username});
            exists = (cursor != null && cursor.getCount() > 0);
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }

        return exists;
    }

    // Method to add user details
    public boolean addUserDetails(String username, String password, String confirmPassword, String email) {
        if (isUserExists(username)) {
            return false; // User already exists
        }

        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(USERNAME_COL, username);
        values.put(PASSWORD_COL, password);
        values.put(CONFIRMPASSWORD_COL, confirmPassword);
        values.put(EMAIL_COL, email); // Adding the email column

        long result = db.insert(TABLE_NAME, null, values);
        db.close();
        return result != -1; // Returns true if insertion was successful
    }

    // Method to get password for a user
    public String getPasswordForUser(String username) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = null;
        String storedPassword = null;

        try {
            String query = "SELECT " + PASSWORD_COL + " FROM " + TABLE_NAME + " WHERE " + USERNAME_COL + "=?";
            cursor = db.rawQuery(query, new String[]{username});

            if (cursor != null && cursor.moveToFirst()) {
                storedPassword = cursor.getString(cursor.getColumnIndex(PASSWORD_COL));
            }
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }

        return storedPassword;
    }

    // Method to authenticate a user
    public boolean authenticateUser(String username, String password) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = null;
        boolean isAuthenticated = false;

        try {
            String query = "SELECT * FROM " + TABLE_NAME + " WHERE " + USERNAME_COL + "=? AND " + PASSWORD_COL + "=?";
            cursor = db.rawQuery(query, new String[]{username, password});
            isAuthenticated = cursor != null && cursor.getCount() > 0;
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }

        return isAuthenticated;
    }

    // Method to enable biometric login
    public boolean enableBiometricLogin(String username) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(BIOMETRIC_COL, 1);

        int result = db.update(TABLE_NAME, values, USERNAME_COL + "=?", new String[]{username});
        db.close();
        return result != -1; // Returns true if update was successful
    }

    // Method to disable biometric login
    public boolean disableBiometricLogin(String username) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(BIOMETRIC_COL, 0);

        int result = db.update(TABLE_NAME, values, USERNAME_COL + "=?", new String[]{username});
        db.close();
        return result != -1; // Returns true if update was successful
    }

    // Method to check if biometric login is enabled for a user
    public boolean isBiometricEnabled(String username) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = null;
        boolean isEnabled = false;

        try {
            String query = "SELECT * FROM " + TABLE_NAME + " WHERE " + USERNAME_COL + "=? AND " + BIOMETRIC_COL + "=?";
            cursor = db.rawQuery(query, new String[]{username, "1"});
            isEnabled = cursor != null && cursor.getCount() > 0;
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }

        return isEnabled;
    }

    public void updateLastLoginTimestamp(String username) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(LAST_LOGIN_COL, System.currentTimeMillis()); // Update timestamp to current time

        db.update(TABLE_NAME, values, USERNAME_COL + "=?", new String[]{username});
        db.close();
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(db);
    }
}