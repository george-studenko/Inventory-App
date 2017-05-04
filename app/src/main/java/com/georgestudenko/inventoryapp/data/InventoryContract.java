package com.georgestudenko.inventoryapp.data;

import android.net.Uri;
import android.provider.BaseColumns;

/**
 * Created by george on 04/05/2017.
 */

public class InventoryContract {
    public final static class InventoryEntry implements BaseColumns {
        public final static String TABLE_NAME = "inventory";
        public final static String COLUMN_ID = BaseColumns._ID;
        public final static String COLUMN_PRODUCT_NAME = "name";
        public final static String COLUMN_PRODUCT_PRICE = "price";
        public final static String COLUMN_QUANTITY = "qty";
        public final static String COLUMN_PRODUCT_DESCRIPTION = "description";
    }
}
