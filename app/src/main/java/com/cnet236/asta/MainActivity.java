package com.cnet236.asta;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.content.Intent;
import android.os.AsyncTask;
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
import android.widget.TextView;

import com.cnet236.asta.dummy.TestContent;

import java.util.ArrayList;

public class MainActivity extends ActionBarActivity {
    TestMaster tm;
    RunTestTask rt;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.container, new PlaceholderFragment())
                    .commit();
        }

        Bundle b = this.getIntent().getExtras();
        if(b!=null)
            tm = new TestMaster(b.getString("password"), getApplicationContext());
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
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

    public void openDetails(View view) {
        Intent i = new Intent(MainActivity.this, TestListActivity.class);
        MainActivity.this.startActivity(i);
    }

    public void runTests(View view) {
        showProgress(true);
        rt = new RunTestTask();
        rt.execute((Void) null);
    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
    private void showProgress(final boolean show) {
        final View mTestStatusView = findViewById(R.id.test_status);
        final View mTestActivityView = findViewById(R.id.results_activity);

        // On Honeycomb MR2 we have the ViewPropertyAnimator APIs, which allow
        // for very easy animations. If available, use these APIs to fade-in
        // the progress spinner.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
            int shortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);

            mTestStatusView.setVisibility(View.VISIBLE);
            mTestStatusView.animate()
                    .setDuration(shortAnimTime)
                    .alpha(show ? 1 : 0)
                    .setListener(new AnimatorListenerAdapter() {
                        @Override
                        public void onAnimationEnd(Animator animation) {
                            mTestStatusView.setVisibility(show ? View.VISIBLE : View.GONE);
                        }
                    });

            mTestActivityView.setVisibility(View.VISIBLE);
            mTestActivityView.animate()
                    .setDuration(shortAnimTime)
                    .alpha(show ? 0 : 1)
                    .setListener(new AnimatorListenerAdapter() {
                        @Override
                        public void onAnimationEnd(Animator animation) {
                            mTestActivityView.setVisibility(show ? View.GONE : View.VISIBLE);
                        }
                    });
        } else {
            // The ViewPropertyAnimator APIs are not available, so simply show
            // and hide the relevant UI components.
            mTestStatusView.setVisibility(show ? View.VISIBLE : View.GONE);
            mTestActivityView.setVisibility(show ? View.GONE : View.VISIBLE);
        }
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
            View rootView = inflater.inflate(R.layout.fragment_main, container, false);

            return rootView;
        }
    }

    public class RunTestTask extends AsyncTask<Void, Void, Boolean> {
        @Override
        protected Boolean doInBackground(Void... voids) {
            tm.runTests();

            return true;
        }

        @Override
        protected void onPostExecute(final Boolean success) {
            ArrayList<TestContent.TestResult> results = tm.getResults();
            TextView temp;

            switch(results.get(0).colour) {
                case 0:
                    temp = (TextView)findViewById(R.id.txtGreenNum);
                    temp.setText("1".toCharArray(), 0, 1);
                    break;
                case 1:
                    temp = (TextView)findViewById(R.id.txtYellowNum);
                    temp.setText("1".toCharArray(), 0, 1);
                    break;
                case 2:
                    temp = (TextView)findViewById(R.id.txtRedNum);
                    temp.setText("1".toCharArray(), 0, 1);
                    break;
            }

            showProgress(false);
        }
    }

}
