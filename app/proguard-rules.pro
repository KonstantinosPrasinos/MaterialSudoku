# Keep line numbers for release stack traces
-keepattributes SourceFile,LineNumberTable
-keepattributes *Annotation*,Signature,InnerClasses,EnclosingMethod

# Keep Firebase Data Models and DTOs from R8 Obfuscation
-keepclassmembers class com.example.multiplayersudoku.classes.** {
    @com.google.firebase.database.IgnoreExtraProperties <fields>;
    @com.google.firebase.database.Exclude <methods>;
    public <init>(...);
    public <fields>;
    public <methods>;
}

-keep class com.example.multiplayersudoku.classes.** { *; }
-keep class Player { *; }

# Preserve Enums for valueOf() serialization (RoomState, Difficulty)
-keepclassmembers enum com.example.multiplayersudoku.classes.** {
    public static **[] values();
    public static ** valueOf(java.lang.String);
}