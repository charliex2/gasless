
### WIFI 调试
```
adb devices;
adb tcpip 5555;
adb connect 192.168.2.4:5555
```

## 上传 apk 文件至 alioss
```
mv app/release/app-release.apk app/release/defarmer.apk
ossutil cp app/release/defarmer.apk  oss://chingshen-public/defarmer/android-release/ -r -f
```