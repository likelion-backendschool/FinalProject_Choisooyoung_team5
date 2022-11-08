# Title: [3Week] 최수용
## 미션 요구사항 분석&체크리스트

### 체크 리스트
---
필수과제
- [x] 관리자 회원
	- [x] 개발자가 회원중 임의로 1명을 골라서 관리자로 지정
		- [x] authLevel을 7로 지정
- [x] 관리자 페이지
	- [x] 관리자만 관리자 페이지에 접속할 수 있다.
- [x] 정산데이터 생성
	- [x] 결제가 된 주문품목은 정산품목으로 생성된 수 있다.
- [x] 건별정산처리
	- [x] 건별정산처리가 가능하다.
- [x] 전체정산처리
	- [x] 전체정산처리를 진행한다.
		- [x] 여러개의 건들을 선택하여 한번에 정산처리
추가과제
- [ ] 정산데이터를 배치로 생성
- [ ] 출금신청기능(사용자기능)
- [ ] 출금처리기능(관리자기능)
### 주요엔드포인트
홈
- [x] 관리자 페이지 GET /adm/home/main

주문
- [x] 정산데이터생성 폼 GET /adm/rebate/makeData
- [x] 정산데이터생성 POST /adm/rebate/makeData

정산
- [x] 정산데이터리스트 GET /adm/rebate/rebateOrderItemList
- [x] 정산(전체, 건별) POST /adm/rebate/rebate
- [x] 정산(전체, 건별) POST /adm/rebate/rebateOne/{rebateOrderItemId}

출금
- [ ] 출금신청 GET /withdraw/apply
- [ ] 출금신청 POST /withdraw/apply
- [ ] 출금처리 GET /adm/withdraw/applyList
- [ ] 출금처리 POST /adm/withdraw/{withdrawApplyId}
### 2주차 미션 요약

[접근방법]
- 기존 음원강의를 기반으로 제작
- 요구사항 정의서에 필수과제를 위주로 제작
[특이사항]
- 이번주 컨디션이 별로 좋지않아서 3주차 과제에 시간을 많이 쓰지 못했습니다.
- 도전과제에 스프링 배치 사용을 접근도 못해 스프링 배치부분을 다시 공부해야겠습니다.
- 강사님의 코드를 기반으로 만들지 말고 직접 만들어봐야겠습니다. 강사님의 코드에 의지하게 되는 것 같습니다.