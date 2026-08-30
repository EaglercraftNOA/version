@echo off
title CompileEagRuntimeJS
set srcFolder=../src/wasm-gc-teavm/js
echo Compiling %srcFolder%
java -jar buildtools/closure-compiler.jar --compilation_level ADVANCED_OPTIMIZATIONS --assume_function_wrapper --emit_use_strict --isolation_mode IIFE --js "%srcFolder%/externs.js" "%srcFolder%/eagruntime_util.js" "%srcFolder%/eagruntime_main.js" "%srcFolder%/platformApplication.js" "%srcFolder%/platformAssets.js" "%srcFolder%/platformAudio.js" "%srcFolder%/platformFilesystem.js" "%srcFolder%/platformInput.js" "%srcFolder%/platformNetworking.js" "%srcFolder%/platformOpenGL.js" "%srcFolder%/platformRuntime.js" "%srcFolder%/WASMGCBufferAllocator.js" "%srcFolder%/teavm_runtime.js" "%srcFolder%/eagruntime_entrypoint.js" --js_output_file javascript/eagruntime.js
pause