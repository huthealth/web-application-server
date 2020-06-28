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

### 요구사항 1 - http://localhost:8080/index.html로 접속시 응답
* request header를 파싱하여 요구하는 파일과 content-type을 얻는다.
* 요구하는 페이지가 존재할 경우 파일과 content-type을 byte배열로 변환 후 출력스트림에 쓴다.
* 페이지가 존재하지 않을 경우 wrong url 문자열을 출력스트림에 쓴다.
* 알게된 사실
* url을 제대로 파싱 후 파일을 return 해줘도 content-type이 올바르지 않으면 브라우져에서 제대로 출력되지 않는다. 

### 요구사항 2 - get 방식으로 회원가입
* 

### 요구사항 3 - post 방식으로 회원가입
* 

### 요구사항 4 - redirect 방식으로 이동
* http 302 응답코드 == redirect
* 302 코드와 함께 location을 응답헤더로 보내주면 클라이언트는 location을 요청한다. 

### 요구사항 5 - cookie
* 

### 요구사항 6 - cookie 이용한 html table 생성
* 문제점 : 서버에서 set-cookie 시 부모 경로에 저장됨
* 해결 : PATH를 명시적으로 설정해줌(/)

### 리팩토링
* 1단계 : HttpRequest & HttpResponse
   - RequestHandler 클래스 하나가 너무 많은 일을 하고 있다.(클라이언트 요청 처리 및 응답 헤더 와 본문 데이터 처리 작업 등)
   - 하나의 객체가 한 가지 책임을 가지도록 설계 개선
   - 클라이언트 요청 데이터와 응답 데이터 처리를 별도의 클래스로 분리함
   - 테스트 코드를 통해 클래스 별로 버그가 있는지 찾아봄으로써 디버그가 쉬워지고 웹 서버 실행한 후 수동으로 일일히 확인하지 않아도 됨
* 2단계 : 다형성을 활용한 URL 분기 처리 제거
   - OCP 객체지향 설계 원칙
      - 요구사항의 변경 및 추가 발생 시, 기존 구성요소는 수정이 일어나지 말아야 하며, 기존 구성요소를 쉽게 확장 재사용할 수 있어야 한다.
   - run() 메소드의 문제점
      - 기능이 추가될 때마다 새로운 else if 절을 추가해 url 처리하는 구조
   - Controller들과 RequestMapping을 통해 각 클래스간 영향 미치지 않고 새로운 기능 추가 가능해짐
      - 새로운 기능 추가 시 새로운 Controller의 service 메소드만 수정하면 됨

