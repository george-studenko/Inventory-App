package com.georgestudenko.inventoryapp.data;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.georgestudenko.inventoryapp.data.InventoryContract.InventoryEntry;

/**
 * Created by george on 04/05/2017.
 */

public class InventoryProvider extends ContentProvider {

    private InventoryDbHelper mDbHelper;
    private UriMatcher mUriMatcher;
    private final int ITEMS = 100;
    private final int ITEM_ID = 101;

    @Override
    public boolean onCreate() {
        mDbHelper = new InventoryDbHelper(getContext(),InventoryDbHelper.DATABASE_NAME,null,InventoryDbHelper.DATABASE_VERSION);
        mUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
        mUriMatcher.addURI(InventoryContract.CONTENT_AUTHORITY,InventoryContract.PATH_INVENTORY,ITEMS);
        mUriMatcher.addURI(InventoryContract.CONTENT_AUTHORITY,InventoryContract.PATH_INVENTORY+"/#",ITEM_ID);
        return true;
    }

    @Nullable
    @Override
    public Cursor query(@NonNull Uri uri, @Nullable String[] projection, @Nullable String selection, @Nullable String[] selectionArgs, @Nullable String sortOrder) {
        SQLiteDatabase db = mDbHelper.getReadableDatabase();

        switch (mUriMatcher.match(uri)){
            case ITEMS:
                return db.query(InventoryEntry.TABLE_NAME,projection,null,null,null,null,sortOrder);
            case ITEM_ID:
                String[] ids = {String.valueOf(ContentUris.parseId(uri))};
                return db.query(InventoryEntry.TABLE_NAME,projection,InventoryEntry._ID + "=?",ids,null,null,sortOrder);
            default:
                throw new IllegalArgumentException("Not implemented uri: " + uri);
        }
    }

    @Nullable
    @Override
    public String getType(@NonNull Uri uri) {
        return null;
    }

    @Nullable
    @Override
    public Uri insert(@NonNull Uri uri, @Nullable ContentValues values) {
        SQLiteDatabase db = mDbHelper.getWritableDatabase();

        switch (mUriMatcher.match(uri)){
            case ITEMS:
                long id = db.insert(InventoryEntry.TABLE_NAME,null,values);
                return ContentUris.withAppendedId(uri,id);
            default:
                throw new IllegalArgumentException("Not implemented uri: " + uri);
        }
    }

    @Override
    public int delete(@NonNull Uri uri, @Nullable String selection, @Nullable String[] selectionArgs) {
        SQLiteDatabase db = mDbHelper.getWritableDatabase();

        switch (mUriMatcher.match(uri)){
            case ITEMS:
                return db.delete(InventoryEntry.TABLE_NAME,null,null);
            case ITEM_ID:
                String[] ids = {String.valueOf(ContentUris.parseId(uri))};
                return db.delete(InventoryEntry.TABLE_NAME,InventoryEntry._ID + "=?",ids);
            default:
                throw new IllegalArgumentException("Not implemented uri: " + uri);
        }
    }

    @Override
    public int update(@NonNull Uri uri, @Nullable ContentValues values, @Nullable String selection, @Nullable String[] selectionArgs) {
        return 0;
    }
}
