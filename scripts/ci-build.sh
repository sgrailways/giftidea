#!/bin/sh
set -e

env
./gradlew clean :giftidea-core:build :giftidea-android:assembleDebug
$ANDROID_HOME/tools/lint -Werror --xml giftidea-android/build/lint-results.xml .

