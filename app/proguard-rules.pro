# Add project specific ProGuard rules here.

# Keep app classes referenced by manifest (activities, services, receivers).
# (AGP keeps these automatically, but explicit keeps document intent and
#  guard against future refactors moving them.)
-keep class com.material.you.widgets.WidgetsApplication
-keep class com.material.you.widgets.MainActivity
-keep class com.material.you.widgets.BootReceiver

# Keep Glance widget classes (loaded via reflection by AppWidgetManager)
-keep class * extends androidx.glance.appwidget.GlanceAppWidget { *; }
-keep class * extends androidx.glance.appwidget.GlanceAppWidgetReceiver { *; }

# Suppress warnings for optional classes referenced by dependencies we don't use
-dontwarn org.bouncycastle.**
-dontwarn org.conscrypt.**
-dontwarn org.openjsse.**
