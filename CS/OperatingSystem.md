# 운영체제
## 1. 프로세스와 스레드


### 1.1 프로세스

CPU Core: 여러 프로세스들을 Switching을 통해 병렬적으로 실행하는 것처럼 보이도록 작동함.

프로세스: 프로그램을 실행하는 단위이며, Interrupt에 대비해 상태를 테이블(ex. 스택 포인터, PC)에 저장할 수 있어야 한다.

그러므로, CPU Core는 적절한 스케줄링 알고리즘을 통해 프로세스들을 실행한다.



+ **CPU 동작 기본 메커니즘**

CPU는 ReadyQueue, BlockQueue를 갖는다. 

1. Process가 생성(fork)되면 ReadyQueue로 보낸다.

2. CPU가 가동되지 않는 상태(Idle)라면, ReadyQueue로부터 스케줄링 알고리즘에 따라 프로세스 하나를 가져온다.

3. Interrupt가 발생하면, 현재 CPU에 가동되고 있는 프로세스를 ReadyQueue에 저장하고 스케줄링 알고리즘에 따라 새로운 프로세스를 가져온다.

4. 만약, I/O에 의한 Interrupt라면, 프로세스는 BlockQueue에 저장되고 I/O가 끝나면 다시 ReadyQueue에 저장한 후, Interrupt를 발생시킨다.


### 1.2 스레드

스레드란 프로세스 내에서의 여러 작업을 개별적으로 처리하기 위해 존재한다.

스레드들은 프로세스에 할당된 메모리를 공유할 수 있다. **그러므로, 동시성 이슈가 있을 수 있다. **

물론, 스레드의 히스토리를 저장하기 위해 개별적인 스택공간도 필요하다. (Program Counter, Register, State, Stack 저장)

스레드는 프로세스 내에서의 개별적인 실행 단위이다. 그러므로, 프로세스 내에서 하나의 스레드가 동작 중이라고 하더라도 나머지 스레드는 다른 작업을 할 수 있다.

+ **User Thread & Kernel Thread**

User 스레드는 User Space에서 관리되는 스레드이다. 그러므로, 스레드 스케줄링을 UserLevel에서 커스터마이징할 수 있다.

물론, Process Block 신호가 오면, User 스레드는 이를 알 수 없다. 이에 대응하기 위해, Block 상태로 전환하기 전에 스레드에게 미리 알리는 방법이 있다.

반면, Kernel Thread는 스레드에 대한 모든 관리를 OS에서 담당한다. 그러므로, User 스레드에 비해 비용이 크다.

두가지를 절충한 Hybrid 스레드는 하나의 Kernel 스레드가 User Thread 묶음을 관리하는 방식이다.


### 1.3 프로세스와 스레드에서의 동시성 문제

대표적으로 프로세스끼리 전역 변수에 접근하거나, 스레드끼리 Heap 공간의 공유 변수에 접근할 때 동시성 문제가 발생할 수 있다.

여러 프로세스(스레드)가 공유 자원에 접근하는 timing에 따라 결과가 달라지는 것을 **Race Condition**이라고 한다.

한 프로세스가 공유 자원에 Write 작업을 하는 시간을 **Critical Region**이라고 한다. 

정리하면, 프로세스가 공유자원에 Write 중이라면, 다른 프로세스는 Race Condition을 피하기 하기 위해 Critical Region을 잘 확인해야 한다.

Race Condition을 막을 수 있는 방법은 아래와 같다.


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

이와 같은 상태를 **DeadLock**이라고 한다. 이를 해결하기 위해 세마포어와 뮤텍스를 사용한다.

**Semaphores & Mutex**

Semaphores와 Mutex 모두 Atomic action을 위해서 존재한다.

두가지 모두 공유변수에 접근하는 것을 제어하고 동기화를 위한 변수이다.

+ Semaphores(세마포어): 공유자원의 상태를 나타내는 카운터 변수이다. 0과 1 뿐만 아니라 더 큰 변수도 저장할 수 있다. 0 또는 임계치에 다다르면 접근하지 못하도록 한다.

+ Mutex(뮤텍스): Critical Region에서 스레드들의 RunTime이 겹치지 않도록 하는 변수이다. lock을 건 프로세스(스레드)만이 그것을 unlock할 수 있다.

전자는 카운팅 세마포어, 후자를 이진 세마포어라고 하기도 한다. 


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
