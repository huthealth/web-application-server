# 실습을 위한 개발 환경 세팅
* https://github.com/slipp/web-application-server 프로젝트를 자신의 계정으로 Fork한다. Github 우측 상단의 Fork 버튼을 클릭하면 자신의 계정으로 Fork된다.
* Fork한 프로젝트를 eclipse 또는 터미널에서 clone 한다.
* Fork한 프로젝트를 eclipse로 import한 후에 Maven 빌드 도구를 활용해 eclipse 프로젝트로 변환한다.(mvn eclipse:clean eclipse:eclipse)
* 빌드가 성공하면 반드시 refresh(fn + f5)를 실행해야 한다.

# 웹 서버 시작 및 테스트
* webserver.WebServer 는 사용자의 요청을 받아 RequestHandler에 작업을 위임하는 클래스이다.
* 사용자 요청에 대한 모든 처리는 RequestHandler 클래스의 run() 메서드가 담당한다.
* WebServer를 실행한 후 브라우저에서 http://localhost:8080으로 접속해 "Hello World" 메시지가 출력되는지 확인한다.

# 각 요구사항별 학습 내용 정리
* 구현 단계에서는 각 요구사항을 구현하는데 집중한다. 
* 구현을 완료한 후 구현 과정에서 새롭게 알게된 내용, 궁금한 내용을 기록한다.
* 각 요구사항을 구현하는 것이 중요한 것이 아니라 구현 과정을 통해 학습한 내용을 인식하는 것이 배움에 중요하다. 

### 요구사항 : HTTP Session 구현
## 배운 점
### 1 
* RequestHandler에서 클라이언트가 서버에 접속 시 요청 쿠키에 세션이 존재하지 않으면 세션아이디만 랜덤으로 만들고
그 세션아이디에 해당하는 HttpSession 객체 생성x
* 실수인줄 알았는데 생각해보니 login하지 않으면 세션에 저장할 데이터 존재하지않으므로 객체 추가할 필요 없음 -> 메모리 낭비 제거
### 2
* Set-Cookie가 아닌 Cookie로 응답헤더를 추가해 요청헤더에 쿠키가 추가 되지 않았다. 그런데 로그인 후 UserList가 출력됨
* login컨트롤러에서 request.getSession -> 요청헤더에 쿠키 없으므로  request객체에서 HttpSessions.getSession(null) 호출됨
* HttpSessions의 getSession(id)에서 id가 null이므로 항상 session이 새로 추가된다고 생각했는데 디버깅해보니 id가 null session이 저장되어
* userlist컨트롤러에서 getSession시 id가 null인 session의 user 검색해 얻어와 출력됨
