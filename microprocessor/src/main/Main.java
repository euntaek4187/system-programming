package main;

public class Main {
    public static void main(String[] args) {
    	MicroProcessor microprocessor = new MicroProcessor();
        MainFrameGUI mainFrameGUI = new MainFrameGUI(microprocessor);
        mainFrameGUI.initialize();
        mainFrameGUI.setVisible(true);
    }
}
// ver.4 - 코드세그먼트에 현재라인 하이라이트되는 부분까지 완료, 세부단계 진행을 위한 세팅
// ver.5 - showExplanation(), 설명 구체화, cpu표현 값 정확히 되도록 수정
// ver.6 - instruction 분리 & 구체화
// ver.7 - 파일 읽는거 기반 생성 + not, and, shr 추가
// ver.8 - 오타 수정 및 기능 오류 수정 + halt바로 종료되는거 수정, 0x표현식 정상으로 수정
// ver.9 - 코드 보기좋게 정리 + gui initialize()로 분리
// ver.9.1 - stack 구현 성공 - call, ret
// ver.9.2 - heap 구현 성공 - newc, star, ldar