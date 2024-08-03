package com.qi.flutterdevtool.utils;

import java.io.File;

public interface FlutterProjectConstant {
    String ANDROID_DIR = "android" + File.separator;
    String ANDROID_APP_DIR = ANDROID_DIR + "app" + File.separator;
    String ANDROID_APP_MAIN_DIR = ANDROID_APP_DIR + "src" + File.separator + "main" + File.separator;
    String ANDROID_MANIFEST_PATH = ANDROID_APP_MAIN_DIR + File.separator + "AndroidManifest.xml";
    String ANDROID_RES_DIR = ANDROID_APP_MAIN_DIR + "res" + File.separator;
    String ANDROID_MIPMAP_HDPI_DIR = ANDROID_RES_DIR + "mipmap-hdpi" + File.separator;
    String ANDROID_MIPMAP_XHDPI_DIR = ANDROID_RES_DIR + "mipmap-xhdpi" + File.separator;
    String ANDROID_MIPMAP_XXHDPI_DIR = ANDROID_RES_DIR + "mipmap-xxhdpi" + File.separator;
    String ANDROID_MIPMAP_XXXHDPI_DIR = ANDROID_RES_DIR + "mipmap-xxxhdpi" + File.separator;
    String ANDROID_MIPMAP_MDPI_DIR = ANDROID_RES_DIR + "mipmap-mdpi" + File.separator;

    String IOS_DIR = "ios" + File.separator;
    String IOS_ASSETS_DIR = IOS_DIR + "Runner" + File.separator + "Assets.xcassets" + File.separator;
    String IOS_ASSET_ACCENT_COLOR_DIR = IOS_ASSETS_DIR + "AccentColor.colorset" + File.separator;
    String IOS_ASSET_APP_ICON_DIR = IOS_ASSETS_DIR + "AppIcon.appiconset" + File.separator;
    String IOS_ASSET_LAUNCH_IMAGE_DIR = IOS_ASSETS_DIR + "LaunchImage.imageset" + File.separator;
    String IOS_LAUNCH_SCREEN_STORYBOARD_PATH = IOS_DIR + "Base.lproj" + File.separator + "LaunchScreen.storyboard";
    String IOS_XCODEPROJ_PATH = IOS_DIR + "Runner.xcodeproj" + File.separator + "project.pbxproj";
    String IOS_INFO_PATH = IOS_DIR + "Runner" + File.separator + "info.plist";
}
