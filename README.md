# Privacy Modules /e/ specific implementations

This Android Module (aar) implements PrivacyModules functionalities with all the privileges required for a smooth user experience.

## How to compile this module


This module requires privacymodulesapi to be already built and publish to your local maven repository

This module relies on many hidden features of the Android framework. The use of these features (methods) are protected in the standard sdk. 
To be able to compile this module, you will need a specific version of android framework library.   

You can use a premade android.jar or you can just follow the instructions given by this project [ anggrayudi/android-hidden-api ](https://github.com/anggrayudi/android-hidden-api) to get everything setup.

With a premade android jar:

To build for Android SDK 29:

Download the premade android.jar from there https://drive.google.com/drive/folders/10TGHc-yK1cQjXruBVidbpZOXURKMvLvZ

This file is in fact a hidden rar file and needs to be extracted 

```unrar e  path-to-download/android.jar```

Replace your sdk jar by the resulting jar

```
cp android29.jar your-android-sdk-folder/platforms/android-29/android.jar
```

# Build

    ./gradlew :privacymodulese:assembleRelease

## To deploy localy duringdevelopment

    ./gradlew --console=verbose publishToMavenLocal

## To push release on gitlab

    ./gradlew --console=verbose publish





# Structure (UML class diagrams)

* v0.0.2.1 (Permissions)


```mermaid

classDiagram
    AbstractPermissionHelper <|-- EPermissionHelper
    AbstractPrivacyLeakageMeter <|-- EPrivacyLeakageMeter
    AbstractPermissionManager <|-- EPermissionManager
