package com.example.dan14z.inventoryapp;

import android.Manifest;
import android.app.LoaderManager;
import android.content.ContentValues;
import android.content.CursorLoader;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.Loader;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.PersistableBundle;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.dan14z.inventoryapp.data.ToolContract.ToolEntry;
import com.example.dan14z.inventoryapp.utils.PictureTools;

import java.io.ByteArrayOutputStream;

public class EditorActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {

    private static final String LOG_TAG = EditorActivity.class.getSimpleName();

    private final static int CURRENT_TOOL_LOADER = 1;
    private final static int CAMERA_PERMISSION_CODE = 100;
    private final static int WRITE_STORAGE_PERMISSION_CODE = 101;
    private final static int READ_STORAGE_PERMISSION_CODE = 102;

    private Uri mCurrentToolUri;
    private EditText mNameEditText;
    private EditText mBrandEditText;
    private EditText mQuantityEditText;
    private int mToolHasChanged = 0;

    private static Button cameraBtn;
    private static final int IMAGE_CAPTURE = 0;
    private static final String mImagePath = "";
    private Uri imageUri;
    private ImageView mImageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editor);

        Intent intent = getIntent();

        mCurrentToolUri = intent.getData();

        mNameEditText = (EditText)findViewById(R.id.edit_tool_name);
        mBrandEditText = (EditText)findViewById(R.id.edit_tool_brand);
        mQuantityEditText = (EditText)findViewById(R.id.edit_tool_quantity);

        if(mCurrentToolUri != null){
            setTitle(getString(R.string.editor_activity_edit_title));
            getLoaderManager().initLoader(CURRENT_TOOL_LOADER, null, this);
        }else{
            invalidateOptionsMenu();
        }

        Button decreaseButton = (Button) findViewById(R.id.button_decrease);
        decreaseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                decreaseQuantity();
            }
        });

        Button incrementButton = (Button) findViewById(R.id.button_increment);
        incrementButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                incrementQuantity();
            }
        });

        cameraBtn = (Button) findViewById(R.id.startCamera);
        mImageView = (ImageView) findViewById(R.id.imageView);

        cameraBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
            startCamera();
            }
        });
    }

    @Override
    public void onSaveInstanceState(Bundle outState, PersistableBundle outPersistentState) {
        BitmapDrawable bitmapDrawable = (BitmapDrawable) mImageView.getDrawable();
        Bitmap bitmap = bitmapDrawable.getBitmap();
        outState.putParcelable("image",bitmap);
        super.onSaveInstanceState(outState, outPersistentState);
    }

    public void startCamera(){
        if(PictureTools.permissionReadMemmory(this)){
            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            imageUri = PictureTools.with(EditorActivity.this).getOutputMediaFileUri(PictureTools.MEDIA_TYPE_IMAGE);
            intent.putExtra(MediaStore.EXTRA_OUTPUT,imageUri);
            startActivityForResult(intent,IMAGE_CAPTURE);
        }

//        Log.d("CAMERA_ACTIVITY","Starting camera on the phone");
//        String fileName = "testphoto.jpg";
//        ContentValues values = new ContentValues();
//        values.put(MediaStore.Images.Media.TITLE,fileName);
//        values.put(MediaStore.Images.Media.DESCRIPTION,"Image captured by camera");
//        values.put(MediaStore.Images.Media.MIME_TYPE,"image/jpeg");
//        imageUri = getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,values);
//        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
//        intent.putExtra(MediaStore.EXTRA_OUTPUT,imageUri);
//        intent.putExtra(MediaStore.EXTRA_VIDEO_QUALITY,1);
//        startActivityForResult(intent,IMAGE_CAPTURE);
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == IMAGE_CAPTURE) {
            if (resultCode == RESULT_OK){
                Bitmap bitmap = PictureTools.decodeSampledBitmapFromUri(PictureTools.currentPhotoPath,200,200);
                mImageView.setImageBitmap(bitmap);
            }
        }
    }

    private static byte[] convertDrawableToByteArray(Drawable drawableResource){
        Bitmap imageBitmap = ((BitmapDrawable) drawableResource).getBitmap();
        ByteArrayOutputStream imageByteStream = new ByteArrayOutputStream();
        imageBitmap.compress(Bitmap.CompressFormat.PNG,100,imageByteStream);
        byte[] imageByteData = imageByteStream.toByteArray();
        return imageByteData;
    }

    private void decreaseQuantity(){
        String currQText = mQuantityEditText.getText().toString().trim();

        int currentQuantity = (currQText == "" ? 0 : Integer.parseInt(currQText));
        if(currentQuantity > 0){
            mQuantityEditText.setText(currentQuantity - 1+"");
        }
    }

    private void incrementQuantity(){
        String currQText = mQuantityEditText.getText().toString().trim();
        int currentQuantity = (currQText == "" ? 0 : Integer.parseInt(currQText));
        mQuantityEditText.setText(currentQuantity + 1+"");
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_editor,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()){
            case R.id.action_save:
                saveTool();
                return true;
            case R.id.action_delete:
                showDeleteConfirmationDialog();
                return true;
            case R.id.home:
                // TODO: Show unsaved changes dialog
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void saveTool(){
        String name = mNameEditText.getText().toString().trim();
        String brand = mBrandEditText.getText().toString().trim();
        String quantityText = mQuantityEditText.getText().toString().trim();
        String imagePath = PictureTools.currentPhotoPath;

        if(mCurrentToolUri == null && TextUtils.isEmpty(name) && TextUtils.isEmpty(brand)){
            Toast.makeText(this,"Input some info before saving a new pet",Toast.LENGTH_SHORT).show();
            return;
        }

        if(TextUtils.isEmpty(name)){
            Toast.makeText(this,"Please input a name",Toast.LENGTH_SHORT).show();
            return;
        }

        if(TextUtils.isEmpty(brand)){
            Toast.makeText(this,"Please input a brand",Toast.LENGTH_SHORT).show();
            return;
        }

        if(imagePath == null || imagePath.length() == 0){
            Toast.makeText(this,"Please take a picture of the tool",Toast.LENGTH_SHORT).show();
            return;
        }

        int quantity = 0;

        if(!TextUtils.isEmpty(quantityText)){
            quantity = Integer.parseInt(quantityText);
        }


//        byte[] image = convertDrawableToByteArray(mImageView.getDrawable());

        ContentValues values = new ContentValues();

        values.put(ToolEntry.COLUMN_TOOL_NAME,name);
        values.put(ToolEntry.COLUMN_TOOL_BRAND,brand);
        values.put(ToolEntry.COLUMN_TOOL_QUANTITY,quantity);
        values.put(ToolEntry.COLUMN_TOOL_IMAGE,imagePath);
//        if(!TextUtils.isEmpty(image.toString())){
//            values.put(ToolEntry.COLUMN_TOOL_IMAGE,image);
//        }

        if(mCurrentToolUri == null){
            Uri newToolUri = getContentResolver().insert(ToolEntry.CONTENT_URI,values);
            if(newToolUri == null){
                Toast.makeText(this,R.string.save_tool_failed,Toast.LENGTH_SHORT).show();
            }else{
                Toast.makeText(this,R.string.save_tool_success,Toast.LENGTH_SHORT).show();
            }
        }else{
            Log.v("UPDATING",mCurrentToolUri+"");
            int rowsAffected = getContentResolver().update(mCurrentToolUri,values,null,null);

            if(rowsAffected == 0){
                Toast.makeText(this,R.string.update_tool_failed,Toast.LENGTH_SHORT).show();
            }else{
                Toast.makeText(this,R.string.update_tool_success,Toast.LENGTH_SHORT).show();
            }
        }

        finish();
    }

    private void showDeleteConfirmationDialog(){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(getString(R.string.alert_delete_message));
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
                deleteTool();
            }
        });

        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    private void deleteTool(){
        if(mCurrentToolUri != null){
            int rowsDeleted = getContentResolver().delete(mCurrentToolUri,null,null);
            if(rowsDeleted == 0 ){
                Toast.makeText(this,R.string.delete_error,Toast.LENGTH_SHORT).show();
            }else{
                Toast.makeText(this,R.string.delete_tools_success,Toast.LENGTH_SHORT).show();
            }
        }
        finish();
    }

    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        Log.v("Editor",mCurrentToolUri+"");
        String[] projection = {
                ToolEntry._ID,
                ToolEntry.COLUMN_TOOL_NAME,
                ToolEntry.COLUMN_TOOL_BRAND,
                ToolEntry.COLUMN_TOOL_QUANTITY,
                ToolEntry.COLUMN_TOOL_IMAGE
        };

        return new CursorLoader(this,
                mCurrentToolUri,
                projection,
                null,
                null,
                null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
        if(cursor == null || cursor.getCount() < 0){
            return ;
        }

        if(cursor.moveToFirst()){
            int nameColumnIndex = cursor.getColumnIndex(ToolEntry.COLUMN_TOOL_NAME);
            int brandColumnIndex = cursor.getColumnIndex(ToolEntry.COLUMN_TOOL_BRAND);
            int quantityColumnIndex = cursor.getColumnIndex(ToolEntry.COLUMN_TOOL_QUANTITY);
            int imageColumnIndex = cursor.getColumnIndex(ToolEntry.COLUMN_TOOL_IMAGE);

            String toolName = cursor.getString(nameColumnIndex);
            String toolBrand = cursor.getString(brandColumnIndex);
            int toolQuantity = cursor.getInt(quantityColumnIndex);
            String toolImage = cursor.getString(imageColumnIndex);

            mNameEditText.setText(toolName);
            mBrandEditText.setText(toolBrand);
            mQuantityEditText.setText(toolQuantity+"");
            if(toolImage != null && toolImage.length() > 0){
                Bitmap bitmap = PictureTools.decodeSampledBitmapFromUri(toolImage,200,200);
                mImageView.setImageBitmap(bitmap);
            }
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mNameEditText.setText("");
        mBrandEditText.setText("");
        mQuantityEditText.setText("");
        mImageView.setImageResource(0);
    }
}
