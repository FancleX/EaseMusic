package edu.northeastern.ease_music_andriod.utils;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

import java.util.Date;

public class DBHandler extends SQLiteOpenHelper {

    private static final String DB_NAME = "EASE_MUSIC_DB";
    private static final int DB_VERSION = 1;
    private static final String ID_COL = "id";
    private static final String TABLE_NAME = "user_profile";
    private static final String USER_NAME_COL = "username";
    private static final String USER_PROFILE_IMAGE_COL = "image";
    private static final String LAST_LOGIN_DATE = "date";


    public DBHandler(@Nullable Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String sql = "CREATE TABLE IF NOT EXISTS  " + TABLE_NAME
                + " ("
                + ID_COL + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + USER_NAME_COL + " TEXT,"
                + USER_PROFILE_IMAGE_COL + " TEXT,"
                + LAST_LOGIN_DATE + " LONG"
                + ")";

        db.execSQL(sql);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(db);
    }

    public void insertOrUpdateUserProfile(String username, @Nullable String imageUri, @Nullable Date date) {
        SQLiteDatabase db = getWritableDatabase();

        String query = "SELECT * FROM " + TABLE_NAME +
                " WHERE " + USER_NAME_COL + String.format(" = '%s'", username);

        Cursor cursor = db.rawQuery(query, null);
        if (cursor.moveToFirst()) {
            int idColIndex = cursor.getColumnIndex(ID_COL);
            long id = cursor.getLong(idColIndex);

            ContentValues values = new ContentValues();
            if (imageUri != null)
                values.put(USER_PROFILE_IMAGE_COL, imageUri);
            if (date != null)
                values.put(LAST_LOGIN_DATE, date.getTime());

            String selection = "id = ?";
            String[] selectionArgs = {String.valueOf(id)};

            db.update(TABLE_NAME, values, selection, selectionArgs);
        } else {
            ContentValues values = new ContentValues();
            values.put(USER_NAME_COL, username);
            if (imageUri != null)
                values.put(USER_PROFILE_IMAGE_COL, imageUri);
            if (date != null)
                values.put(LAST_LOGIN_DATE, date.getTime());

            db.insert(TABLE_NAME, null, values);
        }

        cursor.close();
    }

    @Nullable
    public String getUserImageUriByName(String username) {
        SQLiteDatabase db = getReadableDatabase();

        String sql = "SELECT " + USER_PROFILE_IMAGE_COL +
                " FROM " + TABLE_NAME
                + " WHERE " + USER_NAME_COL + String.format(" = '%s'", username);

        Cursor cursor = db.rawQuery(sql, null);
        String uri = null;

        if (cursor.moveToFirst()) {
            int columnIndex = cursor.getColumnIndex(USER_PROFILE_IMAGE_COL);
            uri = cursor.getString(columnIndex);
        }

        cursor.close();

        return uri;
    }

    @Nullable
    public String getLastLoginUser() {
        SQLiteDatabase db = getReadableDatabase();

        String[] projection = {USER_NAME_COL};
        String sortOrder = LAST_LOGIN_DATE + " DESC";

        Cursor cursor = db.query(TABLE_NAME, projection, null, null, null, null, sortOrder);
        String username = null;

        if (cursor.moveToFirst()) {
            int columnIndex = cursor.getColumnIndex(USER_NAME_COL);
            username = cursor.getString(columnIndex);
        }

        cursor.close();

        return username;
    }
}
