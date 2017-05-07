package com.georgestudenko.inventoryapp.data;

import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.res.ColorStateList;
import android.database.Cursor;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.georgestudenko.inventoryapp.R;
import com.georgestudenko.inventoryapp.model.InventoryItem;

import static java.security.AccessController.getContext;

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
    public void bindView(View view, Context context, final Cursor cursor) {
        TextView name = (TextView) view.findViewById(R.id.itemName);
        TextView qty = (TextView) view.findViewById(R.id.itemQuantity);
        TextView price = (TextView) view.findViewById(R.id.itemPrice);
        ImageButton sellButton = (ImageButton)view.findViewById(R.id.sellListButton);

        final InventoryItem item = InventoryItem.parseInventoryItem(cursor);

        name.setText(item.getName());
        qty.setText(String.valueOf(item.getQuantity()));
        price.setText(item.getFormattedPrice());

        if(item.getQuantity()<=0){
            sellButton.setImageResource(R.drawable.cash_disabled);
            name.setTextColor(Color.RED);
            qty.setTextColor(Color.RED);
            price.setTextColor(Color.RED);

        }else {
            sellButton.setImageResource(R.drawable.cash);
            name.setTextColor(Color.DKGRAY);
            qty.setTextColor(Color.DKGRAY);
            price.setTextColor(Color.DKGRAY);
        }

        sellButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(item.getQuantity()>0) {
                    ContentValues cv = new ContentValues();
                    cv.put(InventoryContract.InventoryEntry.COLUMN_QUANTITY,item.getQuantity()-1);
                    v.getContext().getContentResolver().update(ContentUris.withAppendedId(InventoryContract.InventoryEntry.CONTENT_URI, item.getId()), cv, null, null);
                    Toast.makeText(v.getContext(),"Sold 1 unit of " + item.getName() +" for "+ item.getFormattedPrice(),Toast.LENGTH_SHORT).show();
                }else{
                    Toast.makeText(v.getContext(),"Impossible to make a sale stock is 0",Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}
