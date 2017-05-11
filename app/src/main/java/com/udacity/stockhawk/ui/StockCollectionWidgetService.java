package com.udacity.stockhawk.ui;

import android.appwidget.AppWidgetManager;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService;

import com.udacity.stockhawk.R;
import com.udacity.stockhawk.data.Contract;
import com.udacity.stockhawk.ui.utils.Formatters;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by heider on 10/05/17.
 */

public class StockCollectionWidgetService extends RemoteViewsService {

  @Override
  public RemoteViewsFactory onGetViewFactory(Intent intent) {
    return new StockCollectionRemoteViewsFactory(this.getApplicationContext(), intent);
  }
}

class StockCollectionRemoteViewsFactory implements RemoteViewsService.RemoteViewsFactory {

  private static final String TAG = StockCollectionRemoteViewsFactory.class.getCanonicalName();

  private List<StockWidgetItem> mWidgetItems = new ArrayList<>();
  private Context mContext;
  private int mAppWidgetId;
  private Formatters formatters;

  public StockCollectionRemoteViewsFactory(Context context, Intent intent){
    mContext = context;
    mAppWidgetId = intent.getIntExtra(AppWidgetManager.EXTRA_APPWIDGET_ID,
        AppWidgetManager.INVALID_APPWIDGET_ID);
  }

  @Override
  public void onCreate() {

    formatters = new Formatters();
    loadData();
  }

  private void loadData(){
    Log.d(TAG, "loadData: ");
    mWidgetItems.clear();
    Cursor cursor = mContext.getContentResolver().query(Contract.Quote.URI, Contract.Quote.QUOTE_COLUMNS.toArray(new String[]{}), null, null, null);
    if (cursor.moveToFirst()){
      do{
        mWidgetItems.add(new StockWidgetItem(
            cursor.getString(cursor.getColumnIndex(Contract.Quote.COLUMN_SYMBOL)),
            cursor.getString(cursor.getColumnIndex(Contract.Quote.COLUMN_PRICE)),
            cursor.getString(cursor.getColumnIndex(Contract.Quote.COLUMN_PERCENTAGE_CHANGE))));
      }while(cursor.moveToNext());
    }
    cursor.close();
  }

  @Override
  public void onDataSetChanged() {
    Log.d(TAG, "onDataSetChanged: ");
    loadData();
  }

  @Override
  public void onDestroy() {

  }

  @Override
  public int getCount() {
    return mWidgetItems.size();
  }

  @Override
  public RemoteViews getViewAt(int position) {

    RemoteViews rv = new RemoteViews(mContext.getPackageName(), R.layout.list_item_widget);
    String symbol = mWidgetItems.get(position).symbol;
    rv.setTextViewText(R.id.symbol,symbol);
    String price_str = mWidgetItems.get(position).price;
    String change_str = mWidgetItems.get(position).change;

    float change,price;
    try {
      change = Float.valueOf(change_str);
      price = Float.valueOf(price_str);
    }catch (NumberFormatException e) {
      change = 0;
      price = 0;
    }
    if (change>=0) {
      rv.setTextColor(R.id.change, Color.GREEN);
    }
    else{
      rv.setTextColor(R.id.change, Color.RED);
    }
    rv.setTextViewText(R.id.change, formatters.percentageFormat.format(change/ 100));
    rv.setTextViewText(R.id.price, formatters.dollarFormatWithPlus.format(price));

    Bundle extras = new Bundle();
    extras.putInt(StockCollectionWidgetProvider.EXTRA_ITEM, position);
    Intent fillInIntent = new Intent();
    fillInIntent.putExtras(extras);
    rv.setOnClickFillInIntent(R.id.stock_list_item, fillInIntent);

    //Intent configIntent = new Intent(mContext, DetailActivity.class);
    //configIntent.putExtra(DetailActivity.SYMBOL_EXTRA, symbol);

    //PendingIntent configPendingIntent = PendingIntent.getActivity(mContext, 0, configIntent, 0);

    //rv.setOnClickPendingIntent(R.id.symbol,configPendingIntent);

    return rv;
  }

  @Override
  public RemoteViews getLoadingView() {
    return null;
  }

  @Override
  public int getViewTypeCount() {
    return 1;
  }

  @Override
  public long getItemId(int position) {
    return position;
  }

  @Override
  public boolean hasStableIds() {
    return true;
  }

}
