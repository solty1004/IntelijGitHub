package jpabook.jpashop.service;

import jpabook.jpashop.domain.Member;
import jpabook.jpashop.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(readOnly = true)
//JPA의 모든 데이터 변경 로직은 가급적 트랜젝션에서 실행 되어야한다.
//클레스레벨에서 어노테이션을 걸면, 모든 퍼블릭 메소드가 트렌잭션이 걸린다.
//스프링 어노테이션을 이용해야함(javax는 안됨!)
//readOnly는 조회시 성능 최적화.(제약을 걸어서 성능의 향상을 도모한다.)
@RequiredArgsConstructor //final이 붙은 필드 생성자를 자동 생성한다.
//필수 인자 생성자
public class MemberService {
    //
    // @Autowired 쓰면 안된다.
    // ㄴ> 필드 인젝션 (생성자 주입)
    private final MemberRepository memberRepository; //인스턴스

    //setter 인젝션 예시 (사용지양)
    //@AutoWired
    //pubilc void setMemberRepository(MemberRepository memberRepository) {
    //  this.memberRepository = memberRepository;
    //}

    //AutoWired 생성자 주입 <- 그나마 이게 가장 적정 @Transactional, @RequiredArgsConstructor 안쓴다면.
    //lombok 어노테이션으로 인해 생성자 생략
    //public MemberService(MemberRepository memberRepository) {
    // this.memberRepository = memberRepository;
    //}


    /*회원가입*/
    @Transactional //ReadOnly가 되면 안되서 다시 따로 작업을 건다.
    public Long join(Member member) {
        validateDuplicateMember(member); // 중복 회원 검증
        memberRepository.save(member);

        return member.getId();
    }

    //중복을 찾는건 역산을 해야한다.
    //멀티쓰레드 상황을 고려해서 DB에 name을 유니크 제약조건을 거는것이 좋다.
    //그렇지 않으면 두 회원이 동일한 이름으로 동시에 가입하는 경우, validate을 통과 할 수 있다.
    private void validateDuplicateMember(Member member) {
        List<Member> findMembers = memberRepository.findByName(member.getName());
        if(!findMembers.isEmpty()) {
            throw new IllegalStateException("이미 존재하는 회원입니다.");
        }
    }


    /*회원 전체 조회*/
    public List<Member> findMembers() {
        return memberRepository.findAll();
    }

    /*회원 1명 조회*/
    public Member findOne(Long memberId) { return memberRepository.findOne(memberId); }

    /*회원 정보 수정*/
    @Transactional
    public void update(Long id, String name) {
        Member member = memberRepository.findOne(id);
        member.setName(name); //변경감지
    }

    //TDD 테스트 기반 디벨로퍼기반으로 할 예정인데, 귀찮다.

}
