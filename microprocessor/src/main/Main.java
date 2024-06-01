package main;
public class Main {
    public static void main(String[] args) {
    	MicroProcessor microprocessor = new MicroProcessor();
        MainFrameGUI mainFrameGUI = new MainFrameGUI(microprocessor);
        mainFrameGUI.setVisible(true);
    }
}

// ver.4 - 코드세그먼트에 현재라인 하이라이트되는 부분까지 완료, 세부단계 진행을 위한 세팅
// ver.5 - showExplanation(), 설명 구체화, cpu표현 값 정확히 되도록 수정