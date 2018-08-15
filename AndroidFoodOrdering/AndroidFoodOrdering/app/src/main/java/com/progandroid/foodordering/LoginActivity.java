package com.progandroid.foodordering;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.google.gson.Gson;
import com.progandroid.foodordering.entities.LoginObject;
import com.progandroid.foodordering.network.GsonRequest;
import com.progandroid.foodordering.network.VolleySingleton;
import com.progandroid.foodordering.util.CustomApplication;
import com.progandroid.foodordering.util.Helper;

import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.HashMap;
import java.util.Map;

public class LoginActivity extends AppCompatActivity {

    private static final String TAG = LoginActivity.class.getSimpleName();

    private TextView errorDisplay;
    private TextView signInformation;
    private EditText emailInput;
    private EditText passwordInput;
    String toSend;
    ClientThread clientThread;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        ActionBar actionBar = getSupportActionBar();
        if(null != actionBar){
            actionBar.hide();
        }

        isUserLoggedIn();

        errorDisplay = (TextView)findViewById(R.id.login_error);
        signInformation = (TextView)findViewById(R.id.sign_in_notice);

        emailInput = (EditText)findViewById(R.id.email);
        passwordInput = (EditText)findViewById(R.id.password);

        Button signUpButton = (Button)findViewById(R.id.sign_up);
        signUpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent signInIntent = new Intent(LoginActivity.this, RegistrationActivity.class);
                startActivity(signInIntent);
            }
        });


        Button loginButton = (Button)findViewById(R.id.login_button);
        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String enteredEmail = emailInput.getText().toString().trim();
                String enteredPassword = passwordInput.getText().toString().trim();
                toSend = emailInput.getText().toString();
                toSend += "-" + passwordInput.getText().toString()+".";

                /////////////////////////////////////////////////////////////////////////
                MyClientTask myClientTask = new MyClientTask(
                        "192.168.1.246",
                        49001);
                myClientTask.execute();
                    Intent intentMain = new Intent(LoginActivity.this, MainActivity.class);
                    startActivity(intentMain);

                //////////////////////////////////////////////////////////////////////////

                if(TextUtils.isEmpty(enteredEmail) || TextUtils.isEmpty(enteredPassword)){
                    Helper.displayErrorMessage(LoginActivity.this, getString(R.string.fill_all_fields));
                }
                if(!Helper.isValidEmail(enteredEmail)){
                    Helper.displayErrorMessage(LoginActivity.this, getString(R.string.invalid_email));
                }

                //make server call for user authentication
                authenticateUserInRemoteServer(enteredEmail, enteredPassword);
            }
        });
    }

    private void authenticateUserInRemoteServer(String email, String password){
        Map<String, String> params = new HashMap<String,String>();
        params.put(Helper.EMAIL, email);
        params.put(Helper.PASSWORD, password);

        GsonRequest<LoginObject> serverRequest = new GsonRequest<LoginObject>(
                Request.Method.POST,
                Helper.PATH_TO_SERVER_LOGIN,
                LoginObject.class,
                params,
                createRequestSuccessListener(),
                createRequestErrorListener());

        serverRequest.setRetryPolicy(new DefaultRetryPolicy(
                Helper.MY_SOCKET_TIMEOUT_MS,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        VolleySingleton.getInstance(LoginActivity.this).addToRequestQueue(serverRequest);
    }

    private Response.Listener<LoginObject> createRequestSuccessListener() {
        return new Response.Listener<LoginObject>() {
            @Override
            public void onResponse(LoginObject response) {
                try {
                    Log.d(TAG, "Json Response " + response.getLoggedIn());
                    if(response.getLoggedIn().equals("1")){
                        //save login data to a shared preference
                        String userData = ((CustomApplication)getApplication()).getGsonObject().toJson(response);
                        ((CustomApplication)getApplication()).getShared().setUserData(userData);

                        // navigate to restaurant home
                        Intent loginIntent = new Intent(LoginActivity.this, MainActivity.class);
                        startActivity(loginIntent);
                    }else{
                        Toast.makeText(LoginActivity.this, R.string.failed_login, Toast.LENGTH_LONG).show();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        };
    }

    private Response.ErrorListener createRequestErrorListener() {
        return new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
            }
        };
    }

    private void isUserLoggedIn(){
        Gson mGson = ((CustomApplication)getApplication()).getGsonObject();
        String storedUser = ((CustomApplication)getApplication()).getShared().getUserData();
        LoginObject userObject = mGson.fromJson(storedUser, LoginObject.class);
        if(toSend=="raul7_alex@yahoo.com-test."){
            Intent intentMain = new Intent(LoginActivity.this, MainActivity.class);
            startActivity(intentMain);
        }
    }

    public class MyClientTask extends AsyncTask<String, Void, Void> {

        String dstAddress;
        int dstPort;

        MyClientTask(String addr, int port) {
            dstAddress = addr;
            dstPort = port;
        }

        @Override
        protected Void doInBackground(String... params) {
            try {
                Socket socket = new Socket(dstAddress, dstPort);

                OutputStream outputStream = socket.getOutputStream();
                PrintStream printStream = new PrintStream(outputStream);
                printStream.println("1");
                printStream.println(toSend);

                socket.close();

            } catch (UnknownHostException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            return null;
        }

    }

    private void updateState(String state) {

    }

    private void updateRxMsg(String toSend) {

    }

    private void clientEnd() {
        clientThread = null;
    }

    public static class ClientHandler extends Handler {
        public static final int UPDATE_STATE = 0;
        public static final int UPDATE_MSG = 1;
        public static final int UPDATE_END = 2;
        private LoginActivity parent;

        public ClientHandler(LoginActivity parent) {
            super();
            this.parent = parent;
        }

        @Override
        public void handleMessage(Message msg) {

            switch (msg.what) {
                case UPDATE_STATE:
                    parent.updateState((String) msg.obj);
                    break;
                case UPDATE_MSG:
                    parent.updateRxMsg((String) msg.obj);
                    break;
                case UPDATE_END:
                    parent.clientEnd();
                    break;
                default:
                    super.handleMessage(msg);
            }

        }

    }
}
