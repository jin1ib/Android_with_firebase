package firebase.jin1ib.com.firemessenger.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import firebase.jin1ib.com.firemessenger.R;
import firebase.jin1ib.com.firemessenger.customviews.RoundedImageView;
import firebase.jin1ib.com.firemessenger.models.User;

/**
 * Created by Jin1ib on 2017-12-02.
 */

public class FriendListAdapter extends RecyclerView.Adapter<FriendListAdapter.FriendHolder>{//친구선택에는 1.단일선택모드 2. 여러명서ㄴ택모드
    //리사이클러뷰의 adapter를 구현해야함


    public static final int UNSELECTION_MODE =1;// 콤보박스없는 친구목록에서 클릭해서 대화
    public static final int SELECTION_MODE =2;   // 콤보박스있는 친구목록에서 클릭

    private ArrayList<User> friendList; //친구목록에 친구들을 저장

    public FriendListAdapter(){
        friendList = new ArrayList<>();
    }
    public void addItem(User friend){//친구를 추가하고 알림
        friendList.add(friend);
        notifyDataSetChanged();
    }

    @Override
    public FriendHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.fragment_friend_item,parent,false);
        FriendHolder friendHolder = new FriendHolder(view);
        return friendHolder;
    }

    @Override
    public void onBindViewHolder(FriendHolder holder, int position) {

    }

    @Override
    public int getItemCount() {
        return 0;
    }

    public static class FriendHolder extends RecyclerView.ViewHolder{
        @BindView(R.id.thumb)
        RoundedImageView mProfileView;     //친구목록의 프로필사진 image

        @BindView(R.id.name)
        TextView mNameView;

        @BindView(R.id.email)
        TextView mEmailView;
        private FriendHolder(View v){
            //생성자를 강제호 호출해서 오버라이딩
            super(v);
            ButterKnife.bind(this,v);
        }
    }
}
