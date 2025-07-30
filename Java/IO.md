# IO

데이터 인코딩 및 디코딩, InputStream/OutputStream에 대해서 다룬다.


## 1. InputStream, OutputStream

자바에서는 디스크 IO가 필요할 때 InputStream과 OutputStream을 활용한다. InputStream은 디스크로부터 데이터를 읽을 때 활용하고, OutputStream을 디스크에 데이터를 기록할 때 사용한다.

## 2. 데이터 인코딩 및 디코딩

자바에서는 데이터를 바이트 단위로 인코딩 및 디코딩할 수 있다. 방법은 아래와 같다.

```java
public static void encodeAndDecode(String text, Charset encoding, Charset decoding) {
  byte[] encoded = text.getBytes(text, encoding);
  String decoded = new String(encoded, decoding);
}
```

Charset을 지정하여 인코딩 및 디코딩을 할 수 있다. 만약 명시적으로 지정하지 않는다면, 프로그램의 기본 인코딩이 적용된다.

인코딩 및 디코딩 타입은 서로 호환되도록 적절히 설정해야 한다. 일반적으로 UTF-8을 사용하면 인코딩 및 디코딩이 정상적으로 수행된다.

## 3. 데이터 입출력

바이트 단위로 디스크로 부터 데이터를 읽고, 다시 데이터를 디스크에 쓸 수 있는 방법은 다양하다. 이는 아래와 같다.

### 3.1 1byte 단위 IO

1byte 단위로 IO를 하는 예제 코드는 아래와 같다.

```java

void example() {
  String data = "hello world";
  FileOutputStream fos = new FileOutputStream(FileName);

  byte[] dataBytes = data.getBytes();
  for(int i=0; i<dataBytes.length; i++) {
    fos.write(dataBytes[i]);
  }
  fos.close();

  FileInputStream fis = new FileInputStream(FileName);
  int data;
  while((data = fis.read()) != -1) {
    // TODO. Read File
  }
  fis.close();
}
```

간단하지만 위 코드의 단점은 1Byte를 읽고 쓸 때 마다 매번 디스크 IO가 발생한다는 것이다. IO가 발생할 때 마다 OS 레벨의 시스템 콜이 발생하므로 성능 문제가 있다.

### 3.2 Buffer 단위 IO

IO를 위한 OS 레벨의 시스템 콜을 줄여서 성능을 최적화할 수 있다. 1 바이트 씩 읽지 않고 버퍼 단위로 IO를 함으로써 시스템 콜을 줄일 수 있다.

예제 코드는 아래와 같다.

```java
void test() {
  FileOutputStream fos = new FileOutputStream(FILE_NAME);

  byte[] buffer = new byte[BUFFER_SIZE];
  int bufferIndex = 0;

  for (int i = 0; i < FILE_SIZE; i++) {
    buffer[bufferIndex++] = 1;
    if (bufferIndex == BUFFER_SIZE) {
      fos.write(buffer);
      bufferIndex = 0;
    }
  }

  // 끝 부분에 오면 버퍼가 가득차지 않고, 남아있을 수 있다. 버퍼에 남은 부분 쓰기
  if (bufferIndex > 0) {
    fos.write(buffer, 0, bufferIndex);
  }

  // TODO. FileInputStream도 유사한 방식으로 구현
}
```

버퍼 단위로 IO를 하여 이전보다 시스템 콜을 줄여 성능을 최적화 하였다. 디스크는 4KB 또는 8KB 단위로 Write/Read를 하므로 무한정 버퍼 크기를 늘리기보다는 이를 버퍼 사이즈로 활용하는 것이 좋다.

### 3.3 BufferedInputStream/OutputStream IO

직접 버퍼를 다루지 않고 Java에서 제공하는 BufferedInputStream/OutputStream을 활용할 수도 있다. 하지만, 직접 구현한 것보다 성능은 다소 떨어진다.

왜냐하면 자바에서 제공하는 해당 클래스는 내부적으로 lock-unlock의 동기화 코드가 존재하기 때문에, 락 할당 및 해제에 따라 시간이 발생한다.

그러므로, 동기화를 고려하지 않고 성능이 중요하다면 직접 버퍼를 다루는 것도 좋다.



