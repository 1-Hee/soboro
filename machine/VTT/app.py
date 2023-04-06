from flask import Flask
from flask_cors import CORS
from flask_socketio import SocketIO, emit
import io, json 
from PIL import Image
import base64,cv2
import numpy as np
import joblib
import pandas as pd
import mediapipe as mp
import tensorflow as tf
from PIL import Image
from tensorflow.python.keras.models import Sequential   
from tensorflow.python.keras.layers import LSTM, Dense
from sklearn.preprocessing import LabelEncoder

mp_holistic = mp.solutions.holistic
sequential = tf.keras.models.Sequential

with open("config.json") as f:
    data = f.read()
config = json.loads(data)
origin_dir = config["origin_dir"]
model_dir = config["model_dir"]
sentence_dir = config["sentence_dir"]
data_dir = config["data_dir"]

app = Flask(__name__)
app.config['SECRET_KET'] = 'secret!'
socketio = SocketIO(app, cors_allowed_origins='*')
CORS(app, origins="http://localhost:3000")

def make_word_df(word0, word1, word2, word3, word4):
    info = [[word0, word1, word2, word3, word4]]
    df = pd.DataFrame(info, columns = ['target0', 'target1', 'target2', 'target3', 'target4'])
    return df

## 받은 단어를 숫자로 반환
def get_key(val):
    for key, value in my_dict.items():
         if val == key:
             return value
 
    return "There is no such Key"

## 인자로 받은 단어 5개의 데이터프레임을 
def make_num_df(input_1):
    num_oflist = []
    for i in input_1.columns:
        num_oflist.append(get_key(input_1[i].values))
    input2 = make_word_df(num_oflist[0], num_oflist[1], num_oflist[2], num_oflist[3], num_oflist[4])
    return input2

actions = np.array(['None', '안녕하세요', '고맙다', '카드', '통장', '신용카드', '가능하다',
                    '괜찮다', '대신', '도장', '만들다', '많다', '미안하다', '사인', '새로',
                    '아프다', '없다', '원하다', '있다'])

my_dict ={"None":0, "안녕하세요":1, "고맙다":2, "카드":3, "통장":4, "신용카드" :5,
          "가능하다" : 6, "괜찮다" : 7, "대신" : 8, "도장" : 9, "만들다" : 10,
          "많다" : 11, "미안하다" : 12, "사인" : 13, "새로" : 14, "아프다" : 15,
          "없다" : 16, "원하다" : 17, "있다" : 18 }
model = Sequential()
model.add(LSTM(64, return_sequences=True, activation='relu', input_shape=(30, 126)))
model.add(LSTM(128, return_sequences=True, activation='relu'))
model.add(LSTM(64, return_sequences=False, activation='relu'))
model.add(Dense(64, activation='relu'))
model.add(Dense(32, activation='relu'))
model.add(Dense(actions.shape[0], activation='softmax'))
model.load_weights(model_dir) 
rlf = joblib.load(sentence_dir)
data = pd.read_excel(data_dir, engine = 'openpyxl')
data_x = data.drop(['sentence'], axis = 1)
data_y = data['sentence']
le = LabelEncoder()
le.fit(data['sentence'])

def mediapipe_detection(image, model):
    image = cv2.cvtColor(image, cv2.COLOR_BGR2RGB)
    image.flags.writeable = False
    results = model.process(image)
    return results

def extract_keypoints(results):
    lh = np.array([[res.x*3, res.y*3, res.z*3] for res in results.left_hand_landmarks.landmark]).flatten() if results.left_hand_landmarks else np.zeros(21*3)
    rh = np.array([[res.x*3, res.y*3, res.z*3] for res in results.right_hand_landmarks.landmark]).flatten() if results.right_hand_landmarks else np.zeros(21*3)    

    return np.concatenate([lh, rh])

def readb64(base64_string):
    idx = base64_string.find('base64,')
    base64_string  = base64_string[idx+7:]

    sbuf = io.BytesIO()

    # Take in base64 string and return PIL image
    sbuf.write(base64.b64decode(base64_string, ' /'))
    pimg = Image.open(sbuf)

    # convert PIL Image to an RGB image( technically a numpy array ) that's compatible with opencv
    return cv2.cvtColor(np.array(pimg), cv2.COLOR_RGB2BGR)

def moving_average(x):
    return np.mean(x)


@app.route('/')
def sessions():
    return emit("connect")

@socketio.on('catch-frame')
def catch_frame(data):
    emit('response_back', data)  

global count, sequence, sentence, predictions
sequence = []
sentence = []
predictions = []
count = 0

@socketio.on("connect")
def connected():
    emit("connect")

# soboro.js 로 부터 데이터를 받는 부분
@socketio.on('image')
def image(data_image):
    global sequence, sentence, predictions, count
    threshold = 0.5
    frame = (readb64(data_image))
    with mp_holistic.Holistic(min_detection_confidence=0.5, min_tracking_confidence=0.5) as holistic:
        count = count+1
        results = mediapipe_detection(frame, holistic)
        keypoints = extract_keypoints(results)
        sequence.append(keypoints)

        if (len(sequence) % 30 == 0):
            sentence_len1 = len(sentence)
            res = model.predict(np.expand_dims(sequence, axis=0))[0]
            word = actions[np.argmax(res)]
            
            if (word == "고맙다"): emit("results", "고맙습니다.")
            elif (word == "안녕하세요"): emit("results", "안녕하세요.")
            elif (word == "미안하다"): emit("results", "죄송합니다.")
            elif (word == "괜찮다"): emit("results", "괜찮습니다.")            
            elif (word == "없다"): emit("results", "없습니다.")
            elif res[np.argmax(res)] > threshold: 
                if len(sentence) > 0: 
                    if actions[np.argmax(res)] == 'None':
                            sentence.append(actions[np.argmax(res)])
                    else:
                        if(actions[np.argmax(res)] != sentence[-1]):
                            sentence.append(actions[np.argmax(res)])
                else:
                    sentence.append(actions[np.argmax(res)])
            

            sentence_len2 = len(sentence)
            count = 0
            sequence.clear()
            if(sentence_len1 != sentence_len2):                
                if(len(sentence) == 5):
                    data_form = make_word_df(sentence[0], sentence[1], sentence[2], sentence[3], sentence[4])
                    input_data = make_num_df(data_form)
                    y_pred = rlf.predict(input_data)
                    le.inverse_transform(y_pred)
                    predict_word = np.array2string(le.inverse_transform(y_pred))
                    sentence.clear()
                    emit('result', predict_word)
                else:
                    predict_word = sentence[-1]
                    emit('response_back', predict_word)
            else:
                predict_word = "failed"
                emit('response_back', predict_word)          

if __name__ == '__main__':
    socketio.run(app, port=5001, host='0.0.0.0')