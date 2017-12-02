package firebase.jin1ib.com.firemessenger.views;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ProgressBar;

import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import firebase.jin1ib.com.firemessenger.R;
import firebase.jin1ib.com.firemessenger.models.User;

/**
 * A login screen that offers login via email/password.
 */
public class LoginActivity extends AppCompatActivity {

    private View mProgressView;

    private SignInButton mSginInbtn;

    private GoogleApiClient mGoogleAPIClient;

    private GoogleSignInOptions mGoogleSignInoptions;

    private FirebaseAuth mAuth;
    private String TAG = "signIn ";
    private static final int GOOGLE_LOGIN_OPEN = 100;

    private FirebaseAnalytics mFirebaseAnalytics;

    private FirebaseDatabase mDatabase;

    private DatabaseReference mUserRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        mProgressView = (ProgressBar) findViewById(R.id.login_progress);
        mSginInbtn = (SignInButton) findViewById(R.id.google_sign_in_btn);
        mAuth = FirebaseAuth.getInstance(); //싱글턴 객체로 되어있음
        mFirebaseAnalytics = FirebaseAnalytics.getInstance(this);   //wakelock과
        mDatabase = FirebaseDatabase.getInstance(); //싱글턴으로 되어있기 때문에 객체는 실질적으로 내부에서 하나만 생성된다
        mUserRef = mDatabase.getReference("users"); //DB를 위치할 부분이  users의 하위에 저장하는 것이므로, user의 레퍼런스를 가져오는 것.

        // [START config_signin]
        // Configure Google Sign In
        GoogleSignInOptions mGoogleSignInoptions = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))  //web_client_id를 통해서
                .requestEmail()                                             //이메일을 요청함 구글 로그인할때 요청할 정보를 세팅한것.
                .build();
        //-----------------------------------------------
        // [START api client]
        // Configure Google Sign In
        mGoogleAPIClient = new GoogleApiClient.Builder(this)
                //프레임액티비티가 라이프사이클을 관리할 수 있게 도움 onstart에서 실행해주고 onstop에서 끝어주도록 ,,,접속이 끊어졌을때
                .enableAutoManage(this/*FragmentActivity*/, new GoogleApiClient.OnConnectionFailedListener() {
                    @Override
                    public void onConnectionFailed(ConnectionResult connectionResult) {
                        //실패시 처리 하는 부분
                    }
                })
                .addApi(Auth.GOOGLE_SIGN_IN_API,mGoogleSignInoptions ) //연결할 APi의 명칭을 상수로 집어넣음
                .build();
        // [END config_signin]

        mSginInbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signIn();
            }
        });
    }
        private void signIn() {
            Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleAPIClient);  //계정을 선택할 수 있도록 한 창
            startActivityForResult(signInIntent, GOOGLE_LOGIN_OPEN); //GOOGLE_LOGIN_OPEN 액티비티를 띄워줄때 주는 리퀘스트 코드
        }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
            // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
            if (requestCode == GOOGLE_LOGIN_OPEN) {     //GOOGLE_OGIN_OPEN 코드 값이같으면
                GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data); //GoogleSignInApi의 result값을 가지고 온다
                if (result.isSuccess()) {
                    // Google Sign In was successful, authenticate with Firebase
                    GoogleSignInAccount account = result.getSignInAccount();    //account 정보를 가지고 와서
                    firebaseAuthWithGoogle(account);                    //파이어베이스함수에 어카운트 정보를 넣는다
                } else {
                    // Google Sign In failed, update UI appropriately
                    // ...
                }
            }
    }
    private void firebaseAuthWithGoogle(GoogleSignInAccount acct) {
        AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);
        mAuth.signInWithCredential(credential)  //여기서 얻어진 자격증명으로 로그인
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {//완료가 되면 AuthResult라는 곳으로 콜백, 데이터가 넘어옴
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isComplete()) { //isComplete는 실패든 성공이든 호출 isSuesses는 성공일경우에만.
                            if (task.isSuccessful()) {
                                //파이어베이스에서는 파이어베이스 유저 type이라는 자료형으로 유저의 데이터를 가지고 올 수 있다.
                                FirebaseUser firebaseUser = task.getResult().getUser(); //성공했을때만 가지고옴
                                final User user = new User();
                                user.setEmail(firebaseUser.getEmail());
                                user.setName(firebaseUser.getDisplayName());
                                user.setUid(firebaseUser.getUid());
                                mUserRef.child(user.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
                                            @Override
                                            public void onDataChange(DataSnapshot dataSnapshot) {
                                                if( !dataSnapshot.exists()) {
                                                    mUserRef.child(user.getUid()).setValue(user, new DatabaseReference.CompletionListener() {
                                                        @Override
                                                        //setValue는 비동기 작업이 되기 때문에 이 작업이 완료가 됬는지 안됬는지 알 수가 없다.
                                                        // 그래서 정상적으로 컴플리트가 된 경우에만 로그를 쌓는것으로 한다.
                                                        public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                                                            Snackbar.make(mProgressView, "로그인에 성공하였습니다.", Snackbar.LENGTH_LONG).show();
                                                            if (databaseError == null) {
                                                                startActivity(new Intent(LoginActivity.this, MainActivity.class)); // 로그인에서 main 화면으로 보내느것.
                                                                finish(); // 액티비티가 넘어가고 인증창 멈춤
                                                                Bundle eventBundle = new Bundle();          //이벤트는 번들로 받아야 하기 때문에 생성
                                                                eventBundle.putString("email", user.getEmail()); //이메일만 번들에 입력
                                                                mFirebaseAnalytics.logEvent(FirebaseAnalytics.Event.LOGIN, eventBundle);
                                                                //콜백절에서 실행되기 때문에 데이터가 변경되지 않도록 final이라는 예약어로 데이터가 변경되는 것을 방지해야한다
                                                                // 즉, user를 final로 만듬
                                                                //로그 확인은  if문에 브래이크를 걸고 result를 확인해보면됨
                                                            }
                                                        }
                                                    }); //해당 users는 users DB 밑에 userid들이 들어있는것이므로
                                                    //바로위 user를 사용 할 수 없기 때문에 객체를 새로 만든다.
                                                }
                                                else{
                                                    //만약에 존재한다면 기존의 데이터가 손상되지 않게함
                                                    startActivity(new Intent(LoginActivity.this,MainActivity.class));
                                                    finish();
                                                }
                                            }

                                            @Override
                                            public void onCancelled(DatabaseError databaseError) {

                                            }
                                        });



                               //key값을 파이어베이스에서 제공해줌 지금입력한건

                            }
                            else {
                                Snackbar.make(mProgressView,"로그인에 실패하였습니다.",Snackbar.LENGTH_LONG).show();
                            }
                        }
//                        if (task.isSuccessful()) {
//                            // Sign in success, update UI with the signed-in user's information
//                            Log.d(TAG, "signInWithCredential:success");
//                            FirebaseUser user = mAuth.getCurrentUser();
//                            updateUI(user);
//                        } else {
//                            // If sign in fails, display a message to the user.
//                            Log.w(TAG, "signInWithCredential:failure", task.getException());
//                            Toast.makeText(GoogleSignInActivity.this, "Authentication failed.",
//                                    Toast.LENGTH_SHORT).show();
//                            updateUI(null);
//                        }

                        // ...
                    }
                });
    }

}



