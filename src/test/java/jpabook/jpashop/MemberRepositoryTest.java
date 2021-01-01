package jpabook.jpashop;

import jpabook.jpashop.domain.Member;
import org.assertj.core.api.Assertions;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.Assert.*;

@RunWith(SpringRunner.class)
@SpringBootTest
public class MemberRepositoryTest {

    @Autowired
    MemberRepository memberRepository;

    @Test
    @Transactional  // spring framework 제공 Transactional 어노테이션 사용
    @Rollback(false) // Transactional 어노테이션이 테스트에 있으면 자동 rollback 해준다. 그걸 막으려면 이 어노테이션 추가하면 된다.
    public void testMember() throws Exception {
        //given
        Member member = new Member();
        member.setUsername("memberA");

        //when
        Long savedId = memberRepository.save(member);
        Member findMember = memberRepository.find(savedId);

        //then
        Assertions.assertThat(findMember.getId()).isEqualTo(member.getId());
        Assertions.assertThat(findMember.getUsername()).isEqualTo(member.getUsername());

        // 같은 영속성 컨텍스트에서는 ID(식별자)가 같으면 같은 Entity로 인식한다.
        // 애초에 동일한 Entity가 있기 떄문이(동일한 식별자) select 쿼리가 나가지도 않는다.
        Assertions.assertThat(findMember).isEqualTo(member);
    }
}
