package app.ecuamaps.sdk.content;

import androidx.annotation.NonNull;

public interface DataSource<D>
{
  @NonNull
  D getData();

  void invalidate();
}
