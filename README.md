# 💊 SafePill
**카메라로 알약을 촬영하면** 서버가 약을 분석하고,  
**연령대 · 임신 여부 · 알레르기 정보**를 반영해 개인 맞춤형 주의사항을 안내하는 **AI 기반 알약 정보 앱**입니다.

<p align="center">
  <a href="https://youtube.com/shorts/gKXrew_Xky8?feature=share">
    <img src="https://img.shields.io/badge/DEMO-YouTube-red?style=for-the-badge" />
  </a>
  <a href="./docs/SafePill_Presentation.pptx">
    <img src="https://img.shields.io/badge/PPTX-DOWNLOAD-blue?style=for-the-badge" />
  </a>
  <a href="./backend">
    <img src="https://img.shields.io/badge/Backend-FastAPI-black?style=for-the-badge" />
  </a>
</p>

---

## 🔥 한눈에 보는 핵심
- 📷 **알약 촬영 → 서버 분석 → 약 이름/주의사항 출력**
- 🧑‍⚕️ **내 정보 기반 개인화 안내** (연령대/임신/알레르기)
- 🚨 **119 버튼**: 실제 신고 연결 X, **다이얼 화면만 실행**
- 📜 **결과 스크롤 지원**: 긴 주의사항도 끝까지 확인 가능
- 🤖 **GPT API 기반**: 현재 **사전 정의 5종 알약** 지원

---

## 📽️ 데모 영상
[![SafePill Demo](https://img.youtube.com/vi/gKXrew_Xky8/0.jpg)](https://youtube.com/shorts/gKXrew_Xky8?feature=share)

---

## 🖼️ 앱 실행 화면
> 이미지가 레포 루트에 있다면 아래처럼 그대로 써도 됩니다.  
> (나중에 `images/` 폴더로 옮기면 경로만 바꾸면 됨)

| 메인 화면 | 분석 결과 | 내 정보 |
|---|---|---|
| <img src="./main.jpg" width="260"/> | <img src="./result.jpg" width="260"/> | <img src="./profile.jpg" width="260"/> |

---

## ⚙️ 주요 기능
### 1) 알약 인식 (촬영 기반)
- 사용자가 알약 촬영
- FastAPI 서버로 이미지 전송
- 서버가 GPT 기반으로 약 정보를 구성해 응답
- 앱에서 결과 화면에 출력

### 2) 개인화 주의사항
- **연령대 / 임신 여부 / 알레르기 유무**를 반영해  
  약별 주의사항을 “추가 문장” 형태로 강화 표시

### 3) 119 다이얼(안전 장치)
- 긴급 상황 대응 UX 제공  
- 실제 통화 연결은 하지 않고 **다이얼 화면만 띄움**

---

## 🧩 시스템 구조(아키텍처)
