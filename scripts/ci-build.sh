#!/bin/sh
set -e

env
./gradlew clean :core:build :android:assembleDebug lint
mv android/build/outputs/apk/android-debug-unaligned.apk android/build/outputs/apk/giftidea-debug-unaligned-$BUILD_ID.apk

