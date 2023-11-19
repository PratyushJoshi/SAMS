package com.example.myapp;
import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;
import org.json.JSONException;
import org.json.JSONObject;

public class Subject {

    private static Subject ourInstance;
    private final DBHandler dbHandler;

    private Subject(DBHandler dbHandler) {
        this.dbHandler = dbHandler;
    }

    public static Subject getInstance(DBHandler dbHandler) {
        if (ourInstance == null) {
            ourInstance = new Subject(dbHandler);
        }
        return ourInstance;
    }

    static void truncate(SQLiteDatabase db) {
        db.execSQL("DELETE FROM subject;");
    }

    static void insert(DBHandler dbHandler, JSONObject subject) throws JSONException {
        String code = subject.getString("code");
        String name = subject.getString("name");
        int batch_id = subject.getInt("batch_id");

        ContentValues values = new ContentValues();
        values.put("code", code);
        values.put("name", name);
        values.put("batch_id", batch_id);

        SQLiteDatabase db = dbHandler.getWritableDatabase();
        db.insert("subject", null, values);
        db.close();
    }

    static void createTable(DBHandler dbHandler) {
        SQLiteDatabase db = dbHandler.getWritableDatabase();
        String qry1 = "CREATE TABLE IF NOT EXISTS subject (" +
                " code TEXT NOT NULL UNIQUE," +
                " name TEXT," +
                " batch_id INTEGER NOT NULL," +
                " FOREIGN KEY(batch_id) " +
                " REFERENCES batch(batch_id) ON UPDATE RESTRICT ON DELETE RESTRICT," +
                " PRIMARY KEY(code) )";
        db.execSQL(qry1);
        db.close();
    }

    static void dropTable(DBHandler dbHandler) {
        SQLiteDatabase db = dbHandler.getWritableDatabase();
        String qry1 = "DROP TABLE IF EXISTS subject";
        db.execSQL(qry1);
        db.close();
    }
}
