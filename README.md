# MoKee-WarpShare

MoKee WarpShare ，Part of the compatible airdrop。Android transmitted to Mac.

移植魔趣的“跃传”，支持Android向Mac传输数据

## Reference below

https://github.com/MoKee/android_packages_apps_WarpShare

## To build it Yourself

[![Open in Gitpod](https://gitpod.io/button/open-in-gitpod.svg)](https://gitpod.io/from-referrer/)

```bash
yes | sdk install java 8.0.372-tem
# if prompted to use as default select yes
yes | sdkmanager --licenses

./gradlew assembleDebug
```

download the file located at `app/build/outputs/apk/debug/app-debug.apk` and install on your phone.
