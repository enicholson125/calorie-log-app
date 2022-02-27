## To build app without Android Studio

```
./gradlew task build --build-cache
```
The built APK is put in `app/build/outputs/apk`.

## To install app on connected phone

```
./gradlew task installDebug --build-cache
```

## To build the database

```
sqlite3 -init app/src/main/assets/seeddb.sql seeddatabase.db
mv seeddatabase.db app/src/main/assets/
```
