package com.ll.exam.Week_Mission.app.myBook.service;

import com.ll.exam.Week_Mission.app.member.entity.Member;
import com.ll.exam.Week_Mission.app.myBook.entity.MyBook;
import com.ll.exam.Week_Mission.app.myBook.repository.MyBookRepository;
import com.ll.exam.Week_Mission.app.order.entity.Order;
import com.ll.exam.Week_Mission.app.order.entity.OrderItem;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MyBookService {
    private final MyBookRepository myBookRepository;

    public List<MyBook> findAllByMemberId(Long id) {
        return myBookRepository.findAllByMemberId(id);
    }

    public void add(Order order) {
        Member member = order.getBuyer();
        List<OrderItem> orderItems = order.getOrderItems();

        for (OrderItem orderItem : orderItems) {
            MyBook myBook = MyBook.builder()
                    .member(member)
                    .product(orderItem.getProduct())
                    .build();
            myBookRepository.save(myBook);
        }
    }
}
