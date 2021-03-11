# Privacy Modules /e/ specific implementations

This Android Module (aar) implements PrivacyModules functionalities with all the privileges required for a smooth user experience.

## How to compile this module

This module relies on many hidden features of the Android framework. The use of these features (methods) are protected in the standard sdk. 
To be able to compile this module, you will need a specific version of android framework library.   

You can just follow the instructions given by this project [ anggrayudi/android-hidden-api ](https://github.com/anggrayudi/android-hidden-api) to get everything setup.  

**Caution:** Actually, the given android.jar for sdk 29 (android 10 / Q) doesn't work out of the box. To make it work, you just have to unzip it twice, until you get a list of directories (android, androidx, assets, ...) in the root of the android.jar archive.

# Structure (UML class diagrams)

* v0.0.2.1 (Permissions)


```mermaid

classDiagram
    AbstractPermissionHelper <|-- EPermissionHelper
    AbstractPrivacyLeakageMeter <|-- EPrivacyLeakageMeter
    AbstractPermissionManager <|-- EPermissionManager
