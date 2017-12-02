package firebase.jin1ib.com.firemessenger.views;


import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Iterator;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import firebase.jin1ib.com.firemessenger.R;
import firebase.jin1ib.com.firemessenger.models.User;

/**
 * A simple {@link Fragment} subclass.
 */
public class FriendFragment extends Fragment {

    @BindView(R.id.search_area)
    LinearLayout mSearchArea;   //친구 찾기 버튼

    @BindView(R.id.edtContent)
    EditText edtEmail;   //Email


    //위를 private로 하지 않은 이유는 butterknife의 경우 private로 하면 읽어 올 수 없기 때문

    private FirebaseUser mFirebaseUser;

    private FirebaseAuth mFirebaseAuth; //인증이 이미 로그인쪽에서 되어있기 때문에 auth가 데이터를 가지고 있다.

    private FirebaseDatabase mFirebaseDb;

    private DatabaseReference mFriendsDBRef;
    private DatabaseReference mUserDBRef;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View friendView = inflater.inflate(R.layout.fragment_friends, container,false);//레이아웃 인플레이트를 이용해서 가져오기
        //ButterKnife.bind(this)는 액티비티할때만
        ButterKnife.bind(this,friendView); //이거는 friendView에 bind한다는 의미

        mFirebaseAuth = FirebaseAuth.getInstance(); // 유저의 인스턴스를 가지고옴.
        mFirebaseUser = mFirebaseAuth.getCurrentUser(); // 현재 유저의 정보를 User에 저장
        mFirebaseDb =  FirebaseDatabase.getInstance(); //인스턴스를 가지고오기

        mFriendsDBRef = mFirebaseDb.getReference("users").child(mFirebaseUser.getUid()).child("friends"); //users/{유저 UID}/friends 저장
        mUserDBRef = mFirebaseDb.getReference("users"); //users/{유저 UID}/friends 저장
        return friendView;
    }
    public void  toggleSearchbar(){
        mSearchArea.setVisibility(mSearchArea.getVisibility() == View.VISIBLE ? View.GONE : View.VISIBLE);
    }
    @OnClick(R.id.findBtn) //아이디중 find버튼이랑 onclick을 할 것이다.
    public void addFriend(){
        // 1. 입력된(검색한) 이메일을 가져옵니다.
        final String inputEmail = edtEmail.getText().toString();
        // 2. 이메일이 입력되지 않았다면 이메일을 입력해주시라는 메시지를 띄워줍니다.
        if ( inputEmail.isEmpty()){
            Snackbar.make(mSearchArea, "이메일을 입력해주세요.",Snackbar.LENGTH_LONG).show();
            return;
        }
        // 3. 자기 자신을 친구로 등록할 수 없기 때문에 FirebaseUser의 email이 입력한 이메일과 같다면, 자기자신은 등록 불가 메시지를 띄워줍니다.
        if(inputEmail.equals(mFirebaseUser.getEmail())){
            Snackbar.make(mSearchArea,"자기자신은 친구로 등록할 수 없습니다.",Snackbar.LENGTH_LONG).show();
            return;
        }
        // 3. 이메일이 정상이라면 나의 정보를 조회하여 이미등록된 친구인지를 판단하고
        mFriendsDBRef.addListenerForSingleValueEvent(new ValueEventListener() {
            //addListenerForSingleValueEvent() 한번만 읽고 안읽음 ; //addValueEventListener() 계속읽음
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Iterable<DataSnapshot> friendsIteratble = dataSnapshot.getChildren(); //"user/uid/friends/에 있는 데이터 "1" "2" "3"이 있는것.
                // Iterable을 이용하면 모든 child를 가져올 수 있다.
                Iterator<DataSnapshot> friendsIterator = friendsIteratble.iterator();


                while (friendsIterator.hasNext()){ //friendsIterator에 데이터가 없을때까지 돌림
                    User user = friendsIterator.next().getValue(User.class);
                    if(user.getEmail().equals(inputEmail)) {
                        Snackbar.make(mSearchArea,"이미등록된 친구입니다.",Snackbar.LENGTH_LONG).show();
                    }
                }

                // 4. users db에 존재 하지 않는 이메일이라면, 가입하지 않는 친구라는 메시지를 띄워주고
                //한번만 리스닝하고 끊어버리는거 이기 때문에,
                mUserDBRef.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        //dataSnapshot에 있는 정보를 iterator로 가져오자.
                        Iterable<DataSnapshot> friendsIteratble = dataSnapshot.getChildren();
                        Iterator<DataSnapshot> friendsIterator = friendsIteratble.iterator();
                        int loopCount = 1; // 루프를 도는데, 사용자의 수만큼 돌기로
                        int userCount = (int)dataSnapshot.getChildrenCount(); //칠드런의 수
                        while (friendsIterator.hasNext()){
                            final User currentuser  = friendsIterator.next().getValue(User.class);//받은데이터가 유저데이터를 가지고 있기때문에 User에서 가져온다.

                            //현재 가리키고 있는 유저에 대한 정보를 가지고 올 수 있다.
                            if (mFirebaseUser.getEmail().equals(currentuser.getEmail())){
                                //친구 등록 로직을 수행
                                // 5. users/{myuid}/friends/{someone_uid}/fi상대 정보를 등록하고
                                mFriendsDBRef.push().setValue(currentuser, new DatabaseReference.CompletionListener() {
                                    @Override
                                    public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                                        //6. 정상적으로 등록되어있을시 users/{someone_uid}/friends/{myuid}/상대 정보를 등록
                                        //나의 정보를 가지고 온다. 여기서 mFirebaseUser 메소드를 사용할 수도 있지만,
                                        // models/user를 보면 사용하는 정보가 다르기 때문에 새로이 선언해준다
                                        mUserDBRef.child(mFirebaseUser.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
                                            // 위에 final로 주는 이유는 currentuser는 지역변수, while문에서만 사용하고 끝나는데, ValueEventListener는 필요한경우 currentuser를 사용하는데 final로 해야 쓸수 있음.
                                            @Override
                                            public void onDataChange(DataSnapshot dataSnapshot) {
                                                User user = dataSnapshot.getValue(User.class);
                                                mUserDBRef.child(currentuser.getUid()).child("friends").push().setValue(user);
                                                Snackbar.make(mSearchArea,"친구등록이 완료되었습니다..",Snackbar.LENGTH_LONG).show();

                                            }
                                            @Override
                                            public void onCancelled(DatabaseError databaseError) {
                                            }
                                        });
                                    }
                                });
                            }
                            else{
                                if( loopCount++ >= userCount) {
                                    // 총 사용자의 명수 == loopCount
                                    // 등록된 사용자가 없다는 메시지를 출력 합니다.
                                    Snackbar.make(mSearchArea,"가입을 하지 않은 친구입니다..",Snackbar.LENGTH_LONG).show();
                                    return;
                                }
                            }
                        }
                        //2번째 루프를 돌때도 일치하지 않으면, 다시 루프카운트가 3이 되고 일치하면 나가기
                    }
                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                    }
                });
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });



    }
}
