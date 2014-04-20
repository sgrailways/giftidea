#!/bin/sh
set -e

env
./gradlew clean :giftidea-android:assembleDebug lint
