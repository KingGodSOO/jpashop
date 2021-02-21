package jpabook.jpashop.service;

import jpabook.jpashop.domain.Member;
import jpabook.jpashop.repository.MemberRepository;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;

import static org.junit.Assert.*;

@RunWith(SpringRunner.class) //스프링과 integration되서 테스트 => 정석적인 단위테스트라 보기는 힘들다
@SpringBootTest //스프링과 integration되서 테스트 => 정석적인 단위테스트라 보기는 힘들다
@Transactional  //test class의 경우 자동 rollback
public class MemberServiceTest {

    @Autowired MemberService memberService; //testcase이기에 autowired 사용
    @Autowired MemberRepository memberRepository;
    @Autowired EntityManager em;

    @Test
    public void 회원가입() throws Exception {
        //given
        Member member = new Member();
        member.setName("kim");

        //when
        Long savedId = memberService.join(member);

        //then
        em.flush(); // 강제 DB반영으로 Insert 쿼리를 보기 위함.
        assertEquals(member, memberRepository.findOne(savedId));
        // 동일한 transaction 안에서 같은 Entity(동일한 pk)라면 같은 영속성 컨텍스트 안에서 동일한 Entity로 관리가 된다.

    }

    @Test(expected = IllegalStateException.class)
    public void 중복_회원_예외() throws Exception {
        //given
        Member member1 = new Member();
        member1.setName("kim");

        Member member2 = new Member();
        member2.setName("kim");

        //when
        memberService.join(member1);
        memberService.join(member2);

        //then
        fail("예외가 발생해야 합니다.");
    }
}