package com.catchtwobirds.soboro.consulting.repository;

import com.catchtwobirds.soboro.consulting.entity.Consulting;
import io.lettuce.core.dynamic.annotation.Param;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

public interface ConsultingRepository extends JpaRepository<Consulting, Long> {

    @Query("select c from Consulting c where c.user.userId = :userId")
    List<Consulting> findConsultingListByUserId(@Param("userId") int userId);

    @Query("select c from Consulting c join fetch c.user u where u.userNo = :userNo and c.consultingNo = :consultingNo")
    Optional<Consulting> findConsultingDetailByUserIdAndConsultingNo(@Param("userNo") int userNo, @Param("consultingNo") int consultingNo);
}
