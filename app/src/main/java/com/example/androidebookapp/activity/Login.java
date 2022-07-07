package com.example.androidebookapp.activity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.androidebookapp.R;
import com.example.androidebookapp.response.LoginRP;
import com.example.androidebookapp.response.RegisterRP;
import com.example.androidebookapp.rest.ApiClient;
import com.example.androidebookapp.rest.ApiInterface;
import com.example.androidebookapp.util.API;
import com.example.androidebookapp.util.Events;
import com.example.androidebookapp.util.GlobalBus;
import com.example.androidebookapp.util.Method;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.checkbox.MaterialCheckBox;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textview.MaterialTextView;
import com.google.gson.Gson;
import com.google.gson.JsonObject;

import org.jetbrains.annotations.NotNull;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Arrays;

import cn.refactor.library.SmoothCheckBox;
import io.github.inflationx.viewpump.ViewPumpContextWrapper;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class Login extends AppCompatActivity {

    private Method method;
    private SmoothCheckBox checkBox;
    private MaterialCheckBox checkBoxTerms;
    private TextInputEditText editTextEmail, editTextPassword;

    public static final String mypreference = "mypref";
    public static final String prefEmail = "pref_email";
    public static final String prefPassword = "pref_password";
    public static final String prefCheck = "pref_check";
    private static SharedPreferences pref;
    private static SharedPreferences.Editor editor;
    private ProgressDialog progressDialog;

    //Google login
    GoogleSignInClient mGoogleSignInClient;
    private static final int RC_SIGN_IN = 007;

    //Facebook login
    private CallbackManager callbackManager;
    private static final String EMAIL = "email";

    private InputMethodManager imm;

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(ViewPumpContextWrapper.wrap(newBase));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        method = new Method(Login.this);
        method.forceRTLIfSupported();

        pref = getSharedPreferences(mypreference, 0); // 0 - for private mode
        editor = pref.edit();

        imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);

        progressDialog = new ProgressDialog(Login.this);

        // Configure sign-in to request the user's ID, email address, and basic
        // profile. ID and basic profile are included in DEFAULT_SIGN_IN.
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();

        // Build a GoogleSignInClient with the options specified by gso.
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);

        //facebook button
        callbackManager = CallbackManager.Factory.create();

        editTextEmail = findViewById(R.id.editText_email_login);
        editTextPassword = findViewById(R.id.editText_password_login);

        MaterialButton buttonLogin = findViewById(R.id.button_login);
        MaterialButton buttonSkip = findViewById(R.id.button_skip_login);
        final RelativeLayout relGoogleSign = findViewById(R.id.rel_google_login);
        final FrameLayout frameLayoutFbSign = findViewById(R.id.frameLayout_login);
        MaterialTextView textViewSignUp = findViewById(R.id.textView_signUp_login);
        MaterialTextView textViewFp = findViewById(R.id.textView_fp_login);
        MaterialTextView textViewTerms = findViewById(R.id.textView_terms_login);
        checkBoxTerms = findViewById(R.id.checkbox_terms_login);
        checkBox = findViewById(R.id.checkbox_login);
        checkBox.setChecked(false);

        if (pref.getBoolean(prefCheck, false)) {
            editTextEmail.setText(pref.getString(prefEmail, null));
            editTextPassword.setText(pref.getString(prefPassword, null));
            checkBox.setChecked(true);
        } else {
            editTextEmail.setText("");
            editTextPassword.setText("");
            checkBox.setChecked(false);
        }

        checkBox.setOnCheckedChangeListener((checkBox, isChecked) -> {
            if (isChecked) {
                editor.putString(prefEmail, editTextEmail.getText().toString());
                editor.putString(prefPassword, editTextPassword.getText().toString());
                editor.putBoolean(prefCheck, true);
            } else {
                editor.putBoolean(prefCheck, false);
            }
            editor.commit();
        });

        buttonLogin.setOnClickListener(v -> login());

        relGoogleSign.setOnClickListener(v -> {
            if (checkBoxTerms.isChecked()) {
                signIn();
            } else {
                method.alertBox(getResources().getString(R.string.please_select_terms));
            }
        });

        frameLayoutFbSign.setOnClickListener(v -> {
            if (checkBoxTerms.isChecked()) {
                if (v == frameLayoutFbSign) {
                    LoginManager.getInstance().logInWithReadPermissions(Login.this, Arrays.asList(EMAIL));
                }
            } else {
                method.alertBox(getResources().getString(R.string.please_select_terms));
            }
        });

        LoginManager.getInstance().registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                fbUser(loginResult);
            }

            @Override
            public void onCancel() {
            }

            @Override
            public void onError(FacebookException error) {
                Toast.makeText(Login.this, error.toString(), Toast.LENGTH_SHORT).show();
            }
        });

        textViewSignUp.setOnClickListener(v -> {
            Method.loginBack = false;
            startActivity(new Intent(Login.this, Register.class));
        });

        textViewTerms.setOnClickListener(v -> {
            Method.loginBack = false;
            startActivity(new Intent(Login.this, TermsConditions.class));
        });

        buttonSkip.setOnClickListener(v -> {

            if (Method.loginBack) {
                Method.loginBack = false;
                onBackPressed();
            } else {
                startActivity(new Intent(Login.this, MainActivity.class));
                finish();
            }

        });

        textViewFp.setOnClickListener(v -> {
            Method.loginBack = false;
            startActivity(new Intent(Login.this, ForgetPassword.class));
        });

    }

    //Google login
    private void signIn() {
        if (method.isNetworkAvailable()) {
            Intent signInIntent = mGoogleSignInClient.getSignInIntent();
            startActivityForResult(signInIntent, RC_SIGN_IN);
        } else {
            method.alertBox(getResources().getString(R.string.internet_connection));
        }

    }

    //Google login get callback
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        callbackManager.onActivityResult(requestCode, resultCode, data);

        // Result returned from launching the Intent from GoogleSignInClient.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            // The Task returned from this call is always completed, no need to attach
            // a listener.
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            handleSignInResult(task);
        }

    }

    //Google login
    private void handleSignInResult(Task<GoogleSignInAccount> completedTask) {
        try {
            GoogleSignInAccount account = completedTask.getResult(ApiException.class);

            // Signed in successfully, show authenticated UI.

            assert account != null;
            String id = account.getId();
            String name = account.getDisplayName();
            String email = account.getEmail();

            registerSocialNetwork(id, name, email, "google");

        } catch (ApiException e) {
            // The ApiException status code indicates the detailed failure reason.
            // Please refer to the GoogleSignInStatusCodes class reference for more information.
        }
    }

    //facebook login get email and name
    private void fbUser(LoginResult loginResult) {
        GraphRequest graphRequest = GraphRequest.newMeRequest(loginResult.getAccessToken(), new GraphRequest.GraphJSONObjectCallback() {
            @Override
            public void onCompleted(JSONObject object, GraphResponse response) {
                try {
                    String id = object.getString("id");
                    String name = object.getString("name");
                    String email = object.getString("email");
                    registerSocialNetwork(id, name, email, "facebook");
                } catch (JSONException e) {
                    try {
                        String id = object.getString("id");
                        String name = object.getString("name");
                        registerSocialNetwork(id, name, "", "facebook");
                    } catch (JSONException e1) {
                        e1.printStackTrace();
                    }
                }
            }
        });
        Bundle parameters = new Bundle();
        parameters.putString("fields", "id,name,email"); // Parameters that we ask for facebook
        graphRequest.setParameters(parameters);
        graphRequest.executeAsync();
    }

    private boolean isValidMail(String email) {
        return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }

    public void login() {

        String email = editTextEmail.getText().toString();
        String password = editTextPassword.getText().toString();

        editTextEmail.setError(null);
        editTextPassword.setError(null);

        if (!isValidMail(email) || email.isEmpty()) {
            editTextEmail.requestFocus();
            editTextEmail.setError(getResources().getString(R.string.please_enter_email));
        } else if (password.isEmpty()) {
            editTextPassword.requestFocus();
            editTextPassword.setError(getResources().getString(R.string.please_enter_password));
        } else {

            editTextEmail.clearFocus();
            editTextPassword.clearFocus();
            imm.hideSoftInputFromWindow(editTextEmail.getWindowToken(), 0);
            imm.hideSoftInputFromWindow(editTextPassword.getWindowToken(), 0);

            if (method.isNetworkAvailable()) {
                login(email, password);
            } else {
                method.alertBox(getResources().getString(R.string.internet_connection));
            }

        }
    }

    public void login(final String sendEmail, final String sendPassword) {

        progressDialog.show();
        progressDialog.setMessage(getResources().getString(R.string.loading));
        progressDialog.setCancelable(false);

        JsonObject jsObj = (JsonObject) new Gson().toJsonTree(new API(Login.this));
        jsObj.addProperty("email", sendEmail);
        jsObj.addProperty("password", sendPassword);
        jsObj.addProperty("method_name", "user_login");
        ApiInterface apiService = ApiClient.getClient().create(ApiInterface.class);
        Call<LoginRP> call = apiService.getLogin(API.toBase64(jsObj.toString()));
        call.enqueue(new Callback<LoginRP>() {
            @Override
            public void onResponse(@NotNull Call<LoginRP> call, @NotNull Response<LoginRP> response) {

                try {
                    LoginRP loginRP = response.body();
                    assert loginRP != null;

                    if (loginRP.getStatus().equals("1")) {

                        if (loginRP.getSuccess().equals("1")) {

                            method.editor.putBoolean(method.pref_login, true);
                            method.editor.putString(method.profileId, loginRP.getUser_id());
                            method.editor.putString(method.userImage, loginRP.getUser_image());
                            method.editor.putString(method.loginType, "normal");
                            method.editor.commit();

                            Toast.makeText(Login.this, loginRP.getMsg(), Toast.LENGTH_SHORT).show();

                            if (checkBox.isChecked()) {
                                editor.putString(prefEmail, editTextEmail.getText().toString());
                                editor.putString(prefPassword, editTextPassword.getText().toString());
                                editor.putBoolean(prefCheck, true);
                                editor.commit();
                            }

                            editTextEmail.setText("");
                            editTextPassword.setText("");

                            if (Method.loginBack) {
                                Method.loginBack = false;
                                Events.Login loginNotify = new Events.Login("");
                                GlobalBus.getBus().post(loginNotify);
                                onBackPressed();
                            } else {
                                startActivity(new Intent(Login.this, MainActivity.class)
                                        .setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK));
                                finishAffinity();
                            }


                        } else {
                            method.alertBox(loginRP.getMsg());
                        }

                    } else {
                        method.alertBox(loginRP.getMessage());
                    }

                } catch (Exception e) {
                    Log.d("exception_error", e.toString());
                    method.alertBox(getResources().getString(R.string.failed_try_again));
                }

                progressDialog.dismiss();

            }

            @Override
            public void onFailure(@NotNull Call<LoginRP> call, @NotNull Throwable t) {
                // Log error here since request failed
                Log.e("fail", t.toString());
                progressDialog.dismiss();
                method.alertBox(getResources().getString(R.string.failed_try_again));
            }
        });
    }

    public void registerSocialNetwork(String id, String sendName, String sendEmail, String type) {

        progressDialog.show();
        progressDialog.setMessage(getResources().getString(R.string.loading));
        progressDialog.setCancelable(false);

        JsonObject jsObj = (JsonObject) new Gson().toJsonTree(new API(Login.this));
        jsObj.addProperty("name", sendName);
        jsObj.addProperty("email", sendEmail);
        jsObj.addProperty("auth_id", id);
        jsObj.addProperty("type", type);
        jsObj.addProperty("device_id", method.getDeviceId());
        jsObj.addProperty("method_name", "user_register");
        ApiInterface apiService = ApiClient.getClient().create(ApiInterface.class);
        Call<RegisterRP> call = apiService.getRegisterDetail(API.toBase64(jsObj.toString()));
        call.enqueue(new Callback<RegisterRP>() {
            @Override
            public void onResponse(@NotNull Call<RegisterRP> call, @NotNull Response<RegisterRP> response) {

                try {
                    RegisterRP registerRP = response.body();
                    assert registerRP != null;

                    if (registerRP.getStatus().equals("1")) {

                        if (registerRP.getSuccess().equals("1")) {

                            method.editor.putBoolean(method.pref_login, true);
                            method.editor.putString(method.profileId, registerRP.getUser_id());
                            method.editor.putString(method.loginType, type);
                            method.editor.commit();

                            Toast.makeText(Login.this, registerRP.getMsg(), Toast.LENGTH_SHORT).show();

                            if (Method.loginBack) {
                                Events.Login loginNotify = new Events.Login("");
                                GlobalBus.getBus().post(loginNotify);
                                Method.loginBack = false;
                                onBackPressed();
                            } else {
                                startActivity(new Intent(Login.this, MainActivity.class));
                                finishAffinity();
                            }

                        } else {
                            if (type.equals("google")) {
                                mGoogleSignInClient.signOut()
                                        .addOnCompleteListener(Login.this, task -> {
                                            method.editor.putBoolean(method.pref_login, false);
                                            method.editor.commit();
                                        });
                            } else {
                                LoginManager.getInstance().logOut();
                            }
                            method.alertBox(registerRP.getMsg());
                        }

                    } else {
                        method.alertBox(registerRP.getMessage());
                    }

                } catch (Exception e) {
                    Log.d("exception_error", e.toString());
                    method.alertBox(getResources().getString(R.string.failed_try_again));
                }

                progressDialog.dismiss();

            }

            @Override
            public void onFailure(@NotNull Call<RegisterRP> call, @NotNull Throwable t) {
                // Log error here since request failed
                Log.e("fail", t.toString());
                progressDialog.dismiss();
                method.alertBox(getResources().getString(R.string.failed_try_again));
            }
        });
    }

}

