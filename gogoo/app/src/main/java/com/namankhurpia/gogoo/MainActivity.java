package com.namankhurpia.gogoo;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInApi;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.OptionalPendingResult;
import com.google.android.gms.common.api.ResolvingResultCallbacks;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import static com.google.android.gms.auth.api.signin.GoogleSignIn.getLastSignedInAccount;

public class MainActivity extends AppCompatActivity implements GoogleApiClient.OnConnectionFailedListener {

    private RelativeLayout profilesection;
    private Button signout;
    private SignInButton signin;
    public TextView name,email;
    private ImageView picture;
    private GoogleApiClient googleApiClient;
    private static final int REQ_CODE = 9001;
    private static final String CLIENT_ID= "676618756231-6n7ovrdn7dudtrd2skfe04hs0ihq7r5l.apps.googleusercontent.com";
    private static final String CLIENT_SECRET = "geRWKdvb5DoTd9UoxmZ9yv2b";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        profilesection=(RelativeLayout) findViewById(R.id.linearlayout);
        signout=(Button)findViewById(R.id.logout);
        signin=(SignInButton)findViewById(R.id.login);
        name=(TextView)findViewById(R.id.name_display_here);
        email=(TextView)findViewById(R.id.email_display_here);
        picture=(ImageView)findViewById(R.id.profile_picture);


        GoogleSignInOptions signInOptions=new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(CLIENT_ID)
                .requestEmail()
                .build();
        googleApiClient =new GoogleApiClient.Builder(this).enableAutoManage(this, this).addApi(Auth.GOOGLE_SIGN_IN_API,signInOptions ).build();


        /*OptionalPendingResult<GoogleSignInResult> pendingResult = Auth.GoogleSignInApi.silentSignIn(googleApiClient);
        if (pendingResult != null) {
            if (pendingResult.isDone()) {
                // If the user's cached credentials are valid, the OptionalPendingResult will be "done"
                // and the GoogleSignInResult will be available instantly.
                Log.d("TAG", " ----------------  CACHED SIGN-IN ------------");
                System.out.println("pendingResult is done = ");
                GoogleSignInResult signInResult = pendingResult.get();
                if (signInResult != null) {
                    GoogleSignInAccount signInAccount = signInResult.getSignInAccount();
                    if (signInAccount != null) {
                        String emailAddress = signInAccount.getEmail();
                        String token = signInAccount.getIdToken();
                        System.out.println("token = " + token);
                        System.out.println("emailAddress = " + emailAddress);
                    }
                }
            } else {
                pendingResult.setResultCallback(new ResultCallback<GoogleSignInResult>() {
                    @Override
                    public void onResult(GoogleSignInResult googleSignInResult) {
                        System.out.println("googleSignInResult = " + googleSignInResult);

                    }
                });
            }
        }
        else {
            Toast.makeText(getApplicationContext(), "nope", Toast.LENGTH_SHORT).show();
        }
*/

        signin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                GoogleSignIn.getLastSignedInAccount(getApplicationContext());
                SignIn();

            }
        });

        signout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signout();
            }
        });






    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }


    private void SignIn()
    {

        Intent intent=Auth.GoogleSignInApi.getSignInIntent(googleApiClient);
        startActivityForResult(intent, REQ_CODE);
    }

    private void signout()
    {
        Auth.GoogleSignInApi.signOut(googleApiClient).setResultCallback(new ResultCallback<Status>() {
            @Override
            public void onResult(@NonNull Status status) {
                update(false);
            }
        });

    }

    private void handleresult(GoogleSignInResult result)
    {
        if(result.isSuccess())
        {
            GoogleSignInAccount account=result.getSignInAccount();
            String name_s = account.getDisplayName();
            String email_s =account.getEmail();
            String img_url = account.getPhotoUrl().toString();
            String id_token=account.getIdToken();
            name.setText(name_s);
            email.setText(email_s);
            Glide.with(this).load(img_url).into(picture);
            update(true);
            saveidstate(id_token);

        }
        else
        {
            update(false);
        }

    }

    private void saveidstate(String id_token) {
        SharedPreferences sharedpreferences = getSharedPreferences("YOUR_PREFERENCE_NAME", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedpreferences.edit();
        editor.putString("GG_LOGGED", id_token);
        editor.commit();

    }

    private void update(boolean isLogin)
    {
        if(isLogin)
        {
            signin.setVisibility(View.GONE);

        }
        else
        {

        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode==REQ_CODE)
        {
            GoogleSignInResult result= Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            handleresult(result);
        }

    }
}
