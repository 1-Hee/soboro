package com.catchtwobirds.soboro.consulting.repository;

import com.catchtwobirds.soboro.consulting.entity.Consulting;
import com.catchtwobirds.soboro.user.entity.User;
import io.lettuce.core.dynamic.annotation.Param;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

public interface ConsultingRepository extends JpaRepository<Consulting, Integer> {

//    @Query("select c from Consulting c where c.user.userNo = :userNo")
//    List<Consulting> findByUser(@Param("userNo") int userNo);
//    List<Consulting> findAllByUser(User user);

//    List<Consulting> findByUser_UserNo(Integer userNo);

    Slice<Consulting> findByUser_UserNo(Integer userNo, Pageable pageable);

//    Page<Consulting> findByUser_UserNoAndConsultingVisitClass(Integer userNo, String consultingVisitClass, Pageable pageable);

    Slice<Consulting> findByUser_UserNoAndConsultingVisitClassContaining(Integer userNo, String consultingVisitClass, Pageable pageable);



//    @Query("select c from Consulting c join fetch c.user u where u.userNo = :userNo and c.consultingNo = :consultingNo")
//    List<Consulting> findConsultingDetail(@Param("userNo") int userNo, @Param("consultingNo") int consultingNo);

    List<Consulting> findByUser_UserNoAndConsultingNo(@Param("userNo") Integer userNo, @Param("consultingNo") Integer consultingNo);

}
