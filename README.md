# 💊 SafePill  
**알약을 촬영하면, 복용 전 꼭 알아야 할 정보를 한눈에.**

SafePill은 사용자가 **카메라로 알약을 촬영하면**,  
서버에서 약의 정보를 분석하고  
**연령대 · 임신 여부 · 알레르기 정보**를 반영해  
개인에게 맞는 **복용 주의사항을 안내하는 AI 기반 알약 정보 앱**입니다.

일상에서 약을 복용하기 전  
“이 약, 나한테 괜찮은 걸까?”  
라는 고민을 줄이기 위해 만들어졌습니다.

<p align="center">
  <a href="https://youtube.com/shorts/gKXrew_Xky8?feature=share">
    <img src="https://img.shields.io/badge/DEMO-YouTube-red?style=for-the-badge" />
  </a>
  <a href="./safe%20pill.pptx">
    <img src="https://img.shields.io/badge/PPTX-DOWNLOAD-blue?style=for-the-badge" />
  </a>
  <a href="./backend">
    <img src="https://img.shields.io/badge/Backend-FastAPI-black?style=for-the-badge" />
  </a>
</p>

---

## 🔎 한눈에 보는 SafePill
- 📷 **알약 촬영 한 번으로 정보 확인**
- 🧑‍⚕️ **내 정보 기반 맞춤 주의사항 제공**
- 🚨 **응급 상황 대비 119 다이얼 버튼**
- 📜 **스크롤 가능한 결과 화면**
- 🤖 **GPT API 기반 약 정보 분석**
- 🧪 현재는 **사전 정의된 5종 알약** 지원

---

## 📽️ 데모 영상
SafePill의 실제 동작 흐름은 아래 데모 영상에서 확인할 수 있습니다.

[![SafePill Demo](https://img.youtube.com/vi/gKXrew_Xky8/0.jpg)](https://youtube.com/shorts/gKXrew_Xky8?feature=share)

---

## 🖼️ 앱 실행 화면

| 메인 화면 | 분석 결과 | 내 정보 |
|---|---|---|
| <img src="./main.jpg" width="260"/> | <img src="./result.jpg" width="260"/> | <img src="./profile.jpg" width="260"/> |

---

## ⚙️ 주요 기능 소개

### 📷 알약 인식 및 분석
- 사용자가 알약을 촬영하면 이미지가 서버로 전송됩니다.
- 서버는 GPT API를 활용해 해당 알약의 정보를 분석합니다.
- 분석 결과는 약 이름, 기본 정보, 복용 시 주의사항 형태로 제공됩니다.

---

### 🧑‍⚕️ 개인 맞춤 주의사항
SafePill은 단순한 약 정보 제공을 넘어,  
**사용자의 상황을 고려한 안내**를 목표로 합니다.

- 연령대
- 임신 여부
- 알레르기 유무  

위 정보를 바탕으로,  
일반적인 주의사항에 **개인화된 문장을 추가**해 안내합니다.

---

### 🚨 119 다이얼 버튼
- 응급 상황을 대비한 UX 요소
- 실제 신고는 연결되지 않으며  
  **전화 다이얼 화면만 실행**되도록 구현되어 있습니다.

---

### 📜 결과 화면 스크롤 지원
- 약 정보와 주의사항이 길어질 경우를 고려
- 결과 화면 전체를 **스크롤로 확인 가능**

---

## 🧩 시스템 구조 (Architecture)

