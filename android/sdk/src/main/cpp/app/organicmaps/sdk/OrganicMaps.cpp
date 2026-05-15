#include "app/ecuamaps/sdk/Framework.hpp"

#include "app/ecuamaps/sdk/platform/AndroidPlatform.hpp"

#include "app/ecuamaps/sdk/core/jni_helper.hpp"

extern "C"
{
// static void nativeSetSettingsDir(String settingsPath);
JNIEXPORT void Java_app_ecuamaps_sdk_ecuamaps_nativeSetSettingsDir(JNIEnv * env, jclass clazz,
                                                                         jstring settingsPath)
{
  android::Platform::Instance().SetSettingsDir(jni::ToNativeString(env, settingsPath));
}

// static void nativeInitPlatform(Context context, String apkPath, String storagePath, String privatePath, String
// tmpPath, String flavorName, String buildType, boolean isTablet);
JNIEXPORT void Java_app_ecuamaps_sdk_ecuamaps_nativeInitPlatform(JNIEnv * env, jclass clazz, jobject context,
                                                                       jstring apkPath, jstring writablePath,
                                                                       jstring privatePath, jstring tmpPath,
                                                                       jstring flavorName, jstring buildType,
                                                                       jboolean isTablet)
{
  android::Platform::Instance().Initialize(env, context, apkPath, writablePath, privatePath, tmpPath, flavorName,
                                           buildType, isTablet);
}

// static void nativeInitFramework(@NonNull Runnable onComplete);
JNIEXPORT void Java_app_ecuamaps_sdk_ecuamaps_nativeInitFramework(JNIEnv * env, jclass clazz, jobject onComplete)
{
  if (!g_framework)
  {
    g_framework.Assign(new android::Framework([onComplete = jni::make_global_ref_safe(onComplete)]()
    {
      JNIEnv * env = jni::GetEnv();
      jmethodID const methodId = jni::GetMethodID(env, *onComplete, "run", "()V");
      env->CallVoidMethod(*onComplete, methodId);
    }));
  }
}

// static void nativeAddLocalization(String name, String value);
JNIEXPORT void Java_app_ecuamaps_sdk_ecuamaps_nativeAddLocalization(JNIEnv * env, jclass clazz, jstring name,
                                                                          jstring value)
{
  g_framework->AddString(jni::ToNativeString(env, name), jni::ToNativeString(env, value));
}

JNIEXPORT void Java_app_ecuamaps_sdk_ecuamaps_nativeOnTransit(JNIEnv *, jclass, jboolean foreground)
{
  if (static_cast<bool>(foreground))
    g_framework->NativeFramework()->EnterForeground();
  else
    g_framework->NativeFramework()->EnterBackground();
}
}
