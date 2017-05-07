package com.georgestudenko.inventoryapp;

import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.os.ParcelFileDescriptor;
import android.provider.MediaStore;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.FileProvider;
import android.support.v4.content.Loader;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;

import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.georgestudenko.inventoryapp.data.InventoryContract.InventoryEntry;
import com.georgestudenko.inventoryapp.model.InventoryItem;

import java.io.File;
import java.io.FileDescriptor;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class EditorActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor>{

    private EditText mItemName;
    private EditText mItemDescription;
    private EditText mItemPrice;
    private EditText mItemQuantity;
    private LinearLayout mEditorActions;
    private LinearLayout mAddImagePanel;
    private int mTotalQuantity;
    private int mStockMovement;
    private TextView mSellQuantity;
    private final int LOADER_ID = 201;
    private AlertDialog.Builder dialogBuilder;
    private boolean mSelling;
    private InventoryItem mItem;
    private ImageView mItemPicture;
    private ImageView mOpenCameraIcon;
    private Bitmap mImageBitmap;
    static final int REQUEST_IMAGE_CAPTURE = 1;
    static final int SELECT_IMAGE_FROM_GALLERY = 2;
    private final String FILE_PROVIDER_AUTHORITY = "com.georgestudenko.fileprovider";
    private final String LOG_TAG ="INVENTORY_APP";
    private final String CAMERA_DIR = "/dcim/";
    private final String JPEG_FILE_PREFIX = "INV_IMG_";
    private final String JPEG_FILE_EXTENSION = ".jpg";
    private Uri mPhotoUri;
    private boolean mPhotoLoaded;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editor);
        mItemName =  (EditText) findViewById(R.id.editorItemName);
        mItemDescription =  (EditText) findViewById(R.id.editorItemDescription);
        mItemPrice =  (EditText) findViewById(R.id.editorItemPrice);
        mItemQuantity =  (EditText) findViewById(R.id.editorItemQuantity);
        mEditorActions = (LinearLayout) findViewById(R.id.editorActions);
        mItemPicture = (ImageView) findViewById(R.id.itemPicture);
        mOpenCameraIcon = (ImageView) findViewById(R.id.openCameraIcon);
        mAddImagePanel = (LinearLayout) findViewById(R.id.addImagePanel);
        mStockMovement = 0;
        mPhotoLoaded = false;

        if(getIntent().getData()!=null){
            mPhotoLoaded = true;
            setTitle(getString(R.string.edit_item));
            mEditorActions.setVisibility(View.VISIBLE);
            loadItem();
        }else{
            mEditorActions.setVisibility(View.GONE);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.editor_menu,menu);
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        if(getIntent().getData()==null){
            MenuItem deleteMenuItem = menu.findItem(R.id.editorDeleteButton);
            deleteMenuItem.setVisible(false);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.editorSaveButton:
                saveItem();
                return true;
            case R.id.editorDeleteButton:
                showDeleteItemDialog();
                return true;
        }
        return false;
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelable("thumbnail", mImageBitmap);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        if(savedInstanceState.containsKey("thumbnail")) {
            mImageBitmap = savedInstanceState.getParcelable("thumbnail");
            mItemPicture.setImageBitmap(mImageBitmap);
        }
    }

    private int convertPriceToStore(CharSequence price){
        int convertedPrice = 0;

        if(!TextUtils.isEmpty(price)) {
            double doublePrice = Double.valueOf(price.toString()) * 100;
            convertedPrice = (int) doublePrice;
        }

        return  convertedPrice;
    }

    private void saveItem(){
        ContentValues cv = new ContentValues();
        String name  =  mItemName.getText().toString();
        String description = mItemDescription.getText().toString();
        int price = convertPriceToStore(mItemPrice.getText());

        int quantity = !TextUtils.isEmpty(mItemQuantity.getText()) ? Integer.valueOf(mItemQuantity.getText().toString()) : 0;

        if(TextUtils.isEmpty(name)){
            Toast.makeText(this, R.string.fill_item_name, Toast.LENGTH_LONG).show();
            mItemName.setHintTextColor(Color.RED);
            Animation shake = AnimationUtils.loadAnimation(this, R.anim.shake);
            mItemName.startAnimation(shake);
            return;
        }

        if(!mPhotoLoaded){
            Toast.makeText(this, "Please add an image for the item", Toast.LENGTH_LONG).show();
            mAddImagePanel.setBackgroundColor(Color.rgb(255,148,148));
            return;
        }

        cv.put(InventoryEntry.COLUMN_PRODUCT_NAME,name);
        cv.put(InventoryEntry.COLUMN_PRODUCT_DESCRIPTION,description);
        cv.put(InventoryEntry.COLUMN_PRODUCT_PRICE,price);
        cv.put(InventoryEntry.COLUMN_QUANTITY,quantity);
        cv.put(InventoryEntry.COLUMN_PRODUCT_PHOTO_URI,mPhotoUri.toString());

        if(getIntent().getData()==null) {
            getContentResolver().insert(InventoryEntry.CONTENT_URI, cv);
            Toast.makeText(this,"Item "+name+" added!",Toast.LENGTH_SHORT).show();
        }
        else{
            getContentResolver().update(getIntent().getData(), cv,null,null);
            Toast.makeText(this,"Item "+name+" updated!",Toast.LENGTH_SHORT).show();
        }
        finish();
    }

    private void showDeleteItemDialog(){
        dialogBuilder = new AlertDialog.Builder(this);
        dialogBuilder.setTitle("Delete Item");
        dialogBuilder.setMessage("Are you sure you want to delete the item: "+ mItem.getName()+"?");

        dialogBuilder.setPositiveButton("Delete Item", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                deleteItem();
            }
        });

        dialogBuilder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        dialogBuilder.show();
    }

    private void deleteItem(){
        int rowsDeleted = getContentResolver().delete(getIntent().getData(),null,null);
        if(rowsDeleted>0){
            Toast.makeText(this, R.string.item_deleted,Toast.LENGTH_SHORT).show();
        }else{
            Toast.makeText(this, R.string.delete_error,Toast.LENGTH_SHORT).show();
        }
        finish();
    }

    private void loadItem(){
        invalidateOptionsMenu();
        getSupportLoaderManager().initLoader(LOADER_ID,null,this);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        return new CursorLoader(this,getIntent().getData(),null,null,null,null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        if (data == null || data.getCount() < 1) {
            return;
        }

        data.moveToFirst();
        mItem = InventoryItem.parseInventoryItem(data);
        mItemName.setText(mItem.getName());
        mItemDescription.setText(mItem.getDescription());
        mItemPrice.setText(mItem.getPriceToShow());
        mItemQuantity.setText(String.valueOf(mItem.getQuantity()));
        mTotalQuantity = mItem.getQuantity();
        Bitmap bitmap = getBitmapFromUri(mItem.getPhoto());
        mItemPicture.setImageBitmap(bitmap);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mItemName.setText("");
        mItemDescription.setText("");
        mItemPrice.setText("");
        mItemQuantity.setText("");
    }

    private void updateQuantity(int byQuantity, boolean isSale){
        int totalMovement = byQuantity;
        if(byQuantity<=0){
            return;
        }

        if(isSale){
            byQuantity = byQuantity * -1;
        }
        byQuantity = mItem.getQuantity() + byQuantity;
        ContentValues cv = new ContentValues();
        cv.put(InventoryEntry.COLUMN_QUANTITY,byQuantity);
        getContentResolver().update(getIntent().getData(),cv,null,null);
        mStockMovement = 0;
        if(isSale){
            Toast.makeText(this,"Sold " + totalMovement + ((totalMovement > 1) ? " items" : " item"),Toast.LENGTH_SHORT).show();
        }else{
            Toast.makeText(this,"Received " +  totalMovement + ((totalMovement > 1) ? " items" : " item"),Toast.LENGTH_SHORT).show();
        }
    }

    public void sellItems(View view) {
        mSelling = true;
        dialogBuilder = new AlertDialog.Builder(this);
        View v = getLayoutInflater().inflate(R.layout.stock_movement,null);
        mSellQuantity = (TextView) v.findViewById(R.id.sellQuantity);
        dialogBuilder.setView(v);
        dialogBuilder.setMessage(R.string.how_many_items_sell);
        dialogBuilder.setTitle(R.string.track_sale);
        dialogBuilder.setPositiveButton(R.string.sell, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                updateQuantity(mStockMovement,true);
            }
        });

        dialogBuilder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        dialogBuilder.show();
    }

    public void receiveShipment(View view) {
        mSelling = false;
        dialogBuilder = new AlertDialog.Builder(this);
        View v = getLayoutInflater().inflate(R.layout.stock_movement,null);
        mSellQuantity = (TextView) v.findViewById(R.id.sellQuantity);
        dialogBuilder.setView(v);
        dialogBuilder.setMessage(R.string.how_many_items);
        dialogBuilder.setTitle(R.string.receive_shipment);
        dialogBuilder.setPositiveButton(R.string.receive_order, new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                updateQuantity(mStockMovement,false);
            }
        });

        dialogBuilder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        dialogBuilder.show();
    }

    public void decreaseNumber(View view) {
        if(mStockMovement >0){
            mStockMovement--;
            mSellQuantity.setText(String.valueOf(mStockMovement));
        }
    }

    public void increaseNumber(View view) {
        if(mSelling) {
            if (mStockMovement < mTotalQuantity) {
                mStockMovement++;
            }
        }else{
            mStockMovement++;
        }
        mSellQuantity.setText(String.valueOf(mStockMovement));
    }

    public void makeOrder(View view) {
        Intent intent = new Intent(Intent.ACTION_SENDTO );
        intent.setType("text/plain");
        intent.setData(Uri.parse("mailto:"));
        intent.putExtra(Intent.EXTRA_SUBJECT, "Order "+ mItem.getName());
        intent.putExtra(Intent.EXTRA_TEXT, "Order item: " +mItem.getName() +"\nQuantity: SET_DESIRED_QUANTITY_HERE"  );

        if (intent.resolveActivity(getPackageManager()) != null) {
            startActivity(intent);
        }else{
            Toast.makeText(this,"There is no available app to handle this action on your device",Toast.LENGTH_LONG).show();
        }
    }

    private File getAlbumDir() {
        File storageDir = null;

        if (Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())) {

            storageDir = new File(Environment.getExternalStorageDirectory()
                    + CAMERA_DIR
                    + getString(R.string.app_name));

            Log.d(LOG_TAG, "Dir: " + storageDir);

            if (storageDir != null) {
                if (!storageDir.mkdirs()) {
                    if (!storageDir.exists()) {
                        Log.d(LOG_TAG, "failed to create directory");
                        return null;
                    }
                }
            }

        } else {
            Log.v(getString(R.string.app_name), "External storage is not mounted READ/WRITE.");
        }

        return storageDir;
    }

    private File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = JPEG_FILE_PREFIX + timeStamp + "_";
        File albumF = getAlbumDir();
        File imageF = File.createTempFile(imageFileName, JPEG_FILE_EXTENSION, albumF);
        return imageF;
    }

    public void openCamera(View v) {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

        try {
            File f = createImageFile();

            mPhotoUri = FileProvider.getUriForFile(
                    this, FILE_PROVIDER_AUTHORITY, f);

            takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, mPhotoUri);

            if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.KITKAT) {
                List<ResolveInfo> resInfoList = getPackageManager().queryIntentActivities(takePictureIntent, PackageManager.MATCH_DEFAULT_ONLY);
                for (ResolveInfo resolveInfo : resInfoList) {
                    String packageName = resolveInfo.activityInfo.packageName;
                    grantUriPermission(packageName, mPhotoUri, Intent.FLAG_GRANT_WRITE_URI_PERMISSION | Intent.FLAG_GRANT_READ_URI_PERMISSION);
                }
            }

            if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
                startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private Bitmap getBitmapFromUri(Uri uri) {
        ParcelFileDescriptor parcelFileDescriptor = null;
        try {
            parcelFileDescriptor =
                    getContentResolver().openFileDescriptor(uri, "r");
            FileDescriptor fileDescriptor = parcelFileDescriptor.getFileDescriptor();
            Bitmap image = BitmapFactory.decodeFileDescriptor(fileDescriptor);
            parcelFileDescriptor.close();
            return image;
        } catch (Exception e) {
            Log.e(LOG_TAG, "Failed to load image.", e);
            return null;
        } finally {
            try {
                if (parcelFileDescriptor != null) {
                    parcelFileDescriptor.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
                Log.e(LOG_TAG, "Error closing ParcelFile Descriptor");
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // grab image from camera
        if (requestCode == REQUEST_IMAGE_CAPTURE) {
            if (resultCode == RESULT_OK) {
                Bitmap bitmap = getBitmapFromUri(mPhotoUri);
                mItemPicture.setImageBitmap(bitmap);
                mPhotoLoaded = true;
            } else { // Result was a failure
                Toast.makeText(this, "Picture wasn't taken!", Toast.LENGTH_SHORT).show();
            }
        }
        // grab image from gallery
        if (requestCode == SELECT_IMAGE_FROM_GALLERY && resultCode == RESULT_OK) {
            mPhotoUri = data.getData();
            Bitmap bitmap = getBitmapFromUri(mPhotoUri);
            mItemPicture.setImageBitmap(bitmap);
            mPhotoLoaded = true;
        }
    }

    public void openGallery(View view) {
        Intent intent;

        if (Build.VERSION.SDK_INT < 19) {
            intent = new Intent(Intent.ACTION_GET_CONTENT);
        } else {
            intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
            intent.addCategory(Intent.CATEGORY_OPENABLE);
        }

        intent.setType("image/*");
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), SELECT_IMAGE_FROM_GALLERY);
    }
}
