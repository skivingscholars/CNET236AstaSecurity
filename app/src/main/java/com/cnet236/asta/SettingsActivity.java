package com.cnet236.asta;

import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.ActionBar;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.os.Build;
import android.widget.EditText;
import android.widget.TextView;

import java.io.FileInputStream;
import java.util.Arrays;

public class SettingsActivity extends ActionBarActivity {
    byte[] key;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.container, new PlaceholderFragment())
                    .commit();
        }

        key = this.getIntent().getByteArrayExtra("key");
    }

    public void tryNewPassword(View view) {
        String pw = ((EditText)findViewById(R.id.txtOldPW)).getText().toString();

        Locker diplomat = new Locker("diplomat", pw, getApplicationContext());
        if(checkHash(diplomat.getKeyHash())) {
            Intent i = new Intent(SettingsActivity.this, NewPasswordActivity.class);
            i.putExtra("pw", pw);
            SettingsActivity.this.startActivity(i);
        } else {
            ((TextView)findViewById(R.id.txtCPW)).setError(getString(R.string.error_incorrect_password));
            ((TextView)findViewById(R.id.txtCPW)).requestFocus();
        }
    }

    private boolean checkHash(byte[] diplomatHash) {
        FileInputStream allowed;
        byte[] allowedHash = new byte[32];

        try {
            allowed = getApplicationContext().openFileInput("allowedHash");
            allowed.read(allowedHash, 0, 32);
        } catch (Exception e) {
            e.printStackTrace();
        }

        Log.v("ChangePW", "diplomatHash = " + byteToString(diplomatHash));
        Log.v("ChangePW", "allowedHash =  "+byteToString(allowedHash));

        if(Arrays.equals(allowedHash, diplomatHash))
            return true;

        return false;
    }

    private String byteToString(byte[] bytes) {
        StringBuilder sb = new StringBuilder();
        for (byte b : bytes)
            sb.append(Integer.toHexString((b & 0xff)));

        return sb.toString();
    }
  /*  @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.settings, menu);
        return true;
    }*/

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        } else if (id == android.R.id.home) {
            Log.v("settingsAct", "putting key back to main");
            Intent i = new Intent(SettingsActivity.this, MainActivity.class);
            i.putExtra("from", "setting");
            i.putExtra("key", key);
            SettingsActivity.this.startActivity(i);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    /**
     * A placeholder fragment containing a simple view.
     */
    public static class PlaceholderFragment extends Fragment {

        public PlaceholderFragment() {
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_settings, container, false);
            return rootView;
        }
    }
}
