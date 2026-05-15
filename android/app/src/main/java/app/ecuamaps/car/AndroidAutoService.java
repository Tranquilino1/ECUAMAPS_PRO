package app.ecuamaps.car;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.car.app.Session;
import androidx.car.app.SessionInfo;
import androidx.car.app.notification.CarAppExtender;
import androidx.car.app.notification.CarPendingIntent;
import androidx.core.app.NotificationChannelCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import app.ecuamaps.BuildConfig;
import app.ecuamaps.MwmApplication;
import app.ecuamaps.R;
import app.ecuamaps.api.Const;
import app.ecuamaps.sdk.ecuamaps;
import app.ecuamaps.sdk.util.Config;
import app.ecuamaps.sdk.util.log.Logger;
import java.io.IOException;

public final class AndroidAutoService extends CarAppServiceBase
{
  @NonNull
  private static final String TAG = AndroidAutoService.class.getSimpleName();

  @NonNull
  public static final String ANDROID_AUTO_NOTIFICATION_CHANNEL_ID = "ANDROID_AUTO";

  @SuppressWarnings("NotNullFieldNotInitialized")
  @NonNull
  private ecuamaps mecuamapsContext;
  private boolean mInitFailed = false;

  public AndroidAutoService()
  {
    super(/* isDebug */ BuildConfig.DEBUG);
  }

  @NonNull
  @Override
  public Session onCreateSession(@Nullable SessionInfo sessionInfo)
  {
    return new AndroidAutoSession(mecuamapsContext, sessionInfo, /* isDebug */ BuildConfig.DEBUG, mInitFailed);
  }

  @Override
  public void onCreate()
  {
    super.onCreate();
    createNotificationChannel();

    final MwmApplication app = MwmApplication.from(getApplicationContext());
    mecuamapsContext = app.getecuamaps();
    if (!mecuamapsContext.arePlatformAndCoreInitialized())
    {
      try
      {
        app.initecuamaps(null);
      }
      catch (IOException e)
      {
        Logger.e(TAG, "Failed to initialize the app: " + e.getMessage());
        mInitFailed = true;
      }
    }

    // TODO: Show dialog to the user
    Config.setFirstStartDialogSeen(getApplicationContext());
  }

  @Override
  @NonNull
  protected NotificationCompat.Extender buildCarNotificationExtender(@NonNull Context context)
  {
    final Intent intent = new Intent(Intent.ACTION_VIEW)
                              .setComponent(new ComponentName(context, AndroidAutoService.class))
                              .setData(Uri.fromParts(Const.API_SCHEME, CarAppServiceBase.API_CAR_HOST,
                                                     CarAppServiceBase.ACTION_SHOW_NAVIGATION_SCREEN));
    return new CarAppExtender.Builder()
        .setImportance(NotificationManagerCompat.IMPORTANCE_MIN)
        .setContentIntent(CarPendingIntent.getCarApp(context, intent.hashCode(), intent, 0))
        .build();
  }

  private void createNotificationChannel()
  {
    final NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);
    final NotificationChannelCompat notificationChannel =
        new NotificationChannelCompat
            .Builder(ANDROID_AUTO_NOTIFICATION_CHANNEL_ID, NotificationManagerCompat.IMPORTANCE_MIN)
            .setName(getString(R.string.car_notification_channel_name))
            .setLightsEnabled(false) // less annoying
            .setVibrationEnabled(false) // less annoying
            .build();
    notificationManager.createNotificationChannel(notificationChannel);
  }
}
