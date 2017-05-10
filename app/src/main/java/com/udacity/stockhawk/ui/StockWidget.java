package com.udacity.stockhawk.ui;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.util.Pair;
import android.widget.RemoteViews;

import com.udacity.stockhawk.R;
import com.udacity.stockhawk.data.Contract;
import com.udacity.stockhawk.ui.utils.DataFetcher;
import com.udacity.stockhawk.ui.utils.Formatters;

import java.util.ArrayList;
import java.util.List;

/**
 * Implementation of App Widget functionality.
 */
public class StockWidget extends AppWidgetProvider {

  @Override
  public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
    // There may be multiple widgets active, so update all of them
    for (int appWidgetId : appWidgetIds) {
      updateAppWidget(context, appWidgetManager, appWidgetId);
    }
  }

  @Override
  public void onDeleted(Context context, int[] appWidgetIds) {
    // When the user deletes the widget, delete the preference associated with it.
    for (int appWidgetId : appWidgetIds) {
      StockWidgetConfigureActivity.deleteTitlePref(context, appWidgetId);
    }
  }

  @Override
  public void onEnabled(Context context) {
    // Enter relevant functionality for when the first widget is created
  }

  @Override
  public void onDisabled(Context context) {
    // Enter relevant functionality for when the last widget is disabled
  }

  static void updateAppWidget(Context context, AppWidgetManager appWidgetManager, int appWidgetId) {

    String symbol = StockWidgetConfigureActivity.loadTitlePref(context,appWidgetId);

    Pair<Pair<Pair<String,String>,String>,String> data = DataFetcher.fetchDataOf(context,symbol);

    Formatters formatters = new Formatters();

    // Construct the RemoteViews object
    RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.stock_widget);
    views.setTextViewText(R.id.symbol, symbol);
    float change,price;
    try {
      change = Float.valueOf(data.first.second);
      price = Float.valueOf(data.first.first.second);
    }catch (NumberFormatException e) {
      change = 0;
      price = 0;
    }
    if (change>=0) {
      views.setTextColor(R.id.change, Color.GREEN);
    }
    else{
      views.setTextColor(R.id.change, Color.RED);
    }
    views.setTextViewText(R.id.change, formatters.percentageFormat.format(change/ 100));
    views.setTextViewText(R.id.price, formatters.dollarFormatWithPlus.format(price));

    Intent configIntent = new Intent(context, DetailActivity.class);
    configIntent.putExtra(DetailActivity.SYMBOL_EXTRA, symbol);

    PendingIntent configPendingIntent = PendingIntent.getActivity(context, 0, configIntent, 0);

    views.setOnClickPendingIntent(R.id.widget_layout,configPendingIntent);

    // Instruct the widget manager to update the widget
    appWidgetManager.updateAppWidget(appWidgetId, views);
  }
}

