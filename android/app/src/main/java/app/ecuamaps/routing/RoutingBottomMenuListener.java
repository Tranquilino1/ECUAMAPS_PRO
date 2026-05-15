package app.ecuamaps.routing;

import androidx.annotation.NonNull;
import app.ecuamaps.sdk.routing.RouteMarkType;

public interface RoutingBottomMenuListener
{
  void onUseMyPositionAsStart();
  void onSearchRoutePoint(@NonNull RouteMarkType type);
  void onRoutingStart();
  void onManageRouteOpen();
}
