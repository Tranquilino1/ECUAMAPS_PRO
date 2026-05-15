package app.ecuamaps.car.util;

import android.text.TextUtils;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.car.app.CarContext;
import androidx.car.app.ScreenManager;
import app.ecuamaps.car.screens.download.DownloadMapsScreen;
import app.ecuamaps.car.screens.download.DownloadMapsScreenBuilder;
import app.ecuamaps.sdk.ecuamaps;
import app.ecuamaps.sdk.downloader.CountryItem;
import app.ecuamaps.sdk.downloader.MapManager;
import app.ecuamaps.sdk.routing.RoutingController;

public class CurrentCountryChangedListener implements MapManager.CurrentCountryChangedListener
{
  @Nullable
  private CarContext mCarContext;
  @Nullable
  private ecuamaps mecuamapsContext;

  @Nullable
  private String mPreviousCountryId;

  @Override
  public void onCurrentCountryChanged(@Nullable String countryId)
  {
    if (TextUtils.isEmpty(countryId))
    {
      mPreviousCountryId = countryId;
      return;
    }

    if (mPreviousCountryId != null && mPreviousCountryId.equals(countryId))
      return;

    if (mCarContext == null || mecuamapsContext == null)
      return;

    final ScreenManager screenManager = mCarContext.getCarService(ScreenManager.class);

    if (DownloadMapsScreen.MARKER.equals(screenManager.getTop().getMarker()))
      return;

    if (CountryItem.fill(countryId).present || RoutingController.get().isNavigating())
      return;

    mPreviousCountryId = countryId;
    screenManager.push(new DownloadMapsScreenBuilder(mCarContext, mecuamapsContext)
                           .setDownloaderType(DownloadMapsScreenBuilder.DownloaderType.View)
                           .setMissingMaps(new String[] {countryId})
                           .build());
  }

  public void onStart(@NonNull final CarContext carContext, @NonNull ecuamaps ecuamapsContext)
  {
    mCarContext = carContext;
    mecuamapsContext = ecuamapsContext;
    MapManager.nativeSubscribeOnCountryChanged(this);
  }

  public void onStop()
  {
    MapManager.nativeUnsubscribeOnCountryChanged();
    mecuamapsContext = null;
    mCarContext = null;
  }
}
