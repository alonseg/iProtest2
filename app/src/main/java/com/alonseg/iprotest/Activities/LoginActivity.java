package com.alonseg.iprotest.Activities;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.alonseg.iprotest.Consts;
import com.alonseg.iprotest.Objects.Log;
import com.alonseg.iprotest.R;
import com.parse.LogInCallback;
import com.parse.ParseException;
import com.parse.ParseInstallation;
import com.parse.ParseObject;
import com.parse.ParseUser;

import java.util.List;


public class LoginActivity extends Activity{// implements LoaderCallbacks<Cursor>  {

    // UI references.
    private EditText mUsernameView;
    private EditText mPasswordView;
    private View mProgressView;
    private View mLoginFormView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        final Intent intent = getIntent();

        setTitleAndText(intent);

        handleLogin();

        handleRegister();

        initViews();

    }

    private void handleRegister() {
        final Intent regIntent = new Intent(this, RegisterActivity.class);
        Button register = (Button) findViewById(R.id.register_btn);
        register.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivityForResult(regIntent, Consts.REQ_SIGN);
            }
        });
    }

    private void handleLogin() {
        // Set up the login form.
        mUsernameView = (EditText) findViewById(R.id.username);

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

        Button mEmailSignInButton = (Button) findViewById(R.id.user_sign_in_button);
        mEmailSignInButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                attemptLogin();
            }
        });
    }

    private void initViews() {
        mLoginFormView = findViewById(R.id.login_form);
        mProgressView = findViewById(R.id.login_progress);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == Consts.REQ_SIGN){
            if (resultCode == Consts.REQ_SIGN_SUC){
                app.initUser();
                setResult(Consts.REQ_SIGN_SUC);
                Log.v("LOG_SUC", "Logged in successfully");
                finish();
            }

        }
    }

    private void setTitleAndText(Intent intent) {
        //Set the title
        String ttl = "NO TITLE";
        if (intent.hasExtra("ttl"))
            ttl = intent.getStringExtra("ttl");
        TextView loginTtl = (TextView) findViewById(R.id.loginTtl);
        Consts.setTextToView(loginTtl, ttl);

        //Set the text
        String message = "";
        if (intent.hasExtra("opp"))
            message = intent.getStringExtra("opp");
        TextView loginText = (TextView) findViewById(R.id.loginText);
        Consts.setTextToView(loginText, message);
    }

    /**
     * Attempts to sign in or register the account specified by the login form.
     * If there are form errors (invalid email, missing fields, etc.), the
     * errors are presented and no actual login attempt is made.
     */
    public void attemptLogin() {

        // Reset errors.
        mUsernameView.setError(null);
        mPasswordView.setError(null);

        // Store values at the time of the login attempt.
        String username = mUsernameView.getText().toString();
        String password = mPasswordView.getText().toString();

        boolean cancel = false;
        View focusView = null;

        // Check for a valid password, if the user entered one.
        if (!TextUtils.isEmpty(password) && !isPasswordValid(password)) {
            mPasswordView.setError(getString(R.string.error_invalid_password));
            focusView = mPasswordView;
            cancel = true;
        }

        // Check for a valid username.
        if (TextUtils.isEmpty(username)) {
            mUsernameView.setError(getString(R.string.error_field_required));
            focusView = mUsernameView;
            cancel = true;
        } else if (!isUsernameValid(username)) {
            mUsernameView.setError(getString(R.string.error_invalid_email));
            focusView = mUsernameView;
            cancel = true;
        }

        if (cancel) {
            // There was an error; don't attempt login and focus the first
            // form field with an error.
            focusView.requestFocus();
        } else {
            // Show a progress spinner, and kick off a background task to
            // perform the user login attempt.
            showProgress(true);
            ParseUser.logInInBackground(username, password, new LogInCallback() {
                public void done(ParseUser user, ParseException e) {
                    showProgress(false);
                    if (user != null) {
                        // Hooray! The user is logged in.
                        List<ParseObject> list = Consts.getFollowingListObjects(app.getFollowingList());
                        if (list != null) {
                            ParseInstallation installation = ParseInstallation.getCurrentInstallation();
                            app.initUser();

                            installation.addAllUnique(Consts.PU_FOLLOWING, list);
                            installation.saveInBackground();
                        }

                        setResult(Consts.REQ_SIGN_SUC);
                        Log.v("LOG_SUC", "Logged in successfully");
                        finish();
                    } else {
                        // Signup failed. Look at the ParseException to see what happened.
                        Log.v("LOG_SUC", "Logged in failed " + e.toString());
                    }

                }
            });
        }
    }

    private boolean isUsernameValid(String Username) {
            if (Username.length() > RegisterActivity.MAX_NAME_LEN ||
                    Username.length() < RegisterActivity.MIN_NAME_LEN){
                Toast.makeText(getApplicationContext(), "Name length is invalid, should be " +
                        RegisterActivity.MIN_NAME_LEN + " - " + RegisterActivity.MAX_NAME_LEN +
                        "characters long", Toast.LENGTH_LONG).show();
                return false;
            }
            return true;
    }

    private boolean isPasswordValid(String password) {
        return !(password.length() > RegisterActivity.MAX_NAME_LEN
                || password.length() < RegisterActivity.MIN_PASS_LEN);
    }

    /**
     * Shows the progress UI and hides the login form.
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
    public void showProgress(final boolean show) {
        // On Honeycomb MR2 we have the ViewPropertyAnimator APIs, which allow
        // for very easy animations. If available, use these APIs to fade-in
        // the progress spinner.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
            int shortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);

            mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
            mLoginFormView.animate().setDuration(shortAnimTime).alpha(
                    show ? 0 : 1).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
                }
            });

            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            mProgressView.animate().setDuration(shortAnimTime).alpha(
                    show ? 1 : 0).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
                }
            });
        } else {
            // The ViewPropertyAnimator APIs are not available, so simply show
            // and hide the relevant UI components.
            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
        }
    }
}

