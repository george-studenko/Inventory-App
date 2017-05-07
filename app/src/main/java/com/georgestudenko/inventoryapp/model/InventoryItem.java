package com.georgestudenko.inventoryapp.model;

import android.database.Cursor;
import android.net.Uri;

import com.georgestudenko.inventoryapp.data.InventoryContract.InventoryEntry;

import java.text.DecimalFormat;

/**
 * Created by george on 05/05/2017.
 */

public class InventoryItem {

    private String mName;
    private String mDescription;
    private int mQuantity;
    private int mPrice;
    private Uri mPhoto;
    private long mId;

    public InventoryItem(String name, String description, int quantity, int price, long id, Uri photo) {
        mName = name;
        mDescription = description;
        mQuantity = quantity;
        mPrice = price;
        mId = id;
        mPhoto = photo;
    }

    public Uri getPhoto() {
        return mPhoto;
    }

    public String getName() {
        return mName;
    }

    public long getId() {
        return mId;
    }

    public String getDescription() {
        return mDescription;
    }

    public int getQuantity() {
        return mQuantity;
    }

    public int getPrice() {
        return mPrice;
    }
    public String getPriceToShow(){ return String.valueOf(mPrice/100.0); }
    public String getFormattedPrice(){
        DecimalFormat df = new DecimalFormat("0.00##");
        String result = df.format(mPrice/100.0);
        return "€ " + result;
    }

    public static InventoryItem parseInventoryItem(Cursor cursor){
        if(cursor!=null) {

            InventoryItem item = new InventoryItem(cursor.getString(cursor.getColumnIndex(InventoryEntry.COLUMN_PRODUCT_NAME)),
                    cursor.getString(cursor.getColumnIndex(InventoryEntry.COLUMN_PRODUCT_DESCRIPTION)),
                    cursor.getInt(cursor.getColumnIndex(InventoryEntry.COLUMN_QUANTITY)),
                    cursor.getInt(cursor.getColumnIndex(InventoryEntry.COLUMN_PRODUCT_PRICE)),
                    cursor.getInt(cursor.getColumnIndex(InventoryEntry.COLUMN_ID)),
                    Uri.parse(cursor.getString(cursor.getColumnIndex(InventoryEntry.COLUMN_PRODUCT_PHOTO_URI))));
            return item;
        }else{
            return null;
        }

    }
}
