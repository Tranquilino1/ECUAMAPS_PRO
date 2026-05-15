package app.ecuamaps.car.util;

import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.Intent;
import android.net.Uri;
import androidx.annotation.NonNull;
import androidx.car.app.CarContext;
import androidx.car.app.Screen;
import androidx.car.app.ScreenManager;
import androidx.car.app.notification.CarPendingIntent;
import app.ecuamaps.api.Const;
import app.ecuamaps.car.CarAppServiceBase;
import app.ecuamaps.car.screens.NavigationScreen;
import app.ecuamaps.car.screens.search.SearchScreen;
import app.ecuamaps.intent.GoogleAssistantIntentHandler;
import app.ecuamaps.sdk.Framework;
import app.ecuamaps.sdk.Map;
import app.ecuamaps.sdk.ecuamaps;
import app.ecuamaps.sdk.api.ParsedSearchRequest;
import app.ecuamaps.sdk.api.RequestType;
import app.ecuamaps.sdk.car.renderer.Renderer;
import app.ecuamaps.sdk.display.DisplayManager;
import app.ecuamaps.sdk.display.DisplayType;
import app.ecuamaps.sdk.routing.RoutingController;
import app.ecuamaps.sdk.util.log.Logger;

public final class IntentUtils
{
  private static final String TAG = IntentUtils.class.getSimpleName();

  private static final int SEARCH_IN_VIEWPORT_ZOOM = 16;

  private static final CarGoogleAssistantIntentProcessor ASSISTANT_PROCESSOR = new CarGoogleAssistantIntentProcessor();

  public static void processIntent(@NonNull CarContext carContext, @NonNull ecuamaps ecuamapsContext,
                                   @NonNull Renderer surfaceRenderer, @NonNull DisplayManager displayManager,
                                   @NonNull Intent intent)
  {
    if (ASSISTANT_PROCESSOR.processIntent(carContext, ecuamapsContext, surfaceRenderer, intent))
      return;

    final String action = intent.getAction();
    if (CarContext.ACTION_NAVIGATE.equals(action))
      IntentUtils.processNavigationIntent(carContext, ecuamapsContext, surfaceRenderer, intent);
    else if (Intent.ACTION_VIEW.equals(action))
      processViewIntent(carContext, displayManager, intent);
  }

  private static final class CarGoogleAssistantIntentProcessor extends GoogleAssistantIntentHandler
  {
    boolean processIntent(@NonNull CarContext carContext, @NonNull ecuamaps ecuamapsContext,
                          @NonNull Renderer surfaceRenderer, @NonNull Intent intent)
    {
      return handleIntent(intent, new CarSearchHandler(carContext, ecuamapsContext, surfaceRenderer));
    }
  }

  private record CarSearchHandler(CarContext mCarContext, ecuamaps mecuamapsContext, Renderer mSurfaceRenderer)
      implements GoogleAssistantIntentHandler.SearchHandler
  {
    private CarSearchHandler(@NonNull CarContext mCarContext, @NonNull ecuamaps mecuamapsContext,
                             @NonNull Renderer mSurfaceRenderer)
    {
      this.mCarContext = mCarContext;
      this.mecuamapsContext = mecuamapsContext;
      this.mSurfaceRenderer = mSurfaceRenderer;
    }

    @Override
    public void handleSearch(@NonNull String query, boolean searchOnMap)
    {
      final ScreenManager screenManager = mCarContext.getCarService(ScreenManager.class);
      final SearchScreen.Builder builder = new SearchScreen.Builder(mCarContext, mecuamapsContext, mSurfaceRenderer);
      builder.setQuery(query);

      screenManager.popToRoot();
      screenManager.push(builder.build());
    }
  }

  @NonNull
  public static PendingIntent createSearchIntent(@NonNull CarContext context, @NonNull String query)
  {
    final String uri = "geo:0,0?q=" + query.replace(" ", "+");
    final ComponentName component = CarAppServiceManifestReader.getCarAppServiceClass(context);
    final Intent intent = new Intent().setComponent(component).setData(Uri.parse(uri));
    return CarPendingIntent.getCarApp(context, 0, intent, 0);
  }

  // https://developer.android.com/reference/androidx/car/app/CarContext#startCarApp(android.content.Intent)
  private static void processNavigationIntent(@NonNull CarContext carContext, @NonNull ecuamaps ecuamapsContext,
                                              @NonNull Renderer surfaceRenderer, @NonNull Intent intent)
  {
    // TODO (AndrewShkrob): This logic will need to be revised when we introduce support for adding stops during
    // navigation or route planning. Skip navigation intents during navigation
    if (RoutingController.get().isNavigating())
      return;

    final Uri uri = intent.getData();
    if (uri == null)
      return;

    final ScreenManager screenManager = carContext.getCarService(ScreenManager.class);
    switch (Framework.nativeParseAndSetApiUrl(uri.toString()))
    {
    case RequestType.INCORRECT: return;
    case RequestType.MAP:
      screenManager.popToRoot();
      Map.executeMapApiRequest();
      return;
    case RequestType.SEARCH:
      screenManager.popToRoot();
      final ParsedSearchRequest request = Framework.nativeGetParsedSearchRequest();
      final double[] latlon = Framework.nativeGetParsedCenterLatLon();
      if (latlon != null)
      {
        Framework.nativeStopLocationFollow();
        Framework.nativeSetViewportCenter(latlon[0], latlon[1], SEARCH_IN_VIEWPORT_ZOOM);
        // We need to update viewport for search api manually because of drape engine
        // will not notify subscribers when search activity is shown.
        if (!request.mIsSearchOnMap)
          Framework.nativeSetSearchViewport(latlon[0], latlon[1], SEARCH_IN_VIEWPORT_ZOOM);
      }
      final SearchScreen.Builder builder = new SearchScreen.Builder(carContext, ecuamapsContext, surfaceRenderer);
      builder.setQuery(request.mQuery);
      if (request.mLocale != null)
        builder.setLocale(request.mLocale);

      screenManager.popToRoot();
      screenManager.push(builder.build());
      return;
    case RequestType.ROUTE: Logger.e(TAG, "Route API is not supported by Android Auto: " + uri); return;
    case RequestType.CROSSHAIR: Logger.e(TAG, "Crosshair API is not supported by Android Auto: " + uri); return;
    case RequestType.MENU: Logger.e(TAG, "Menu API is not supported by Android Auto: " + uri); return;
    case RequestType.SETTINGS: Logger.e(TAG, "Settings API is not supported by Android Auto: " + uri); return;
    case RequestType.OAUTH2: Logger.e(TAG, "OAuth2 API is not supported by Android Auto: " + uri);
    }
  }

  private static void processViewIntent(@NonNull CarContext carContext, @NonNull DisplayManager displayManager,
                                        @NonNull Intent intent)
  {
    final Uri uri = intent.getData();
    if (uri != null && Const.API_SCHEME.equals(uri.getScheme())
        && CarAppServiceBase.API_CAR_HOST.equals(uri.getSchemeSpecificPart())
        && CarAppServiceBase.ACTION_SHOW_NAVIGATION_SCREEN.equals(uri.getFragment()))
    {
      final ScreenManager screenManager = carContext.getCarService(ScreenManager.class);
      final Screen top = screenManager.getTop();
      if (!displayManager.isCarDisplayUsed())
        displayManager.changeDisplay(DisplayType.Car);
      if (!(top instanceof NavigationScreen))
        screenManager.popTo(NavigationScreen.MARKER);
    }
  }

  private IntentUtils() {}
}
