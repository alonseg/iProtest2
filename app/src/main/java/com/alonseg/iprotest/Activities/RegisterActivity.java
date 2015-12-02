package com.alonseg.iprotest.Activities;

import android.app.AlertDialog;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.alonseg.iprotest.Consts;
import com.alonseg.iprotest.Objects.Log;
import com.alonseg.iprotest.R;
import com.parse.ParseException;
import com.parse.ParseUser;
import com.parse.SaveCallback;
import com.parse.SignUpCallback;

public class RegisterActivity extends FragmentActivity {

    public final String TAG = "REGISTER";

    public static final int MIN_NAME_LEN = 4;
    public static final int MAX_NAME_LEN = 20;
    public static final int MIN_PASS_LEN = 4;
    private String username;
    private String password;
    private EditText editPass;
    private EditText editPassVerify;
    private EditText editUsername;

    private Context con;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        con = this;

        initViews();

        TextView tv = (TextView) findViewById(R.id.registerTtl);
        tv.setTypeface(Consts.appFont);
        tv = (TextView) findViewById(R.id.registerText);
        tv.setTypeface(Consts.appFont);

        Button performReg = (Button) findViewById(R.id.want_to_register);
        performReg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!validateUsername()){
                    clearUsernameField();
                }else if (!validatePass()){
                    clearPasswordFields();
                }else{
                    final ParseUser user = new ParseUser();
                    user.setUsername(username);
                    user.setPassword(password);

                    user.signUpInBackground(new SignUpCallback() {
                        public void done(ParseException e) {
                            if (e == null) {
                                // Hooray! Let them use the app now.
                                Log.v(TAG, "Successfully Registered");
                                AlertDialog dialog = new AlertDialog.Builder(con)
                                        .setMessage(getResources().getString(R.string.register_success)).show();
                                TextView textView = (TextView) dialog.findViewById(android.R.id.message);
                                textView.setTypeface(Consts.appFont);
                                textView.setGravity(Gravity.CENTER);
                                textView.setTextSize(30);
                                user.saveInBackground(new SaveCallback() {
                                    @Override
                                    public void done(ParseException e) {
                                        if (e == null) {
                                            Log.v(TAG, "saved user following list");
                                        } else {
                                            Log.v(TAG, "failed to save user following list");
                                        }
                                    }
                                });
                                setResult(Consts.REQ_SIGN_SUC);
                                try {
                                    Thread.sleep(2000);
                                } catch (InterruptedException e1) {
                                    Log.d(TAG, e1.getMessage());
                                }
                                finish();
                            } else {
                                if (e.getCode() == Consts.USERNAME_TAKEN){
                                    editUsername.setError("Username already taken");
                                    clearUsernameField();
                                    ParseUser.logOutInBackground();
                                }else {
                                    // Sign up didn't succeed. Look at the ParseException
                                    // to figure out what went wrong
                                    editUsername.setError("Something went wrong, try again later");
                                    Log.v("REG_ERR", e.toString());
                                    ParseUser.logOutInBackground();
                                    clearPasswordFields();
                                    clearUsernameField();
                                }
                            }
                        }
                    });

                }

            }
        });
    }

    private void initViews() {
        editPassVerify = (EditText) findViewById(R.id.reg_password_verify);
        editUsername = (EditText) findViewById(R.id.reg_username);
        editPass = (EditText) findViewById(R.id.reg_password);
    }

    private void clearUsernameField() {
        editUsername.setText("");
        editUsername.setError("invalid username");
    }

    private void clearPasswordFields() {
        editPass.setText("");
        editPassVerify.setText("");

    }

    private boolean validatePass() {

        String tmp = editPass.getText().toString();
        if (tmp.length() > MAX_NAME_LEN || tmp.length() < MIN_PASS_LEN){
            editPass.setError("Password length is invalid,\nshould be " +
                    MIN_PASS_LEN + " - " + MAX_NAME_LEN + " characters long");
            return false;
        }


        String tmp2 = editPassVerify.getText().toString();
        if (!tmp.equals(tmp2))
        {
            editPassVerify.setError("Password verify doesn't match!");
            return false;
        }
        password = tmp;
        return true;
    }

    private boolean validateUsername() {

        String tmp = editUsername.getText().toString();
        if (tmp.length() > MAX_NAME_LEN || tmp.length() < MIN_NAME_LEN){
            editUsername.setError("Name length is invalid,\nshould be " +
                            MIN_NAME_LEN + " - " + MAX_NAME_LEN + " characters long");
            return false;
        }
        username = tmp;
        return true;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_register, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
