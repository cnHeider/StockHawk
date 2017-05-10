package com.udacity.stockhawk.ui;

import android.app.Activity;
import android.appwidget.AppWidgetManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.SimpleAdapter;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;
import android.widget.TextView;

import com.udacity.stockhawk.R;
import com.udacity.stockhawk.data.Contract;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;

public class StockWidgetConfigureActivity extends Activity {

  @BindView(R.id.stock_spinner)
  Spinner stock_spinner;


  @BindView(R.id.add_button)
  Button add_button;

  private static final String PREFS_NAME = "com.udacity.stockhawk.ui.StockWidget";
  private static final String PREF_PREFIX_KEY = "appwidget_";
  int mAppWidgetId = AppWidgetManager.INVALID_APPWIDGET_ID;
  SpinnerAdapter spinnerAdapter;


  View.OnClickListener mOnClickListener = new View.OnClickListener() {
    public void onClick(View v) {
      final Context context = StockWidgetConfigureActivity.this;

      // When the button is clicked, store the string locally
      String widgetText = stock_spinner.getSelectedItem().toString();
      saveTitlePref(context, mAppWidgetId, widgetText);

      AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);
      StockWidget.updateAppWidget(context, appWidgetManager, mAppWidgetId);

      Intent resultValue = new Intent();
      resultValue.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, mAppWidgetId);
      setResult(RESULT_OK, resultValue);
      finish();
    }
  };

  public StockWidgetConfigureActivity() {
    super();
  }

  // Write the prefix to the SharedPreferences object for this widget
  static void saveTitlePref(Context context, int appWidgetId, String text) {
    SharedPreferences.Editor prefs = context.getSharedPreferences(PREFS_NAME, 0).edit();
    prefs.putString(PREF_PREFIX_KEY + appWidgetId, text);
    prefs.apply();
  }

  static void deleteTitlePref(Context context, int appWidgetId) {
    SharedPreferences.Editor prefs = context.getSharedPreferences(PREFS_NAME, 0).edit();
    prefs.remove(PREF_PREFIX_KEY + appWidgetId);
    prefs.apply();
  }

  @Override
  public void onCreate(Bundle icicle) {
    super.onCreate(icicle);

    // Set the result to CANCELED.  This will cause the widget host to cancel
    // out of the widget placement if the user presses the back button.
    setResult(RESULT_CANCELED);

    setContentView(R.layout.stock_widget_configure);
    ButterKnife.bind(this);
    add_button.setOnClickListener(mOnClickListener);

    // Find the widget id from the intent.
    Intent intent = getIntent();
    Bundle extras = intent.getExtras();
    if (extras != null) {
      mAppWidgetId = extras.getInt(AppWidgetManager.EXTRA_APPWIDGET_ID, AppWidgetManager.INVALID_APPWIDGET_ID);
    }

    // If this activity was started with an intent without an app widget ID, finish with an error.
    if (mAppWidgetId == AppWidgetManager.INVALID_APPWIDGET_ID) {
      finish();
      return;
    }

    Cursor cursor = getContentResolver().query(Contract.Quote.URI, Contract.Quote.QUOTE_COLUMNS.toArray(new String[]{}), null, null, null);
    List<String> symbols= new ArrayList<>();
    if (cursor.moveToFirst()){
      do{
        symbols.add(cursor.getString(cursor.getColumnIndex(Contract.Quote.COLUMN_SYMBOL)));
      }while(cursor.moveToNext());
    }
    cursor.close();

    spinnerAdapter = new ArrayAdapter<String>(this,android.R.layout.simple_spinner_item,symbols);

    stock_spinner.setAdapter(spinnerAdapter); //.setText(loadTitlePref(StockWidgetConfigureActivity.this, mAppWidgetId));
  }

  // Read the prefix from the SharedPreferences object for this widget.
  // If there is no preference saved, get the default from a resource
  static String loadTitlePref(Context context, int appWidgetId) {
    SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, 0);
    String titleValue = prefs.getString(PREF_PREFIX_KEY + appWidgetId, null);
    if (titleValue != null) {
      return titleValue;
    } else {
      return context.getString(R.string.not_found);
    }
  }
}

