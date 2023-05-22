import java.util.ArrayList;
import java.util.List;

public class GC_Leak {
    // leak 가 발생하는 코드
    // list는 정적변수이므로 프로그램 실행 내내 지속 -> 목록이 게속해서 무한정 증가하고 목록에 추가된 개체 중 어느 것도 제거 되지 않음
    // 목록의 개체가 계속 참조되어 가비지 수집을 방지하기 때문에 메모리 누수가 발생
    // 결과적으로 프로그램의 메모리 사용량은 메모리가 부족할 때 까지 계속 증가
    private static final List<Object> list = new ArrayList<>();

    public static void main(String[] args) {
        while (true) {
            Object object = new Object();
            list.add(object);
        }
    }
}
