# AIFacetest — AI 관상 분석 웹앱

딥러닝 기반 외부 얼굴인식 API를 호출해 사진 속 얼굴을 분석하고 "관상" 결과를 보여주는 데모 웹 애플리케이션입니다. Spring Boot 백엔드와 정적 HTML/JavaScript 프런트엔드로 구성되어 있습니다.

> 원본 프로젝트(`molpass/AIFacetest`)를 포크해 UI 텍스트와 코드 주석을 한국어로 현지화한 버전입니다.

## 주요 흐름

1. **사진 업로드** — 사용자가 얼굴 사진을 업로드합니다. (원본은 WeChat(微信) JS-SDK의 이미지 선택·업로드 기능을 사용합니다.)
2. **얼굴 특징점 분석** — 백엔드가 알리바바 클라우드의 얼굴 속성(face attribute) API에 이미지를 전달해 성별·나이·표정·안경 착용 여부와 얼굴 특징점(landmark)을 받아옵니다. 받은 특징점을 원본 이미지 위에 그려 시각화합니다.
3. **관상 결과** — 분석된 성별·연령대에 맞춰 미리 준비된 결과 이미지를 골라 "외모"와 "능력" 두 장의 카드로 보여줍니다.

## 기술 스택

- **백엔드**: Java 8, Spring Boot 1.5.9 (WAR 패키징, 내장 Tomcat), fastjson
- **프런트엔드**: 정적 HTML + 바닐라 JavaScript (Swiper, Snap.svg 등), Canvas 파티클 배경
- **외부 API**: 알리바바 클라우드 얼굴 인식, WeChat JS-SDK
- **빌드**: Maven (`mvnw` 래퍼 포함)

## 실행 방법

```bash
./mvnw spring-boot:run
```

> 참고: 이 프로젝트는 Java 8을 기준으로 작성되었으며, `sun.misc.BASE64Encoder` 등 JDK 9 이상에서 제거된 내부 API에 의존합니다. 빌드·실행에는 JDK 8 환경을 권장합니다. 또한 외부 API 키와 서버 경로(`Parameter.java`)가 원작자 환경 기준으로 하드코딩되어 있어, 실제 동작을 재현하려면 해당 값과 API 자격 증명을 본인 환경에 맞게 교체해야 합니다.

## 면책 문구

이 프로젝트는 오락·학습 목적의 데모입니다. "관상" 분석 결과는 과학적 근거가 없으며, 어떠한 실제 판단의 근거로도 사용해서는 안 됩니다.

## 홈 화면
![](src/main/resources/static/images/1.png)

## 이미지 업로드
![](src/main/resources/static/images/2.png)

## 특징점 인식
![](src/main/resources/static/images/3.png)

## 관상 분석
![](src/main/resources/static/images/4.png)
