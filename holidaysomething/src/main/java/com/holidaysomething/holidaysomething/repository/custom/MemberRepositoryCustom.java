package com.holidaysomething.holidaysomething.repository.custom;

import com.holidaysomething.holidaysomething.domain.Member;
import com.holidaysomething.holidaysomething.domain.Order;
import com.holidaysomething.holidaysomething.dto.SearchOrderMemberDto;
import com.querydsl.core.Tuple;
import java.util.List;

import org.springframework.data.domain.Pageable;

public interface MemberRepositoryCustom {

  // test
  Member getMemberByDsl(Long id);

  List<Member> getMembersByDsl(SearchOrderMemberDto searchOrderMemberDto, Pageable pageable);

  List<Tuple> findMembersByLoginIdInOrdersByDsl(String loginId);

  List<Tuple> findMembersByNameInOrdersByDsl(String loginId);

  List<Tuple> findMembersByProductNameInOrdersByDsl(String productName);

}
