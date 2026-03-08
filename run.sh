#!/usr/bin/env bash
# Build and run VibeText using Gradle (respects the Java 25 toolchain)
set -e
cd "$(dirname "$0")"
exec ./gradlew run "$@"
