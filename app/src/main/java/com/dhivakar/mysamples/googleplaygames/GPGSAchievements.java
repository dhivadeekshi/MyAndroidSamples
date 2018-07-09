package com.dhivakar.mysamples.googleplaygames;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.accounts.AccountManagerCallback;
import android.accounts.AccountManagerFuture;
import android.accounts.AuthenticatorException;
import android.accounts.OperationCanceledException;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.view.View;
import android.webkit.WebView;
import android.widget.CheckBox;
import android.widget.RadioButton;
import android.widget.TextView;

import com.dhivakar.mysamples.BaseAppCompatActivity;
import com.dhivakar.mysamples.R;
import com.dhivakar.mysamples.activity.WebViewActivity;
import com.dhivakar.mysamples.utils.HTTPRequestHandler;
import com.dhivakar.mysamples.utils.LogUtils;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInOptionsExtension;
import com.google.android.gms.auth.api.signin.GoogleSignInStatusCodes;
import com.google.android.gms.auth.api.signin.internal.GoogleSignInOptionsExtensionCreator;
import com.google.android.gms.common.Scopes;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.Scope;
import com.google.android.gms.games.Games;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.HashMap;
import java.util.Set;

public class GPGSAchievements extends BaseAppCompatActivity {

    private static final String TAG = "GPGSAchievements";
    private static final int REQUEST_CODE_SELECT_ACCOUNT = 2;
    private static final int REQUEST_CODE_SIGN_IN = 3;
    private static final int REQUEST_CODE_WEB_INTENT = 4;
    private static final int REQUEST_CODE_REQUEST_SCOPES = 5;
    private static final int REQUEST_CODE_WEB_PAGE = 6;
    private static final int REQUEST_CODE_SELECT_ACCOUNT_FURTHER = 7;
    private static final String ResetAchievementURL = "https://www.googleapis.com/games/v1management/achievements/reset";
    private static final String AuthorizeURLV2 = "https://accounts.google.com/o/oauth2/v2/auth";
    private static final String GetAccessTokenURLV4 = "https://www.googleapis.com/oauth2/v4/token";
    private static final String GetAccessTokenURL = "https://accounts.google.com/o/oauth2/token";
    private static final String VerifyAccessTokenURL = "https://www.googleapis.com/oauth2/v3/tokeninfo";
    private static final String RefreshAccessTokenURL = "";
    private static final String ClientSecret = "L9k0nyYxz9a8AIyECt56A1a7";
    private static final String RedirecrUriAuthorize = "com.ubisoft.dragonfire:/oauth2callback";
    private static final String RedirecrUriAuthorizeRedirect = "com.ubisoft.dragonfire:/oauth2redirect";
    private static final String RedirecrUriToken = "com.ubisoft.dragonfire:/oauth2callback";
    private static final String RedirecrUriTokenWebClient = "https://ubisoft.dragonfire.com/oauth2callback";
    private static final String RedirecrUri = "dragonfire://ubisoft.dragonfire.com/oauth2callback";

    private static final String[] ClientIds = {
            "265464518803.apps.googleusercontent.com", // Client Id
            "265464518803-3jnqov9h917mdj5c3kg0u39790tj9k7s.apps.googleusercontent.com", // WebClientId
            "265464518803-oahuqab9ukg046l8g9h9fpng32750nia.apps.googleusercontent.com", // AndroidClientId
            "265464518803-m2t36itrdceppu5hig2h7n6rbjfikchg.apps.googleusercontent.com" // WebClientId Auto
    };

    private int selectedSignInMode = 0;
    private int selectedClientId = 0;
    private int selectedScopes = 0;
    private StringBuilder error = new StringBuilder();
    private String authCode;
    private String accessToken;
    private String refreshToken;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gpgsachievements);

        //ConfigureGoogleSignIn(0);
        //AttemptSilentSignIn();
        UpdateActivityHeader(getString(R.string.gpgachievement_samples));
        UpdateUITexts();
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        LogUtils.i(this, "Achievements new Intent");
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        LogUtils.i(this, "Achievements restarted");

    }

    public void onClick(View v) {
        super.onClick(v);
        switch(v.getId())
        {
            // Reset Achievement
            case R.id.buttonResetAllAchievements: ResetAchievements(); break;
            case R.id.buttonAuthorizeAndReset: ResetAllAchievements(0); break;
            case R.id.buttonResetWebActivity: ResetAllAchievements(1); break;

            // Google SignIn
            case R.id.buttonSignIn: SignInGoogleAccount(); break;
            case R.id.buttonSignOut: SignOutGoogleAccount(); break;
            case R.id.buttonGetAccounts: GetAllAccounts(); break;

            // Access Tokens
            case R.id.buttonGetAccessToken: GetAccessToken(); break;
            case R.id.buttonRefreshAccessToken: RefreshAccessToken(); break;
            case R.id.buttonVerifyAccessToken: VerifyAccessToken(); break;

            // Select Client Ids
            case R.id.buttonClientId: if(((RadioButton)v).isChecked()) selectedClientId = 0; break;
            case R.id.buttonWebClientId: if(((RadioButton)v).isChecked()) selectedClientId = 1; break;
            case R.id.buttonAndroidClientId: if(((RadioButton)v).isChecked()) selectedClientId = 2; break;
            case R.id.buttonAtuoGeneratedClientId: if(((RadioButton)v).isChecked()) selectedClientId = 3; break;

            // Scopes
            case R.id.buttonRadioGames: ScopesSelected(); break;
            case R.id.buttonRadioPlus: ScopesSelected(); break;

            // Error
            case R.id.buttonClearError: error = new StringBuilder(); break;
        }

        UpdateUITexts();
        UpdateError("");
    }

    private void ScopesSelected()
    {
        CheckBox scopeGames = (CheckBox) findViewById(R.id.buttonRadioGames);
        CheckBox scopePlus = (CheckBox) findViewById(R.id.buttonRadioPlus);

        selectedScopes = 0;
        if(scopeGames.isChecked())
            selectedScopes += 1;
        if(scopePlus.isChecked())
            selectedScopes += 2;
    }

    private void UpdateUITexts()
    {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {

                TextView textSelectedClient = (TextView) findViewById(R.id.textSelectedClientId);
                TextView textReceivedAuthCode = (TextView) findViewById(R.id.textReceivedAuthCode);
                TextView textReceivedToken = (TextView) findViewById(R.id.textReceivedToken);
                TextView textRefreshToken = (TextView) findViewById(R.id.textRefreshToken);

                textSelectedClient.setText("ClientId:"+ClientIds[selectedClientId]);
                textReceivedAuthCode.setText("AuthCode:"+authCode);
                textReceivedToken.setText("Access Token:"+accessToken);
                textRefreshToken.setText("Refresh Token:"+refreshToken);
            }
        });
    }

    private void UpdateError(String error) {
        if(this.error == null) this.error = new StringBuilder();
        this.error.append(error);
        final String errorToDisplay = this.error.toString();
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                TextView textErrorText = (TextView) findViewById(R.id.textErrorText);
                textErrorText.setText("Error:" + errorToDisplay);
            }
        });
    }


    private void GetAllAccounts() {




        /*Intent intent = AccountPicker.newChooseAccountIntent(null, null,
                new String[] {"com.google", "com.google.android.legacyimap"},
                false, null, null, null, null);

    startActivity(intent);*/

        /*if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
        {
            if(checkSelfPermission(Manifest.permission.GET_ACCOUNTS) != PackageManager.PERMISSION_GRANTED)
            {
                requestPermissions(new String[]{Manifest.permission.GET_ACCOUNTS}, 1);
                return;
            }
        }*/


        final Account[] accounts = AccountManager.get(this).getAccounts();
        LogUtils.i(this, "Get All Accounts : " + accounts.length);
        for (Account account : accounts)
            LogUtils.i(this, "Get All Accounts accountName:" + account.name);


        startActivityForResult(AccountManager.newChooseAccountIntent(null, null,
                new String[] {"com.google", "com.google.android.legacyimap"},
                false, null, null, null, null), REQUEST_CODE_SELECT_ACCOUNT);
        AccountManager manager = (AccountManager) getSystemService(ACCOUNT_SERVICE);
        if(manager != null) {
            final Account[] accounts1 = manager.getAccounts();
            LogUtils.i(this, "Get All Accounts : " + accounts1.length);
            for (Account account : accounts1)
                LogUtils.i(this, "Get All Accounts accountName:" + account.name);


        }
    }

    private class OnTokenAcquired implements AccountManagerCallback<Bundle> {
        @Override
        public void run(AccountManagerFuture<Bundle> result) {
            try {
                // Get the result of the operation from the AccountManagerFuture.
                Bundle bundle = result.getResult();

                // The token is a named value in the bundle. The name of the value
                // is stored in the constant AccountManager.KEY_AUTHTOKEN.
                authCode = bundle.getString(AccountManager.KEY_AUTHTOKEN);

                Intent launch = (Intent) result.getResult().get(AccountManager.KEY_INTENT);
                if (launch != null) {
                    startActivityForResult(launch, REQUEST_CODE_SELECT_ACCOUNT_FURTHER);
                    return;
                }
            }catch (OperationCanceledException | IOException | AuthenticatorException e)
            {
                e.printStackTrace();
            }
        }
    }

    private class OnError implements Handler.Callback {
        @Override
        public boolean handleMessage(Message msg) {
            return false;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        Bundle extras = data!=null? data.getExtras():new Bundle();
        LogUtils.i(this, "onActivityResult requestCode:"+requestCode+" resultCode:"+resultCode+" data:"+data+" extras:"+extras.toString());
        switch (requestCode)
        {
            case REQUEST_CODE_SELECT_ACCOUNT_FURTHER:

                // The token is a named value in the bundle. The name of the value
                // is stored in the constant AccountManager.KEY_AUTHTOKEN.
                authCode = extras.getString(AccountManager.KEY_AUTHTOKEN);
                LogUtils.i(this,"onActivityResult authCode:"+authCode);
                break;

            case REQUEST_CODE_SELECT_ACCOUNT:
                LogUtils.i(this,"onActivityResult accountName:"+extras.getString("authAccount"));
                Account[] accounts = AccountManager.get(this).getAccounts();
                LogUtils.i(this, "Get All Accounts : " + accounts.length);
                for (Account account : accounts)
                    LogUtils.i(this, "Get All Accounts accountName:" + account.name);
                for(String key : extras.keySet())
                {
                    try{
                    LogUtils.i(this, "Get all accounts extras:["+key+"]:"+(extras.get(key) != null ? extras.get(key).toString():"null"));
                    }catch (NullPointerException e)
                    {
                        e.printStackTrace();
                    }
                }


                AccountManager am = AccountManager.get(this);
                Bundle options = new Bundle();

                //accounts = am.getAccountsByType("com.goolge");

                if(accounts != null && accounts.length > 0)
                am.getAuthToken(
                        accounts[0],                     // Account retrieved using getAccountsByType()
                        Games.SCOPE_GAMES.toString(),            // Auth scope
                        options,                        // Authenticator-specific options
                        this,                           // Your activity
                        new OnTokenAcquired(),          // Callback called when a token is successfully acquired
                        new Handler(new OnError()));    // Callback called if an error occurs

                break;

            case REQUEST_CODE_REQUEST_SCOPES:
                GoogleSignInAccount account = GoogleSignIn.getAccountForScopes(this, Games.SCOPE_GAMES);
                if(account != null)
                {
                    try{
                        LogUtils.i(this,"onActivityResult Account received for scopes");
                        PrintAccountDetails(account);
                        UpdateUITexts();
                    }
                    catch (Exception e)
                    {
                        LogUtils.i(this,"onActivityResult get account for scopes failed:"+e.getMessage());
                    }
                }
                else
                    CheckAndRequestScopes();
                break;

            case REQUEST_CODE_SIGN_IN:
                Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
                try{
                    LogUtils.i(this,"onActivityResult Retrive Account");
                    account = task.getResult(ApiException.class);
                    LogUtils.i(this,"onActivityResult Account retrived");
                    PrintAccountDetails(account);
                    authCode = account.getServerAuthCode();
                    UpdateUITexts();
                }
                catch (ApiException e)
                {
                    if(e.getStatusCode() == 8)
                    {
                        account = GoogleSignIn.getLastSignedInAccount(this);
                        PrintAccountDetails(account);
                        if(account != null) authCode = account.getServerAuthCode();
                    }
                    LogUtils.i(this,"onActivityResult get account statusCode:"+e.getStatusCode()+":"+ GoogleSignInStatusCodes.getStatusCodeString(e.getStatusCode()));
                }

                break;

            case REQUEST_CODE_WEB_INTENT:
                LogUtils.i(this,"onActivityResult Web Intent opened");
                break;

            case REQUEST_CODE_WEB_PAGE:
                LogUtils.i(this,"onActivityResult Web Page opened");
                break;
        }
    }

    private void PrintAccountDetails(GoogleSignInAccount account)
    {
        String accountName = "Null";
        try{
            LogUtils.i(this,"AccountDetails Name:"+account.getDisplayName());
            LogUtils.i(this,"AccountDetails Email:"+account.getEmail());
            LogUtils.i(this,"AccountDetails AuthCode:"+account.getServerAuthCode());
            LogUtils.i(this,"AccountDetails Id:"+account.getId());
            LogUtils.i(this,"AccountDetails IdToken:"+account.getIdToken());
            LogUtils.i(this,"AccountDetails ExpirationSecs:"+account.getExpirationTimeSecs());
            LogUtils.i(this,"AccountDetails PhotoUrl:"+account.getPhotoUrl());

            Set<Scope> scopes = account.getGrantedScopes();
            for (Scope scope:
                 scopes) {
                LogUtils.i(this,"AccountDetails GrantedScopes:"+scope);
            }
            scopes = account.getRequestedScopes();
            for (Scope scope:
                    scopes) {
                LogUtils.i(this,"AccountDetails RequestedScopes:"+scope);
            }
        }catch (Exception e)
        {
            LogUtils.i(this, "Print Account info for "+accountName+" crashed : "+e.getMessage());
        }
    }

    private GoogleSignInClient mGoogleSignInClient;
    private void ConfigureGoogleSignIn() {
        if(mGoogleSignInClient != null)
            mGoogleSignInClient.signOut();
        mGoogleSignInClient = GoogleSignIn.getClient(this, signInOptions());
    }

    private GoogleSignInOptions signInOptions()
    {
        GoogleSignInOptions.Builder builder = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_GAMES_SIGN_IN)
                .requestServerAuthCode(ClientIds[selectedClientId], true);

        switch (selectedScopes)
        {
            case 3: // Both Games and Plu Login
                builder.requestScopes(Games.SCOPE_GAMES, new Scope(Scopes.PLUS_LOGIN));
            case 2: // Plus Login
                builder.requestScopes(new Scope(Scopes.PLUS_LOGIN));
            case 1: // Games
                builder.requestScopes(Games.SCOPE_GAMES);
            default:
                break;
        }

        return builder.build();
    }

    private void AttemptSilentSignIn()
    {
        ConfigureGoogleSignIn();
        if(mGoogleSignInClient != null)
        {
            LogUtils.i(this, "Attempt SilentLogin GoogleSigninClinet");
            Task<GoogleSignInAccount> task = mGoogleSignInClient.silentSignIn();
            if(task.isSuccessful())
            {
                GoogleSignInAccount account = task.getResult();
                LogUtils.i(this, "SilentSignInSuccess");
                PrintAccountDetails(account);
            }
            else {
                LogUtils.i(this, "SilentLoginFailed" );
                task.addOnCompleteListener(new OnCompleteListener<GoogleSignInAccount>() {
                    @Override
                    public void onComplete(@NonNull Task<GoogleSignInAccount> task) {
                        LogUtils.i(TAG, "SilentLogin Task Complted");
                        try {
                            GoogleSignInAccount signInAccount = task.getResult(ApiException.class);
                            PrintAccountDetails(signInAccount);
                        } catch (ApiException apiException) {
                            // You can get from apiException.getStatusCode() the detailed error code
                            // e.g. GoogleSignInStatusCodes.SIGN_IN_REQUIRED means user needs to take
                            // explicit action to finish sign-in;
                            // Please refer to GoogleSignInStatusCodes Javadoc for details
                            //updateButtonsAndStatusFromErrorCode(apiException.getStatusCode());
                            LogUtils.e(TAG,"SilentLogin completed exception on fetch account:"+apiException.getMessage());
                        }
                    }
                });
            }
        }
    }

    private GoogleSignInAccount signedInAccount(){
        return GoogleSignIn.getLastSignedInAccount(this);
    }

    private void AuthorizeUsingGoogleSignIn(int mode) {
        ConfigureGoogleSignIn();
        GoogleSignInAccount account = signedInAccount();

        if (account != null) {
            LogUtils.i(this, "Already sigined in account : " + account.getDisplayName());
            PrintAccountDetails(account);
            UpdateUITexts();
        } else if (mGoogleSignInClient != null) {
            LogUtils.i(this, "Not sigined in account. Attempt Login");
            Intent signInIntent = mGoogleSignInClient.getSignInIntent();
            startActivityForResult(signInIntent, REQUEST_CODE_SIGN_IN);
        }
    }

    private static void SetAuthCode(String authCode)
    {

    }

    private void SignInGoogleAccount()
    {
        ConfigureGoogleSignIn();
        GoogleSignInAccount account = signedInAccount();

        if (account != null) {
            LogUtils.i(this, "Already sigined in account : " + account.getDisplayName());
            PrintAccountDetails(account);
            authCode = account.getServerAuthCode();
            UpdateUITexts();
        } else if (mGoogleSignInClient != null) {
            Intent signInIntent = mGoogleSignInClient.getSignInIntent();
            startActivityForResult(signInIntent, REQUEST_CODE_SIGN_IN);
        }
    }

    private void SignOutGoogleAccount() {
        if (signedInAccount() != null && mGoogleSignInClient != null)
            mGoogleSignInClient.signOut();
        authCode = "";
        accessToken = "";
        refreshToken = "";
        LogUtils.i(this, "Account Logged Out");
    }

    private String codeChallengeS256()
    {
        return  "";//Base64.encodeToString(SHA256(codeVerifier()));
    }

    private String codeVerifier()
    {
        return "";
    }

    private void Authorize(boolean justFun, HTTPRequestHandler.Callback onRequestCompleted)
    {
        if(justFun) {
            try {

                // Authorize using playground
                /*https://accounts.google.com/o/oauth2/v2/auth?
                redirect_uri=https://developers.google.com/oauthplayground&
                prompt=consent&
                response_type=code&
                client_id=407408718192.apps.googleusercontent.com&
                scope=https://www.googleapis.com/auth/plus.login+
                    https://www.googleapis.com/auth/plus.me+
                    https://www.googleapis.com/auth/userinfo.email+
                    https://www.googleapis.com/auth/userinfo.profile+
                    https://www.googleapis.com/auth/games&
                access_type=offline*/

                StringBuilder url = new StringBuilder();
                url.append(AuthorizeURLV2).append('?');
                url.append("client_id=").append(ClientIds[selectedClientId]); // Required
                url.append("&redirect_uri=").append(RedirecrUriTokenWebClient); // Required
                url.append("&response_type=").append("code"); // Required
                url.append("&scope=").append(Games.SCOPE_GAMES.toString());//.append(" ").append(Scopes.PLUS_LOGIN); // Required
                url.append("&access_type=").append("offline");
                url.append("&prompt=").append("consent");

                LogUtils.i(this, "Url : " + Uri.parse(url.toString()).toString());

                Intent webIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(url.toString()));
                startActivityForResult(webIntent, REQUEST_CODE_WEB_INTENT);

            } catch (Exception e) {
                LogUtils.e(this, "Authorize failed with exception:" + e.getMessage());
            }
        }
        else
            Authorize(onRequestCompleted);
    }

    private void Authorize(HTTPRequestHandler.Callback onRequestCompleted) {
        HashMap<String, String> properties = new HashMap<String, String>();
        properties.put("client_id", ClientIds[selectedClientId]); // Required
        properties.put("redirect_uri", RedirecrUriTokenWebClient); // Required
        properties.put("response_type", "code"); // Required
        properties.put("scope", Games.SCOPE_GAMES.toString());// +" "+Scopes.PLUS_LOGIN); // Required
        properties.put("access_type","offline");
        properties.put("prompt","consent");
        //properties.put("state",""); // Recommended
        boolean codeChallengeEncoded = false;
        //properties.put("code_challenge_method",codeChallengeEncoded? "S256":"plain"); // Recommended
        //properties.put("code_challenge",codeChallengeEncoded?codeChallengeS256():codeVerifier()); // Recommended
        //properties.put("login_hint",""); // Optional

        HTTPRequestHandler.PostRequest(AuthorizeURLV2, null, properties, onRequestCompleted);
    }

    private void VerifyAccessToken()
    {
        LogUtils.i(this, "Verify Access Token accessToken:"+accessToken);
        if(accessToken.isEmpty()) return;
        HashMap<String, String> properties = new HashMap<String, String>();
        properties.put("access_token", accessToken); // Required

        final Context context = this;
        HTTPRequestHandler.PostRequest(VerifyAccessTokenURL, null, properties, new HTTPRequestHandler.Callback() {
            @Override
            public void OnRequestSuccess(String response) {
                LogUtils.i(TAG, "Reset Achievement RefreshAccessToken Success : " + response, true, context);

                JSONObject responseJson = null;
                try {
                    responseJson = new JSONObject(response);
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                if(responseJson != null) LogUtils.i(TAG, "VerifyAccessToken : "+responseJson.toString());
            }

            @Override
            public void OnRequestFailed(String response, int responseCode) {
                LogUtils.e(TAG, "Reset Achievement VerifyAccessToken Failed : " + responseCode + ":" + response, true, context);
                UpdateError("Reset Achievement VerifyAccessToken Failed : " + responseCode + ":" + response);
            }
        });
    }

    private void RefreshAccessToken()
    {
        LogUtils.i(this, "Refresh Access Token refreshToken:"+refreshToken);
        if(refreshToken.isEmpty()) return;
        HashMap<String, String> properties = new HashMap<String, String>();
        properties.put("refresh_token", refreshToken); // Required
        properties.put("client_id", ClientIds[selectedClientId]); // Required
        properties.put("client_secret", ClientSecret); // Required
        properties.put("grant_type", "refresh_token"); // Required

        final Context context = this;
        HTTPRequestHandler.PostRequest(GetAccessTokenURLV4, null, properties, new HTTPRequestHandler.Callback() {
            @Override
            public void OnRequestSuccess(String response) {
                LogUtils.i(TAG, "Reset Achievement RefreshAccessToken Success : " + response, true, context);

                JSONObject responseJson = null;
                try {
                    responseJson = new JSONObject(response);
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                accessToken = "";
                if (responseJson != null) {
                    try {
                        accessToken = responseJson.getString("access_token");
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }

                if (!accessToken.isEmpty()) {
                    LogUtils.i(TAG, "Reset Achievement RefreshAccessToken Success Token: " + accessToken, true, context);
                } else {
                    LogUtils.e(TAG, "Reset Achievement RefreshAccessToken Failed Token not found", true, context);
                }
            }

            @Override
            public void OnRequestFailed(String response, int responseCode) {
                LogUtils.e(TAG, "Reset Achievement RefreshAccessToken Failed : " + responseCode + ":" + response, true, context);
                UpdateError("Reset Achievement RefreshAccessToken Failed : " + responseCode + ":" + response);
            }
        });
    }

    private boolean CheckAndRequestScopes()
    {
        switch (selectedScopes) {
            case 3: // Both
                    LogUtils.i(this, "Request permission for scopes Games and PlusLogin");
                    return CheckAndRequestScopes(Games.SCOPE_GAMES, new Scope(Scopes.PLUS_LOGIN));
            case 2: // PlusLogin
                    LogUtils.i(this, "Request permission for scopes PlusLogin");
                    return CheckAndRequestScopes(new Scope(Scopes.PLUS_LOGIN));
            case 1: // Games
                    LogUtils.i(this, "Request permission for scopes Games");
                    return CheckAndRequestScopes(Games.SCOPE_GAMES);
            default:
                break;
        }
        return false;
    }

    private boolean CheckAndRequestScopes(@NonNull Scope... scopes) {

        GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(this);
        if (account != null && !GoogleSignIn.hasPermissions(account, scopes)) {
            GoogleSignIn.requestPermissions(this, REQUEST_CODE_REQUEST_SCOPES, account, scopes);
            return true;
        }
        return false;
    }

    private void GetAccessToken()
    {
        if(!CheckAndRequestScopes() && authCode != null && !authCode.isEmpty()) {
            final Context context = this;
            GetAccessToken(authCode, new HTTPRequestHandler.Callback() {
                @Override
                public void OnRequestSuccess(String response) {
                    LogUtils.i(TAG, "Reset Achievement GetAccessToken Success : " + response, true, context);

                    JSONObject responseJson = null;
                    try {
                        responseJson = new JSONObject(response);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    accessToken = "";
                    refreshToken = "";
                    if (responseJson != null) {
                        try {
                            accessToken = responseJson.getString("access_token");
                            refreshToken = responseJson.getString("refresh_token");
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

                    if (!accessToken.isEmpty()) {
                        LogUtils.i(TAG, "Reset Achievement GetAccessToken Success Token: " + accessToken, true, context);
                    } else {
                        LogUtils.e(TAG, "Reset Achievement GetAccessToken Failed Token not found", true, context);
                    }

                    UpdateUITexts();
                }

                @Override
                public void OnRequestFailed(String response, int responseCode) {
                    LogUtils.e(TAG, "Reset Achievement GetAccessToken Failed : " + responseCode + ":" + response, true, context);
                    UpdateError("Reset Achievement GetAccessToken Failed : " + responseCode + ":" + response);
                }
            });
        }
    }

    private void GetAccessToken(String code, HTTPRequestHandler.Callback onRequestCompleted)
    {
        // Accesstoken for playground
        /*code=4%2FAADKwTeZ7ePeiTk5Ws15CkokJ9J8la7yu4Wlq5n9SfYe00KYwRgygPY7zWQgO0dCT9q_EoOfifJIZN7Qbqbhma4&
        redirect_uri=https://developers.google.com/oauthplayground&
        client_id=407408718192.apps.googleusercontent.com&
        client_secret=************&
        scope=&
        grant_type=authorization_code*/

        LogUtils.i(this, "selectedClientId : "+selectedClientId);
        HashMap<String, String> properties = new HashMap<String, String>();
        properties.put("code", code); // Required
        properties.put("client_id", ClientIds[selectedClientId]); // Required
        properties.put("client_secret", ClientSecret); // Required
        properties.put("redirect_uri", RedirecrUriTokenWebClient); // Required
        properties.put("grant_type", "authorization_code"); // Required
        properties.put("scope", Games.SCOPE_GAMES.toString());// +" "+Scopes.PLUS_LOGIN); // Required
        //properties.put("code_verifier", codeVerifier()); // Optional

        HTTPRequestHandler.PostRequest(GetAccessTokenURLV4, null, properties, onRequestCompleted);
    }

    private void ResetAchievements(String token, HTTPRequestHandler.Callback onRequestCompleted)
    {
        HashMap<String, String> properties = new HashMap<String, String>();
        properties.put("access_token", token); // Required
        //properties.put("scope", Games.SCOPE_GAMES.toString()); // Optional

        HTTPRequestHandler.PostRequest(ResetAchievementURL, null, properties, onRequestCompleted);
    }

    private void ResetAchievements()
    {
        if(!accessToken.isEmpty())
        {
            final Context context = this;
            ResetAchievements(accessToken, new HTTPRequestHandler.Callback() {
                @Override
                public void OnRequestSuccess(String response) {
                    LogUtils.i(TAG, "Reset Achievements Success : " + response, true, context);
                }

                @Override
                public void OnRequestFailed(String response, int responseCode) {
                    LogUtils.e(TAG, "Reset Achievements Failed : " + responseCode+":"+response, true, context);
                    UpdateError("Reset Achievements Failed : " + responseCode+":"+response);
                }
            });
        }
    }

    private void OnAuthCodeReceived(String code, final Context context)
    {
        authCode = code;
        UpdateUITexts();
        GetAccessToken();
    }

    private void OnAccessTokenReceived(String accessToken, final Context context) {
        this.accessToken = accessToken;
        ResetAchievements();
    }

    private void ResetAllAchievements(final int mode) {
        LogUtils.i(this, "Reset all achievements");
        final Context context = this;

        Authorize(mode == 0, new HTTPRequestHandler.Callback() {
            @Override
            public void OnRequestSuccess(String response) {
                LogUtils.i(TAG, "Reset Achievement Authorize Success : " + response, true, context);

                if(mode == 1){
                    Intent intent = new Intent(context, WebViewActivity.class);
                    Bundle data = new Bundle();
                    data.putString("htmlPage", response);
                    intent.putExtras(data);
                    startActivityForResult(intent, REQUEST_CODE_WEB_PAGE);
                    LogUtils.i(TAG, "Reset Achievement Authorize Success htmlPage: " + response, true, context);
                }
                else if (response.contains("code")) {
                    String code = response.substring(response.indexOf("code"));
                    LogUtils.i(TAG, "Reset Achievement Authorize Success Code: " + code, true, context);
                    authCode = code;
                    UpdateUITexts();
                    //OnAuthCodeReceived(code, context);
                } else {
                    String error = "";
                    if (response.contains("error"))
                        error = response.substring(response.indexOf("error"));
                    LogUtils.i(TAG, "Reset Achievement Authorize Failed code not found" + (error.isEmpty() ? "" : " " + error), true, context);
                }
            }

            @Override
            public void OnRequestFailed(String response, int responseCode) {
                LogUtils.i(TAG, "Reset Achievement Authorize Failed : " + responseCode + ":" + response, true, context);
                UpdateError("Reset Achievements Failed : " + responseCode + ":" + response);
            }
        });

    }
}
