package com.georgestudenko.inventoryapp.data;

import android.net.Uri;
import android.provider.BaseColumns;

/**
 * Created by george on 04/05/2017.
 */

public final class InventoryContract {

    public static final String CONTENT_AUTHORITY = "com.georgestudenko.inventoryapp";
    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);
    public static final String PATH_INVENTORY = "inventory";

    public final static class InventoryEntry implements BaseColumns {
        public static final Uri CONTENT_URI = Uri.withAppendedPath(BASE_CONTENT_URI, PATH_INVENTORY);
        public final static String TABLE_NAME = "inventory";
        public final static String COLUMN_ID = BaseColumns._ID;
        public final static String COLUMN_PRODUCT_NAME = "name";
        public final static String COLUMN_PRODUCT_PRICE = "price";
        public final static String COLUMN_QUANTITY = "qty";
        public final static String COLUMN_PRODUCT_DESCRIPTION = "description";
        public final static String COLUMN_PRODUCT_PHOTO_URI = "photo";
    }
}
