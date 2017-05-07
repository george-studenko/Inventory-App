package com.georgestudenko.inventoryapp.model;

import android.database.Cursor;

import com.georgestudenko.inventoryapp.R;
import com.georgestudenko.inventoryapp.data.InventoryContract;

import java.text.DecimalFormat;

/**
 * Created by george on 05/05/2017.
 */

public class InventoryItem {

    private String mName;
    private String mDescription;
    private int mQuantity;
    private int mPrice;
    private long mId;

    public InventoryItem(String name, String description, int quantity, int price, long id) {
        mName = name;
        mDescription = description;
        mQuantity = quantity;
        mPrice = price;
        mId = id;
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

    public String getFormattedPrice(){
        return "$ " + mPrice/100;
    }

    public static InventoryItem parseInventoryItem(Cursor cursor){
        if(cursor!=null) {
            InventoryItem item = new InventoryItem(cursor.getString(cursor.getColumnIndex(InventoryContract.InventoryEntry.COLUMN_PRODUCT_NAME)),
                    cursor.getString(cursor.getColumnIndex(InventoryContract.InventoryEntry.COLUMN_PRODUCT_DESCRIPTION)),
                    cursor.getInt(cursor.getColumnIndex(InventoryContract.InventoryEntry.COLUMN_QUANTITY)),
                    cursor.getInt(cursor.getColumnIndex(InventoryContract.InventoryEntry.COLUMN_PRODUCT_PRICE)),
                    cursor.getInt(cursor.getColumnIndex(InventoryContract.InventoryEntry.COLUMN_ID)));
            return item;
        }else{
            return null;
        }

    }
}
