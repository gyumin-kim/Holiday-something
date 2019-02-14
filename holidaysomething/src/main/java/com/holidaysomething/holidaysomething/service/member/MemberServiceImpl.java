package com.holidaysomething.holidaysomething.service.member;

import com.holidaysomething.holidaysomething.domain.Member;
import com.holidaysomething.holidaysomething.domain.Role;
import com.holidaysomething.holidaysomething.dto.AddOrderMemberDto;
import com.holidaysomething.holidaysomething.dto.CurrentMemberDto;
import com.holidaysomething.holidaysomething.dto.MemberMileageDto;
import com.holidaysomething.holidaysomething.dto.MemberSearchDto;
import com.holidaysomething.holidaysomething.dto.OrderMemberDto;
import com.holidaysomething.holidaysomething.dto.SearchDto;
import com.holidaysomething.holidaysomething.dto.SearchOrderMemberDto;
import com.holidaysomething.holidaysomething.dto.UserCartProductDto;
import com.holidaysomething.holidaysomething.repository.CartProductRepository;
import com.holidaysomething.holidaysomething.repository.MemberRepository;
import com.holidaysomething.holidaysomething.repository.RoleRepository;
import com.querydsl.core.Tuple;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class MemberServiceImpl implements MemberService {

  private final MemberRepository memberRepository;
  private final CartProductRepository cartProductRepository;
  private final RoleRepository roleRepository;

  @Override
  @Transactional(readOnly = true)
  public Page<Member> findAllOrSearch(SearchDto searchDto, Pageable pageable) {
    Page<Member> members = null;
    if (searchDto.isSearched()) {
      members = memberRepository.findMembersByLoginId(searchDto.getKeyword(), pageable);
    } else {
      members = memberRepository.findAll(pageable);
    }
    return members;
  }

  /**
   * 현재 로그인 된 userId로 유저의 모든 정보를 찾는다. https://stackoverflow.com/a/49317013/8962314
   */
  @Override
  @Transactional(readOnly = true)
  public Member getCurrentMemberInfo(Long userId) {
    return memberRepository.findById(userId)
        .orElse(null);
  }

  @Override
  @Transactional(readOnly = true)
  public List<CurrentMemberDto> getCurrentMemberInfoJPQL(Long userId) {
    return memberRepository.findCurrentMember(userId);
  }

  @Override
  @Transactional(readOnly = true)
  public Member findMemberByLoginId(String loginId) {
    log.info("============== loginId : " + loginId);
    return memberRepository.findMemberByLoginId(loginId);
  }

  @Override
  @Transactional
  public void updateMember(MemberMileageDto memberMileageDto) {
    Member member = memberRepository.findMemberByLoginId(memberMileageDto.getLoginId());

    int mileage = member.getMileage();

    if (memberMileageDto.getPlusOrMinus().equals("+")) {
      mileage += memberMileageDto.getAddMileage();
    } else if (memberMileageDto.getPlusOrMinus().equals("-")) {
      mileage += memberMileageDto.getAddMileage() * -1;
    }
    member.setMileage(mileage);

    memberRepository.save(member);
  }

  @Override
  public Member patchMember(Member member) {
    return memberRepository.save(member);
  }

  @Override
  public Page<Member> searchMembers(MemberSearchDto memberSearchDto, Pageable pageable) {
    Page<Member> memberPage = memberRepository.searchMembers(memberSearchDto, pageable);

    return memberPage;
  }


  /**
   * @author JDragon member/order.html 에서 입력한 폼 데이터를 이용해 검색하는 서비스.
   */
  @Override
  @Transactional
  public Page<OrderMemberDto> findMembersBySearchingInQuerydsl(
      SearchOrderMemberDto searchOrderMemberDto, Pageable pageable) {
    log.info("==== searchOrderMemberDto.getName() : " + searchOrderMemberDto.getName());
    log.info(
        "==== searchOrderMemberDto.getProductName() : " + searchOrderMemberDto.getProductName());
    log.info(
        "==== searchOrderMemberDto.getOrderNumber() : " + searchOrderMemberDto.getOrderNumber());
    log.info("==== searchOrderMemberDto.getOrderStartDate() : " + searchOrderMemberDto
        .getOrderStartDate());
    log.info(
        "==== searchOrderMemberDto.getOrderEndDate() : " + searchOrderMemberDto.getOrderEndDate());
    log.info("==== searchOrderMemberDto.getLoginId() : " + searchOrderMemberDto.getLoginId());

    Page<Tuple> tuples = memberRepository.getMembersByDsl(searchOrderMemberDto, pageable);
    log.info("==========tuples getTotalPages : " + tuples.getTotalPages());
    log.info("==========tuples getSize : " + tuples.getSize());

    long totalElements = tuples.getTotalElements();

    //List<Tuple> orderMemberDtos = tuples.getContent();
    List<OrderMemberDto> orderMemberDtoList = new ArrayList<>();

    //Object[] objects = null;
    //OrderMemberDto temp = new OrderMemberDto(null,null,null);

    for (Tuple tuple : tuples) {
      Object[] objects = tuple.toArray();
      OrderMemberDto temp = new OrderMemberDto((Member) objects[0], (LocalDateTime) objects[1],
          (String) objects[2]);
      orderMemberDtoList.add(temp);
    }

    Page<OrderMemberDto> orderMemberDtoPage =
        new PageImpl<>(orderMemberDtoList, pageable, totalElements);

    return orderMemberDtoPage;
  }

  /***************************** User *******************************/
  @Override
  @Transactional
  public void addMember(Member member) {
    memberRepository.save(member);
  }

  @Override
  @Transactional
  public void addRole(Role role) {
    roleRepository.save(role);

  }

  @Override
  public AddOrderMemberDto findMemberById(Long id) {
    AddOrderMemberDto addOrderMemberDto = new AddOrderMemberDto();

    Member member = memberRepository.findMemberById(id);

    addOrderMemberDto.setName(member.getName());
    addOrderMemberDto.setPhone(member.getPhone());
    addOrderMemberDto.setEmail(member.getEmail());
    addOrderMemberDto.setAddress1(member.getAddress1());
    addOrderMemberDto.setAddress2(member.getAddress2());
    addOrderMemberDto.setPostcode(member.getPostcode());
    addOrderMemberDto.setMileage(member.getMileage());

    return addOrderMemberDto;
  }

  public List<UserCartProductDto> getUserCartProduct(Long userId) {
    return cartProductRepository.findCartProductById(userId);

  }
}
