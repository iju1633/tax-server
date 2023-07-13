package com.tax.o3server.entity;

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
@Table(name = "scrap_data")
public class ScrapData { // 스크랩 시, 스크랩 정보를 활용하여 계산 이후 결과 데이터를 담을 엔티티

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id; // 고유한 id

    private String userName; // 유저 이름
    private String 결정세액;
    private String 퇴직연금세액공제;
}
