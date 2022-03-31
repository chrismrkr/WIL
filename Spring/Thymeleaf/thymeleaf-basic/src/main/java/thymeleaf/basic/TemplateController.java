package thymeleaf.basic;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/template")
public class TemplateController {

    // 템플릿 조각: 웹 페이지 공통 영역들을 나타나내는 부분을 모듈화해서 코드 조각으로 사용함
    @GetMapping("/fragment")
    public String template() {
        return "template/fragment/fragmentMain";
    }

    // 템플릿 레이아웃: 코드조각을 아예 레이아웃에 넘겨서 사용함(헤드에 사용)
    @GetMapping("/layout")
    public String layout() {
        return "template/layout/layoutMain";
    }

    // 템플릿 레이아웃2: 헤드를 넘어 HTML 전체에 템플릿 적용
    @GetMapping("/layoutExtend")
    public String layoutExtend() {
        return "template/layoutExtend/layoutExtendMain";
    }

}
