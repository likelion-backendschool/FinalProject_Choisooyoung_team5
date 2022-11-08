package com.ll.exam.Week_Mission.app.email.repository;

import com.ll.exam.Week_Mission.app.email.entity.SendEmailLog;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SendEmailLogRepository extends JpaRepository<SendEmailLog, Long> {
}
