package com.example.dan14z.inventoryapp.data;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.Log;

import com.example.dan14z.inventoryapp.data.ToolContract.ToolEntry;

/**
 * Created by Dan14z on 01/09/2017.
 */

public class ToolProvider extends ContentProvider{

    public static final String LOG_TAG = ToolProvider.class.getSimpleName();
    private static final int TOOLS = 100;
    private static final int TOOL_ID = 101;

    private static final UriMatcher sUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);

    static{
        sUriMatcher.addURI(ToolContract.CONTENT_AUTHORITY, ToolContract.PATH_TOOLS, TOOLS);

        sUriMatcher.addURI(ToolContract.CONTENT_AUTHORITY, ToolContract.PATH_TOOLS + "/#", TOOL_ID);
    }

    private ToolDbHelper mDbHelper;

    @Override
    public boolean onCreate() {
        mDbHelper = new ToolDbHelper(getContext());
        return false;
    }

    @Nullable
    @Override
    public Cursor query(@NonNull Uri uri, @Nullable String[] projection, @Nullable String selection, @Nullable String[] selectionArgs, @Nullable String sortOrder) {
        SQLiteDatabase db = mDbHelper.getReadableDatabase();

        Cursor cursor = null;

        int match = sUriMatcher.match(uri);

        switch(match){
            case TOOLS:
                cursor = db.query(ToolEntry.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder);
                break;
            case TOOL_ID:
                selection = ToolEntry._ID + "=?";
                selectionArgs = new String[] {String.valueOf(ContentUris.parseId(uri))};
                cursor = db.query(ToolEntry.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder);
                break;
            default:
                throw new IllegalArgumentException("Cannot query Unknown URI " + uri);
        }

        cursor.setNotificationUri(getContext().getContentResolver(),uri);

        return cursor;
    }

    @Nullable
    @Override
    public String getType(@NonNull Uri uri) {
        final int match = sUriMatcher.match(uri);
        switch(match){
            case TOOLS:
                return ToolEntry.CONTENT_LIST_TYPE;
            case TOOL_ID:
                return ToolEntry.CONTENT_ITEM_TYPE;
            default:
                throw new IllegalStateException("Unknown URI " + uri + " with match " + match);
        }
    }

    @Nullable
    @Override
    public Uri insert(@NonNull Uri uri, @Nullable ContentValues contentValues) {
        final int match = sUriMatcher.match(uri);
        switch(match){
            case TOOLS:
                return insertTool(uri,contentValues);
            default:
                throw new IllegalArgumentException("Insertion is not supported for " + uri);
        }
    }

    private Uri insertTool(Uri uri, ContentValues values){

        Log.v("Inserting",values.toString());

        String name = values.getAsString(ToolEntry.COLUMN_TOOL_NAME).trim();
        if(name == null || TextUtils.isEmpty(name)){
            throw new IllegalArgumentException("Tool requires a name");
        }

        String brand = values.getAsString(ToolEntry.COLUMN_TOOL_NAME).trim();
        if(brand == null || TextUtils.isEmpty(brand)){
            throw new IllegalArgumentException("Tool requires a brand");
        }

        Integer quantity = values.getAsInteger(ToolEntry.COLUMN_TOOL_QUANTITY);
        if(quantity < 0){
            throw new IllegalArgumentException("Tool quantity can't be a negative number");
        }

        SQLiteDatabase db = mDbHelper.getWritableDatabase();

        long newRowId = db.insert(ToolEntry.TABLE_NAME,null,values);

        if(newRowId == -1){
            Log.e(LOG_TAG, "Failed to insert row for " + uri);
            return null;
        }

        getContext().getContentResolver().notifyChange(uri,null);

        return ContentUris.withAppendedId(uri,newRowId);
    }

    @Override
    public int delete(@NonNull Uri uri, @Nullable String selection, @Nullable String[] selectionArgs) {
        Log.v("DELETING",uri+"");
        SQLiteDatabase db = mDbHelper.getWritableDatabase();

        final int match = sUriMatcher.match(uri);

        int rowsDeleted;
        int rowDeleted;

        switch (match){
            case TOOLS:
                rowsDeleted = db.delete(ToolEntry.TABLE_NAME,selection,selectionArgs);

                if(rowsDeleted != 0){
                    getContext().getContentResolver().notifyChange(uri,null);
                }

                return rowsDeleted;
            case TOOL_ID:
                selection = ToolEntry._ID + "=?";
                selectionArgs = new String[] { String.valueOf(ContentUris.parseId(uri))};

                rowDeleted = db.delete(ToolEntry.TABLE_NAME,selection,selectionArgs);

                if(rowDeleted != 0){
                    getContext().getContentResolver().notifyChange(uri,null);
                }

                return rowDeleted;
            default:
                throw new IllegalArgumentException("Deletion is not supported for  " + uri);

        }
    }


    @Override
    public int update(@NonNull Uri uri, @Nullable ContentValues contentValues, @Nullable String selection, @Nullable String[] selectionArgs) {
        final int match = sUriMatcher.match(uri);
        switch (match){
            case TOOLS:
                return updateTool(uri,contentValues,selection,selectionArgs);
            case TOOL_ID:
                selection = ToolEntry._ID + "=?";
                selectionArgs = new String[] {String.valueOf(ContentUris.parseId(uri))};
                return updateTool(uri,contentValues,selection,selectionArgs);
            default:
                throw new IllegalArgumentException("Update is not supported for " + uri);
        }
    }

    public int updateTool(Uri uri, ContentValues contentValues, String selection, String[] selectionArgs){
        if(contentValues.containsKey(ToolEntry.COLUMN_TOOL_NAME)){
            String name = contentValues.getAsString(ToolEntry.COLUMN_TOOL_NAME);
            if(name == "" || TextUtils.isEmpty(name)){
                throw new IllegalArgumentException("Tool requires a valid name");
            }
        }

        if(contentValues.containsKey(ToolEntry.COLUMN_TOOL_BRAND)){
            String brand = contentValues.getAsString(ToolEntry.COLUMN_TOOL_BRAND);
            if(brand == "" || TextUtils.isEmpty(brand)){
                throw new IllegalArgumentException("Tool requires a valid brand");
            }
        }

        if(contentValues.containsKey(ToolEntry.COLUMN_TOOL_QUANTITY)){
            int quantity = contentValues.getAsInteger(ToolEntry.COLUMN_TOOL_QUANTITY);
            if(quantity < 0 ){
                throw new IllegalArgumentException("Tool quantity cannot be negative");
            }
        }

        SQLiteDatabase db = mDbHelper.getWritableDatabase();

        Log.v("PROVIDER",uri+"");

        int rowsUpdated = db.update(ToolEntry.TABLE_NAME,contentValues,selection,selectionArgs);

        if(rowsUpdated != 0){
            getContext().getContentResolver().notifyChange(uri,null);
        }

        return rowsUpdated;
    }


}
