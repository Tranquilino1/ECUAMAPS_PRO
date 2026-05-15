cmake_minimum_required(VERSION 3.22.1)

project(ecuamaps C CXX)

set(SRC
  # JNI headers
  ../../../../../private.h
  app/ecuamaps/sdk/core/jni_helper.hpp
  app/ecuamaps/sdk/core/jni_java_methods.hpp
  app/ecuamaps/sdk/core/logging.hpp
  app/ecuamaps/sdk/core/ScopedEnv.hpp
  app/ecuamaps/sdk/core/ScopedLocalRef.hpp
  app/ecuamaps/sdk/Framework.hpp
  app/ecuamaps/sdk/opengl/android_gl_utils.hpp
  app/ecuamaps/sdk/opengl/androidoglcontext.hpp
  app/ecuamaps/sdk/opengl/androidoglcontextfactory.hpp
  app/ecuamaps/sdk/opengl/gl3stub.h
  app/ecuamaps/sdk/platform/GuiThread.hpp
  app/ecuamaps/sdk/platform/AndroidPlatform.hpp
  app/ecuamaps/sdk/util/Distance.hpp
  app/ecuamaps/sdk/vulkan/android_vulkan_context_factory.hpp

  # JNI sources
  app/ecuamaps/sdk/search/DisplayedCategories.cpp
  app/ecuamaps/sdk/search/SearchEngine.cpp
  app/ecuamaps/sdk/search/SearchRecents.cpp
  app/ecuamaps/sdk/routing/roadshield/RoadShield.cpp
  app/ecuamaps/sdk/routing/roadshield/RoadShield.hpp
  app/ecuamaps/sdk/routing/roadshield/RoadShieldInfo.cpp
  app/ecuamaps/sdk/routing/roadshield/RoadShieldInfo.hpp
  app/ecuamaps/sdk/routing/roadshield/RoadShieldType.cpp
  app/ecuamaps/sdk/routing/roadshield/RoadShieldType.hpp
  app/ecuamaps/sdk/routing/CarDirection.cpp
  app/ecuamaps/sdk/routing/CarDirection.hpp
  app/ecuamaps/sdk/routing/JunctionInfo.hpp
  app/ecuamaps/sdk/routing/LaneInfo.cpp
  app/ecuamaps/sdk/routing/LaneInfo.hpp
  app/ecuamaps/sdk/routing/PedestrianDirection.cpp
  app/ecuamaps/sdk/routing/PedestrianDirection.hpp
  app/ecuamaps/sdk/routing/RouteMarkData.hpp
  app/ecuamaps/sdk/routing/RouteMarkType.hpp
  app/ecuamaps/sdk/routing/RoutePointInfo.hpp
  app/ecuamaps/sdk/routing/RouteRecommendationType.hpp
  app/ecuamaps/sdk/routing/RoutingInfo.hpp
  app/ecuamaps/sdk/routing/RoutingOptions.cpp
  app/ecuamaps/sdk/routing/TransitRouteInfo.hpp
  app/ecuamaps/sdk/routing/TransitStepInfo.hpp
  app/ecuamaps/sdk/ChoosePositionMode.cpp
  app/ecuamaps/sdk/MapStyle.cpp
  app/ecuamaps/sdk/ecuamaps.cpp
  app/ecuamaps/sdk/Router.cpp
  app/ecuamaps/sdk/core/jni_helper.cpp
  app/ecuamaps/sdk/core/jni_java_methods.cpp
  app/ecuamaps/sdk/core/logging.cpp
  app/ecuamaps/sdk/bookmarks/data/Bookmark.cpp
  app/ecuamaps/sdk/bookmarks/data/Bookmark.hpp
  app/ecuamaps/sdk/bookmarks/data/BookmarkCategory.cpp
  app/ecuamaps/sdk/bookmarks/data/BookmarkCategory.hpp
  app/ecuamaps/sdk/bookmarks/data/BookmarkManager.cpp
  app/ecuamaps/sdk/bookmarks/data/ElevationInfo.cpp
  app/ecuamaps/sdk/bookmarks/data/ElevationInfo.hpp
  app/ecuamaps/sdk/bookmarks/data/Icon.cpp
  app/ecuamaps/sdk/bookmarks/data/Icon.hpp
  app/ecuamaps/sdk/bookmarks/data/MapObject.cpp
  app/ecuamaps/sdk/bookmarks/data/MapObject.hpp
  app/ecuamaps/sdk/bookmarks/data/Metadata.cpp
  app/ecuamaps/sdk/bookmarks/data/Metadata.hpp
  app/ecuamaps/sdk/bookmarks/data/PredefinedColors.cpp
  app/ecuamaps/sdk/bookmarks/data/PredefinedColors.hpp
  app/ecuamaps/sdk/bookmarks/data/Track.cpp
  app/ecuamaps/sdk/bookmarks/data/Track.hpp
  app/ecuamaps/sdk/bookmarks/data/TrackStatistics.cpp
  app/ecuamaps/sdk/bookmarks/data/TrackStatistics.hpp
  app/ecuamaps/sdk/DownloadResourcesLegacyActivity.cpp
  app/ecuamaps/sdk/editor/Editor.cpp
  app/ecuamaps/sdk/editor/OpeningHours.cpp
  app/ecuamaps/sdk/editor/OsmOAuth.cpp
  app/ecuamaps/sdk/Framework.cpp
  app/ecuamaps/sdk/isolines/IsolinesManager.cpp
  app/ecuamaps/sdk/LocationState.cpp
  app/ecuamaps/sdk/Map.cpp
  app/ecuamaps/sdk/MapManager.cpp
  app/ecuamaps/sdk/settings/UnitLocale.cpp
  app/ecuamaps/sdk/settings/MapLanguageCode.cpp
  app/ecuamaps/sdk/sound/tts.cpp
  app/ecuamaps/sdk/subway/SubwayManager.cpp
  app/ecuamaps/sdk/TrackRecorder.cpp
  app/ecuamaps/sdk/TrafficState.cpp
  app/ecuamaps/sdk/opengl/android_gl_utils.cpp
  app/ecuamaps/sdk/opengl/androidoglcontext.cpp
  app/ecuamaps/sdk/opengl/androidoglcontextfactory.cpp
  app/ecuamaps/sdk/opengl/gl3stub.cpp
  app/ecuamaps/sdk/platform/GuiThread.cpp
  app/ecuamaps/sdk/platform/PlatformGuiThread.cpp
  app/ecuamaps/sdk/platform/Language.cpp
  app/ecuamaps/sdk/platform/Localization.cpp
  app/ecuamaps/sdk/platform/AndroidPlatform.cpp
  app/ecuamaps/sdk/platform/PThreadImpl.cpp
  app/ecuamaps/sdk/platform/SecureStorage.cpp
  app/ecuamaps/sdk/platform/SocketImpl.cpp
  app/ecuamaps/sdk/util/Config.cpp
  app/ecuamaps/sdk/util/GeoUtils.cpp
  app/ecuamaps/sdk/util/HttpClient.cpp
  app/ecuamaps/sdk/util/Language.cpp
  app/ecuamaps/sdk/util/LogsManager.cpp
  app/ecuamaps/sdk/util/NetworkPolicy.cpp
  app/ecuamaps/sdk/util/StringUtils.cpp
  app/ecuamaps/sdk/util/UiThread.cpp
  app/ecuamaps/sdk/vulkan/android_vulkan_context_factory.cpp
)

omim_add_library(${PROJECT_NAME} SHARED ${SRC})

target_include_directories(${PROJECT_NAME} PRIVATE .)

target_link_libraries(${PROJECT_NAME} PRIVATE
  # ECUAMAPS libs
  map
  # ge0
  # tracking
  # routing
  # traffic
  # routing_common
  # drape_frontend
  # shaders
  # search
  # storage
  # descriptions
  # drape
  # kml
  # editor
  # transit
  # indexer
  # platform
  # mwm_diff
  # bsdiff
  # geometry
  # coding
  # base
  # opening_hours
  # pugixml
  # expat
  # freetype
  # minizip
  # cppjansson
  # protobuf
  # succinct
  # stb_image
  # icu
  # agg
  # vulkan_wrapper

  # Android libs
  log
  android
  EGL
  GLESv2
  atomic
  z
)

#target_link_options(${PROJECT_NAME} PRIVATE "-fuse-ld=gold")
