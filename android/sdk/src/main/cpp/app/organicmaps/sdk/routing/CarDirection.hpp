#pragma once

#include <jni.h>

#include "app/ecuamaps/sdk/core/jni_helper.hpp"

#include "routing/turns.hpp"

jobject ToJavaCarDirection(JNIEnv * env, routing::turns::CarDirection turn);
