package app.ecuamaps.car;

import android.content.Intent;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.car.app.Screen;
import androidx.car.app.SessionInfo;
import androidx.lifecycle.LifecycleOwner;
import app.ecuamaps.MwmApplication;
import app.ecuamaps.R;
import app.ecuamaps.car.screens.ErrorScreen;
import app.ecuamaps.car.screens.MapPlaceholderScreen;
import app.ecuamaps.car.screens.MapScreen;
import app.ecuamaps.car.screens.download.DownloadMapsScreenBuilder;
import app.ecuamaps.car.screens.download.DownloaderHelpers;
import app.ecuamaps.car.screens.permissions.RequestPermissionsScreenBuilder;
import app.ecuamaps.car.util.IntentUtils;
import app.ecuamaps.car.util.UserActionRequired;
import app.ecuamaps.sdk.ecuamaps;
import app.ecuamaps.sdk.display.DisplayChangedListener;
import app.ecuamaps.sdk.display.DisplayType;
import app.ecuamaps.sdk.location.LocationUtils;
import app.ecuamaps.sdk.util.Assert;
import app.ecuamaps.sdk.util.log.Logger;
import java.util.ArrayList;
import java.util.List;

public final class AndroidAutoSession extends CarAppSessionBase implements DisplayChangedListener
{
  private static final String TAG = AndroidAutoSession.class.getSimpleName();

  private final boolean mInitFailed;

  public AndroidAutoSession(@NonNull ecuamaps ecuamapsContext, @Nullable SessionInfo sessionInfo, boolean isDebug,
                            boolean initFailed)
  {
    super(ecuamapsContext, sessionInfo, isDebug);
    mInitFailed = initFailed;
  }

  @Override
  public void onNewIntent(@NonNull Intent intent)
  {
    Logger.d(TAG, intent.toString());
    Assert.debug(mDisplayManager != null, "mDisplayManager is null");
    IntentUtils.processIntent(getCarContext(), mecuamapsContext, mSurfaceRenderer, mDisplayManager, intent);
  }

  @Override
  public void onCreate(@NonNull LifecycleOwner owner)
  {
    super.onCreate(owner);
    mDisplayManager = MwmApplication.from(getCarContext()).getDisplayManager();
    mDisplayManager.addListener(DisplayType.Car, this);
  }

  @Override
  public void onDestroy(@NonNull LifecycleOwner owner)
  {
    super.onDestroy(owner);
    Assert.debug(mDisplayManager != null, "mDisplayManager is null");
    mDisplayManager.removeListener(DisplayType.Car);
  }

  @NonNull
  protected Screen prepareScreens()
  {
    if (mInitFailed)
      return new ErrorScreen.Builder(getCarContext(), mecuamapsContext)
          .setErrorMessage(R.string.dialog_error_storage_message)
          .build();

    final List<Screen> screensStack = new ArrayList<>();
    screensStack.add(new MapScreen(getCarContext(), mecuamapsContext, mSurfaceRenderer));

    if (DownloaderHelpers.isWorldMapsDownloadNeeded(mecuamapsContext.getFlavor()))
    {
      mScreenManager.push(new DownloadMapsScreenBuilder(getCarContext(), mecuamapsContext)
                              .setDownloaderType(DownloadMapsScreenBuilder.DownloaderType.FirstLaunch)
                              .build());
    }

    if (!LocationUtils.checkFineLocationPermission(getCarContext()))
      screensStack.add(
          RequestPermissionsScreenBuilder.build(getCarContext(), mecuamapsContext, mSensorsManager::onStart));

    Assert.debug(mDisplayManager != null, "mDisplayManager is null");
    if (mDisplayManager.isDeviceDisplayUsed())
    {
      mSurfaceRenderer.disable();
      onStop(this);
      screensStack.add(new MapPlaceholderScreen(getCarContext(), mecuamapsContext));
    }

    for (int i = 0; i < screensStack.size() - 1; i++)
      mScreenManager.push(screensStack.get(i));

    return screensStack.get(screensStack.size() - 1);
  }

  @Override
  public void onDisplayChangedToDevice(@NonNull Runnable onTaskFinishedCallback)
  {
    Logger.d(TAG);
    final Screen topScreen = mScreenManager.getTop();
    onStop(this);
    mSurfaceRenderer.disable();

    final MapPlaceholderScreen mapPlaceholderScreen = new MapPlaceholderScreen(getCarContext(), mecuamapsContext);
    if (topScreen instanceof UserActionRequired)
      mScreenManager.popToRoot();

    mScreenManager.push(mapPlaceholderScreen);

    onTaskFinishedCallback.run();
  }

  @Override
  public void onDisplayChangedToCar(@NonNull Runnable onTaskFinishedCallback)
  {
    Logger.d(TAG);
    onStart(this);
    mSurfaceRenderer.enable();

    if (mScreenManager.getTop() instanceof MapPlaceholderScreen)
      mScreenManager.pop();

    onTaskFinishedCallback.run();
  }

  @Override
  protected boolean isCarScreenUsed()
  {
    Assert.debug(mDisplayManager != null, "mDisplayManager is null");
    return mDisplayManager.isCarDisplayUsed();
  }
}
