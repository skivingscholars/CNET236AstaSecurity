package com.cnet236.asta;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Parcelable;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.cnet236.asta.dummy.TestContent;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class NewPasswordActivity extends ActionBarActivity {
    static String oldPW;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_password);

        Bundle b = this.getIntent().getExtras();

        if(b != null) {
            oldPW = b.getString("pw");
            switchToChangePassword();
        }
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
        String user = ((TextView)findViewById(R.id.txtUser)).getText().toString();
        FileOutputStream file;
        byte[] hash;

        if(pw.equals(cpw) == false) {
            Log.v("cnet236NewPassword", "testing: " + pw + " : " + cpw);
            EditText pass = (EditText) findViewById(R.id.password);
            pass.setError(getString(R.string.passwordsdontmatch));
            pass.requestFocus();
            return;
        } else {
            new UserRegisterTask().execute((Void) null);
        }


        /*Locker allowed = new Locker("diplomat", pw, getApplicationContext());

        try {
            file = getApplicationContext().openFileOutput("allowedHash", Context.MODE_PRIVATE);
            hash = allowed.getKeyHash();
            file.write(hash, 0, hash.length);
            file.close();
        } catch (Exception e) {
            e.printStackTrace();
            return;
        } */

        //Intent i = new Intent(NewPasswordActivity.this, LoginActivity.class);
        //NewPasswordActivity.this.startActivity(i);
    }



    private void switchToChangePassword() {
        Button b = (Button)findViewById(R.id.btnSavePassword);


        b.setText("Confirm new password");
        b.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {
                String[] s = {"results", "TripTestFile", "HoneypotFile"};
                String newPW = ((EditText)findViewById(R.id.password)).getText().toString();
                String confirmPW = ((EditText)findViewById(R.id.confirmPassword)).getText().toString();

                if(newPW.equals(confirmPW)) {
                    for (String string: s) {
                        Locker l = new Locker(string, oldPW, getApplicationContext());
                        l.changePassword(newPW);
                        try {
                            FileOutputStream file = getApplicationContext().openFileOutput("allowedHash", Context.MODE_PRIVATE);
                            byte[] hash = l.getKeyHash();
                            file.write(hash, 0, hash.length);
                            file.close();
                        } catch (Exception e) {
                            e.printStackTrace();
                            return;
                        }
                        Intent i = new Intent(NewPasswordActivity.this, MainActivity.class);
                        i.putExtra("from", "newpassword");
                        i.putExtra("password", newPW);
                        NewPasswordActivity.this.startActivity(i);
                    }
                } else {
                    Log.v("cnet236NewPassword", "testing: " + newPW + " : " + confirmPW);
                    EditText pass = (EditText) findViewById(R.id.password);
                    pass.setError(getString(R.string.passwordsdontmatch));
                    pass.requestFocus();
                    return;
                }
            }
        });
    }

    public class UserRegisterTask extends AsyncTask<Void, Void, Boolean> {
        @Override
        protected Boolean doInBackground(Void... params) {
            String pw = ((TextView)findViewById(R.id.password)).getText().toString();
            String user = ((TextView)findViewById(R.id.txtUser)).getText().toString();
            Locker diplomat = new Locker("diplomat", pw, getApplicationContext());
            String hash = byteToString(diplomat.getKeyHash());
            return registerUser(user, hash);
        }

        @Override
        protected void onPostExecute(final Boolean success) {
            if (success) {
                Intent i = new Intent(NewPasswordActivity.this, LoginActivity.class);
                NewPasswordActivity.this.startActivity(i);
            } else {
                ((EditText)findViewById(R.id.password)).setError("Error, could not register user");
                ((EditText)findViewById(R.id.password)).requestFocus();
            }
        }

        private boolean registerUser(String name, String hash) {
            StringBuffer response = null;

            try {
                String url = "http://"+ TestContent.authHost+"/api.php";
                URL obj = new URL(url);
                //HttpsURLConnection con = (HttpsURLConnection) obj.openConnection();
                HttpURLConnection con = (HttpURLConnection) obj.openConnection();

                //add reuqest header
                con.setRequestMethod("POST");
                con.setRequestProperty("Accept-Language", "en-US,en;q=0.5");
                String urlParameters = "action=register&n="+name+"&h="+hash;

                // Send post request
                con.setDoOutput(true);
                DataOutputStream wr = new DataOutputStream(con.getOutputStream());
                wr.writeBytes(urlParameters);
                wr.flush();
                wr.close();

                BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
                String inputLine;
                response = new StringBuffer();

                while ((inputLine = in.readLine()) != null) {
                    response.append(inputLine);
                }
                in.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
            Log.v("auth", "response - " + response.toString());

            if(response.toString().equals("rd"))
                return true;
            else
                return false;
        }

        private String byteToString(byte[] bytes) {
            StringBuilder sb = new StringBuilder();
            for (byte b : bytes)
                sb.append(Integer.toHexString((b & 0xff)));

            return sb.toString();
        }
    }
}
