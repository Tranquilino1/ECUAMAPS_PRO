#include <jni.h>
#include "app/ecuamaps/sdk/core/logging.hpp"

extern "C"
{
JNIEXPORT void Java_app_ecuamaps_sdk_util_log_LogsManager_nativeToggleCoreDebugLogs(JNIEnv * /*env*/,
                                                                                       jclass /*clazz*/,
                                                                                       jboolean enabled)
{
  jni::ToggleDebugLogs(enabled);
}
}  // extern "C"
