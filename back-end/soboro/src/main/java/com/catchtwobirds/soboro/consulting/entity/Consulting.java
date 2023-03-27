package com.catchtwobirds.soboro.consulting.entity;

import com.catchtwobirds.soboro.user.entity.User;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.springframework.lang.Nullable;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "consulting")
@Setter @Getter
@NoArgsConstructor//(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
@ToString
public class Consulting {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "consulting_no")
    private Integer consultingNo;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "consulting_user_no")
    private User user;
    @CreationTimestamp
    private LocalDateTime consultingVisitDate;
    private String consultingVisitPlace;
    private String consultingVisitClass;
    @Nullable
    private String videoLocation;


}
