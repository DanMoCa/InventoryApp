package com.example.dan14z.inventoryapp;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.dan14z.inventoryapp.data.ToolContract;
import com.example.dan14z.inventoryapp.utils.PictureTools;

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
        int imageColumnIndex = cursor.getColumnIndex(ToolContract.ToolEntry.COLUMN_TOOL_IMAGE);

        String toolName = cursor.getString(nameColumnIndex);
        String toolBrand = cursor.getString(brandColumnIndex);
        int toolQuantity = cursor.getInt(quantityColumnIndex);
        String toolImagePath = cursor.getString(imageColumnIndex);


        TextView nameText = view.findViewById(R.id.name);
        TextView brandText = view.findViewById(R.id.summary);
        TextView quantityText = view.findViewById(R.id.quantity);
        ImageView imageView = view.findViewById(R.id.imageView);

        if(toolImagePath != null && !TextUtils.isEmpty(toolImagePath)){
            Bitmap bitmap = PictureTools.decodeSampledBitmapFromUri(toolImagePath,100,100);
            imageView.setImageBitmap(bitmap);
        }else{
            imageView.setImageResource(R.drawable.empty_wrench_image);
        }

        nameText.setText(toolName);
        brandText.setText(toolBrand);
        quantityText.setText(toolQuantity+"");

    }
}
