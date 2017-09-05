package com.example.dan14z.inventoryapp.data;

import android.content.ContentResolver;
import android.net.Uri;
import android.provider.BaseColumns;

import java.sql.Blob;

/**
 * Created by Dan14z on 01/09/2017.
 */

public class ToolContract {

    private ToolContract(){}

    public static final String CONTENT_AUTHORITY = "com.example.dan14z.inventoryapp";
    public static final Uri BASE_CONTENT_URI = Uri.parse("content://"+CONTENT_AUTHORITY);
    public static final String PATH_TOOLS = "tools";

    public static final class ToolEntry implements BaseColumns{

        public static final Uri CONTENT_URI = Uri.withAppendedPath(BASE_CONTENT_URI,PATH_TOOLS);
        public static final String CONTENT_LIST_TYPE = ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_TOOLS;
        public static final String CONTENT_ITEM_TYPE = ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_TOOLS;

        public static final String TABLE_NAME = "tools";

        public static final String _ID = BaseColumns._ID;
        public static final String COLUMN_TOOL_NAME = "name";
        public static final String COLUMN_TOOL_IMAGE = "image";
        public static final String COLUMN_TOOL_BRAND = "brand";
        public static final String COLUMN_TOOL_QUANTITY = "quantity";
    }
}
