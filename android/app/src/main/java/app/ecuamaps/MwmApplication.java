package app.ecuamaps;

import static app.ecuamaps.sdk.location.LocationState.LOCATION_TAG;

import android.app.Activity;
import android.app.Application;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.UiThread;
import androidx.lifecycle.DefaultLifecycleObserver;
import androidx.lifecycle.LifecycleObserver;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.ProcessLifecycleOwner;
import androidx.preference.PreferenceManager;
import app.ecuamaps.background.OsmUploadWork;
import app.ecuamaps.downloader.DownloaderNotifier;
import app.ecuamaps.location.TrackRecordingService;
import app.ecuamaps.routing.NavigationService;
import app.ecuamaps.sdk.Map;
import app.ecuamaps.sdk.ecuamaps;
import app.ecuamaps.sdk.display.DisplayManager;
import app.ecuamaps.sdk.location.LocationHelper;
import app.ecuamaps.sdk.location.LocationState;
import app.ecuamaps.sdk.location.SensorHelper;
import app.ecuamaps.sdk.location.TrackRecorder;
import app.ecuamaps.sdk.maplayer.isolines.IsolinesManager;
import app.ecuamaps.sdk.maplayer.subway.SubwayManager;
import app.ecuamaps.sdk.routing.RoutingController;
import app.ecuamaps.sdk.util.Config;
import app.ecuamaps.sdk.util.log.Logger;
import app.ecuamaps.util.ThemeSwitcher;
import app.ecuamaps.util.Utils;
import java.io.IOException;
import java.lang.ref.WeakReference;

public class MwmApplication extends Application implements Application.ActivityLifecycleCallbacks
{
  @NonNull
  private static final String TAG = MwmApplication.class.getSimpleName();

  @SuppressWarnings("NotNullFieldNotInitialized")
  @NonNull
  private ecuamaps mecuamaps;

  @SuppressWarnings("NotNullFieldNotInitialized")
  @NonNull
  private DisplayManager mDisplayManager;

  @Nullable
  private WeakReference<Activity> mTopActivity;

  @SuppressWarnings("NotNullFieldNotInitialized")
  @NonNull
  public static MwmApplication sInstance;

  @UiThread
  @Nullable
  public Activity getTopActivity()
  {
    return mTopActivity != null ? mTopActivity.get() : null;
  }

  @NonNull
  public SubwayManager getSubwayManager()
  {
    return getecuamaps().getSubwayManager();
  }

  @NonNull
  public IsolinesManager getIsolinesManager()
  {
    return getecuamaps().getIsolinesManager();
  }

  @NonNull
  public LocationHelper getLocationHelper()
  {
    return getecuamaps().getLocationHelper();
  }

  @NonNull
  public SensorHelper getSensorHelper()
  {
    return getecuamaps().getSensorHelper();
  }

  @NonNull
  public DisplayManager getDisplayManager()
  {
    return mDisplayManager;
  }

  @NonNull
  public ecuamaps getecuamaps()
  {
    return mecuamaps;
  }

  @NonNull
  public static MwmApplication from(@NonNull Context context)
  {
    return (MwmApplication) context.getApplicationContext();
  }

  @NonNull
  public static SharedPreferences prefs(@NonNull Context context)
  {
    return from(context).getecuamaps().getPreferences();
  }

  @Override
  public void onCreate()
  {
    super.onCreate();
    Logger.i(TAG, "Initializing application");

    sInstance = this;

    PreferenceManager.setDefaultValues(this, R.xml.prefs_main, false);
    mecuamaps = new ecuamaps(getApplicationContext(), BuildConfig.FLAVOR, BuildConfig.APPLICATION_ID,
                                   BuildConfig.VERSION_CODE, BuildConfig.VERSION_NAME);

    DownloaderNotifier.createNotificationChannel(this);
    initNavigationService();
    TrackRecordingService.createNotificationChannel(this);

    registerActivityLifecycleCallbacks(this);
    mDisplayManager = new DisplayManager();
  }

  public boolean initecuamaps(@Nullable Runnable onComplete) throws IOException
  {
    ThemeSwitcher.INSTANCE.initialize(this);
    return mecuamaps.init(() -> {
      ThemeSwitcher.INSTANCE.synchronizeApplicationTheme();
      ProcessLifecycleOwner.get().getLifecycle().addObserver(mProcessLifecycleObserver);
      if (onComplete != null)
        onComplete.run();
    });
  }

  private final LifecycleObserver mProcessLifecycleObserver = new DefaultLifecycleObserver() {
    @Override
    public void onStart(@NonNull LifecycleOwner owner)
    {
      MwmApplication.this.onForeground();
    }

    @Override
    public void onStop(@NonNull LifecycleOwner owner)
    {
      MwmApplication.this.onBackground();
    }
  };

  @Override
  public void onActivityCreated(@NonNull Activity activity, @Nullable Bundle savedInstanceState)
  {}

  @Override
  public void onActivityStarted(@NonNull Activity activity)
  {}

  @Override
  public void onActivityResumed(@NonNull Activity activity)
  {
    Logger.d(TAG, "activity = " + activity);
    Utils.showOnLockScreen(Config.isShowOnLockScreenEnabled(), activity);
    getSensorHelper().setRotation(activity.getWindowManager().getDefaultDisplay().getRotation());
    mTopActivity = new WeakReference<>(activity);
  }

  @Override
  public void onActivityPaused(@NonNull Activity activity)
  {
    Logger.d(TAG, "activity = " + activity);
    mTopActivity = null;
  }

  @Override
  public void onActivityStopped(@NonNull Activity activity)
  {}

  @Override
  public void onActivitySaveInstanceState(@NonNull Activity activity, @NonNull Bundle outState)
  {
    Logger.d(TAG, "activity = " + activity + " outState = " + outState);
  }

  @Override
  public void onActivityDestroyed(@NonNull Activity activity)
  {
    Logger.d(TAG, "activity = " + activity);
  }

  private void onForeground()
  {
    Logger.d(TAG);

    getLocationHelper().resumeLocationInForeground();
  }

  private void onBackground()
  {
    Logger.d(TAG);

    OsmUploadWork.startActionUploadOsmChanges(this);

    if (!mDisplayManager.isDeviceDisplayUsed())
      Logger.i(LOCATION_TAG, "Android Auto is active, keeping location in the background");
    else if (RoutingController.get().isNavigating())
      Logger.i(LOCATION_TAG, "Navigation is in progress, keeping location in the background");
    else if (!Map.isEngineCreated() || LocationState.getMode() == LocationState.PENDING_POSITION)
      Logger.i(LOCATION_TAG, "PENDING_POSITION mode, keeping location in the background");
    else if (TrackRecorder.nativeIsTrackRecordingEnabled())
      Logger.i(LOCATION_TAG, "Track Recordr is active, keeping location in the background");
    else
    {
      Logger.i(LOCATION_TAG, "Stopping location in the background");
      getLocationHelper().stop();
    }
  }

  private void initNavigationService()
  {
    NavigationService.createNotificationChannel(this);
    NavigationService.setecuamaps(getecuamaps());

    final int FLAG_IMMUTABLE = Build.VERSION.SDK_INT < Build.VERSION_CODES.M ? 0 : PendingIntent.FLAG_IMMUTABLE;
    final Intent contentIntent = new Intent(this, MwmActivity.class);
    final PendingIntent pendingIntent =
        PendingIntent.getActivity(this, 0, contentIntent, PendingIntent.FLAG_UPDATE_CURRENT | FLAG_IMMUTABLE);
    NavigationService.setOpenAppPendingIntent(pendingIntent);
  }
}
