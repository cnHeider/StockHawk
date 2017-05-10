package com.udacity.stockhawk.ui;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.widget.RemoteViews;

import com.udacity.stockhawk.R;

/**
 * Implementation of App Widget functionality.
 */
public class StockCollectionWidget extends AppWidgetProvider {
  public static final String TOAST_ACTION = "TOAST_ACTION";
  public static final String EXTRA_ITEM = "EXTRA_ITEM";

  private StockAdapter adapter;

  @Override
  public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
    // There may be multiple widgets active, so update all of them
    for (int appWidgetId : appWidgetIds) {
      updateAppWidget(context, appWidgetManager, appWidgetId);
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

    Intent intent = new Intent(context, StockCollectionWidgetService.class);

    RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.stock_collection_widget);

    intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId);
    intent.setData(Uri.parse(intent.toUri(Intent.URI_INTENT_SCHEME))
    );
    views.setRemoteAdapter(R.id.stock_widget_list, intent);
    views.setEmptyView(R.id.stock_widget_list, R.id.empty_view);
    Intent toastIntent = new Intent(context, StockCollectionWidget.class);
    toastIntent.setAction(StockCollectionWidget.TOAST_ACTION);
    toastIntent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId);
    intent.setData(Uri.parse(intent.toUri(Intent.URI_INTENT_SCHEME)));
    PendingIntent toastPendingIntent = PendingIntent.getBroadcast(context, 0, toastIntent,
        PendingIntent.FLAG_UPDATE_CURRENT);
    views.setPendingIntentTemplate(R.id.stock_widget_list, toastPendingIntent);

    // Instruct the widget manager to update the widget
    appWidgetManager.updateAppWidget(appWidgetId, views);
  }
}

