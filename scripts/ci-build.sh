#!/bin/sh
set -e

env
./gradlew clean :giftidea-core:build :giftidea-android:assembleDebug lint
