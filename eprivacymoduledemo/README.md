### To run apk on /e/OS devices

./gradlew :eprivacymoduledemo:assembleDebug


PrivacyCentral needs to be installed as system app and whitelisting in order to grant some system specific permissions. Follow these steps to make it work properly on /e/OS

1. From `Developer options`, enable `Android debugging` and `Rooted debugging`
1. Sign the apk with platform certificate. You can use this command to do that

    ```shell
    apksigner sign --key platform.pk8 --cert platform.x509.pem EPrivacyModuleDemo.apk app-e-release-unsigned.apk
    ```

   If you are running your tests on an `/test` build, you can find keys at https://gitlab.e.foundation/e/os/android_build/-/tree/v1-q/target/product/security
1. Install apk as system app and push permissions whitelist with following commands:
    ```shell
    adb root && adb remount
    adb shell mkdir system/priv-app/EPrivacyModuleDemo
    adb push EPrivacyModuleDemo.apk system/priv-app/EPrivacyModuleDemo
    ```

1. Push permissions whitelist.
    - it requires the whitelisting [privapp-permissions-foundation.e.privacycentralapp.xml](privapp-permissions-foundation.e.eprivacymoduledemo.xml) file that can be found in the project repository.
    - then use the following command
        ```bash
        adb push privapp-permissions-foundation.e.eprivacymoduledemo.xml /system/etc/permissions/
        ```
1. Allow the fake location service to run in background. Add     <allow-in-power-save package="foundation.e.privacymoduledemo" /> in the file /system/etc/permissions/platform.xml . 

1. Reboot the device
    ```shell
    adb reboot
    ```