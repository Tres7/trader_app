#!/usr/bin/env bash

adb reverse tcp:8080 tcp:8080
adb reverse tcp:8081 tcp:8081
adb install client/android/app/build/outputs/apk/debug/app-debug.apk
sleep 10
adb logcat -c
adb logcat > "$GITHUB_WORKSPACE/logs/device.log" &
LOGCAT_PID=$!
maestro test .maestro/flows/sign-in.yaml > "$GITHUB_WORKSPACE/logs/maestro-output.log" 2>&1
MAESTRO_FAILED=$?
cat "$GITHUB_WORKSPACE/logs/maestro-output.log"
kill "$LOGCAT_PID" 2>/dev/null || true
echo "$MAESTRO_FAILED" > "$GITHUB_WORKSPACE/logs/maestro-exit-code.txt"
adb emu kill || true
exit "$MAESTRO_FAILED"
