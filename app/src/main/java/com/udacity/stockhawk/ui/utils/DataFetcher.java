package com.udacity.stockhawk.ui.utils;

import android.content.Context;
import android.database.Cursor;
import android.util.Pair;

import com.udacity.stockhawk.data.Contract;

/**
 * Created by heider on 10/05/17.
 */

public class DataFetcher {

  public static Pair<Pair<Pair<String,String>,String>,String>  fetchDataOf(Context context,String symbol){
    Cursor cursor = context.getContentResolver().query(Contract.Quote.makeUriForStock(symbol), Contract.Quote.QUOTE_COLUMNS.toArray(new String[]{}), Contract.Quote.COLUMN_SYMBOL, null, null);
    String history="",price="",change="",abschange = "";
    if (cursor.moveToFirst()){
      do{
        abschange= cursor.getString(cursor.getColumnIndex(Contract.Quote.COLUMN_ABSOLUTE_CHANGE));
        change= cursor.getString(cursor.getColumnIndex(Contract.Quote.COLUMN_PERCENTAGE_CHANGE));
        price= cursor.getString(cursor.getColumnIndex(Contract.Quote.COLUMN_PRICE));
        history = cursor.getString(cursor.getColumnIndex(Contract.Quote.COLUMN_HISTORY));
      }while(cursor.moveToNext());
    }
    cursor.close();
    return Pair.create(Pair.create(Pair.create(history,price),change),abschange);
  }

}
