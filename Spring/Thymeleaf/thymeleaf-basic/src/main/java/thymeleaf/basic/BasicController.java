package thymeleaf.basic;

import lombok.Data;
import org.springframework.boot.Banner;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpSession;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/basic")
public class BasicController {
    @GetMapping("/text-basic")
    public String textBasic(Model model) {
        model.addAttribute("data", "Hello Spring!");
        return "basic/text-basic";
    }

    @GetMapping("/text-unescaped")
    public String textUnescaped(Model model) {
        model.addAttribute("data", "<b>Hello Spring!</b>");
        return "basic/text-unescaped";
    }

    // 변수를 사용할 때 사용: SpringEL
    @GetMapping("/variable")
    public String variable(Model model) {
        User userA = new User("userA", 10);
        User userB = new User("userB", 20);

        List<User> list = new ArrayList<>();
        list.add(userA);
        list.add(userB);

        Map<String, User> map = new HashMap<>();
        map.put("userA", userA);
        map.put("userB", userB);

        model.addAttribute("user", userA);
        model.addAttribute("users", list);
        model.addAttribute("userMap", map);

        return "basic/variable";
    }

    // 타임리프 기본객체 출력
    @GetMapping("/basic-objects")
    public String basicObjects(HttpSession session) {
        session.setAttribute("sessionData", "Hello Session");
        return "basic/basic-objects";
    }

    // 유틸리티 객체와 날짜 출력
    @GetMapping("/date")
    public String date(Model model) {
        model.addAttribute("localDateTime", LocalDateTime.now());
        return "basic/date";
    }

    // URL 링크 생성: 다른 URL로 넘어갈 수 있는 버튼 생성
    @GetMapping("/link")
    public String link(Model model) {
        model.addAttribute("param1", "data1");
        model.addAttribute("param2", "data2");
        return "basic/link";
    }

    // 리터럴: 타임리프의 리터럴은 항상 작은따옴표로 감싼다.
    @GetMapping("/literal")
    public String literal(Model model) {
        model.addAttribute("data", "Spring!");
        return "basic/literal";
    }

    //연산: HTML 내에서 연산자 사용
    @GetMapping("/operation")
    public String operation(Model model) {
        model.addAttribute("nullData", null);
        model.addAttribute("data", "Spring!");
        return "basic/operation";
    }

    //속성 값 설정: 쓸 수 있는 빈칸이나 체크 박스 만들기
    @GetMapping("/attribute")
    public String attribute() {
        return "basic/attribute";
    }

    // 반복(th:each) 타임리프에서 객체를 반복으로 출력할 때
    @GetMapping("/each")
    public String each(Model model) {
        addUsers(model);
        return "basic/each";
    }

    // 조건: 조건에 맞으면 출력, 아니면 출력하지 않게 만듬
    @GetMapping("/condition")
    public String condition(Model model) {
        addUsers(model);
        return "basic/condition";
    }

    //주석: 타임리프에서의 주석처리
    @GetMapping("/comments")
    public String comments(Model model) {
        model.addAttribute("data", "Spring!");
        return "basic/comments";
    }

    //블록: 타임리프의 자체 태그
    @GetMapping("/block")
    public String block(Model model) {
        addUsers(model);
        return "basic/block";
    }

    //자바스크립트 인라인
    @GetMapping("/javascript")
    public String javascript(Model model) {
        model.addAttribute("user", new User("userA", 10));
        return "basic/javascript";
    }

    void addUsers(Model model) {
        List<User> list = new ArrayList<>();
        list.add(new User("userA", 10));
        list.add(new User("userB", 20));
        list.add(new User("userC", 30));
        model.addAttribute("users", list);
    }

    @Data
    static class User {
        private String username;
        private int age;
        public User(String username, int age) {
            this.age = age;
            this.username = username;
        }
    }

    @Component("helloBean")
    static class HelloBean {
        public String hello(String data) {
            return "Hello " + data;
        }
    }
}
