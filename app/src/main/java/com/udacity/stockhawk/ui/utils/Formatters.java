package com.udacity.stockhawk.ui.utils;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Locale;

/**
 * Created by heider on 09/05/17.
 */

public class Formatters {

  public final DecimalFormat dollarFormatWithPlus = (DecimalFormat) NumberFormat
      .getCurrencyInstance(Locale.US);
  public final DecimalFormat dollarFormat = (DecimalFormat) NumberFormat.getCurrencyInstance(Locale
      .US);
  public final DecimalFormat percentageFormat =(DecimalFormat) NumberFormat.getPercentInstance(Locale.getDefault());

  public Formatters() {
    dollarFormatWithPlus.setPositivePrefix("+$");
    percentageFormat.setMaximumFractionDigits(2);
    percentageFormat.setMinimumFractionDigits(2);
    percentageFormat.setPositivePrefix("+");
  }
}
