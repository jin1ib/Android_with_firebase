package firebase.jin1ib.com.firemessenger.views;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import butterknife.BindView;
import butterknife.ButterKnife;
import firebase.jin1ib.com.firemessenger.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class FriendFragment extends Fragment {

    @BindView(R.id.search_area)
    LinearLayout mSearchArea;   //친구 찾기 버튼

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View friendView = inflater.inflate(R.layout.fragment_friends, container,false);//레이아웃 인플레이트를 이용해서 가져오기
        //ButterKnife.bind(this)는 액티비티할때만
        ButterKnife.bind(this,friendView); //이거는 friendView에 bind한다는 의미
        return friendView;
    }
    public void  toggleSearchbar(){
        mSearchArea.setVisibility(mSearchArea.getVisibility() == View.VISIBLE ? View.GONE : View.VISIBLE);
    }
    private void addFriend(){
        // 1. 입력된 이메일을 가져옵니다.
        // 이메일이 입력되지 않았다면 이메일을 입력해주시라는 메시지를 띄워줍니다.
    }
}
