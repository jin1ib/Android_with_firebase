package firebase.jin1ib.com.firemessenger.views;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import firebase.jin1ib.com.firemessenger.R;

public class MainActivity extends AppCompatActivity {
    /*
    버터나이프의 사용 용도는 매번 findByViewId를 이용하여 View를 연결 짓는데
    번거로움이 있어서 대체 하였습니다.
    */
    @BindView(R.id.tabs)    //탭에 관한 findByViewId
    TabLayout mTabLayout;

    @BindView(R.id.fab)     //5시방향버튼에 관한 findByViewId
    FloatingActionButton mfab;

    @BindView(R.id.viewpager)// 흰색화면에 관한 findByViewId
    ViewPager mViewPager;

    ViewPagerAdapter mPagerAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main); // 액티비티
        ButterKnife.bind(this); //화면을 그려주고 바인드this컨텍트로 위의 레이아웃들이 annotation을 통해 bind 될 수 있다 .

        mTabLayout.setupWithViewPager(mViewPager); //어떤 뷰페이저를 이용하겠는가? mViewPager를 이용하겠다.
        //tab에는 viewpager를 붙었으니 메소드를 사용해서 붙인다.
        setUpViewPager(); //내부에서만 쓰는 메소드이기에 private과
        mfab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //어떤 프레그먼트를 보고있는지
                Fragment currentFragment = mPagerAdapter.getItem(mViewPager.getCurrentItem()); //포지션을 가지고 오는것
                if(currentFragment instanceof FriendFragment){
                    //visibility를 해주기 위한 기능을 FriendFragment
                    ((FriendFragment) currentFragment).toggleSearchbar(); // 토글바

                }
            }
        });
    }
    private void setUpViewPager(){
        mPagerAdapter = new ViewPagerAdapter(getSupportFragmentManager()); //객체생성
        mPagerAdapter.addFragment(new ChatFragment(), "채팅");
        mPagerAdapter.addFragment(new FriendFragment(), "친구");
        //뷰페이저와 아답타를 연결
        mViewPager.setAdapter(mPagerAdapter);
    }
    private class ViewPagerAdapter extends FragmentPagerAdapter {//상속을 받아서 만듬

        //뷰페이저안에 들어올 프레그먼트를 담을 리스트 작성
        private List<Fragment> fragmentList = new ArrayList<>();
        private List<String > fragmentTitleList = new ArrayList<>();

        public ViewPagerAdapter(FragmentManager fragmentManager) { //생성자 생성 생성자가 받는건 FragmentManager
            super(fragmentManager); //부모클래스를 호출
        }
        @Override
        public Fragment getItem(int position) {
            // 0번이면 채팅 1번일땐 친구리스트 이런식으로 구현이 가능하다.
            return fragmentList.get(position);
        }

        @Override
        public CharSequence getPageTitle(int position) {
            //아이템을 반환해준거를 pageTitle
            return fragmentTitleList.get(position);
        }

        @Override
        public int getCount() {
            //프레그먼트 사이즈를 출력해줌.
            return fragmentList.size();
        }
        public void addFragment(Fragment fragment, String title){ // 외부에서 추가가 가능하게 해주기 위함
        // 뷰에 추가해주는 메소드
            fragmentList.add(fragment);
            fragmentTitleList.add(title);
        }
    }
}
