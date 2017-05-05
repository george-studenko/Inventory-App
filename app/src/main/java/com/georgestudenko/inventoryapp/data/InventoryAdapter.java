package com.georgestudenko.inventoryapp.data;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.TextView;

import com.georgestudenko.inventoryapp.R;
import com.georgestudenko.inventoryapp.model.InventoryItem;

/**
 * Created by george on 04/05/2017.
 */

public class InventoryAdapter extends CursorAdapter {

    public InventoryAdapter(Context context, Cursor c, boolean autoRequery) {
        super(context, c, autoRequery);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        View view = LayoutInflater.from(context).inflate(R.layout.inventory_item,parent,false);
        return view;
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {

    }
}
