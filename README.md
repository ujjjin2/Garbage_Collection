# 가비지컬렉션
#### ■ 가비지컬렉션이란?
- 자바의 메모리 관리 방법 중 하나로 JVM (자바 가상머신)의 Heap 영역에서 *동적으로 할당했던 메모리* 중 *필요 없게 된 메모리 객체(garbage)를 모아 주기적으로 제거*하는 프로세스
- C/C++에서는 가비지 컬렉션 x -> 프로그래머가 수동으로 메모리 할당과 해제를 직접 해줘야 됨
- 반면, 자바에서는 가비지 컬렉터가 관리를 대행 -> 한정된 메모리를 효율적으로 사용할 수 있음
- 또한, 개발자의 입장에서 메모리 관리, 메모리 누수(memory leak)문제에 대해 관리하지 않아도 되어 오롯이 *개발에만 집중* 할 수 있다는 장점이 있음

#### [ 예시 ] 
<pre><code>for (int i = 0; i < 10000; i++) {
  NewObject obj = new NewObject();  
  obj.doSomething();
}</code></pre>
- NewObject 객체는 루프가 끝나고 루프 밖에서는 더이상 사용할 일 x
- 이런 객체들이 메모리를 계속 점유하고 있다면, 다른 코드를 실행하기 위한 메모리 자원 지속적으로 줄어든다.
- 가비지 컬렉션 (GC)이 한번 쓰이고 버려지는 객체들을 주기적으로 비워줌으로써 한정된 메모리를 효율적으로 사용할 수 있게 해줌

> 가비지 컬렉션의 단점
> 자동으로 처리해준다 해도 메모리가 언제 해제되는지 정확하게 알 수 x
> 가비지 컬렉션이 동작하는 동안 <code>다른동작을 멈추기</code> 떄문에 <code>오베헤드</code> 발생 -> Stop-The-World
>> STW(Stop The World)
>> - GC를 수행하기 위해 JVM이 프로그램 실행을 멈추는 현상
>> - GC가 작동하는 동안 GC와 관련 Thread를 제외한 모든 Thread는 멈추게 되어 서비스에 차질  => 이 시간을 최소화 시키는 것이 쟁점

GC 최적화 작업 : GC 튜닝

### ■ 가비지컬렉션 대상
가비지 컬렉션은 어떤 object를 Garbage로 판단해서 지울까?
<p>
가비지 컬렉션은 특정 객체가 garbage인지 아닌지 판단하기 위해서 도달성, 도달 능력이라는 개념을 적용<p>
- 객체에 레퍼런스가 있다, 객체가 참조되고 있는 상태 : Reachable<p>
- 객체에 유효한 레퍼런스가 없다, 객체가 참조되고 있지 않은 상태(GC의 대상) : Unreachable

### ■ 가비지컬렉션 청소 방식
Mark-Sweep : 다양한 GC에서 사용되는 객체를 솎아내는 내부 알고리즘<p>
가비지 컬렉션이 동작하는 아주 기초적인 청소 과정 -> 가비지 컬렉션이 될 대상 객체를 *식별(Mark)* 하고 *제거(Sweep)* 하며 제거되어 파편화된 메모리 영역을 앞으로부터 *채워나가는 작업(Compaction)* 을 수행된다.
- Mark 과정 : 먼저 <code>Root Space</code>로부터 그래프 순회를 통해 연결된 객체들을 찾아내어 각각 어떤 객체를 참조하고 있는지 찾아서 마킹한다.
- Sweep 과정 : 참조하고 있지 않은 객체 즉 Unreachable 객체들을 Heap에서 제거한다.
- Compact 과정 : Sweep 후에 분산된 객체들을 Heap의 시작 주소로 모아 메모리가 할당된 부분과 그렇지 않은 부분으로 압축한다. (가비지 콜렉터 종류에 따라 하지 않는 경우도 있음)

> [ GC의 Root Space ]<P>
> Mark And Sweep 방식은 루트로 부터 해당 객체에 접근이 가능한지가 해제의 기준<P>
> JVM GC에서의 Root Space는 Heap 메모리 영역을 참조하는 method area, static 변수, stack, native method stack이 되게 된다.

### ■ Minor GC와 Major GC
#### Heap 메모리의 구조
JVM의 힙(heap) 영역은 동적 레퍼런스 데이터가 저장되는 공간, 가비지 컬렉션에 대상이 되는 공간<p>
[ 처음 설계 될 때 하는 2가지의 전제]<p>
1. 대부분의 객체는 금방 접근 불가능한 상태(Unreachable)가 된다.
2. 오래된 객체에서 새로운 객체로의 참조는 아주 적게 존재한다.<p>

=> 이러한 특성을 이용하여 메모리 관리를 효율적으로 하기 위해서 객체의 생존 기간에따른 물리적인 Heap 영역을 나누고, <code>Young과 Old 총 2가지 영역</code> 으로 설게함

#### Young 영역
- 새롭게 생성된 객체가 할당(Allocation)되는 영역
- 대부분의 객체가 금방 Unreachable 상태가 되기 때문에, 많은 객체가 Young 영역에 생성되었다가 사라진다.
- Young 영역에 대한 가비지 컬렉션을 Minor GC라고 부른다.

#### Old 영역
- Young영역에서 Reachable 상태를 유지하여 살아남은 객체가 복사되는 영역
- Young 영역보다 크게 할당되며, 영역의 크기가 큰 만큼 가비지는 적게 발생
- Old 영역에 대한 가비지 컬렉션을 Major GC 또는 Full GC라고 부른다.

※ Old영역이 Young 영역보다 크게 할당되는 이유 <p>
Young 영역의 수명이 짧은 객체들은 큰 공간 필요 x, 큰 객체들은 Young 영역이 아니라 Old 영역에 할당됨

### ■ Garbage Collection(가비지 컬렉션)의 동작 방식
young구조는 1개의 Eden, 2개의 Survivor 영역
- Eden 영역 : 새로 생성된 객체가 할당(Allocation)되는 영역
- Survivor 영역 : 최소 1번 GC 이상 살아남은 객체가 존재하는 영역

[Minor GC 동작 방식]<p>
객체가 새롭게 생성 -> Eden 영역에 할당됨 -> Eden 영역이 꽉차면 Minor GC발생<p>

1. 새로 생성된 객체가 Eden 영역에 할당된다
2.  객체가 계속 생성되어 Eden 영역이 꽉차게 되고 Minor GC가 실행된다
  - Eden 영역에서 사용되지 않는 객체의 메모리가 해제된다.
  - Eden 영역에서 살아남은 객체는 1개의 Survivor 영역으로 이동된다.
3. 1~2번의 과정이 반복되다가 Survivor 영역이 가득 차게 되면 Survivor 영역의 살아남은 객체를 다른 Survivor 영역으로 이동시킨다.(1개의 Survivor 영역은 반드시 빈 상태가 된다.)
4. 이러한 과정을 반복하여 계속해서 살아남은 객체는 Old 영역으로 이동(Promotion)된다.

객체의 생존 횟수를 카운트하기 위해 Minor GC에서 살아남은 횟수를 의미 하는 age를 Object Header에 기록<p>
Minor GC때 Object Header에 기록된 age를 보고 Promotion 여부를 결정

[ Major GC의 동작 방식 ]
- Young 영역에서 오래 살아남은 객체는 Old 영역으로 Promotion됨을 확인할 수 있다.
- Major GC는 객체들이 계속 Promotion되어 Old 영역의 메모리가 부족해지면 발생
- Old 영역은 Young 영역보다 크며 Young 영역을 참조 가능 -> Major GC는 일반적으로 Minor GC보다 시간이 오래걸리며, 10배 이상의 시간을 사용


|GC 종류|Minor GC|Major GC|
|---|---|---|
|대상|Young Generation|Old Generation|
|실행 시점|Eden 영역이 꽉찬 경우|Old 영역이 꽉 찬 경우|
|실행 속도|빠르다|느리다|
