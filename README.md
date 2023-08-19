# MoKee-WarpShare

MoKee WarpShare ，Part of the compatible airdrop。Android transmitted to Mac.

移植魔趣的“跃传”，支持Android向Mac传输数据

## Reference below

https://github.com/MoKee/android_packages_apps_WarpShare

## to build on gitpod

```bash
sdk install java 8.0.372-tem
# when prompted to use as default select yes
yes | sdkmanager --licenses

./gradlew assembleDebug
```

download the file `app/build/outputs/apk/debug/app-debug.apk` and install on your phone.
