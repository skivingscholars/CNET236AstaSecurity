package com.cnet236.asta;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.ActionBar;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.os.Build;
import android.widget.EditText;
import android.widget.TextView;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;

public class PasswordActivity extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_password);

        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.container, new PlaceholderFragment())
                    .commit();
        }

        findViewById(R.id.btnSavePassword).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                tryPassword();
            }
        });
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.password, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
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
            View rootView = inflater.inflate(R.layout.fragment_password, container, false);
            return rootView;
        }
    }

    public void tryPassword() {
        TextView pw = (TextView)findViewById(R.id.password), cpw = (TextView)findViewById(R.id.confirmPassword);
        FileOutputStream file;
        byte[] hash;

        if(pw.getText().toString() != cpw.getText().toString()) {
            EditText pass = (EditText) findViewById(R.id.newPassword);
            pass.setError(getString(R.string.passwordsdontmatch));
            pass.requestFocus();
            return;
        }

        Locker allowed = new Locker("diplomat", pw.getText().toString(), getApplicationContext());

        try {
            file = getApplicationContext().openFileOutput("allowedHash", Context.MODE_PRIVATE);
            hash = allowed.getKeyHash();
            file.write(hash, 0, hash.length);
            file.close();
        } catch (Exception e) {
            e.printStackTrace();
            return;
        }

        Intent i = new Intent(PasswordActivity.this, LoginActivity.class);
        PasswordActivity.this.startActivity(i);
    }
}
