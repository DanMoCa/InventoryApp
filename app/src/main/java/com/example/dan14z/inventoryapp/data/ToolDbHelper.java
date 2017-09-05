package com.example.dan14z.inventoryapp.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import com.example.dan14z.inventoryapp.data.ToolContract.ToolEntry;

/**
 * Created by Dan14z on 01/09/2017.
 */

public class ToolDbHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "tools.db";
    private static final int DATABASE_VERSION = 1;

    public ToolDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        String SQL_CREATE_TOOLS_DATABASE = "CREATE TABLE " + ToolEntry.TABLE_NAME + "(" +
                ToolEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                ToolEntry.COLUMN_TOOL_NAME + " TEXT NOT NULL, " +
                ToolEntry.COLUMN_TOOL_IMAGE + " BLOB, " +
                ToolEntry.COLUMN_TOOL_BRAND + " TEXT, " +
                ToolEntry.COLUMN_TOOL_QUANTITY + " INTEGER NOT NULL DEFAULT 0);";

        sqLiteDatabase.execSQL(SQL_CREATE_TOOLS_DATABASE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

    }
}
