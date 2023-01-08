# 이펙티브 자바

Java의 심화적인 내용을 다루도록 한다.

## 1. Builder 패턴

빌더 패턴은 객체 생성 방식의 가독성을 높이기 위해 등장한 패턴이다. 예를 들어, 아래의 필드를 갖는 회원 엔티티 클래스가 존재한다고 하자.

```java
public class Member {
  private String username;
  private String password;
  private String email;
  private int age;
  private String birthday;
}
```

생성자를 통해 아래와 같이 객체를 생성할 수 있다.

```java
public class Member {
  private String username;
  private String password;
  private String email;
  private int age;
  private String birthday;
  
  public Member(String username, String password, String email, int age, String birthday) {
    this.username = username;
    this.password = password;
    this.email = email;
    this.age = age;
    this.birthday = birthday;
  }
}

public void main() {
  Member member = new Member("user", "1234", "xxxx@...com", 30, "1998-11-11");
}
```

위 방식에는 몇가지 문제가 있다.

첫번째는 가독성이 떨어진다. 개발자가 Member 객체를 생성할 때, 항상 클래스의 생성자 함수가 어떻게 되어 있는지 확인해야 한다.

두번째는 유연성이 떨어진다. 개발자가 특정 필드만을 설정해서 객체를 생성하려고 하면, 매번 새로운 생성자 함수를 만들어야 한다.

극단적으로 필드가 총 5개이므로 기본 생성자까지 포함해 총 5 + 5*4/2*1 + 5*4*3/3*2*1 + 5*4*3*2/4*3*2*1 + 1 개의 생성자가 필요하다.

이러한 경우에 빌더 패턴이 필요하다. 아래와 같이 코드를 수정할 수 있다.

```java
public class Member {
  private String username;
  private String password;
  private String email;
  private int age;
  private String birthday;
  
  public static Builder builder() {
    return new Builder();
  }
  
  private Member(Builder builder) {
    this.username = builder.username;
    this.password = builder.password;
    this.age = builder.age;
    this.email = builder.email;
    this.birthday = builder.birthday;
  }
  
  private static class Builder {
    private String username;
    private String password;
    private String email;
    private int age;
    private String birthday;
        
    public Builder username(String username) {
      this.username = username;
      return this;
    }
    
    public Builder password(String password) {
      this.password = password;
      return this;
    }
    
    public Builder email(String email) {
      this.email = email;
      return this;
    }
    
    public Builder age(int age) {
      this.age = age;
      return this;
    }
    
    public Builder birthday(String birthday) {
      this.birthday = birthday;
      return this;
    }
    
    public Member build() {
      return new Member(this);
    }
  }
}

public void main() {
  Member member = Member.builder()
                        .username("user")
                        .password("1234")
                        .build();
}
```

코드가 길어졌다는 단점이 있다. 그러나, 객체를 생성하는 입장에서는 생성자 방식보다 더 직관적으로 사용할 수 있다.
