package firebase.jin1ib.com.firemessenger.models;

import lombok.Data;

@Data
public class User {
    private String uid, email, name, profileUrl;

    public void setUid(String uid) {
        this.uid = uid;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setProfileUrl(String profileUrl) {
        this.profileUrl = profileUrl;
    }

    public String getEmail() {
        return email;
    }

    public String getUid() {
        return uid;
    }

    public String getName() {
        return name;
    }

    public String getProfileUrl() {
        return profileUrl;
    }
}
/* 롬복(lombok)
자바 클래스 생성시 setter/getter을 매번 생성해줘야 하는 부분을
어노테이션으로 대체하여 편리하게 사용하기 위해 사용한것.


 */
