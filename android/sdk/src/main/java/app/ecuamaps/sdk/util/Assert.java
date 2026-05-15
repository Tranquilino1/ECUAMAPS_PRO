package app.ecuamaps.sdk.util;

import app.ecuamaps.sdk.BuildConfig;

public class Assert
{
  public static void debug(boolean condition, String message)
  {
    if (BuildConfig.DEBUG && !condition)
      throw new AssertionError(message);
  }

  public static void always(boolean condition, String message)
  {
    if (!condition)
      throw new AssertionError(message);
  }
}
