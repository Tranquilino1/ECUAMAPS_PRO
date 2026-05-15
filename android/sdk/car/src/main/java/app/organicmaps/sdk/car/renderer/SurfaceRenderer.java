package app.ecuamaps.sdk.car.renderer;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.car.app.CarContext;
import androidx.lifecycle.LifecycleOwner;
import app.ecuamaps.sdk.MapController;
import app.ecuamaps.sdk.MapView;
import app.ecuamaps.sdk.display.DisplayManager;
import app.ecuamaps.sdk.display.DisplayType;
import app.ecuamaps.sdk.location.LocationHelper;

@RequiresApi(23)
final class SurfaceRenderer extends RendererImpl
{
  @NonNull
  private final MapController mMapController;

  @NonNull
  private final SurfaceCallback mSurfaceCallback;

  public SurfaceRenderer(@NonNull CarContext carContext, @NonNull DisplayManager displayManager,
                         @NonNull LocationHelper locationHelper, @NonNull LifecycleOwner lifecycleOwner)
  {
    super(carContext, displayManager, locationHelper, lifecycleOwner);

    mMapController = new MapController(new MapView(carContext, DisplayType.Car), locationHelper,
                                       getMapRenderingListener(), null, false);
    mLifecycleOwner.getLifecycle().addObserver(mMapController);
    mSurfaceCallback = new SurfaceCallback(mCarContext, mMapController);
    setSurfaceCallback(mSurfaceCallback);
  }

  @Override
  public void enable()
  {
    super.enable();

    mMapController.onStart(mLifecycleOwner);
    mMapController.updateMyPositionRoutingOffset(0);
    mSurfaceCallback.startPresenting();
  }

  @Override
  public void disable()
  {
    super.disable();

    mMapController.onPause(mLifecycleOwner);
    mSurfaceCallback.stopPresenting();
    mMapController.onStop(mLifecycleOwner);
  }

  @Override
  public void setSpeedLimit(int speedLimit, boolean speedLimitExceeded)
  {
    mSurfaceCallback.getSpeedLimitView().setSpeedLimit(speedLimit, speedLimitExceeded);
  }
}
