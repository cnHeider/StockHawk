package com.udacity.stockhawk.ui;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.util.Log;
import android.widget.RemoteViews;

import com.udacity.stockhawk.R;
import com.udacity.stockhawk.sync.QuoteSyncJob;

/**
 * Implementation of App Widget functionality.
 */
public class StockCollectionWidgetProvider extends AppWidgetProvider {
  private static final String TAG = StockCollectionWidgetProvider.class.getCanonicalName();
  public static final String TOAST_ACTION = "com.udacity.stockhawk.TOAST_ACTION";
  public static final String EXTRA_ITEM = "com.udacity.stockhawk.EXTRA_ITEM";

  @Override
  public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
    Log.d(TAG, "onUpdate: ");
    // There may be multiple widgets active, so update all of them
    for (int appWidgetId : appWidgetIds) {
      Log.d(TAG, "onUpdate loop: " + appWidgetId);
      updateAppWidget(context, appWidgetManager, appWidgetId);
    }
  }

  @Override
  public void onReceive(Context context, Intent intent) {
    super.onReceive(context, intent);
    String strAction = intent.getAction();
    if (QuoteSyncJob.ACTION_DATA_UPDATED.equals(strAction)) {
        updateWidgets(context);
    }
  }

  public void updateWidgets(Context context) {
    Intent intent = new Intent(context.getApplicationContext(), StockCollectionWidgetProvider.class);
    intent.setAction(AppWidgetManager.ACTION_APPWIDGET_UPDATE);
    AppWidgetManager widgetManager = AppWidgetManager.getInstance(context);
    int[] ids = widgetManager.getAppWidgetIds(new ComponentName(context, StockCollectionWidgetProvider.class));

    Log.d(TAG, "updateWidgets: ");

    intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS, ids);
    context.sendBroadcast(intent);
    onUpdate(context,widgetManager,ids);
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


    Log.d(TAG, "updateAppWidget: ");
    Intent intent = new Intent(context, StockCollectionWidgetService.class);

    RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.stock_collection_widget);

    intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId);
    intent.setData(Uri.parse(intent.toUri(Intent.URI_INTENT_SCHEME))
    );
    views.setRemoteAdapter(R.id.stock_widget_list, intent);
    views.setEmptyView(R.id.stock_widget_list, R.id.empty_view);
    Intent toastIntent = new Intent(context, StockCollectionWidgetProvider.class);
    toastIntent.setAction(StockCollectionWidgetProvider.TOAST_ACTION);
    toastIntent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId);
    intent.setData(Uri.parse(intent.toUri(Intent.URI_INTENT_SCHEME)));
    PendingIntent toastPendingIntent = PendingIntent.getBroadcast(context, 0, toastIntent,
        PendingIntent.FLAG_UPDATE_CURRENT);
    views.setPendingIntentTemplate(R.id.stock_widget_list, toastPendingIntent);

    //Intent onclickintent = new Intent(context, MainActivity.class);
    //PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, onclickintent, 0);

   // views.setOnClickPendingIntent(R.id.stock_widget_list, pendingIntent);

    // Instruct the widget manager to update the widget
    appWidgetManager.updateAppWidget(appWidgetId, views);
    appWidgetManager.notifyAppWidgetViewDataChanged(appWidgetId, R.id.stock_widget_list);
  }
}

