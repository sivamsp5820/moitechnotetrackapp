# Proguard rules for Moi Note Tracker
-keep class com.moitech.notetracker.data.model.** { *; }
-keepclassmembers class * {
    @com.google.gson.annotations.SerializedName <fields>;
}
