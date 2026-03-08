#!/usr/bin/env bash
# Build and run VibeText directly (bypasses Gradle's forked JVM)
set -e
cd "$(dirname "$0")"

./gradlew classes -q --no-daemon 2>/dev/null
exec java -cp build/classes/java/main com.vibeText.Main "$@"
