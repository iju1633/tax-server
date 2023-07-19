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
@Table(name = "scrap_data")
public class ScrapData { // 스크랩 시, 스크랩 정보를 활용하여 계산 이후 결과 데이터를 담을 엔티티

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id; // 고유한 id

    private long 총급여;
    private long 산출세액;
    private long 퇴직연금납입금액;
    private long 보험료납입금액;
    private long 의료비납입금액;
    private long 교육비납입금액;
    private long 기부금납입금액;
}
