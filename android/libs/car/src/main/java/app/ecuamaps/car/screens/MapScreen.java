package app.ecuamaps.car.screens;

import androidx.annotation.NonNull;
import androidx.car.app.CarContext;
import androidx.car.app.model.Action;
import androidx.car.app.model.ActionStrip;
import androidx.car.app.model.CarIcon;
import androidx.car.app.model.GridItem;
import androidx.car.app.model.GridTemplate;
import androidx.car.app.model.Header;
import androidx.car.app.model.Item;
import androidx.car.app.model.ItemList;
import androidx.car.app.model.Template;
import androidx.car.app.navigation.model.MapWithContentTemplate;
import androidx.core.graphics.drawable.IconCompat;
import app.ecuamaps.car.R;
import app.ecuamaps.car.screens.bookmarks.BookmarkCategoriesScreen;
import app.ecuamaps.car.screens.search.SearchScreen;
import app.ecuamaps.car.screens.settings.SettingsScreen;
import app.ecuamaps.car.util.SuggestionsHelpers;
import app.ecuamaps.car.util.UiHelpers;
import app.ecuamaps.sdk.ecuamaps;
import app.ecuamaps.sdk.car.renderer.Renderer;
import app.ecuamaps.sdk.car.screens.BaseMapScreen;

public class MapScreen extends BaseMapScreen
{
  public MapScreen(@NonNull CarContext carContext, @NonNull ecuamaps ecuamapsContext,
                   @NonNull Renderer surfaceRenderer)
  {
    super(carContext, ecuamapsContext, surfaceRenderer);
  }

  @NonNull
  @Override
  protected Template onGetTemplateImpl()
  {
    SuggestionsHelpers.updateSuggestions(getCarContext());

    final MapWithContentTemplate.Builder builder = new MapWithContentTemplate.Builder();
    builder.setMapController(UiHelpers.createMapController(getCarContext(), getSurfaceRenderer(), getLocationHelper()));
    builder.setActionStrip(createActionStrip());
    builder.setContentTemplate(createGridTemplate());
    return builder.build();
  }

  @NonNull
  private Header createHeader()
  {
    final Header.Builder builder = new Header.Builder();
    builder.setStartHeaderAction(new Action.Builder(Action.APP_ICON).build());
    builder.setTitle(getCarContext().getString(app.ecuamaps.branding.R.string.app_name));
    return builder.build();
  }

  @NonNull
  private ActionStrip createActionStrip()
  {
    final Action.Builder freeDriveScreenBuilder = new Action.Builder();
    freeDriveScreenBuilder.setIcon(
        new CarIcon.Builder(IconCompat.createWithResource(getCarContext(), R.drawable.ic_steering_wheel)).build());
    freeDriveScreenBuilder.setOnClickListener(()
                                                  -> getScreenManager().push(new FreeDriveScreen(
                                                      getCarContext(), getecuamapsContext(), getSurfaceRenderer())));

    final ActionStrip.Builder builder = new ActionStrip.Builder();
    builder.addAction(freeDriveScreenBuilder.build());
    return builder.build();
  }

  @NonNull
  private GridTemplate createGridTemplate()
  {
    final GridTemplate.Builder builder = new GridTemplate.Builder();

    final ItemList.Builder itemsBuilder = new ItemList.Builder();
    itemsBuilder.addItem(createSearchItem());
    itemsBuilder.addItem(createCategoriesItem());
    itemsBuilder.addItem(createBookmarksItem());
    itemsBuilder.addItem(createSettingsItem());

    builder.setHeader(createHeader());
    builder.setSingleList(itemsBuilder.build());
    return builder.build();
  }

  @NonNull
  private Item createSearchItem()
  {
    final CarIcon iconSearch =
        new CarIcon.Builder(IconCompat.createWithResource(getCarContext(), R.drawable.ic_search)).build();

    final GridItem.Builder builder = new GridItem.Builder();
    builder.setTitle(getCarContext().getString(R.string.search));
    builder.setImage(iconSearch);
    builder.setOnClickListener(this::openSearch);
    return builder.build();
  }

  @NonNull
  private Item createCategoriesItem()
  {
    final CarIcon iconCategories =
        new CarIcon.Builder(IconCompat.createWithResource(getCarContext(), R.drawable.ic_address)).build();

    final GridItem.Builder builder = new GridItem.Builder();
    builder.setImage(iconCategories);
    builder.setTitle(getCarContext().getString(R.string.categories));
    builder.setOnClickListener(this::openCategories);
    return builder.build();
  }

  @NonNull
  private Item createBookmarksItem()
  {
    final CarIcon iconBookmarks =
        new CarIcon.Builder(IconCompat.createWithResource(getCarContext(), R.drawable.ic_bookmarks_and_tracks)).build();

    final GridItem.Builder builder = new GridItem.Builder();
    builder.setImage(iconBookmarks);
    builder.setTitle(getCarContext().getString(R.string.bookmarks));
    builder.setOnClickListener(this::openBookmarks);
    return builder.build();
  }

  @NonNull
  private Item createSettingsItem()
  {
    final CarIcon iconSettings =
        new CarIcon.Builder(IconCompat.createWithResource(getCarContext(), R.drawable.ic_settings)).build();

    final GridItem.Builder builder = new GridItem.Builder();
    builder.setImage(iconSettings);
    builder.setTitle(getCarContext().getString(R.string.settings));
    builder.setOnClickListener(this::openSettings);
    return builder.build();
  }

  private void openSearch()
  {
    // Details in UiHelpers.createSettingsAction()
    if (getScreenManager().getTop() != this)
      return;
    getScreenManager().push(
        new SearchScreen.Builder(getCarContext(), getecuamapsContext(), getSurfaceRenderer()).build());
  }

  private void openCategories()
  {
    // Details in UiHelpers.createSettingsAction()
    if (getScreenManager().getTop() != this)
      return;
    getScreenManager().push(new CategoriesScreen(getCarContext(), getecuamapsContext(), getSurfaceRenderer()));
  }

  private void openBookmarks()
  {
    // Details in UiHelpers.createSettingsAction()
    if (getScreenManager().getTop() != this)
      return;
    getScreenManager().push(
        new BookmarkCategoriesScreen(getCarContext(), getecuamapsContext(), getSurfaceRenderer()));
  }

  private void openSettings()
  {
    // Details in UiHelpers.createSettingsAction()
    if (getScreenManager().getTop() != this)
      return;
    getScreenManager().push(new SettingsScreen(getCarContext(), getecuamapsContext(), getSurfaceRenderer()));
  }
}
