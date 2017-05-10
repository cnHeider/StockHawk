package com.udacity.stockhawk.ui;

/**
 * Created by heider on 10/05/17.
 */

public class StockWidgetItem {
  public String symbol;
  public String price;
  public String change;
  public StockWidgetItem(String symbol,String price,String change) {
    this.symbol = symbol;
    this.price = price;
    this.change = change;
  }
}
