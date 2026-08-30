@echo off
cd /d "%~dp0\.."
set "GRADLE_OPTS=-Xmx8g"
call gradlew target_teavm_javascript:makeMainOfflineDownload "-Dorg.gradle.jvmargs=-Xmx8G -Xms2G"
pause
