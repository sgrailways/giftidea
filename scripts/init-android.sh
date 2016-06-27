#!/bin/sh
set -e

ANDROID_SDK_PACKAGE=android-sdk_r24.4.1-linux.tgz
wget http://dl.google.com/android/$ANDROID_SDK_PACKAGE -nv
tar xzf $ANDROID_SDK_PACKAGE
echo yes | android update sdk --all --filter platform-tools,build-tools-24.0.0,android-24 --no-ui --force
