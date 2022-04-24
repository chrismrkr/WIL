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




