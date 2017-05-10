package com.udacity.stockhawk.ui;

import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Pair;
import android.widget.TextView;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;
import com.udacity.stockhawk.R;
import com.udacity.stockhawk.data.Contract;
import com.udacity.stockhawk.ui.utils.DataFetcher;
import com.udacity.stockhawk.ui.utils.Formatters;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class DetailActivity extends AppCompatActivity {

  public static final String SYMBOL_EXTRA = "SYMBOL_EXTRA";
  @BindView(R.id.chart)
  LineChart chart;

  @BindView(R.id.stock_name)
  TextView stock_name;
  @BindView(R.id.stock_value)
  TextView stock_value;
  @BindView(R.id.stock_change)
  TextView stock_change;
  @BindView(R.id.stock_abschange)
  TextView stock_abschange;

  private String mSymbol;
  private Formatters mFormatters;


  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_detail);
    ButterKnife.bind(this);

    mFormatters = new Formatters();

    Intent intent = getIntent();
    if (intent.hasExtra(SYMBOL_EXTRA))
      mSymbol = intent.getStringExtra(SYMBOL_EXTRA);

    updateChart();
  }

  private void updateChart() {
    long[] date_array = {1, 2, 3, 4, 5};
    float[] value_array = {1, 2, 3, 4, 5};

    List<Entry> entries = new ArrayList<>();


    if (mSymbol != null) {
      Pair<Pair<Pair<String,String>,String>,String> data= DataFetcher.fetchDataOf(this, mSymbol);
      String history = data.first.first.first;
      String price = data.first.first.second;
      String change = data.first.second;
      String abschange = data.second;

      if (Float.valueOf(abschange) > 0) {
        stock_change.setBackgroundResource(R.drawable.percent_change_pill_green);
        stock_abschange.setBackgroundResource(R.drawable.percent_change_pill_green);
      } else {
        stock_change.setBackgroundResource(R.drawable.percent_change_pill_red);
        stock_abschange.setBackgroundResource(R.drawable.percent_change_pill_red);
      }

      stock_name.setText(mSymbol);
      stock_value.setText(mFormatters.dollarFormat.format(Float.valueOf(price)));
      stock_change.setText(mFormatters.percentageFormat.format(Float.valueOf(change) / 100));
      stock_abschange.setText(mFormatters.dollarFormatWithPlus.format(Float.valueOf(abschange)));

      Pair<List<Long>,List<Float>> history_pair = processHistoryData(history);
      List<Long> history_date = history_pair.first;
      List<Float> history_value = history_pair.second;

      date_array = getLongs(history_date);
      value_array = getFloats(history_value);
    }

    int i=value_array.length-1;
    for (float data : value_array) {
      // turn your data into Entry objects
      entries.add(new Entry(date_array[i], data));
      i=i-1;
    }

    LineDataSet dataSet = new LineDataSet(entries, mSymbol); // add entries to dataset

    XAxis xAxis = chart.getXAxis();
    xAxis.setPosition(XAxis.XAxisPosition.TOP_INSIDE);
    xAxis.setTextSize(8f);
    xAxis.setGranularity(1f);
    xAxis.setValueFormatter(new IAxisValueFormatter() {

      private SimpleDateFormat mFormat = new SimpleDateFormat("dd/MM/yy");

      @Override
      public String getFormattedValue(float value, AxisBase axis) {

        long millis = (long) value;
        return mFormat.format(new Date(millis));
      }
    });

    chart.getDescription().setEnabled(false);
    chart.setContentDescription(mSymbol+ " line chart");


    LineData lineData = new LineData(dataSet);
    chart.setData(lineData);
    chart.invalidate(); // refresh
  }

  private Pair<List<Long>,List<Float>> processHistoryData(String history){
    String[] history_array = history.split("\n");
    List<Long> history_dates = new ArrayList<>();
    List<Float> history_value = new ArrayList<>();

    for(String point : history_array){
      String[] date_and_value_pair = point.split(",");
      history_value.add(Float.valueOf(date_and_value_pair[1]));
      history_dates.add(Long.valueOf(date_and_value_pair[0]));
    }
    return Pair.create(history_dates,history_value);
  }

  private static float[] getFloats(List<Float> values) {
    int length = values.size();
    float[] result = new float[length];
    for (int i = 0; i < length; i++) {
      result[i] = values.get(i).floatValue();
    }
    return result;
  }


  private static long[] getLongs(List<Long> values) {
    int length = values.size();
    long[] result = new long[length];
    for (int i = 0; i < length; i++) {
      result[i] = values.get(i).longValue();
    }
    return result;
  }

  private static int[] getInts(List<Integer> values) {
    int length = values.size();
    int[] result = new int[length];
    for (int i = 0; i < length; i++) {
      result[i] = values.get(i).intValue();
    }
    return result;
  }
}
