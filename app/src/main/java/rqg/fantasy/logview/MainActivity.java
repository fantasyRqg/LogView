package rqg.fantasy.logview;

import android.Manifest;
import android.content.pm.PackageManager;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";

    // Used to load the 'native-lib' library on application startup.
    static {
        System.loadLibrary("native-lib");
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Example of a call to a native method
        TextView tv = (TextView) findViewById(R.id.sample_text);
        tv.setText(stringFromJNI());


//        if (checkPermission())
        addLogView();
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        Log.d(TAG, "onRequestPermissionsResult() called with: requestCode = [" + requestCode + "], permissions = [" + permissions[0] + "], grantResults = [" + grantResults[0] + "]");
        if (requestCode == 1) {
            boolean allGranted = true;

            for (int gp : grantResults) {
                if (gp != PackageManager.PERMISSION_GRANTED) {
                    allGranted = false;
                    break;
                }
            }

            if (allGranted)
                addLogView();
        }
    }

    private boolean checkPermission() {
        List<String> permissionList = new ArrayList<>();
        List<String> requestList = new ArrayList<>();

        permissionList.add(Manifest.permission.SYSTEM_ALERT_WINDOW);

        for (String p : permissionList) {
            if (ActivityCompat.checkSelfPermission(this, p) != PackageManager.PERMISSION_GRANTED) {
                requestList.add(p);
            }
        }


        if (requestList.size() != 0) {
            ActivityCompat.requestPermissions(this, requestList.toArray(new String[requestList.size()]), 1);
            return false;
        }

        return true;
    }


    private void addLogView() {
        WindowManager windowManager = getWindowManager();
        LayoutInflater inflater = getLayoutInflater();
        View view = inflater.inflate(R.layout.window_log, null);

        WindowManager.LayoutParams params = new WindowManager.LayoutParams();
        params.copyFrom(getWindow().getAttributes());

        params.type = WindowManager.LayoutParams.TYPE_APPLICATION;


        windowManager.addView(view, params);
    }

    /**
     * A native method that is implemented by the 'native-lib' native library,
     * which is packaged with this application.
     */
    public native String stringFromJNI();
}
