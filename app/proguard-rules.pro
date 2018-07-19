# Add project specific ProGuard rules here.
# You can control the set of applied configuration files using the
# proguardFiles setting in build.gradle.
#
# For more details, see
#   http://developer.android.com/guide/developing/tools/proguard.html

# If your project uses WebView with JS, uncomment the following
# and specify the fully qualified class name to the JavaScript interface
# class:
#-keepclassmembers class fqcn.of.javascript.interface.for.webview {
#   public *;
#}

# Uncomment this to preserve the line number information for
# debugging stack traces.
#-keepattributes SourceFile,LineNumberTable

# If you keep the line number information, uncomment this to
# hide the original source file name.
#-renamesourcefileattribute SourceFile

-verbose
-dontpreverify
-ignorewarnings
#指定压缩级别
-optimizationpasses 5
-dontusemixedcaseclassnames
-dontskipnonpubliclibraryclasses
-keepattributes *Annotation*
-keepattributes SourceFile,LineNumberTable
-keepattributes Exceptions,InnerClasses,Signature
-keepattributes Exceptions, Signature, InnerClasses
#混淆时采用的算法
-optimizations !code/simplification/arithmetic,!field/*,!class/merging/*
-keepattributes EnclosingMethod
-keep public class javax.**
-keep public class android.webkit.**
-keep class android.support.v4.** {*; }
-keep public class * extends android.app.Service
-keep public class * extends android.app.Activity
-keep public class * extends android.app.Application
-keep class * extends java.lang.annotation.Annotation
-keep public class * extends android.preference.Preference
-keep class * extends java.lang.annotation.Annotation { *; }
-keep public class * extends android.content.ContentProvider
-keep public class * extends android.content.BroadcastReceiver
-keep public class * extends android.app.backup.BackupAgentHelper
-keep public class com.android.vending.licensing.ILicensingService

-keep public class * extends android.support.v4.app.Fragment
-keep public class * extends android.app.Service
-keep public class * extends android.content.BroadcastReceiver
-keep public class * extends android.content.ContentProvider
-keep public class * extends android.app.backup.BackupAgentHelper
-keep public class * extends android.preference.Preference
-keep public class com.android.vending.licensing.ILicensingService