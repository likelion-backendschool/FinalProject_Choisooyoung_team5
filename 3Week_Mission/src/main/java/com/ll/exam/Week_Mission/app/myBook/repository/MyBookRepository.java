package com.ll.exam.Week_Mission.app.myBook.repository;

import com.ll.exam.Week_Mission.app.myBook.entity.MyBook;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MyBookRepository extends JpaRepository<MyBook, Long> {
    List<MyBook> findAllByMemberId(Long id);
}
