package com.cnet236.asta;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import java.io.FileOutputStream;

public class NewPasswordActivity extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_password);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.new_password, menu);
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

    public void tryPassword(View v) {
        String pw = ((TextView)findViewById(R.id.password)).getText().toString();
        String cpw = ((TextView)findViewById(R.id.confirmPassword)).getText().toString();
        FileOutputStream file;
        byte[] hash;

        if(pw.equals(cpw) == false) {
            Log.v("cnet236NewPassword", "testing: " + pw + " : " + cpw);
            EditText pass = (EditText) findViewById(R.id.password);
            pass.setError(getString(R.string.passwordsdontmatch));
            pass.requestFocus();
            return;
        }

        Locker allowed = new Locker("diplomat", pw, getApplicationContext());

        try {
            file = getApplicationContext().openFileOutput("allowedHash", Context.MODE_PRIVATE);
            hash = allowed.getKeyHash();
            file.write(hash, 0, hash.length);
            file.close();
        } catch (Exception e) {
            e.printStackTrace();
            return;
        }

        Intent i = new Intent(NewPasswordActivity.this, LoginActivity.class);
        NewPasswordActivity.this.startActivity(i);
    }
}
