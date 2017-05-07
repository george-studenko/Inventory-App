package com.georgestudenko.inventoryapp.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.georgestudenko.inventoryapp.data.InventoryContract.InventoryEntry;

/**
 * Created by george on 04/05/2017.
 */

public class InventoryDbHelper extends SQLiteOpenHelper {

    public static final String DATABASE_NAME = "inventory.db";
    public static final int DATABASE_VERSION = 2;
    public final String SQL_CREATE_INVENTORY_TABLE = "CREATE TABLE " + InventoryEntry.TABLE_NAME + "("+
            InventoryEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
            InventoryEntry.COLUMN_PRODUCT_NAME + " TEXT NOT NULL,"+
            InventoryEntry.COLUMN_PRODUCT_DESCRIPTION + " TEXT," +
            InventoryEntry.COLUMN_PRODUCT_PRICE + " INTEGER NOT NULL,"+
            InventoryEntry.COLUMN_PRODUCT_PHOTO_URI + " TEXT,"+
            InventoryEntry.COLUMN_QUANTITY + " INTEGER NOT NULL)";

    public final String SQL_DROP_INVENTORY_TABLE = "DROP TABLE IF EXISTS "+
            InventoryEntry.TABLE_NAME;

    public InventoryDbHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(SQL_CREATE_INVENTORY_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL(SQL_DROP_INVENTORY_TABLE);
        onCreate(db);
    }
}
