package com.atilsamancioglu.professionalartbook;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import java.util.HashMap;

public class ArtContentProvider extends ContentProvider {

    static final String PROVIDER_NAME = "com.atilsamancioglu.professionalartbook.ArtContentProvider";
    static final String URL = "content://" + PROVIDER_NAME + "/arts";
    static final Uri CONTENT_URI = Uri.parse(URL);

    static final String NAME = "name";
    static final String IMAGE = "image";


    static final int ARTS = 1;
    static final UriMatcher uriMatcher;

    static {
        uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
        uriMatcher.addURI(PROVIDER_NAME,"arts", ARTS);
    }


    private static HashMap<String, String> ART_PROJECTION_MAP;

    //-------------------- Database ---------------------


    private SQLiteDatabase sqLiteDatabase;
    static final String DATABASE_NAME = "Arts";
    static final String ARTS_TABLE_NAME = "arts";
    static final int DATABASE_VERSION = 1;
    static final String CREATE_DATABASE_TABLE = "CREATE TABLE " +
                        ARTS_TABLE_NAME + "(name TEXT NOT NULL, " +
                        "image BLOB NOT NULL);";



    private static class DatabaseHelper extends SQLiteOpenHelper {

        public DatabaseHelper(Context context) {
            super(context,DATABASE_NAME,null,DATABASE_VERSION);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            db.execSQL(CREATE_DATABASE_TABLE);
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            db.execSQL("DROP TABLE IF EXISTS " + ARTS_TABLE_NAME);
            onCreate(db);
        }
    }


    //-------------------- Database ---------------------

    @Override
    public boolean onCreate() {

        Context context = getContext();
        DatabaseHelper databaseHelper = new DatabaseHelper(context);

        sqLiteDatabase = databaseHelper.getWritableDatabase();

        return sqLiteDatabase != null;
    }

    @Nullable
    @Override
    public Cursor query(@NonNull Uri uri, @Nullable String[] projection, @Nullable String selection, @Nullable String[] selectionArgs, @Nullable String sortOrder) {

        SQLiteQueryBuilder sqLiteQueryBuilder = new SQLiteQueryBuilder();
        sqLiteQueryBuilder.setTables(ARTS_TABLE_NAME);

        switch (uriMatcher.match(uri)) {
            case ARTS:
                sqLiteQueryBuilder.setProjectionMap(ART_PROJECTION_MAP);
                break;

                default:
                    //
        }

        if (sortOrder == null || sortOrder.matches("")) {
            sortOrder = NAME;
        }

        Cursor cursor = sqLiteQueryBuilder.query(sqLiteDatabase,projection,selection,selectionArgs,null,null,sortOrder);

        cursor.setNotificationUri(getContext().getContentResolver(),uri);

        return cursor;
    }

    @Nullable
    @Override
    public String getType(@NonNull Uri uri) {
        return null;
    }

    @Nullable
    @Override
    public Uri insert(@NonNull Uri uri, @Nullable ContentValues values) {

        long rowID = sqLiteDatabase.insert(ARTS_TABLE_NAME,"",values);

        if (rowID > 0) {
            Uri newUri = ContentUris.withAppendedId(CONTENT_URI,rowID);
            getContext().getContentResolver().notifyChange(newUri,null);
            return newUri;
        }

        throw new SQLException("Error!");

    }

    @Override
    public int delete(@NonNull Uri uri, @Nullable String selection, @Nullable String[] selectionArgs) {


        int rowCount = 0;
        switch (uriMatcher.match(uri)) {
            case ARTS:
                   //delete
                rowCount = sqLiteDatabase.delete(ARTS_TABLE_NAME,selection,selectionArgs);
                break;

                default:
                throw new IllegalArgumentException("Failed URI");

        }

        getContext().getContentResolver().notifyChange(uri,null );

        return rowCount;
    }

    @Override
    public int update(@NonNull Uri uri, @Nullable ContentValues values, @Nullable String selection, @Nullable String[] selectionArgs) {


        int rowCount = 0;
        switch (uriMatcher.match(uri)) {
            case ARTS:
                //update
                rowCount = sqLiteDatabase.update(ARTS_TABLE_NAME,values,selection,selectionArgs);
                break;
                default:
                    throw new IllegalArgumentException("Failed URI");
        }

        getContext().getContentResolver().notifyChange(uri,null );

        return rowCount;
    }
}
