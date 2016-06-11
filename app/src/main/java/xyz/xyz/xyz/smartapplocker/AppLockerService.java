package xyz.xyz.xyz.smartapplocker;

import android.app.ActivityManager;
import android.app.IntentService;
import android.app.usage.UsageStats;
import android.app.usage.UsageStatsManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.preference.PreferenceManager;
import android.util.Log;

import java.util.HashSet;
import java.util.List;
import java.util.SortedMap;
import java.util.TreeMap;

/**
 * Created by Lomesh on 4/26/2016.
 */
public class AppLockerService extends IntentService {
    PackageManager packageManager;
    SharedPreferences sharedPref;
    HashSet<String> tempSet;
    public AppLockerService() {
        super("AppLockerService");
    }/**
     * Creates an IntentService.  Invoked by your subclass's constructor.
     *
     * @param name Used to name the worker thread, important only for debugging.
     */
    public AppLockerService(String name) {
        super(name);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        Log.d("AppLockerService", "onHandleIntent:Service has started.");
        while(true) {
            try {
                Thread.sleep(1000);
                Log.d("AppLockerService", "Polling..");

                sharedPref= PreferenceManager.getDefaultSharedPreferences(this);
                HashSet<String> lockedappList=(HashSet<String>)sharedPref.getStringSet("LockedApps",null);
                HashSet<String>allApps=(HashSet<String>)sharedPref.getStringSet("LockedApps",null);

                packageManager = getPackageManager();
                String appPackage=printForegroundTask();


                ApplicationInfo applicationInfo = null;
                try {
                    applicationInfo = packageManager.getApplicationInfo(appPackage, 0);
                } catch (final PackageManager.NameNotFoundException e) {}
                final String appTitle = (String)((applicationInfo != null) ? packageManager.getApplicationLabel(applicationInfo) : "???");





                if(allApps!=null) {
                    if (!allApps.contains(appTitle)) {

                        Log.d("AppLockerService", "Locking all apps");
                        for(String s:lockedappList){
                            sharedPref.edit().putBoolean(s,true).commit();
                        }
                    }
                }

              /* String lastApp=sharedPref.getString("lastApp","none");
                boolean runit=false;
                if(lastApp.equals(appTitle)) {
                    sharedPref.edit().putString("lastApp", appTitle);
                    runit=false;
                }
*/


                if(lockedappList!=null) {
                    if (lockedappList.contains(appTitle)) {
                        sharedPref.edit().putString("lastApp", appTitle).commit();
                        Log.e("AppLockerService", "App name is supposed to be locked " + appTitle);
                        Intent i = new Intent();
                        i.setClass(this, LockedActivity.class);
                        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

                        boolean isLocked=sharedPref.getBoolean(appTitle,true);

                       /* boolean isAppLocked=sharedPref.getBoolean("isAppLocked",true);
                        Log.e("AppLockerService", "lockAll " + lockAll);
                        Log.e("AppLockerService", "runit " + lockAll);
                        Log.e("AppLockerService", "isAppLocked " + isAppLocked);
                        */
                        if (isLocked)
                            startActivity(i);

                    }
                }

                Log.e("AppLockerService", "App name is" + appTitle);
            } catch (InterruptedException e) {

                e.printStackTrace();
            }
        }
    }
    private String printForegroundTask() {
        String currentApp = "NULL";
        if(android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
            UsageStatsManager usm = (UsageStatsManager) this.getSystemService(Context.USAGE_STATS_SERVICE);
            long time = System.currentTimeMillis();
            List<UsageStats> appList = usm.queryUsageStats(UsageStatsManager.INTERVAL_DAILY,  time - 1000*1000, time);
            if (appList != null && appList.size() > 0) {
                SortedMap<Long, UsageStats> mySortedMap = new TreeMap<Long, UsageStats>();
                for (UsageStats usageStats : appList) {
                    mySortedMap.put(usageStats.getLastTimeUsed(), usageStats);
                }
                if (mySortedMap != null && !mySortedMap.isEmpty()) {
                    currentApp = mySortedMap.get(mySortedMap.lastKey()).getPackageName();
                }
            }
        } else {
            ActivityManager am = (ActivityManager)this.getSystemService(Context.ACTIVITY_SERVICE);
            List<ActivityManager.RunningAppProcessInfo> tasks = am.getRunningAppProcesses();
            currentApp = tasks.get(0).processName;
        }

        Log.e("AppLockerService", "Current App in foreground is: " + currentApp);
        return currentApp;
    }
}
