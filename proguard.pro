
-keep class * implements com.intellij.openapi.components.PersistentStateComponent {*;}
-keep class * {public static ** INSTANCE;}
-keep class com.intellij.util.* {*;}
-keepattributes Exceptions,InnerClasses,Signature,Deprecated,SourceFile,LineNumberTable,*Annotation*,EnclosingMethod
