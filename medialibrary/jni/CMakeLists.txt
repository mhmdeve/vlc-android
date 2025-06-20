cmake_minimum_required(VERSION 3.21) # proper support with the NDK

project(medialibrary)

set(CMAKE_C_STANDARD 17)
set(CMAKE_CXX_STANDARD 17)

set(ANDROID_STL c++_shared)
set(ANDROID_CPP_FEATURES "rtti exceptions")

set(LIBVLCJNI_SRC_DIR "${CMAKE_CURRENT_SOURCE_DIR}/../../libvlcjni" CACHE PATH "libvlcjni base directory")
set(PREBUILT_PREFIX "${CMAKE_CURRENT_SOURCE_DIR}/../prefix/android-${ANDROID_PLATFORM}-${ANDROID_ABI}")
set(LIBRAY_OUTPUT_DIR "${CMAKE_CURRENT_SOURCE_DIR}/libs/${ANDROID_NDK_ABI_NAME}")

# prebuilt binaries
## built with bash script (for now)
add_library(libvlc STATIC IMPORTED)
set_target_properties(libvlc
                      PROPERTIES IMPORTED_LOCATION
                      ${LIBVLCJNI_SRC_DIR}/libvlc/jni/libs/${ANDROID_NDK_ABI_NAME}/libvlc.so)

## built with meson script (cannot be called by gradle)
add_library(medialibrary STATIC IMPORTED)
set_target_properties(medialibrary
                      PROPERTIES IMPORTED_LOCATION
                      ${PREBUILT_PREFIX}/lib/libmedialibrary.a)

## built with bash script
add_library(sqlite STATIC IMPORTED)
set_target_properties(sqlite
                      PROPERTIES IMPORTED_LOCATION
                      ${PREBUILT_PREFIX}/lib/libsqlite3.a)

find_library(MATH_LIB m)
find_library(LOG_LIB log)
set(CMAKE_SHARED_LINKER_FLAGS "${CMAKE_SHARED_LINKER_FLAGS} -Wl,-Bsymbolic-functions")

# mla
add_library(mla SHARED
            ${CMAKE_CURRENT_SOURCE_DIR}/medialibrary.cpp 
            ${CMAKE_CURRENT_SOURCE_DIR}/AndroidMediaLibrary.cpp 
            ${CMAKE_CURRENT_SOURCE_DIR}/AndroidDeviceLister.cpp 
            ${CMAKE_CURRENT_SOURCE_DIR}/utils.cpp)

target_link_options(mla PRIVATE "-Wl,-z,max-page-size=16384")
target_link_libraries(mla libvlc medialibrary sqlite ${MATH_LIB} ${LOG_LIB})
target_include_directories(mla PRIVATE ${PREBUILT_PREFIX}/include)
set_target_properties(mla PROPERTIES
                      LIBRARY_OUTPUT_DIRECTORY "${LIBRAY_OUTPUT_DIR}")

# Copy the C++ runtime
add_custom_command(TARGET mla PRE_BUILD
                   COMMAND ${CMAKE_COMMAND} -E copy_if_different
                   ${ANDROID_TOOLCHAIN_ROOT}/sysroot/usr/lib/${CMAKE_LIBRARY_ARCHITECTURE}/libc++_shared.so
                   "${LIBRAY_OUTPUT_DIR}")

