import os
import base64
import json

from fastapi import FastAPI, UploadFile, File, HTTPException
from fastapi.middleware.cors import CORSMiddleware
from openai import OpenAI

# 환경 변수에서 OPENAI_API_KEY 읽기 (CMD에서 setx로 등록한 값)
client = OpenAI(api_key=os.environ.get("OPENAI_API_KEY"))

app = FastAPI()

app.add_middleware(
    CORSMiddleware,
    allow_origins=["*"],
    allow_methods=["*"],
    allow_headers=["*"],
)

# ------------------------------
# 서버 상태 확인용 엔드포인트
# ------------------------------
@app.get("/health")
def health():
    return {"status": "ok"}


# ------------------------------
# 약 분석 엔드포인트
# ------------------------------
@app.post("/analyze-pill")
async def analyze_pill(image: UploadFile = File(...)):
    """
    안드로이드 앱에서 전송한 알약 사진을 받아서
    ChatGPT에게 5개 후보 중 어떤 약과 가장 비슷한지 물어본 뒤
    그 결과를 JSON으로 반환한다.
    """

    # 1) 이미지 데이터 읽기
    content = await image.read()
    if not content:
        raise HTTPException(status_code=400, detail="이미지 파일이 비어 있습니다.")

    # MIME 타입 (없으면 기본적으로 jpeg로 처리)
    mime_type = image.content_type or "image/jpeg"

    # 2) Base64 인코딩 후 data URL 만들기
    b64 = base64.b64encode(content).decode("utf-8")
    data_url = f"data:{mime_type};base64,{b64}"

    # 3) 약 후보 리스트 (너가 말한 버전 그대로)
    candidates = [
        "타이레놀 500mg (흰색 타원형, 중앙 분할선)",
        "탁센 400mg (연두색 캡슐 형태)",
        "게보린 (분홍색 둥근 정제)",
        "이지엔6 (파랑색 계열 캡슐 또는 정제)",
        "펜잘큐 (노랑색 정제 또는 캡슐)",
    ]

    # 4) ChatGPT에게 줄 프롬프트 문자열
    prompt = (
        "이 이미지는 한국에서 판매되는 알약 사진입니다.\n"
        "아래 5개 후보 중에서 이미지와 가장 비슷한 약을 '정확히 하나만' 고르세요.\n\n"
        "후보 목록:\n"
        + "\n".join(f"{i+1}. {c}" for i, c in enumerate(candidates))
        + "\n\n"
        "반드시 아래 형식의 JSON으로만 답해주세요.\n"
        '{ \"name\": \"타이레놀 500mg\", \"index\": 1 }\n'
        "추가 설명 문장, 한국어 설명, 코드블록 등은 전혀 쓰지 말고 JSON 한 줄만 출력하세요."
    )

    # 5) ChatGPT API 호출 (이미지 + 텍스트 같이 보냄)
    try:
        response = client.responses.create(
            model="gpt-4.1-mini",
            input=[
                {
                    "role": "user",
                    "content": [
                        {"type": "input_text", "text": prompt},
                        {"type": "input_image", "image_url": data_url},
                    ],
                }
            ],
        )
    except Exception as e:
        raise HTTPException(status_code=500, detail=f"ChatGPT 호출 실패: {e}")

    # 6) 모델이 생성한 텍스트(JSON 문자열) 꺼내기
    try:
        output_text = response.output_text  # 전체 응답을 하나의 문자열로
    except Exception:
        raise HTTPException(status_code=500, detail="모델 응답을 읽는 데 실패했습니다.")

    # 7) JSON 파싱
    try:
        data = json.loads(output_text)
        name = data.get("name")
        index = data.get("index")
    except Exception:
        raise HTTPException(status_code=500, detail=f"JSON 파싱 실패: {output_text}")

    if not name:
        raise HTTPException(status_code=500, detail="약 이름(name)이 응답에 없습니다.")

    # 8) 최종으로 앱에 돌려줄 데이터
    return {
        "name": name,
        "index": index,
    }