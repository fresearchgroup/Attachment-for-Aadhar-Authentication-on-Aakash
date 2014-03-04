LOCAL_PATH := $(call my-dir)

include $(CLEAR_VARS)

#OPENCV_CAMERA_MODULES:=off
#OPENCV_INSTALL_MODULES:=on
#OPENCV_LIB_TYPE:=STATIC
include D:\opencv\OpenCV-2.4.5-android-sdk\sdk\native\jni\OpenCV.mk

LOCAL_SRC_FILES  := opencv_code.cpp
LOCAL_C_INCLUDES += $(LOCAL_PATH)
LOCAL_LDLIBS     += -L$(NDKROOT)/usr/lib -llog -ldl


LOCAL_MODULE     := authentication

include $(BUILD_SHARED_LIBRARY)