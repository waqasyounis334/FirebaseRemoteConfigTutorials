package com.androidbull.firebaseremoteconfigtutorial;

import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Toast;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.remoteconfig.FirebaseRemoteConfig;
import com.google.firebase.remoteconfig.FirebaseRemoteConfigSettings;
import java.util.HashMap;

public class MainActivity extends AppCompatActivity {

  private FirebaseRemoteConfig mFirebaseRemoteConfig = FirebaseRemoteConfig.getInstance();
  private HashMap<String, Object> firebaseDefaultMap;
  public static final String VERSION_CODE_KEY = "latest_app_version";
  private static final String TAG = "MainActivity";

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);
//This is default Map
    //Setting the Default Map Value with the current version code
    firebaseDefaultMap = new HashMap<>();
    firebaseDefaultMap.put(VERSION_CODE_KEY, getCurrentVersionCode());
    mFirebaseRemoteConfig.setDefaults(firebaseDefaultMap);

    //Setting that default Map to Firebase Remote Config

    //Setting Developer Mode enabled to fast retrieve the values
    mFirebaseRemoteConfig.setConfigSettings(
        new FirebaseRemoteConfigSettings.Builder().setDeveloperModeEnabled(BuildConfig.DEBUG)
            .build());

    //Fetching the values here
    mFirebaseRemoteConfig.fetch().addOnCompleteListener(new OnCompleteListener<Void>() {
      @Override
      public void onComplete(@NonNull Task<Void> task) {
        if (task.isSuccessful()) {
          mFirebaseRemoteConfig.activateFetched();
          Log.d(TAG, "Fetched value: " + mFirebaseRemoteConfig.getString(VERSION_CODE_KEY));
          //calling function to check if new version is available or not
          checkForUpdate();
        } else {
          Toast.makeText(MainActivity.this, "Something went wrong please try again",
              Toast.LENGTH_SHORT).show();
        }
      }
    });

    Log.d(TAG, "Default value: " + mFirebaseRemoteConfig.getString(VERSION_CODE_KEY));
  }

  private void checkForUpdate() {
    int latestAppVersion = (int) mFirebaseRemoteConfig.getDouble(VERSION_CODE_KEY);
    if (latestAppVersion > getCurrentVersionCode()) {
      new AlertDialog.Builder(this).setTitle("Please Update the App")
          .setMessage("A new version of this app is available. Please update it").setPositiveButton(
          "OK", new OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
              Toast
                  .makeText(MainActivity.this, "Take user to Google Play Store", Toast.LENGTH_SHORT)
                  .show();
            }
          }).setCancelable(false).show();
    } else {
      Toast.makeText(this,"This app is already up to date", Toast.LENGTH_SHORT).show();
    }
  }

  private int getCurrentVersionCode() {
    try {
      return getPackageManager().getPackageInfo(getPackageName(), 0).versionCode;
    } catch (NameNotFoundException e) {
      e.printStackTrace();
    }
    return -1;
  }
}
