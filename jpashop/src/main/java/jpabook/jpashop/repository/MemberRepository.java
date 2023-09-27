package jpabook.jpashop.repository;

import jpabook.jpashop.domain.Member;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.List;

@Repository
public class MemberRepository {
    
    @PersistenceContext
    private EntityManager em;
    
    public void save(Member member) { //저장
        em.persist(member);
    }
    
    public Member findOne(Long id) { //하나 찾기
        return em.find(Member.class, id);
    }
    
    public List<Member> findAll() {
        return em.createQuery("select m from Member m", Member.class)
                .getResultList();
    }
    
    public List<Member> findByName(String name) {
        return em.createQuery("select m from Member m where m.name = :name",
                Member.class)
                .setParameter("name", name)
                .getResultList();
    }
    
    // @Repository : 스프링 빈으로 등록. JPA예외를 스프링기반 예외로 변환 시켜주는것
    // @PersistenceContext: Entty 매니저 주입
    //@PersistenceUnit: 엔티티 매니저 팩토리를 주입
    
    //다음에 만들어야 하는것
    //회원 서비스 개발 
    //상품 도메인 개발
    //상품 엔티티 개발(비지니스 로직 추가)
    //상품 레포지토리 개발
    //주문 개발
}
