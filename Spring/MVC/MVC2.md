# 스프링 MVC 2

## 1. 메세지, 국제화

### 핵심 내용 정리 질문


***
***

### 1.1 메세지

화면에 보이는 여러가지 뷰 속에는 공통으로 포함된 메세지가 있을 수 있다. 만약, 공통으로 포함된 메세지를 모두 바꾸고자 한다면 시간낭비가 클 수 있다.

공통으로 포함된 메세지를 한 곳에서 관리하는 기능을 **메세지** 기능이라고 한다.

메세지 기능은 MessageSource 인터페이스를 통해 사용하지만, 스프링 부트에서는 자동으로 사용할 수 있다.

메세지 기능을 사용하는 방법은 아래와 같다.

+ application.properties에 메세지 소스를 설정한다.

```java
  spring.messages.basename=messages
```

messages는 디폴트로 설정되어 있으므로 추가로 MessageSource를 사용하지 않으면 application.properties에 설정하지 않아도 된다.

+ messages 파일을 생성한다

```java
# messages.properties

label.item=상품
label.item.id=상품 ID
label.item.itemName=상품명
label.item.price=가격
label.item.quantity=수량

page.items=상품 목록
page.item=상품 상세
page.addItem=상품 등록
page.updateItem=상품 수정

button.save=저장
button.cancel=취소
```

국제화를 위해서 아래의 코드를 추가할 수 있다.

```java
# messages_en.properties

label.item=Item
label.item.id=Item ID
label.item.itemName=Item Name
label.item.price=price
label.item.quantity=quantity

page.items=Item List
page.item=Item Detail
page.addItem=Item Add
page.updateItem=Item Update

button.save=Save
button.cancel=Cancel
```

+ th:text="#{label.item.price}" 방법으로 원래 메세지를 치환한다.

```HTML
<form action="item.html" th:action th:object="${item}" method="post">
        <div>
            <label for="itemName" th:text="#{label.item.itemName}">상품명</label>
            <input type="text" id="itemName" th:field="*{itemName}" class="form-control" placeholder="이름을 입력하세요">
        </div>
        <div>
            <label for="price" th:text="#{label.item.price}">가격</label>
            <input type="text" id="price" th:field="*{price}" class="form-control" placeholder="가격을 입력하세요">
        </div>
        <div>
            <label for="quantity" th:text="#{label.item.quantity}">수량</label>
            <input type="text" id="quantity" th:field="*{quantity}" class="form-control" placeholder="수량을 입력하세요">
        </div>

        <hr class="my-4">

        <div class="row">
            <div class="col">
                <button class="w-100 btn btn-primary btn-lg" type="submit" th:text="#{button.save}">상품 등록</button>
            </div>
            <div class="col">
                <button class="w-100 btn btn-secondary btn-lg"
                        onclick="location.href='items.html'"
                        th:onclick="|location.href='@{/message/items}'|"
                        type="button" th:text="#{button.cancel}">취소</button>
            </div>
        </div>

    </form>
```

### 1.2 국제화

messages.properties 대신에 messages_en.properties 추가함으로써 자동으로 국제화 기능을 추가할 수 있다.

자동으로 추가되는 이유는 스프링 부트는 AcceptHeaderLocaleResolver를 사용하기 때문이다.

LocaleResolver는 Locale을 선택하는 인터페이스이고, AcceptHeaderLocaleResolver는 Accept-Language에 기반해 국제화하는 구현체이다.

**LocaleResolver의 구현체를 적절히 변경해 쿠키 기반으로 국제화를 할 수 있다.**

***

## 2. 검증1 - Validation

검증은 서버에서 Http Request를 받았을 때, Http Request가 정상적인지를 판별하는 작업이다. 

예를 들어, 웹 화면에서 클라이언트가 Integer를 입력해 서버로 Request 한다고 하자. 서버 입장에서는 클라이언트가 특정 범위 내의 Integer만으로 제한해야 할 수 있고, Integer 이외의 다른 타입의 값을 입력하는 것을 막아야한다.

이러한 일련의 작업을 검증이라고 한다.

### 2.1 검증 처리 - 기본

검증 작업을 하기 이전에 스프링 MVC의 동작 메커니즘을 다시 살펴볼 필요가 있다.

사용자가 특정 웹 페이지에서 Http API를 통해 서버에 Request한다. 서버의 컨트롤러는 Request를 받아서 검증한다.

검증 후, Request가 정상적이라면 뷰로 렌더링한다.

만약, Request가 정상적이지 않다면 Model에 에러코드(검증 결과)를 담아 원래 웹 페이지로 돌아온 후, 어떤 것이 정상적인지 않은지 알려야 한다.

코드를 통해 확인하도록 한다. 아래의 예시는 상품의 이름, 가격, 그리고 수량을 추가하는 컨트롤러이다.

```java
  @PostMapping("/add")
    public String addItem(@ModelAttribute Item item, RedirectAttributes redirectAttributes, Model model) {

        // 검증 오류 결과를 보관
        Map<String, String> errors = new HashMap<>();

        // 검증 로직
        if(!StringUtils.hasText(item.getItemName())) { // itemName이 입력안됨
            errors.put("itemName", "상품 이름은 필수입니다.");
        }
        if(item.getPrice() == null || item.getPrice() < 1000 || item.getPrice() > 1000000) { // price가 이상함
            errors.put("price", "가격은 1,000 ~ 1,000,000 까지 허용합니다.");
        }
        if(item.getQuantity() == null || item.getQuantity() >= 9999) { // quantity 비정상적
            errors.put("quantity", "수량은 최대 9,999개 까지 허용합니다.");
        }

        // 특정 필드가 아닌 복합 룰 검증
        if(item.getPrice() != null && item.getQuantity() != null) {
            int resultPrice = item.getPrice() * item.getQuantity();
            if (resultPrice < 10000) {
                errors.put("globalError", "가격 * 수량의 합은 10,000원 이상이어야 합니다. 현재 값 = " + resultPrice);
            }
        }

        // 검증에 실패하면 다시 입력 폼으로
        if (!errors.isEmpty()) {
            log.info("errors = {}", errors);
            model.addAttribute("errors", errors);
            return "validation/v1/addForm";
        }

        // 성공 로직
        Item savedItem = itemRepository.save(item);

        // 리다이렉트 객체에 저장한다.
        redirectAttributes.addAttribute("itemId", savedItem.getId());
        redirectAttributes.addAttribute("status", true);
       
        
        return "redirect:/validation/v1/items/{itemId}";
    }
```

위의 코드는 검증 로직을 거친 후, 검증에 성공하면 다음 페이지로 redirect하는 컨트롤러이다.

검증 실패 시, errors Map에 내용을 저장한다. 그리고 errors를 Model에 담아서 에러코드와 함께 웹 페이지를 다시 호출한다.

```HTML
 <!-- 검증 오류 처리 -->
        <div th:if="${errors?.containsKey('globalError')}">
          
            <p class="field-error" th:text="${errors['globalError']}">전체 오류 메세지</p>
          
        </div>

        <!-- 검증 오류 처리 -->
        <div>
            <label for="itemName" th:text="#{label.item.itemName}">상품명</label>
            <input type="text" id="itemName" th:field="*{itemName}"
                   
                   th:class="${errors?.containsKey('itemName')} ? 'form-control field-error' : 'form-control'"
                   
                   class="form-control" placeholder="이름을 입력하세요">
          
            <div class="field-error" th:if="${errors?.containsKey('itemName')}" th:text="${errors['itemName']}" >
              
                상품명 오류
            </div>
        </div>
```

th:class="${errors?.containsKey('itemName')} ? 'form-control field-error' : 'form-control'"

th:if="${errors?.containsKey('itemName')}"

이 두 부분을 통해 Model 안에 있는 에러코드(errors)를 출력할 수 있다.

**그러나, 이와 같은 검증 방법은 아래와 같은 문제점이 있다.**

+ 뷰 템플릿에서 중복처리가 많다.
+ 타입 오류는 검증할 수 없다. 
+ 타입 오류가 발생하더라도 클라이언트가 입력한 값을 유지해야한다.

### 2.2 검증 처리 V1

스프링이 제공하는 BindingResult를 통해 기본 검증 처리에서 사용했던 Errors Map을 대체하도록 한다.

BindingResult는 스프링에서 제공하는 검증 오류 보관 객체이다.

```java
    @PostMapping("/add") -> 오류가 발생하는 경우 사용자가 입력한 내용이 모두 없어진다.
    public String addItemV1(@ModelAttribute Item item, BindingResult bindingResult, RedirectAttributes redirectAttributes, Model model) {

        // 검증 오류 결과를 보관

        // 검증 로직 (FieldError: 필드에러, ObjectError: 글로벌 에러)
        if(!StringUtils.hasText(item.getItemName())) { // itemName이 입력안됨
            bindingResult.addError(new FieldError("item", "itemName", "상품 이름은 필수입니다.")); // ctrl+p 옵션
        }
        if(item.getPrice() == null || item.getPrice() < 1000 || item.getPrice() > 1000000) { // price가 이상함
            bindingResult.addError(new FieldError("item", "price", "가격은 1,000 ~ 1,000,000까지 허용됩니다.")); // ctrl+p 옵션 확인
        }
        if(item.getQuantity() == null || item.getQuantity() >= 9999) {
            bindingResult.addError(new FieldError("item", "quantity", "수량은 최대 9,999개 까지 허용됩니다.")); // ctrl+p 옵션 확인
        }

        // 특정 필드가 아닌 복합 룰 검증
        if(item.getPrice() != null && item.getQuantity() != null) {
            int resultPrice = item.getPrice() * item.getQuantity();
            if (resultPrice < 10000) {
                bindingResult.addError(new ObjectError("item", "가격 * 수량의 합은 10,000원 이상이어야 합니다. 현재 값 = " + resultPrice)); // ctrl+p 옵션 확인
            }
        }

        // 검증에 실패하면 다시 입력 폼으로
        if (bindingResult.hasErrors()) {
            log.info("errors = {}", bindingResult);
            return "validation/v2/addForm";
        }

        // 성공 로직
        Item savedItem = itemRepository.save(item);

        redirectAttributes.addAttribute("itemId", savedItem.getId());
        redirectAttributes.addAttribute("status", true);
        return "redirect:/validation/v2/items/{itemId}";
    }
```

Error Map을 사용하는 기본 검증 처리와 크게 달라진 것은 없다. 글로벌 오류와 필드 오류의 차이만 주의하도록 하자.

### 2.3 검증 처리 V2

검증 처리 V1에서 사용했던 BindingResult는 @ModelAttribute 바인딩 시 타입 오류가 있는 경우도 처리할 수 있다.

V1에서는 검증 에러(타입 에러, 비즈니스 로직 에러) 발생 시 사용자가 입력한 내용이 유지되지 않는다.

FieldError 객체의 생성자를 살펴보자

```java
new FieldError(String objectName, String field, @Nullable Object rejectedValue, boolean bindingFailure, @Nullable String[] codes, 
                  @Nullable Object[] arguments, @Nullable String defaultMessage);
 ```
   
rejectedValue에 기존에 Item의 값을 추가함으로써 사용자가 입력한 내용을 유지할 수 있다.


```java
    @PostMapping("/add")
    public String addItemV2(@ModelAttribute Item item, BindingResult bindingResult, RedirectAttributes redirectAttributes, Model model) {

        // 검증 오류 결과를 보관

        // 검증 로직 (FieldError: 필드에러, ObjectError: 글로벌 에러)
        if(!StringUtils.hasText(item.getItemName())) { // itemName이 입력안됨
            bindingResult.addError(new FieldError("item", "itemName",
                    item.getItemName(), false, null, null, "상품 이름은 필수입니다."));
        }
        if(item.getPrice() == null || item.getPrice() < 1000 || item.getPrice() > 1000000) { // price가 이상함
            bindingResult.addError(new FieldError("item", "price",
                    item.getPrice(), false, null, null, "가격은 1,000 ~ 1,000,000까지 허용됩니다."));
        }
        if(item.getQuantity() == null || item.getQuantity() >= 9999) {
            bindingResult.addError(new FieldError("item", "quantity",
                    item.getQuantity(), false, null, null, "수량은 최대 9,999개 까지 허용됩니다."));
        }

        // 특정 필드가 아닌 복합 룰 검증
        if(item.getPrice() != null && item.getQuantity() != null) {
            int resultPrice = item.getPrice() * item.getQuantity();
            if (resultPrice < 10000) {
                bindingResult.addError(new ObjectError("item", null, null,"가격 * 수량의 합은 10,000원 이상이어야 합니다. 현재 값 = " + resultPrice)); // ctrl+p 옵션 확인
            }
        }

        // 검증에 실패하면 다시 입력 폼으로
        if (bindingResult.hasErrors()) {
            log.info("errors = {}", bindingResult);
            return "validation/v2/addForm";
        }

        // 성공 로직
        Item savedItem = itemRepository.save(item);

        redirectAttributes.addAttribute("itemId", savedItem.getId());
        redirectAttributes.addAttribute("status", true);
        return "redirect:/validation/v2/items/{itemId}";
    }
 ```


### 2.4 검증 처리 V3

검증 처리 V2는 검증 오류 발생 시, 사용자에게 보여주는 오류 메세지를 직접 컨트롤러마다 작성해야한다.

만약 오류 메세지를 바꿔야 한다면, 컨트롤러 자체를 수정해야 한다. 이는 시간이 낭비되는 작업이다.

그러므로, errors 메세지 파일을 생성해서 오류 메세지를 한 곳에서 관리하도록 하자.

```java
# application.properties
spring.messages.basename=messages, errors

# errors.properties

required.item.itemName=상품 이름은 필수입니다.
range.item.price=가격은 {0} ~ {1} 까지 허용합니다.
max.item.quantity=수량은 최대 {0} 까지 허용합니다.
totalPriceMin=가격 * 수량의 합은 {0}원 이상이어야 합니다. 현재 값 = {1}
```

오류 메세지를 관리하는 errors.properties를 만들고 아래와 같이 컨트롤러를 수정한다.

```java
    @PostMapping("/add")
    public String addItemV3(@ModelAttribute Item item, BindingResult bindingResult, RedirectAttributes redirectAttributes, Model model) {

        // 검증 로직 (FieldError: 필드에러, ObjectError: 글로벌 에러)
        if(!StringUtils.hasText(item.getItemName())) { // itemName이 입력안됨
            bindingResult.addError(new FieldError("item", "itemName",
                    item.getItemName(), false, new String[]{"required.item.itemName"}, null, "null"));
        }
        if(item.getPrice() == null || item.getPrice() < 1000 || item.getPrice() > 1000000) { // price가 이상함
            bindingResult.addError(new FieldError("item", "price",
                    item.getPrice(), false, new String[]{"range.item.price"}, new Object[]{1000, 1000000}, null));
        }
        if(item.getQuantity() == null || item.getQuantity() >= 9999) {
            bindingResult.addError(new FieldError("item", "quantity",
                    item.getQuantity(), false, new String[]{"max.item.quantity"}, new Object[]{9999}, null));
        }

        // 특정 필드가 아닌 복합 룰 검증
        if(item.getPrice() != null && item.getQuantity() != null) {
            int resultPrice = item.getPrice() * item.getQuantity();
            if (resultPrice < 10000) {
                bindingResult.addError(new ObjectError("item", new String[]{"totalPriceMin"}, new Object[]{10000, resultPrice}, null));
            }
        }

        // 검증에 실패하면 다시 입력 폼으로
        if (bindingResult.hasErrors()) {
            log.info("errors = {}", bindingResult);
            return "validation/v2/addForm";
        }

        // 성공 로직
        Item savedItem = itemRepository.save(item);

        redirectAttributes.addAttribute("itemId", savedItem.getId());
        redirectAttributes.addAttribute("status", true);
        return "redirect:/validation/v2/items/{itemId}";
    }
```

물론, errors_en.properties 등을 추가하면 오류 메세지를 국제화할 수 있다.

### 2.5 검증 처리 V4

FieldError와 ObjectError 객체를 사용하는 방식을 보면, 다소 번거로운 것이 있다. 

BindingResult가 제공하는 rejectValue()와 reject()를 통해 더 깔끔하게 검증 오류를 다룰 수 있다.

```java
    @PostMapping("/add")
    public String addItemV4(@ModelAttribute Item item, BindingResult bindingResult, RedirectAttributes redirectAttributes, Model model) {

        log.info("objectName={}", bindingResult.getObjectName());
        log.info("target={}", bindingResult.getTarget());

        // 검증 로직 (FieldError: 필드에러, ObjectError: 글로벌 에러)
        if(!StringUtils.hasText(item.getItemName())) { // itemName이 입력안됨
            bindingResult.rejectValue("itemName", "required");
            //errorCode.objectName.field 순서로 쓰면 error.properties에서 찾는다.
        }
        if(item.getPrice() == null || item.getPrice() < 1000 || item.getPrice() > 1000000) { // price가 이상함
            bindingResult.rejectValue("price", "range", new Object[]{1000, 1000000}, null);
        }
        if(item.getQuantity() == null || item.getQuantity() >= 9999) {
            bindingResult.rejectValue("quantity", "max", new Object[]{9999}, null);
        }

        // 특정 필드가 아닌 복합 룰 검증
        if(item.getPrice() != null && item.getQuantity() != null) {
            int resultPrice = item.getPrice() * item.getQuantity();
            if (resultPrice < 10000) {
                bindingResult.reject("totalPriceMin", new Object[]{10000, resultPrice}, null);
            }
        }

        // 검증에 실패하면 다시 입력 폼으로
        if (bindingResult.hasErrors()) {
            log.info("errors = {}", bindingResult);
            return "validation/v2/addForm";
        }

        // 성공 로직
        Item savedItem = itemRepository.save(item);

        redirectAttributes.addAttribute("itemId", savedItem.getId());
        redirectAttributes.addAttribute("status", true);
        return "redirect:/validation/v2/items/{itemId}";
    }
```

rejectValue()와 reject()를 사용하면 자동으로 errors.properties에서 에러 메세지를 찾는다. 어떻게 이것이 가능할까?

rejectValue()와 reject()에서 MessageCodesResolver를 사용해서 오류 코드를 생성한다.

rejectValue()와 reject()의 매개변수는 아래와 같다.

```java
  void rejectValue(@Nullable String field, String errorCode, @Nullable Object[] errorArgs, @Nullable String defaultMessage);
  void reject(String errorCode, @Nullable Object[] errorArgs, @Nullable String defaultMessage);
```

MessageCodeResolver는 rejectValue()의 에러 코드를 아래와 같은 방식으로 생성한다.

+ errorCode.objectName.field
+ errorCode.field
+ errorCode.java.lang.String
+ errorCode

reject()의 경우는 아래와 같다.

+ errorCode.objectName
+ errorCode

에러 메세지를 관리하는 파일(errors.properties)에 해당된 에러 코드가 있다면 그것을 불러온다.

구체적인 에러 코드에서 그렇지 않은 에러 코드 순서로 오류 메세지를 할당한다.

타입을 잘못 입력해 발생하는 오류 또한 error.properties에서 관리할 수 있다.

```java

#==ObjectError==
#Level1
totalPriceMin.item=상품의 가격 * 수량의 합은 {0}원 이상이어야 합니다. 현재 값 = {1}

#Level2 - 생략
totalPriceMin=전체 가격은 {0}원 이상이어야 합니다. 현재 값 = {1}

#==FieldError==
#Level1
required.item.itemName=상품 이름은 필수입니다.
range.item.price=가격은 {0} ~ {1} 까지 허용합니다.
max.item.quantity=수량은 최대 {0} 까지 허용합니다.

#Level2 - 생략

#Level3
required.java.lang.String = 필수 문자입니다.
required.java.lang.Integer = 필수 숫자입니다.
min.java.lang.String = {0} 이상의 문자를 입력해주세요.
min.java.lang.Integer = {0} 이상의 숫자를 입력해주세요.
range.java.lang.String = {0} ~ {1} 까지의 문자를 입력해주세요.
range.java.lang.Integer = {0} ~ {1} 까지의 숫자를 입력해주세요.
max.java.lang.String = {0} 까지의 문자를 허용합니다.
max.java.lang.Integer = {0} 까지의 숫자를 허용합니다.

#Level4
required = 필수 값 입니다.
min= {0} 이상이어야 합니다.
range= {0} ~ {1} 범위를 허용합니다.
max= {0} 까지 허용합니다.

#추가
typeMismatch.java.lang.Integer=숫자를 입력해주세요
typeMismatch=타입 오류입니다.

```

### 2.6 검증 처리 V5

컨트롤러에서 검증 처리하는 부분은 실제 정상 로직을 처리하는 부분보다 더욱 많다.

검증 처리 하는 부분을 스프링에서 제공하는 Validator 인터페이스를 구현해 클래스로 만들어 따로 정리하고, 이를 스프링 빈으로 등록해 사용할 수 있다.

```java
@Component
public class ItemValidator implements Validator {

    @Override
    public boolean supports(Class<?> clazz) {
        return Item.class.isAssignableFrom(clazz);
    }

    @Override
    public void validate(Object target, Errors errors) {
        // Object: 컨트롤러에서 관리하는 Data, Errors: BindingResult의 부모클래스
        Item item = (Item) target;
        BindingResult bindingResult = (BindingResult) errors;

        // 검증 로직 (FieldError: 필드에러, ObjectError: 글로벌 에러)
        if(!StringUtils.hasText(item.getItemName())) { // itemName이 입력안됨
            bindingResult.rejectValue("itemName", "required");
            //errorCode.objectName.field 순서로 쓰면 error.properties에서 찾는다.
        }
        if(item.getPrice() == null || item.getPrice() < 1000 || item.getPrice() > 1000000) { // price가 이상함
            bindingResult.rejectValue("price", "range", new Object[]{1000, 1000000}, null);
        }
        if(item.getQuantity() == null || item.getQuantity() >= 9999) {
            bindingResult.rejectValue("quantity", "max", new Object[]{9999}, null);
        }

        // 특정 필드가 아닌 복합 룰 검증
        if(item.getPrice() != null && item.getQuantity() != null) {
            int resultPrice = item.getPrice() * item.getQuantity();
            if (resultPrice < 10000) {
                bindingResult.reject("totalPriceMin", new Object[]{10000, resultPrice}, null);
            }
        }
    }
}
```

```java
@Controller
@RequestMapping("/validation/v2/items")
@RequiredArgsConstructor
public class ValidationItemControllerV2 {

    private final ItemRepository itemRepository;
    private final ItemValidator itemValidator;

    ...
    
    @PostMapping("/add")
    public String addItemV5(@ModelAttribute Item item, BindingResult bindingResult, RedirectAttributes redirectAttributes, Model model) {

        itemValidator.validate(item, bindingResult);

        // 검증에 실패하면 다시 입력 폼으로
        if (bindingResult.hasErrors()) {
            log.info("errors = {}", bindingResult);
            return "validation/v2/addForm";
        }

        // 성공 로직
        Item savedItem = itemRepository.save(item);

        redirectAttributes.addAttribute("itemId", savedItem.getId());
        redirectAttributes.addAttribute("status", true);
        return "redirect:/validation/v2/items/{itemId}";
    }
}
```

### 2.7 검증 처리 V6

컨트롤러 안에서 검증 로직을 실행 할 수도 있지만, @Validated Annotation을 통해서도 가능하다. 아래를 확인하자.

```java
    @InitBinder
    public void init(WebDataBinder dataBinder) {
        dataBinder.addValidators(itemValidator);
    }

    @PostMapping("/add")
    public String addItemV6(@Validated @ModelAttribute Item item, BindingResult bindingResult, RedirectAttributes redirectAttributes, Model model) {

        // 검증에 실패하면 다시 입력 폼으로
        if (bindingResult.hasErrors()) {
            log.info("errors = {}", bindingResult);
            return "validation/v2/addForm";
        }

        // 성공 로직
        Item savedItem = itemRepository.save(item);
        redirectAttributes.addAttribute("itemId", savedItem.getId());
        redirectAttributes.addAttribute("status", true);
        return "redirect:/validation/v2/items/{itemId}";
    }  
```

***

## 3. 검증2 - Bean Validation

모든 검증 기능을 매번 BindingResult를 이용해 코드로 작성하는 것은 번거롭다.

모든 프로젝트에 적용할 수 있는 일반화된 검증 로직이 Bean Validation이다.

이전의 검증 로직은 아래와 같다.

+ Item은 itemName이 반드시 존재해야 한다.
+ Item의 Price는 1000 ~ 1000000이다.
+ Item의 Quantity는 최대 9999이다.


이를 Bean Validation을 통해 간단히 아래와 같이 적용할 수 있다.


```java
@Data // getter, setter 모두 사용
public class Item {

    @NotNull(groups = UpdateCheck.class)
    private Long id;

    @NotBlank(message = "공백안됨.", groups = {SaveCheck.class, UpdateCheck.class})
    private String itemName;

    @NotNull(groups = {SaveCheck.class, UpdateCheck.class})
    @Range(min=1000, max=1000000, groups = {SaveCheck.class, UpdateCheck.class})
    private Integer price;

    @NotNull(groups = {SaveCheck.class, UpdateCheck.class})
    @Max(value=9999, groups = {SaveCheck.class})
    private Integer quantity;

    public Item() {
    }

    public Item(String itemName, Integer price, Integer quantity) {
        this.itemName = itemName;
        this.price = price;
        this.quantity = quantity;
    }
}
```

**물론, 스프링 부트에 implementation 'org.springframework.boot:spring-boot-starter-validation'를 추가 해야한다.**

또한, @Validated Annotation이 적용되어야 한다.

검증 오류 발생 시, 자동으로 BindingResult에 FieldError를 담는다.

@Validated를 통한 검증 메커니즘은 아래와 같다.

+ ModelAttribute를 통해 타입 바인딩을 시도한다.
+ 만약, 바인딩에 실패하면 TypeMismatch 에러 코드를 BindingResult에 담는다.
+ 바인딩 성공 시, 자동으로 검증이 시작된다.

### 3.1 Bean Validation 에러 코드 수정

Bean Validation의 에러 코드는 자체적으로 디폴트 값이 존재한다. 이를 변경하려면 어떻게 해야 할까?

아래와 같은 패턴으로 에러코드가 생성된다. (이전에는 ErrorCode.object.field 순서로 생성되었다.)

**@NotNull**
+ NotNull.object.fieldName
+ NotNull.fieldName
+ NotNull.java.lang.String
+ NotBlank

**@Range**
+ Range.object.fieldName
+ Range.fieldName
+ Range.java.lang.Integer
+ Range

마찬가지로 errors.properties에 에러코드를 추가해 에러 코드를 바꿀 수 있다.

Object 에러는 비즈니스 요구사항에 따라 변화할 수 있으므로 직접 Validation 코드를 작성하는 것이 좋다.


### 3.2 Form 전송 객체 분리 

만약 Item 등록과 Item 수정의 검증 로직이 다르다면, 위와 같은 방식으로 Bean Validation을 적용할 수 없다. 

그러므로, Item 등록 Form과 Item 수정 Form을 분리해야 한다.

```java
    @PostMapping("/add")
    public String addItem(@Validated @ModelAttribute("item") ItemSaveForm form, BindingResult bindingResult, RedirectAttributes redirectAttributes) {
        // "item" 지정 안해주면 Model.addAttribute("itemSaveForm", form)으로 들어감

        // 특정 필드가 아닌 복합 룰 검증
        if(form.getPrice() != null && form.getQuantity() != null) {
            int resultPrice = form.getPrice() * form.getQuantity();
            if (resultPrice < 10000) {
                bindingResult.reject("totalPriceMin", new Object[]{10000, resultPrice}, null);
            }
        }
        // 검증에 실패하면 다시 입력 폼으로
        if (bindingResult.hasErrors()) {
            log.info("errors = {}", bindingResult);
            return "validation/v4/addForm";
        }

        // 성공 로직

        Item item = new Item(form.getItemName(), form.getPrice(), form.getQuantity());

        Item savedItem = itemRepository.save(item);
        redirectAttributes.addAttribute("itemId", savedItem.getId());
        redirectAttributes.addAttribute("status", true);
        return "redirect:/validation/v4/items/{itemId}";
    }
    
    @PostMapping("/{itemId}/edit")
    public String editV2(@PathVariable Long itemId, @Validated @ModelAttribute("item") ItemUpdateForm form, BindingResult bindingResult,
                         RedirectAttributes redirectAttributes) {
        // 특정 필드가 아닌 복합 룰 검증
        if(form.getPrice() != null && form.getQuantity() != null) {
            int resultPrice = form.getPrice() * form.getQuantity();
            if (resultPrice < 10000) {
                bindingResult.reject("totalPriceMin", new Object[]{10000, resultPrice}, null);
            }
        }

        if(bindingResult.hasErrors()) {
            log.info("errors = {}", bindingResult);
            return "validation/v4/editForm";
        }

        Item itemParam = new Item(form.getItemName(), form.getPrice(), form.getQuantity());

        itemRepository.update(itemId, itemParam);
        return "redirect:/validation/v4/items/{itemId}";
    }
```

@ModelAttribute에 "item"을 지정하지 않으면 model.addAttribute("itemUpdateForm", form)으로 model 객체에 저장된다.

### 3.3 Bean Validation: HTTP 메세지 컨버터

지금까지는 request Body를 포함하는 Http 요청을 검증하는 과정은 없었다.

request Body에 내용이 담긴 Http Request의 검증 로직은 아래와 같다.

+ 1. @RequestBody로 메세지를 받을 때, MessageConverter를 통해 객체로 적절히 변환되었는가?
+ 2. 객체로 적절히 변형되었다면, field 또는 globalError가 있는가?
+ 3. 검증 성공

```java
@RestController
@RequestMapping("/validation/api/items")
public class ValidationItemApiController {

    @PostMapping("/add")
    public Object addItem(@RequestBody @Validated ItemSaveForm form, BindingResult bindingResult) {
        log.info("API 컨트롤러 호출");
        if(bindingResult.hasErrors()) {
            log.info("검증 오류 발생", bindingResult);
            return bindingResult.getAllErrors();
        }

        log.info("API 컨트롤러 성공");
        return form;
    }
}
```

1번의 오류의 경우, 아예 Json Body가 ItemSaveForm 객체로 변환되는 것에 실패되었으므로 컨트롤러 자체가 호출되지 않는다.

**@ModelAttribute와 @ResponseBody 차이점은?**

@ModelAttribute는 HTTP request Parameter를 필드 개별로 바인딩하고,

@ResponseBody는 Http MessageBody의 json 메세지를 객체에 바인딩하는 것이다. 그러므로, 후자의 경우 바인딩에 실패하면 컨트롤러가 아예 호출되지 않는다.

컨트롤러가 호출되지 않으므로, 검증 처리도 불가능하다.

***

## 4. 쿠키와 세션

### 4.1 로그인 처리를 위한 쿠키 사용

HTTP는 기본적으로 stateless하다. 그러나, 로그인 이후에는 stateful한 서비스를 제공해야 한다.

이를 위해서 쿠키를 사용한다. 클라이언트의 로그인 요청 시, 서버는 쿠키를 생성해 클라이언트에게 전달하고 클라이언트는 쿠키를 계속 사용한다.

```java
    @PostMapping("/login")
    public String login(@Validated @ModelAttribute("loginForm") LoginForm form, BindingResult bindingResult, HttpServletResponse response) {
        if(bindingResult.hasErrors()) {
            log.info("bindingResult={}", bindingResult);
            return "login/loginForm";
        }

        Member login = loginService.login(form.getLoginId(), form.getPassword());
        if(login == null) {
            bindingResult.reject("loginFail", "아이디 또는 비밀번호가 맞지 않습니다.");
            return "login/loginForm";
        }

        // 로그인 성공 처리
        // 클라이언트에게 쿠키를 제공해야함. 시간정보를 주지 않으면 세션쿠키(브라우저 종료시 모두 없어짐)
        Cookie idCookie = new Cookie("memberId", String.valueOf(login.getId()));
        response.addCookie(idCookie);
        return "redirect:/";
    }
    
    
    @GetMapping("/")
    public String homeLogin(@CookieValue(name="memberId", required = false) Long memberId, Model model) {
        if(memberId == null) {
            return "home";
        }

        // 쿠키가 있는 사용자
        log.info("memberId={}", memberId);
        Member loginMember = memberRepository.findById(memberId);

        if(loginMember == null) {
            return "home";
        }

        model.addAttribute("member", loginMember);
        return "loginHome";
    }
    
```

로그인 시, 쿠키를 생성하고 이후 작업들은 클라이언트가 가진 쿠키를 활용하도록 한다.

그러나, 이와 같은 방식은 쿠키 정보를 누군가 탈취할 수 있고, 쿠키를 클라이언트가 강제로 변경할 수 있다.

### 4.2 로그인 처리를 위한 세션 사용

위의 방식을 개선하기 위해 세션을 사용한다. 세션은 쿠키를 서버에서 관리한다는 점에서 차이가 있다.

```java
@Component
public class SessionManager {

    public static final String SESSION_COOKIE_NAME = "mySessionId";
    private Map<String, Object> sessionStore = new ConcurrentHashMap<>();

    /*
    세션 생성
     */
    public void createSession(Object value, HttpServletResponse response) {
        // 세션 Id 생성하고, 값을 세션에 저장 -> 서버에서 관리할 수 있도록 함.
        String sessionId = UUID.randomUUID().toString();
        sessionStore.put(sessionId, value);

        // 쿠키 생성 후, response에 전달 -> 클라이언트에게 세션쿠키를 전달함.
        Cookie cookie = new Cookie(SESSION_COOKIE_NAME, sessionId);
        response.addCookie(cookie);
    }

    /*
    세션 조회
     */
    public Object getSession(HttpServletRequest request) {
        Cookie cookie = findCookie(request, SESSION_COOKIE_NAME);
        if(cookie == null) return null;
        else return sessionStore.get(cookie.getValue());
    }

    public Cookie findCookie(HttpServletRequest request, String cookieName) {
        Cookie[] cookies = request.getCookies();
        if(cookies == null) return null;
        return Arrays.stream(cookies).filter(cookie -> cookie.getName().equals(cookieName))
                .findAny().orElse(null);
    }

    /*
    세션 만료
     */
    public void expire(HttpServletRequest request) {
        Cookie cookie = findCookie(request, SESSION_COOKIE_NAME);
        if(cookie != null) {
            sessionStore.remove(cookie.getValue());
        }
    }
}

...

/* Controller */

    @PostMapping("/login")
    public String loginV2(@Validated @ModelAttribute("loginForm") LoginForm form, BindingResult bindingResult, HttpServletResponse response) {
        if(bindingResult.hasErrors()) {
            log.info("bindingResult={}", bindingResult);
            return "login/loginForm";
        }

        Member loginMember = loginService.login(form.getLoginId(), form.getPassword());
        if(loginMember == null) {
            bindingResult.reject("loginFail", "아이디 또는 비밀번호가 맞지 않습니다.");
            return "login/loginForm";
        }

        // 로그인 성공 처리
        // 클라이언트에게 쿠키를 제공해야함. 시간정보를 주지 않으면 세션쿠키(브라우저 종료시 모두 없어짐)
        sessionManager.createSession(loginMember, response);
        return "redirect:/";
    }
```

물론, 세션을 직접 구현하지 않고 서블릿에서 제공하는 **HttpSession**을 사용할 수 있다.

세션의 동작 방식은 직접 구현한 것과 크게 다르지 않다.

```java
    @PostMapping("/login")
    public String loginV3(@Validated @ModelAttribute("loginForm") LoginForm form, BindingResult bindingResult, HttpServletRequest request) {
        if(bindingResult.hasErrors()) {
            log.info("bindingResult={}", bindingResult);
            return "login/loginForm";
        }

        Member loginMember = loginService.login(form.getLoginId(), form.getPassword());
        if(loginMember == null) {
            bindingResult.reject("loginFail", "아이디 또는 비밀번호가 맞지 않습니다.");
            return "login/loginForm";
        }

        // 로그인 성공 처리
        HttpSession session = request.getSession();
        
        session.setAttribute(SessionConst.LOGIN_MEMBER, loginMember);
        /*
          1. 랜덤 쿠키(Id)를 생성한다.
          2. session에 <Id, loginMember>를 저장한다.
          3. session에서 SessionConst.LOGIN_MEMBER를 통해 사용자의 Id를 찾을 수 있다.
        */
        return "redirect:/";
    }
    
    @GetMapping("/")
    public String homeLoginV3(HttpServletRequest request, Model model) {

        // 세션 관리자에 저장된 회원정보 조회
        HttpSession session = request.getSession(false);
        if(session == null) { return "home"; }

        Member member = (Member)session.getAttribute(SessionConst.LOGIN_MEMBER);
        if(member == null) {
            return "home";
        }

        model.addAttribute("member", member);
        return "loginHome";
    }

```

**세션 클러스터링이란?**

실무에서는 트래픽의 분산처리를 위해 여러 WAS를 사용하게 된다. 이때, WAS 끼리는 session 저장소를 공유할 수 있도록 하는 것을 세션 클러스터링이라고 한다.

물론, JVM의 Heap 공간을 키울 수 있지만, GC 때문에 처리시간이 길어져 장애가 발생할 수 있다.


세션에 이미 저장된 것을 찾을 때는 @SessionAttribute Annotation을 사용할 수 있다.

```java
    @GetMapping("/")
    public String homeLoginV3Spring(@SessionAttribute(name = SessionConst.LOGIN_MEMBER, required = false) Member member, Model model) {
        if(member == null) {
            return "home";
        }

        model.addAttribute("member", member);
        return "loginHome";
    }
```

세션이 URL에 노출되지 않도록 application.properties에 아래를 추가할 수 있다.

server.servlet.session.tracking-modes=cookie

세션은 기본적으로 30분에 한번씩 초기화되도록 설정되어 있다. 

이를 application.properties에서 global 초기화 시간을 수정할 수 있다.

server.servlet.session.timeout=60 // 60초로 바꾼다.

물론, 특정 세션 단위로 이를 변경할 수 있다.


**리프레시 토큰이란?**

세션의 유지 시간을 늘리면 사용자의 편의성은 증가하지만, 보안성은 떨어진다. 이를 개선하기 위해 나온 개념이 리프레시 토큰이다.

클라이언트가 로그인하면, 서버는 세션과 리프레시 토큰을 함께 보낸다.

만약, 세션이 만료된 상태로 사용자가 서버에 요청을 보내면, 서버는 클라이언트에게 세션이 만료되었다고 신호를 보낸다.

클라이언트는 만료된 세션과 리프레시 토큰을 서버에 전송하고, 리프레시 토큰을 검증 후 서버는 새로운 세션과 요청에 대한 응답을 전달한다.

***

## 5. 필터와 인터셉터

지금까지 설계한 예제는 외부에서 직접 URL 링크로 로그인이 필요한 뷰로 접근할 수 있었다.

권한이 없는 사용자가 외부 링크를 통해 접근하는 것을 막는 **서블릿 필터** 기능이 필요하다.

스프링에서 제공하는 Filter 인터페이스를 이용해 필터 기능을 구현할 수 있다. Filter 인터페이스를 구현한 객체는 싱글톤 객체로 생성된다.

### 5.1 필터를 이용한 로그 체크

필터의 구현 및 동작 방법을 이해하기 위해서 로그를 체크하는 간단한 필터를 생성하도록 하자.

```java

@Slfj4
public class LogFilter implements Filter {
  @Override
  public void init(...) { ... }
  @Override 
  public void destroy() { ... }
  
  /* 초기화, 종료 메서드는 기본적으로 구현되어있지만, 추가로 커스터마이징 할 수 있다. */
  
 @Override 
 public void doFilter(ServletRequest request, ServletResponse response, FilterChain filterChain) throws IOException, ServletException {
      HttpRequestServlet httpRequest = (HttpServletRequest) request;
      String requestURI = httpRequest.getURI();
      
      String uuid = UUID.randomUUID().toString();
      try {
          log.info("REQUEST [{}][{}]", uuid, requestURI);
          chain.doFilter(request, response);
      } catch (Exception e) {
          throw e;
      } finally {
            log.info("RESPONSE [{}][{}]", uuid, requestURI);
      }    
   }
}

@Configuration
public class WebConfig {
    @Bean
    public FilterRegistrationBean logFilter() {
        FilterRegistrationBean<Filter> filterFilterRegistrationBean = new FilterRegistrationBean<>();
        filterFilterRegistrationBean.setFilter(new LogFilter());
        filterFilterRegistrationBean.setOrder(1);
        filterFilterRegistrationBean.addUrlPatterns("/*");
        return filterFilterRegistrationBean;
    }
 }
```

필터는 Http 요청 -> WAS -> 필터 -> 서블릿-> 컨트롤러 흐름으로 동작한다.

### 5.2 필터를 이용한 로그인 인증 체크

마찬가지로 Filter를 구현한다.

```java
public class LoginCheckFilter implements Filter {

    private static final String[] whileList = {"/", "/members/add", "/login", "/logout", "/css/*"};

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        HttpServletRequest httpRequest = (HttpServletRequest) request;
        String requestURI = httpRequest.getRequestURI();

        HttpServletResponse httpResponse = (HttpServletResponse) response;

        try {
            log.info("인증 체크 필터 시작{}", requestURI);

            if(isLoginCheckPath(requestURI)) {
                log.info("인증 체크 로직 실행 {}", requestURI);
                HttpSession session = httpRequest.getSession(false);
                if(session == null || session.getAttribute(SessionConst.LOGIN_MEMBER) == null) {
                    log.info("미인증 사용자 요청 {}", request);
                    // 로그인으로 redirect
                    httpResponse.sendRedirect("/login?redirectURL=" + requestURI);
                    return;
                }
            }

            chain.doFilter(request, response);
        }
        catch (Exception e) {
            throw e; // 예외 로깅 가능 하지만, 톰켓까지 예외를 보내야한다.
        } finally {
            log.info("인증 체크 필터 종료 {}", requestURI);
        }
     }
     /*
     화이트 리스트의 경우 인증 체크를 안하는 메서드
      */
    private boolean isLoginCheckPath(String requestURI) {
        return !PatternMatchUtils.simpleMatch(whileList, requestURI);
        // 화이트 리스트에 없다 -> 로그인 체크를 해야한다.
    }
}

class WebConfig implements WebMvcConfigurer {
    ...
    @Bean
    public FilterRegistrationBean loginCheckFilter() {
        FilterRegistrationBean<Filter> filterFilterRegistrationBean = new FilterRegistrationBean<>();
        filterFilterRegistrationBean.setFilter(new LoginCheckFilter());
        filterFilterRegistrationBean.setOrder(2);
        filterFilterRegistrationBean.addUrlPatterns("/*");
        return filterFilterRegistrationBean;
    }
}

```

### 5.3 인터셉터를 이용한 로그 및 인증 체크

스프링에서 제공하는 인터셉터를 통해 위와 동일한 기능을 구현할 수 있다.

필터와 달리 스프링 인터셉터의 흐름은, Http 요청 -> WAS -> 서블릿 -> 필터 -> 컨트롤러 순서로 동작한다.

그러므로, 서블릿 이전에 필터링을 해야하는 것이 아니라면 스프링 필터 기능을 사용하는 것이 바람직하다.

스프링 인터셉터가 MVC 패턴에서 동작하는 과정은 아래와 같다.

1. HTTP Request
2. Dispatcher에서 prehandler를 호출한다. 
3. HTTP Request에 맞는 핸들러 어댑터와 핸들러를 호출한 후, ModelAndView를 반환한다.
4. posthandler를 호출한다
5. 뷰로 렌더링 한 후, afterCompletion을 호출해 인터셉터를 종료한다.

만약, HTTP Request에서 예외가 발생하면 preHandler와 afterCompletion만 호출한다.

스프링 인터셉터를 구현하기 위해서는 HandlerInterceptor 인터페이스를 구현하면 된다. 마찬가지로 인터페이스를 구현하면 싱글톤 객체로 관리된다.

@RequestMapping을 활용한다면 handler에는 HandlerMethod가 넘어오고, 정적 리소스를 호출하면 ResourceHttpRequestHandler가 넘어온다.

로그 체크와 인증 기능을 담당하는 스프링 인터셉터는 아래와 같다.


```java
public class LogInterceptor implements HandlerInterceptor {

    private final static String LOG_ID = "logId";

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        String requestURI = request.getRequestURI();
        String uuid = UUID.randomUUID().toString();

        request.setAttribute(LOG_ID, uuid);

        // @RequestMapping: HandlerMethod
        // 정적 리소스: ResourceHttpRequestHandler

        if(handler instanceof HandlerMethod) {
            HandlerMethod hm = (HandlerMethod) handler; // 호출할 컨트롤러 메서드의 모든 정보가 포함되어있다.
        }

        log.info("REQUEST [{}][{}][{}]", uuid, requestURI, handler);

        return true;
    }

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {
        log.info("postHanlder [{}]", modelAndView);
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        String requestURI = request.getRequestURI();
        String logId = (String) request.getAttribute(LOG_ID);
        log.info("RESPONSE [{}][{}][{}]", logId, requestURI, handler);

        if(ex != null) { // 예외가 있다면?
            log.error("afterCompletion error!!", ex);
        }
    }
}

...

public class LoginCheckInterceptor implements HandlerInterceptor {
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        String requestURI = request.getRequestURI();

        log.info("인증 체크 인터셉터 실행 {}", requestURI);

        try {
            HttpSession session = request.getSession();
            if(session == null || session.getAttribute(SessionConst.LOGIN_MEMBER) == null) {
                log.info("미인증 사용자 요청");
                response.sendRedirect("/login?redirectURL="+requestURI);
                // whiteList Check는 WebConfig에 등록 시 설정한다.
                return false;
            }
        } catch (Exception e) {

        }
        return true;
    }
}

public class WebConfig implements WebMvcConfigurer {
    ...
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(new LogInterceptor())
                .order(1)
                .addPathPatterns("/**")
                .excludePathPatterns("/css/**", "/*.ico", "/error");
        registry.addInterceptor(new LoginCheckInterceptor())
                .order(2)
                .addPathPatterns("/**")
                .excludePathPatterns("/", "/members/add", "/login", "/logout", "/css/**", "/*.icon", "/error");
    }
}
```

***




