package com.cnet236.asta;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.TextView;

import com.cnet236.asta.dummy.TestContent;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Arrays;

import javax.net.ssl.HttpsURLConnection;

/**
 * Activity which displays a login screen to the user
 */
public class LoginActivity extends Activity {

    /**
     * Keep track of the login task to ensure we can cancel it if requested.
     */
    private UserLoginTask mAuthTask = null;

    // Values for email and password at the time of the login attempt.
    //private String mEmail;
    private String mPassword;

    // UI references.
    //private EditText mEmailView;
    private EditText mPasswordView;
    private View mLoginFormView;
    private View mLoginStatusView;
    private TextView mLoginStatusMessageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_login);

        mPasswordView = (EditText) findViewById(R.id.password);
        mPasswordView.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int id, KeyEvent keyEvent) {
                if (id == R.id.login || id == EditorInfo.IME_NULL) {
                    attemptLogin();
                    return true;
                }
                return false;
            }
        });

        mLoginFormView = findViewById(R.id.login_form);
        mLoginStatusView = findViewById(R.id.login_status);
        mLoginStatusMessageView = (TextView) findViewById(R.id.login_status_message);

        findViewById(R.id.sign_in_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                attemptLogin();
            }
        });

        try {
            getApplicationContext().openFileInput("allowedHash");
            Log.v("Unlocker", "Previous run detected.");
        } catch(FileNotFoundException e) {
            Log.v("Unlocker", "First run detected");
            Intent i = new Intent(LoginActivity.this, NewPasswordActivity.class);
            LoginActivity.this.startActivity(i);
        }
        Log.v("LoginActivity", "onCreate finished");
    }


    /*@Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.login, menu);
        return true;
    }*/

    /**
     * Attempts to sign in or register the account specified by the login form.
     * If there are form errors (invalid email, missing fields, etc.), the
     * errors are presented and no actual login attempt is made.
     */
    public void attemptLogin() {
        if (mAuthTask != null) {
            return;
        }

        // Reset errors.
        //mEmailView.setError(null);
        mPasswordView.setError(null);

        // Store values at the time of the login attempt.
        //mEmail = mEmailView.getText().toString();
        mPassword = mPasswordView.getText().toString();

        boolean cancel = false;
        View focusView = null;

        // Check for a valid password.
        if (TextUtils.isEmpty(mPassword)) {
            mPasswordView.setError(getString(R.string.error_field_required));
            focusView = mPasswordView;
            cancel = true;
        } else if (mPassword.length() < 4) {
            mPasswordView.setError(getString(R.string.error_invalid_password));
            focusView = mPasswordView;
            cancel = true;
        }

        if (cancel) {
            // There was an error; don't attempt login and focus the first
            // form field with an error.
            focusView.requestFocus();
        } else {
            // Show a progress spinner, and kick off a background task to
            // perform the user login attempt.
            mLoginStatusMessageView.setText(R.string.login_progress_signing_in);
            showProgress(true);
            mAuthTask = new UserLoginTask();
            mAuthTask.execute((Void) null);
        }
    }

    public void registerUser (View view) {
        Intent i = new Intent(LoginActivity.this, NewPasswordActivity.class);
        LoginActivity.this.startActivity(i);
    }

    /**
     * Shows the progress UI and hides the login form.
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
    private void showProgress(final boolean show) {
        // On Honeycomb MR2 we have the ViewPropertyAnimator APIs, which allow
        // for very easy animations. If available, use these APIs to fade-in
        // the progress spinner.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
            int shortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);

            mLoginStatusView.setVisibility(View.VISIBLE);
            mLoginStatusView.animate()
                    .setDuration(shortAnimTime)
                    .alpha(show ? 1 : 0)
                    .setListener(new AnimatorListenerAdapter() {
                        @Override
                        public void onAnimationEnd(Animator animation) {
                            mLoginStatusView.setVisibility(show ? View.VISIBLE : View.GONE);
                        }
                    });

            mLoginFormView.setVisibility(View.VISIBLE);
            mLoginFormView.animate()
                    .setDuration(shortAnimTime)
                    .alpha(show ? 0 : 1)
                    .setListener(new AnimatorListenerAdapter() {
                        @Override
                        public void onAnimationEnd(Animator animation) {
                            mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
                        }
                    });
        } else {
            // The ViewPropertyAnimator APIs are not available, so simply show
            // and hide the relevant UI components.
            mLoginStatusView.setVisibility(show ? View.VISIBLE : View.GONE);
            mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
        }
    }

    /**
     * Represents an asynchronous login/registration task used to authenticate
     * the user.
     */
    public class UserLoginTask extends AsyncTask<Void, Void, Boolean> {
        @Override
        protected Boolean doInBackground(Void... params) {
            EditText textV;

            try {
                textV = (EditText) findViewById(R.id.password);
                mPassword = textV.getText().toString();

                String mUsername = ((EditText)findViewById(R.id.username)).getText().toString();

                Log.v("Unlocker", "password challenge: " + mPassword + " - " + mUsername);
                Locker diplomat = new Locker("diplomat", mPassword, getApplicationContext());
                String hash = byteToString(diplomat.getKeyHash());
                Log.v("Unlocker", "hash: " + hash);
                return tryAuth(mUsername, hash);
                //return checkHash(diplomat.getKeyHash());
                /*Locker guard = new Locker("guard", "password", getApplicationContext());
                if (guard.equals(diplomat) == true) {
                    Log.i("Unlocker", "auth: allowed");
                    return true;
                }
                else {
                    Log.i("Unlocker", "auth: denied");
                    return false;
                }*/
            } catch (Exception e) {
                Log.v("Unlocker", "login error");
                e.printStackTrace();
                return false;
            }
        }

        @Override
        protected void onPostExecute(final Boolean success) {
            mAuthTask = null;
            showProgress(false);

            if (success) {
                Intent i = new Intent(LoginActivity.this, MainActivity.class);
                i.putExtra("from", "login");
                i.putExtra("password", mPassword);
                LoginActivity.this.startActivity(i);
            } else {
                mPasswordView.setError(getString(R.string.error_incorrect_password));
                mPasswordView.requestFocus();
            }
        }

        @Override
        protected void onCancelled() {
            mAuthTask = null;
            showProgress(false);
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

            Log.v("Unlocker", "diplomatHash = "+byteToString(diplomatHash));
            Log.v("Unlocker", "allowedHash =  "+byteToString(allowedHash));

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

        private boolean tryAuth(String name, String hash) {
            StringBuffer response = null;

            try {
                String url = "http://"+ TestContent.authHost+"/api.php";
                URL obj = new URL(url);
                //HttpsURLConnection con = (HttpsURLConnection) obj.openConnection();
                HttpURLConnection con = (HttpURLConnection) obj.openConnection();

                //add reuqest header
                con.setRequestMethod("POST");
                con.setRequestProperty("Accept-Language", "en-US,en;q=0.5");
                String urlParameters = "action=auth&n="+name+"&h="+hash;

                // Send post request
                con.setDoOutput(true);
                DataOutputStream wr = new DataOutputStream(con.getOutputStream());
                wr.writeBytes(urlParameters);
                wr.flush();
                wr.close();

                int responseCode = con.getResponseCode();

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

            if(response.toString().equals("success"))
                return true;
            else
                return false;
        }
    }
}
