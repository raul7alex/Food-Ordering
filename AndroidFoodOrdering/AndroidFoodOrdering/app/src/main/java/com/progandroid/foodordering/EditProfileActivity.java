package com.progandroid.foodordering;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.progandroid.foodordering.entities.LoginObject;
import com.progandroid.foodordering.entities.SuccessObject;
import com.progandroid.foodordering.network.GsonRequest;
import com.progandroid.foodordering.network.VolleySingleton;
import com.progandroid.foodordering.util.CustomApplication;
import com.progandroid.foodordering.util.Helper;

import java.util.HashMap;
import java.util.Map;

public class EditProfileActivity extends AppCompatActivity {

    private static final String TAG = EditProfileActivity.class.getSimpleName();

    private TextView displayError;

    private EditText username;
    private EditText email;
    private EditText address;
    private EditText phoneNumber;

    private LoginObject loginUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);

        setTitle(getString(R.string.edit_user_profile));

        displayError = (TextView)findViewById(R.id.login_error);

        username = (EditText)findViewById(R.id.username);
        email = (EditText)findViewById(R.id.email);
        address = (EditText)findViewById(R.id.address);
        phoneNumber = (EditText)findViewById(R.id.phone_number);

        loginUser = ((CustomApplication)getApplication()).getLoginUser();
        username.setText(loginUser.getUsername());
        email.setText(loginUser.getEmail());
        address.setText(loginUser.getAddress());
        phoneNumber.setText(loginUser.getPhone());

        Button editUserButton = (Button)findViewById(R.id.edit_user_button);
        editUserButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int mId = loginUser.getId();
                String mUsername = loginUser.getUsername();
                String mEmail = loginUser.getEmail();
                String mAddress = loginUser.getAddress();
                String mPhone = loginUser.getPhone();

                editUserInRemoteServer(mId, mUsername, mEmail, mAddress, mPhone);
            }
        });
    }

    private void editUserInRemoteServer(int id, String username, String email, String address, String phoneNumber){
        Map<String, String> params = new HashMap<String,String>();
        params.put(Helper.ID, String.valueOf(id));
        params.put(Helper.USERNAME, username);
        params.put(Helper.EMAIL, email);
        params.put(Helper.ADDRESS, address);
        params.put(Helper.PHONE_NUMBER, phoneNumber);

        GsonRequest<SuccessObject> serverRequest = new GsonRequest<SuccessObject>(
                Request.Method.POST,
                Helper.PATH_TO_EDIT_USER,
                SuccessObject.class,
                params,
                createRequestSuccessListener(),
                createRequestErrorListener());

        serverRequest.setRetryPolicy(new DefaultRetryPolicy(
                Helper.MY_SOCKET_TIMEOUT_MS,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        VolleySingleton.getInstance(EditProfileActivity.this).addToRequestQueue(serverRequest);
    }

    private Response.Listener<SuccessObject> createRequestSuccessListener() {
        return new Response.Listener<SuccessObject>() {
            @Override
            public void onResponse(SuccessObject response) {
                try {
                    Log.d(TAG, "Json Response " + response.getSuccess());
                    if(response.getSuccess() == 1){
                        //clear shared session
                        ((CustomApplication)getApplication()).getShared().setUserData("");
                        // remove added input content
                        Toast.makeText(EditProfileActivity.this, R.string.update_success, Toast.LENGTH_LONG).show();
                        username.setText("");
                        email.setText("");
                        address.setText("");
                        phoneNumber.setText("");

                    }else{
                        Toast.makeText(EditProfileActivity.this, R.string.cannot_edit_user, Toast.LENGTH_LONG).show();
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
}
