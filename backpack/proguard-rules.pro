# For stack traces
-keepattributes SourceFile, LineNumberTable

# Get rid of package names, makes file smaller
-repackageclasses

-keep class com.cyb3rko.backpack.** { *; }
