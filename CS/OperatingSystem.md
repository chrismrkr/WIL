# 운영체제

## 1. 프로세스와 스레드

### 1.1 프로세스

+ CPU Core: 여러 프로세스들을 Switching을 통해 병렬적으로 실행하는 것처럼 보이도록 작동함.

+ 프로세스: 프로그램을 실행하는 단위이며, Interrupt에 대비해 상태를 테이블(ex. 스택 포인터, Program Counter)에 저장할 수 있어야 한다.

그러므로, CPU Core는 적절한 스케줄링 알고리즘을 통해 프로세스들을 실행한다. 이를 통해 프로세스들이 마치 동시에 실행되는 것처럼 보일 수 있다.  

+ **CPU 동작 기본 메커니즘**

CPU는 ReadyQueue, BlockQueue를 갖는다. 

1. Process가 생성(fork)되면 ReadyQueue로 보낸다.

2. CPU가 가동되지 않는 상태(Idle)라면, ReadyQueue로부터 스케줄링 알고리즘에 따라 프로세스 하나를 가져온다.

3. Interrupt가 발생하면, 현재 CPU에 가동되고 있는 프로세스를 ReadyQueue에 저장하고 스케줄링 알고리즘에 따라 새로운 프로세스를 가져온다.

4. 만약, I/O에 의한 Interrupt라면, 프로세스는 BlockQueue에 저장되고 I/O가 끝나면 다시 ReadyQueue에 저장한 후, Interrupt를 발생시킨다.


### 1.2 스레드

스레드란 프로세스 내에서의 여러 작업을 개별적으로 처리하기 위해 존재한다.

스레드들은 프로세스에 할당된 메모리(힙, 코드, 데이터 영역)를 공유할 수 있다. **그러므로, 동시성 이슈가 있을 수 있다.**

물론, 스레드의 히스토리를 저장하기 위해 개별적인 스택공간도 필요하다. (Program Counter, Register, State, Stack 저장)

스레드는 프로세스 내에서의 개별적인 실행 단위이다. 그러므로, 프로세스 내에서 하나의 스레드가 동작 중이라고 하더라도 나머지 스레드는 다른 작업을 할 수 있다.

+ **싱글 스레드와 멀티 스레드의 장단점**

싱글 스레드는 프로세스 내에서의 race condition 문제가 발생하지 않으므로 deadlock 문제에서 자유롭고, event-driven에 유리하다는 장점이 있다.

그러나, task가 끝날 때 까지 기다려야 한다는 단점이 있다.

멀티 스레드는 여러 작업을 동시에 처리할 수 있으므로 응답성이 높다는 장점이 있다.

그러나, 리소스 점유로 인한 race condition 및 deadlock의 위험성이 있다는 단점이 있다.

+ **User Thread & Kernel Thread**

User 스레드는 User Space에서 관리되는 스레드이다. 그러므로, 스레드 스케줄링을 UserLevel에서 커스터마이징할 수 있다.

물론, Process Block 신호가 오면, User 스레드는 이를 알 수 없다. 이에 대응하기 위해, Block 상태로 전환하기 전에 스레드에게 미리 알리는 방법이 있다.

반면, Kernel Thread는 스레드에 대한 모든 관리를 OS에서 담당한다. 그러므로, User 스레드에 비해 비용이 크다.

두가지를 절충한 Hybrid 스레드는 하나의 Kernel 스레드가 User Thread 묶음을 관리하는 방식이다.


### 1.3 프로세스와 스레드에서의 동시성 문제

프로세스끼리 공유 자원(ex. 프린트기)에 접근하거나, 스레드끼리 Heap, Data, Code 영역 메모리에 접근할 때 동시성 문제가 발생할 수 있다.

여러 프로세스(스레드)가 공유 자원에 접근하는 timing에 따라 결과가 달라지는 것을 **Race Condition**이라고 한다.

프로세스가 공유 자원에 접근하여 작업하므로 다른 프로세스의 접근을 막아야 하는 것을 **Critical Region**이라고 한다. 

정리하면, 프로세스(스레드)가 공유자원에 접근 중이라면, 다른 프로세스(스레드)는 Race Condition을 피하기 하기 위해 Critical Region을 잘 확인해야 한다.

Race Condition을 막을 수 있는 방법은 아래와 같다. 공통적으로 락을 사용한다. (경쟁상태 방지 == 리소스 동기화)

#### 1.3.1 Busy Waiting

Busy Waiting은 매 clock마다 상태를 확인해야 하므로 좋은 해결방법은 아니다.

+ Disabling Interrupt: 한 프로세스가 접근 중이라면 들어오지 못하도록 하는 방법이다. 멀티 코어 환경에서 바람직하지 않다.
+ Lock Variable: 공유변수에 LockVariable을 설정한다. Lock=0일 때만 접근할 수 있도록 한다. 그러나, 동시에 접근하면 문제가 발생한다.
+ Strict Alternation: 프로세스 0은 turn=0일 때, 프로세스 1은 turn=1일 때 접근할 수 있도록 한다.

이 방법은, 두 프로세스가 반드시 번갈아 작동해야 하므로 현실성이 없다.

Lock Variable과 Strict Alternation을 조합하여 Race Condition을 피할 수 있지만, Busy Waiting은 잘 사용되지 않는다.


#### 1.3.2 Sleep and Wake

Busy Wait의 CPU 자원 낭비를 막기 위해 나온 방식이다. Sleep and Wake 방식으로 동시성 이슈를 해결하기 위해서는 **생산자-소비자 문제**를 이해해야 한다.

Python으로 작성된 아래의 의사코드를 보자.

```python
def produce():
  if buffer.full(): sleep()
  else:
      makeItem()
      count++
  if count == 1: wakeConsumer()
  
def consume():
  if buffer.isEmpty(): sleep()
  else:
      consumeItem()
      count--
  if count == N-1: wakeProducer()
```

위의 코드의 문제점을 확인하기 위해, buffer의 최대 크기를 6이라고 하고, 현재 Count는 5이고, buffer에도 5개가 있다고 하자.

1. 생산자가 item을 생성한다. (makeItem())
2. 그 순간 소비자가 item을 소비한다. (consumeItem()) 이때, 소비자는 count는 5라고 인식한다.
3. 생산자가 count를 1 추가한다. (count = 5+1 = 6) buffer가 꽉 찼으므로 생산자는 sleep()한다.
4. 소비자는 count를 5로 인식하고 있기 때문에 소비 이후 count=5-1=4로 설정한다. 
5. count=4이므로 소비자는 생산자를 깨우지 않는다.
6. 소비자가 buffer를 모두 소비하면 sleep() 상태로 전환한다.

이와 같은 상태를 데드락이라고 한다. 

#### 1.3.3 뮤텍스(Mutex)

공유자원 동기화를 위해 사용된다. 스레드가 임계구역에 접근할 때 뮤텍스 상태를 Lock(1)으로 변경한다.

뮤텍스가 Lock으로 변경되면 다른 스레드는 임계구역에 접근할 수 없다.

만약 임계구역 접근이 끝나면 뮤텍스 상태 Unlock(0)으로 변경하고 임계 구역에서 빠져나온다. 뮤텍스를 이진 세마포어라고도 한다.

뮤텍스는 프로세스 동기화를 위해서도 사용되며 대표적으로 bakery 알고리즘이 있다.

+ 1. 모든 프로세스는 번호표를 받는다.
+ 2. 작은 수의 번호표를 받는 사람이 먼저 임계 구역에 진입한다.

#### 1.3.4 세마포어(semaphore)

뮤텍스와 달리 동기화 대상이 여러 개일 때 사용한다. 세마포어는 허용 가능한 리소스의 상태(counter)를 나타낸다.

+ 1. wait()를 통해 프로세스(스레드)가 현재 임계구역에 진입할 수 있는지 확인한데.
+ 2. 진입할 수 있다면 wait()를 빠져나온 후, 임계구역에 들어간다.
+ 3. 임계구역에 빠져나온 후, signal()을 호출한다.


#### 1.3.5 모니터(monitor)

자바에서 사용되는 동기화 도구이다.




### 1.4 스케줄링 알고리즘

프로세스 스케줄링은 프로세스 생성, 프로세스 종료, 프로세스 block, interrupt가 발생했을 때 일어난다.

물론, preemptive 알고리즘(퀀텀 내 다른 프로세스로 스케줄링, Interactive System에서 사용됨)인 경우 clock interrupt가 발생할 때 스케줄링이 일어난다.

#### 1.4.1 Non Preemptive 스케줄링

+ FIFO: 선입선출, 가장 기본이 되는 스케줄링 방식(이것보다는 성능이 높아야 의미있는 스케줄링 알고리즘이다.)
+ SJF(Shortest Job First): 가장 적게 걸리는 프로세스부터 처리. 중간에 Process fork가 있다면 최적의 알고리즘이 아니다.
+ SRTN(Shortest Remaining Time Next): 남은 시간이 가장 적은 프로세스 부터 처리. 최적의 방법이지만 실제로는 남은 시간을 알 수 없다.

#### 1.4.2 Preemptive 스케줄링

+ Round-Robin: 모든 프로세스를 번갈아가면서 퀀텀만큼 작동시킨다. 퀀텀을 얼마로 할 것인지가 중요하다.
+ Priority Scheduling: 우선순위가 높은 프로세스부터 처리한다. starvation이 있을 수 있지만, 오래 기다리는 것의 우선순위를 높임으로써 해결할 수 있다.
+ Lottery Scheduling: n개의 티켓을 할당하고 뽑힌 ticket을 갖는 프로세스마다 일정 CPU Time 할당. 프로세스간 ticket 교환 가능. (highly responsive)

***


## 2. 메모리 관리

메모리는 프로그램 실행을 위해 필요한 한정된 자원이므로 이를 관리하는 것이 필요하다.

### 2.1 No Memory Abstraction

Memory Abstraction이 없다면 직접 Physical Memory에 접근하는 것을 의미한다. 

이러한 경우, 두 프로그램이 동시에 동작한다면 서로의 메모리를 침범할 수 있는 문제가 생긴다.

### 2.2 Address Space를 통한 Memory Abstraction

여전히 Physical Memory에 직접 접근한다는 점에서 이전과 차이는 없다.

그러나, 한 프로그램이 사용하는 메모리를 다른 프로그램이 침범하지 못하도록 적절한 방법을 사용할 수 있다.


#### 2.2.1 Base & limit Address

각 프로세스마다 Base & Limit 레지스터를 통해 메모리를 동적할당한다. 예를 들어, 한 프로그램의 Base가 1000이라고 하자.

JMP 24와 같은 어셈블리 명령어는 JMP 24+1000으로 변환된다.

물론, reference를 할 때 마다 계산을 해야하는 문제도 있다.

또한, 메모리의 크기는 프로세스가 실행됨에 따라 증가할 수 있으므로 여유공간을 확보하는 것이 필요하다

그러나, 여유 공간을 확보하면 Internal Fragmentation 문제가 발생한다.

(Internal Fragmentation: 프로세스가 실제 필요한 메모리 용량보다 더 많은 양을 차지해 낭비가 있는 상태)

#### 2.2.2 Swapping

메모리는 한정적이므로 모든 프로세스를 메모리에서 전부 동작시킬 수 없다. 

Swapping은 프로세스가 idle 상태가 되면 이것을 디스크로 밀어내 메모리에 공간을 확보하는 방식을 의미한다.

메모리를 밀어내면서 External Fragmentation 문제가 발생할 수 있다. 

(External Fragmentation: Free Memory가 충분함에도 흩어져 있어서 프로세스를 실행할 수 없는 경우) 

이를 관리할 수 있는 방법이 필요하다.

#### 2.2.3 Free Memory 관리

메모리를 밀어내면서 생기는 공간을 Free Memory라고 하자. 이를 관리하는 방법은 두가지이다.

첫번째는 비트맵을 통해 관리하는 방법이고 두번째는 연결 리스트를 통해 관리하는 방법이다.

전자는 비트맵 형태로 메모리에서의 한 Unit이 할당되었는지 아닌지를 저장한다. Unit의 크기에 따라 비트맵의 크기가 달라진다.

후자는 연결리스트로 메모리 상태를 관리한다. 그러므로, 할당된 메모리 부분과 Free 메모리 부분이 연결된 형태로 나타난다. 프로세스 시작으로 인한 메모리 할당 시 Free Memory 부분을 찾는다. 

#### 2.3 Virtual Memory

메모리의 전체 용량이 증가하고 프로세스가 필요로 하는 메모리의 크기 또한 증가하면서 모든 프로그램을 메모리에 담을 수 없게 되었다.

그러므로, Swapping을 통해서 더이상 메모리 관리를 할 수 없게 돼 Virtual Memory(가상 메모리)기법이 등장했다.

기본적인 아이디어는 아래와 같다.

+ 프로세스가 필요한 메모리마다 가상 메모리 주소가 존재한다. 
+ 실행하고자 하는 프로그램이 필요한 메모리의 크기를 페이지 단위(ex. 4KB)로 나눈다. 물리 메모리 또한 페이지 단위로 나누어져 있다.
+ 접근하고자 하는 페이지(메모리의 부분)가 PageTable에 맵핑되어 있다면 그것을 실행한다.(hit)
+ 만약 page Table이 매핑되어있지 않고(page fault), 물리 메모리 할당이 가능하다면 Page를 할당한다.
+ 만약 page Table이 매핑되어있지 않고(page fault), 물리 메모리 할당이 불가능하다면 적절한 알고리즘을 통해 대체한다.(replacement)

