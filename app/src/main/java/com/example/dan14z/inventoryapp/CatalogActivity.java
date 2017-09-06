package com.example.dan14z.inventoryapp;

import android.app.LoaderManager;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.CursorLoader;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.example.dan14z.inventoryapp.data.ToolContract.ToolEntry;
import com.example.dan14z.inventoryapp.data.ToolDbHelper;

public class CatalogActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor>{

    private ToolDbHelper mDbHelper;
    private ToolCursorAdapter mToolCursorAdapter;

    private static final int LOADER_ID = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_catalog);

        ListView toolsListView = (ListView)findViewById(R.id.list);
        View emptyView = findViewById(R.id.empty_view);
        toolsListView.setEmptyView(emptyView);

        FloatingActionButton newToolButton = (FloatingActionButton) findViewById(R.id.fab);

        newToolButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(CatalogActivity.this,EditorActivity.class);
                startActivity(intent);
            }
        });

        mDbHelper = new ToolDbHelper(this);

        mToolCursorAdapter = new ToolCursorAdapter(this,null);

        toolsListView.setAdapter(mToolCursorAdapter);

        toolsListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                Intent intent = new Intent(CatalogActivity.this,EditorActivity.class);

                Uri currentToolUri = ContentUris.withAppendedId(ToolEntry.CONTENT_URI,id);

                intent.setData(currentToolUri);

                startActivity(intent);
            }
        });

        getLoaderManager().initLoader(LOADER_ID,null,this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_catalog,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()){
            case R.id.action_insert_dummy_data:
                insertDummyData();
                return true;
            case R.id.action_delete_all_entries:
                showDeleteConfirmationDialog();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void insertDummyData(){
        ContentValues values = new ContentValues();
        values.put(ToolEntry.COLUMN_TOOL_NAME,"Martillo");
        values.put(ToolEntry.COLUMN_TOOL_BRAND,"HammerDown");
        values.put(ToolEntry.COLUMN_TOOL_QUANTITY,0);

        Uri newRow = getContentResolver().insert(ToolEntry.CONTENT_URI,values);
    }

    private void showDeleteConfirmationDialog(){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(getString(R.string.alert_delete_all_message));
        builder.setNegativeButton(getString(R.string.cancel), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                if(dialogInterface != null){
                    dialogInterface.dismiss();
                }
            }
        });
        builder.setPositiveButton(getString(R.string.delete), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                deleteTools();
            }
        });

        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    public void deleteTools(){
        int rowsDeleted = getContentResolver().delete(ToolEntry.CONTENT_URI,null,null);
        if(rowsDeleted == 0){
            Toast.makeText(this,getString(R.string.delete_error), Toast.LENGTH_SHORT).show();
        }else{
            Toast.makeText(this,getString(R.string.delete_tools_success),Toast.LENGTH_SHORT).show();
        }
        Log.v("CatalogActivity",rowsDeleted + " rows deleted from tools database");
    }

    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        String[] projection = {
                ToolEntry._ID,
                ToolEntry.COLUMN_TOOL_NAME,
                ToolEntry.COLUMN_TOOL_BRAND,
                ToolEntry.COLUMN_TOOL_QUANTITY,
                ToolEntry.COLUMN_TOOL_IMAGE
        };

        return new CursorLoader(this,
                ToolEntry.CONTENT_URI,
                projection,
                null,
                null,
                null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        mToolCursorAdapter.swapCursor(data);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mToolCursorAdapter.swapCursor(null);
    }
}
