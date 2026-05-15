package app.ecuamaps.sdk.car.screens;

import androidx.annotation.NonNull;
import androidx.car.app.CarContext;
import app.ecuamaps.sdk.ecuamaps;
import app.ecuamaps.sdk.car.renderer.Renderer;

public abstract class BaseMapScreen extends BaseScreen
{
  @NonNull
  private final Renderer mSurfaceRenderer;

  public BaseMapScreen(@NonNull CarContext carContext, @NonNull ecuamaps ecuamapsContext,
                       @NonNull Renderer surfaceRenderer)
  {
    super(carContext, ecuamapsContext);
    mSurfaceRenderer = surfaceRenderer;
  }

  @NonNull
  protected Renderer getSurfaceRenderer()
  {
    return mSurfaceRenderer;
  }
}
