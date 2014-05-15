#!/bin/sh
set -e

env
./gradlew clean :core:build :android:assembleDebug lint
