package org.swengineer.user.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "users")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /*
    - **글자수:** 영문 소문자 및 숫자 혼용 **4자 이상 ~ 15자 이하**
    - **정규식 제약:** 영문 소문자와 숫자만 허용 (공백, 특수문자, 대문자 불가)
    - **중복 여부:** **필수 (Unique)**
     */
    @Column(unique = true, nullable = false, length = 15)
    private String routinerId;

    /*
    - **글자수:** 한글, 영문, 숫자 구분 없이 **2자 이상 ~ 10자 이하**
    - **정규식 제약:** 한글(자음/모음 결합된 완성형만), 영문 대소문자, 숫자 허용 (공백 및 특수문자 불가, 초성/중성 단독 입력 불가 예: 'ㄱㄱ', 'ㅏㅏ')
     */
    @Column(nullable = false, length = 10)
    private String nickname;

    /*
    - **글자수:** 영문, 숫자, 특수문자 조합 **8자 이상 ~ 20자 이하**
    - **조합 제약:** 영문 대/소문자, 숫자, 특수문자(`!@#$%^&*`) 중 **최소 2가지 이상**의 조합 필수
     */
    @Column(nullable = false)
    private String password;

    @Column
    private LocalDateTime deletedAt;

    public static User create(String routinerId, String nickname, String password) {
        User user = new User();
        user.routinerId = routinerId;
        user.nickname = nickname;
        user.password = password;
        return user;
    }

    public void updateNickname(String nickname) {
        this.nickname = nickname;
    }

    public void withdraw() {
        this.deletedAt = LocalDateTime.now();
    }

    public boolean isWithdrawn() {
        return this.deletedAt != null;
    }
}
