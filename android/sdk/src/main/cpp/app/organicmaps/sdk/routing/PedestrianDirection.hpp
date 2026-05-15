#pragma once

#include <jni.h>

#include "app/ecuamaps/sdk/core/jni_helper.hpp"

#include "routing/turns.hpp"

jobject ToJavaPedestrianDirection(JNIEnv * env, routing::turns::PedestrianDirection turn);
