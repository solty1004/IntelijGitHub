package jpabook.jpashop.api;

import jpabook.jpashop.domain.Member;
import jpabook.jpashop.service.MemberService;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;
import java.util.stream.Collectors;

// @Controller + @ResponseBody =
// 데이터를 바로 json 이나 xml 으로 반환할때 쓰는 어노테이션
@RestController
@RequiredArgsConstructor
public class MemberApiController {
//은닉화를 위한 작업
    private final MemberService memberService;

    /**
     * 조회 ver 1 : 응답 값으로 엔티티를 직접 외부 노출
     * -문제점
     * 엔티티에 프레젠테이션 계층을 위한 로직이 추가 됨 : 로직이 어렵고 양이 많음
     * 기본적으로 엔티티의 모든 값이 노출됨 (@RestController 바디 값이 다나왔음)
     * 응답스펙을 맞춰줘야 하므로 로지깅 또 추가됨, 어노테이션도 추가
     * 실무에서는 같은 엔티티에 대해서 API가 용도에 따라 다양하게 만들어지는데,
     * 한 엔티티에 각각의 API를 위한 프레젠테이션 응답로직을 담기가 어렵다.
     * 엔티티가 바뀌면 API 스펙도 바뀜.
     * -결론
     * API 응답 스팩에 맞추어 별도의 DTO를 반환함
     * */
    // 조회 ver 1 : 사실상 최악의 API
    @GetMapping("/api/v1/members")
    public List<Member> memberV1() {
        return memberService.findMembers();
        // @RestController 어노테이션 덕분에 json으로 자동 변환되어서 리턴됨.
    }

    /**
     * 조회 ver : 응답 값으로 엔티티가 아닌 별도의 DTO 자체를 반환한다.
     * */

    @GetMapping("/api/v2/members")
    public Result memberV2() {
        List<Member> findMembers = memberService.findMembers();
        // 엔티티 -> DTO로 변환 시킴
        List<MemberDto> collect = findMembers.stream()
                .map(m -> new MemberDto(m.getName()))
                .collect(Collectors.toList());

        return new Result(collect);
    }

    @Data
    @AllArgsConstructor
    static class Result<T> { //object 타입으로 반환하기 위한 껍데기. (list, collection 타입반환 x)
        //static은 실행 되자마자 있고 빈공간을 만들어두고 있다?
        private T data;     // T = Generic type => 계산하기위한 or 무엇을 담기위한.
    }

    @Data
    @AllArgsConstructor
    static class MemberDto {
        private String name;
    }

    /**
     * V1 : 요청값으로 Member 엔티티를 직접 받는다.
     * -단점
     * 위와 상동하나 추가적인 단점은
     * 엔티티의 API 검증을 위한 로직이 들어간다 (@NotEmpty 등등)
     * -결론
     * API 요청 스펙에 맞추어 별도의 DTO를 파라미터로 받는다.
     * */

    @PostMapping("/api/v1/members")
    public CreateMemberResponse saveMemberV1(@RequestBody @Valid Member member) {
        Long id = memberService.join(member);
        return new CreateMemberResponse(id);
    }

    /**
     * 등록 v2 : 요청 값으로 Member 엔티티 대신에 별도의 DTO를 받는다
     * */
    @PostMapping("/api/v2/members")
    public CreateMemberResponse saveMemberV2(@RequestBody @Valid CreateMemberRequest request) {

        Member member = new Member();
        member.setName(request.name);

        Long id = memberService.join(member);
        return  new CreateMemberResponse(id);
    }

    /**
     * 수정
     * */
    @PostMapping("/api/v2/members/{id}")
    public UpdateMemberResponse updateMemberV2(
            @PathVariable("id") Long id,
            @RequestBody @Valid UpdateMemberRequest request) {

        memberService.update(id, request.getName());
        Member findMember = memberService.findOne(id);
        return new UpdateMemberResponse(findMember.getId(), findMember.getName());
    }


    //Update
    @Data
    @AllArgsConstructor
    static class UpdateMemberResponse {
        private Long id;
        private String name;
    }

    //Update
    @Data
    static class UpdateMemberRequest {
        private String name;
    }

    //Create
    @Data
    static class CreateMemberResponse {
        private Long id;

        public CreateMemberResponse(Long id) {
            this.id = id;
        }
    }

    //Create
    @Data
    static class CreateMemberRequest {
        private String name;

    }

}
