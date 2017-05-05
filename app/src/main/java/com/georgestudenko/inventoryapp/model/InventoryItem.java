package com.georgestudenko.inventoryapp.model;

import android.database.Cursor;

import com.georgestudenko.inventoryapp.data.InventoryContract;

/**
 * Created by george on 05/05/2017.
 */

public class InventoryItem {

    private String mName;
    private String mDescription;
    private int mQuantity;
    private int mPrice;

    public InventoryItem(String name, String description, int quantity, int price) {
        mName = name;
        mDescription = description;
        mQuantity = quantity;
        mPrice = price;
    }

    public String getName() {
        return mName;
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
        InventoryItem item = new InventoryItem(cursor.getString(cursor.getColumnIndex(InventoryContract.InventoryEntry.COLUMN_PRODUCT_NAME)),
                cursor.getString(cursor.getColumnIndex(InventoryContract.InventoryEntry.COLUMN_PRODUCT_DESCRIPTION)),
                cursor.getInt(cursor.getColumnIndex(InventoryContract.InventoryEntry.COLUMN_QUANTITY)),
                cursor.getInt(cursor.getColumnIndex(InventoryContract.InventoryEntry.COLUMN_PRODUCT_PRICE)));

        return item;
    }
}
