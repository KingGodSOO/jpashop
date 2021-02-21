package jpabook.jpashop.service;

import jpabook.jpashop.domain.Member;
import jpabook.jpashop.repository.MemberRepository;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(readOnly = true) // jpa의 모든 데이터 변경, 로직은 가급적 트랜잭션 안에서 실행되어야 한다.
// 클래스 레벨에서 annotation을 걸면 모든 public 메서드는 트랜잭션이 걸린다.
// 스프링 어노테이션을 이용할 것!(javax 말고)
// readOnly는 조회 시 성능 최적화를 해준다. (반드시 조회시에만!)
// @AllArgsConstructor // 모든 필드 생성자 자동 만들어줌.
@RequiredArgsConstructor    // final 붙은 필드 생성자 자동생성.
public class MemberService {

    // @Autowired 필드 인젝션은 반드시 지양해야 한다.
    private final MemberRepository memberRepository;    // 변경할 일이 없는 경우 final로 해준다.

//    setter 인젝션은 인젝션을 바꿔야 하는 경우에만 쓰자.
//    @Autowired
//    public void setMemberRepository(MemberRepository memberRepository) {
//        this.memberRepository = memberRepository;
//    }

//    @Autowired 생성자 주입이 가장 적절하다. 이 때 인자가 하나만 있는 경우 autowired 하지 않아도 스프링이 자동 인젝션 해준다.
//    lombok 어노테이션으로 인해 생성자 생략
//    public MemberService(MemberRepository memberRepository) {
//        this.memberRepository = memberRepository;
//    }

    /**
     *회원 가입
     */
    @Transactional // readOnly가 되면 안되므로 따로 걸어준다. (deafult가 readOnly = false)
    public Long join(Member member) {
        validateDuplicateMember(member); // 중복 회원 검증
        memberRepository.save(member);
        return member.getId();
    }

    // 멀티 쓰레드 상황을 고려해서, DB에 name을 유니크 제약조건을 거는것이 좋다.
    // 그렇지 않으면 두 회원이 동일한 이름으로 동시에 가입하는 경우, validate를 통과할 수도 있다.
    private void validateDuplicateMember(Member member) {
        List<Member> findMembers = memberRepository.findByName(member.getName());
        if (!findMembers.isEmpty()) {
            throw new IllegalStateException("이미 존재하는 회원입니다.");
        }
    }

    //회원 전체 조회
    public List<Member> findMembers() {
        return memberRepository.findAll();
    }

    public Member findOne(Long memberId) {
        return memberRepository.findOne(memberId);
    }
}
