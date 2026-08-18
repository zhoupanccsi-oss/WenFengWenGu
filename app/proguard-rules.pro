# ProGuard rules for WenFengWenGu

# Keep Android entry points
-keep public class * extends android.app.Activity
-keep public class * extends android.app.Service
-keep public class * extends android.content.BroadcastReceiver
-keep public class * extends android.view.View

# Keep custom View constructors (needed by LayoutInflater)
-keep class com.wenfeng.wengu.BorderView { <init>(...); }

# Google Play Billing
-keep class com.android.billingclient.api.** { *; }

# Keep R class for resource access
-keep class com.wenfeng.wengu.R { *; }
-keep class com.wenfeng.wengu.R$* { *; }
