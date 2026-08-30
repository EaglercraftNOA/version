@echo off
title gradlew target_lwjgl_desktop:eaglercraftDebugRuntime
cd ../
call gradlew target_lwjgl_desktop:eaglercraftDebugRuntime --init-script init.gradle
pause
