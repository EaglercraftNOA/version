@echo off
title MakeOfflineDownload
java -cp "desktopRuntime/MakeOfflineDownload.jar;desktopRuntime/CompileEPK.jar" net.lax1dude.eaglercraft.v1_8.buildtools.workspace.MakeOfflineDownload "javascript/OfflineDownloadTemplate.txt" "javascript/classes.js" "javascript/assets.epk" "javascript/Eaglercraft_Beta_1.1_02_Offline_Download.html"
pause
