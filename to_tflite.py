import tensorflow as tf

# 1) 우리가 학습해둔 Keras 모델 파일 불러오기
model = tf.keras.models.load_model("saved_model.keras")

# 2) 안드로이드에서 돌아가도록 TFLite 변환 옵션 설정
converter = tf.lite.TFLiteConverter.from_keras_model(model)
converter.target_spec.supported_ops = [
    tf.lite.OpsSet.TFLITE_BUILTINS  # 안드로이드 기본 연산만 사용
]
converter.optimizations = []  # 양자화 안 함 (문제 생길 요소 줄이기)

# 3) 실제 변환
tflite_model = converter.convert()

# 4) 현재 폴더에 model.tflite 이름으로 저장 (덮어쓰기)
with open("model.tflite", "wb") as f:
    f.write(tflite_model)

print("✅ 변환 완료! model.tflite 생성/갱신됨")
