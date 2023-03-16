package com.catchtwobirds.soboro.config.properties;

import com.querydsl.jpa.impl.JPAQueryFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

/**
 * 이 코드는 Spring Boot 프레임워크에서 QueryDSL을 사용하기 위한 설정 파일인 QueryDslConfig 클래스를 정의하는 Java 코드입니다. <br>
 * QueryDslConfig 클래스는 EntityManager 필드를 가지고 있습니다. <br>
 * `@PersistenceContext` 어노테이션은 Spring이 Entity Manager를 자동으로 주입하여 관리하도록 지시합니다. <br>
 * jpaQueryFactory() 메서드는 JPAQueryFactory 객체를 생성하고, 생성자로 EntityManager 필드를 전달합니다. <br>
 * JPAQueryFactory는 QueryDSL을 사용하여 JPA 쿼리를 실행하기 위한 클래스입니다. 이를 사용하여 데이터베이스 쿼리를 쉽게 작성하고 실행할 수 있습니다. <br>
 */
@Configuration
public class QueryDslConfig {

    @PersistenceContext
    private EntityManager entityManager;

    @Bean
    public JPAQueryFactory jpaQueryFactory() {
        return new JPAQueryFactory(entityManager);
    }
}

