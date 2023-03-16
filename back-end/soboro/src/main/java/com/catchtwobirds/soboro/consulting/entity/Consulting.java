package com.catchtwobirds.soboro.consulting.entity;

import com.catchtwobirds.soboro.entity.User;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Setter @Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Consulting {
    @Id
    @GeneratedValue
    @Column(name = "consulting_no")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_no")
    private User user;
    private LocalDateTime consultingVisitDate;
    private String consultingVisitPlace;
    private String consultingVisitClass;

    @OneToMany(mappedBy = "consulting")
    private List<Video> videos = new ArrayList<>();

}
