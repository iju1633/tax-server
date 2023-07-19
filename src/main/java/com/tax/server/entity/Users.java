package com.tax.server.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "users")
public class Users { // 회원가입 시, 유저의 정보를 담을 엔티티

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id; // 고유한 id

    private String userId; // 서비스 로그인 아이디
    private String password; // 서비스 로그인 비밀번호
    private String name; // 사용자 이름
    private String regNo; // 사용자 주민번호

    @Column(updatable = false) // 한 번 생성이 되고 나서는 수정되면 안됨
    private String registerDate;
}
