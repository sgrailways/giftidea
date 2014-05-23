#!/bin/sh
set -e

env
./gradlew clean :core:build :android:assembleDebug lint
mv android/build/apk/android-debug-unaligned{,-$BUILD_ID}.apk

