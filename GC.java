import java.util.ArrayList;
import java.util.List;

public class GC{
    // leak 가 발생하지 않는 코드
    // 목록의 크기가 특정 임계값(10000)에 도달했는지 확인하기 위해 루프 내부에 조건을 추가
    // 임계값에 도달하면 remove(0)매서드를 사용하여 목록에서 첫번째 개체를 제거
    // 이러한 과정을 통해서 목록이 무한정으로 계속 증가하지 않도록 방지합니다. 
    
    // 프로그램은 목록에 개체를  계속 추가하지만 임계값에 도달하면 가장 오래된 개체도 제거 
    // -> 가비지 수집기가 더이상 참조되지 않는 개체가 차지하는 메모리 회수 -> 메모리 누수 방지
    private static final List<Object> list = new ArrayList<>();

    public static void main(String[] args) {
        while (true) {
            Object object = new Object();
            list.add(object);

            if (list.size() >= 10000) { 
                list.remove(0); 
            }
        }
    }
}