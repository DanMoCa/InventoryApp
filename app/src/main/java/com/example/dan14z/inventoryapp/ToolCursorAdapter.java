package com.example.dan14z.inventoryapp;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.EditText;
import android.widget.TextView;

import com.example.dan14z.inventoryapp.data.ToolContract;

import org.w3c.dom.Text;


/**
 * Created by Dan14z on 01/09/2017.
 */

public class ToolCursorAdapter extends CursorAdapter{

    public ToolCursorAdapter(Context context, Cursor c) {
        super(context, c,0 /*flags*/);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        return LayoutInflater.from(context).inflate(R.layout.list_item,parent,false);
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        int nameColumnIndex = cursor.getColumnIndex(ToolContract.ToolEntry.COLUMN_TOOL_NAME);
        int brandColumnIndex = cursor.getColumnIndex(ToolContract.ToolEntry.COLUMN_TOOL_BRAND);
        int quantityColumnIndex = cursor.getColumnIndex(ToolContract.ToolEntry.COLUMN_TOOL_QUANTITY);

        String toolName = cursor.getString(nameColumnIndex);
        String toolBrand = cursor.getString(brandColumnIndex);
        int toolQuantity = cursor.getInt(quantityColumnIndex);

        TextView nameText = view.findViewById(R.id.name);
        TextView brandText = view.findViewById(R.id.summary);
        TextView quantityText = view.findViewById(R.id.quantity);

        nameText.setText(toolName);
        brandText.setText(toolBrand);
        quantityText.setText(toolQuantity+"");

    }
}
