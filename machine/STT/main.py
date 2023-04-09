# Copyright (c) 2020, Soohwan Kim. All rights reserved.
#
# Licensed under the Apache License, Version 2.0 (the "License");
# you may not use this file except in compliance with the License.
# You may obtain a copy of the License at
#
#     http://www.apache.org/licenses/LICENSE-2.0
#
# Unless required by applicable law or agreed to in writing, software
# distributed under the License is distributed on an "AS IS" BASIS,
# WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
# See the License for the specific language governing permissions and
# limitations under the License.

# [original code] inference.py

# import argparse
# import torch
# import torch.nn as nn
# import numpy as np
# import torchaudio
# from torch import Tensor

# from kospeech.vocabs.ksponspeech import KsponSpeechVocabulary
# from kospeech.data.audio.core import load_audio
# from kospeech.models import (
#     SpeechTransformer,
#     Jasper,
#     DeepSpeech2,
#     ListenAttendSpell,
#     Conformer,
# )


# def parse_audio(audio_path: str, del_silence: bool = False, audio_extension: str = 'pcm') -> Tensor:
#     signal = load_audio(audio_path, del_silence, extension=audio_extension)
#     feature = torchaudio.compliance.kaldi.fbank(
#         waveform=Tensor(signal).unsqueeze(0),
#         num_mel_bins=80,
#         frame_length=20,
#         frame_shift=10,
#         window_type='hamming'
#     ).transpose(0, 1).numpy()

#     feature -= feature.mean()
#     feature /= np.std(feature)

#     return torch.FloatTensor(feature).transpose(0, 1)


# parser = argparse.ArgumentParser(description='KoSpeech')
# parser.add_argument('--model_path', type=str, required=True)
# parser.add_argument('--audio_path', type=str, required=True)
# parser.add_argument('--device', type=str, require=False, default='cpu')
# opt = parser.parse_args()

# feature = parse_audio(opt.audio_path, del_silence=True)
# input_length = torch.LongTensor([len(feature)])
# vocab = KsponSpeechVocabulary('data/vocab/aihub_character_vocabs.csv')

# model = torch.load(opt.model_path, map_location=lambda storage, loc: storage).to(opt.device)
# if isinstance(model, nn.DataParallel):
#     model = model.module
# model.eval()

# if isinstance(model, ListenAttendSpell):
#     model.encoder.device = opt.device
#     model.decoder.device = opt.device

#     y_hats = model.recognize(feature.unsqueeze(0), input_length)
# elif isinstance(model, DeepSpeech2):
#     model.device = opt.device
#     y_hats = model.recognize(feature.unsqueeze(0), input_length)
# elif isinstance(model, SpeechTransformer) or isinstance(model, Jasper) or isinstance(model, Conformer):
#     y_hats = model.recognize(feature.unsqueeze(0), input_length)

# sentence = vocab.label_to_string(y_hats.cpu().detach().numpy())
# print(sentence)


from typing import Annotated, Union
from fastapi import Body
import torch
import torch.nn as nn
import numpy as np
import torchaudio
from pydub import AudioSegment
import asyncio
import io
import logging

from torch import Tensor

from fastapi.middleware.cors import CORSMiddleware
from pydantic import BaseModel

import requests
import sys
import json

from fastapi import FastAPI, File, UploadFile, Form

import uvicorn

from kospeech.data.audio.core import load_audio
from kospeech.vocabs.ksponspeech import KsponSpeechVocabulary

origins = [
        "*"
]


def parse_audio(audio_path: str, del_silence: bool = False, audio_extension: str = 'wav') -> Tensor:
    signal = load_audio(audio_path, del_silence, extension=audio_extension)
    feature = torchaudio.compliance.kaldi.fbank(
        waveform=Tensor(signal).unsqueeze(0),
        num_mel_bins=80,
        frame_length=20,
        frame_shift=10,
        window_type='hamming'
    ).transpose(0, 1).numpy()

    feature -= feature.mean()
    feature /= np.std(feature)

    return torch.FloatTensor(feature).transpose(0, 1)

# class
class SttModel:
    device = torch.device("cpu")
    model_path = "/app/model/model.pt"
    model = torch.load(model_path, map_location=lambda storage, loc: storage).to(device)
    vocab = KsponSpeechVocabulary('data/vocab/aihub_character_vocabs.csv')

class ConfigInfo:
    springUrl = ""
    lang = "Kor" # 언어 코드 ( Kor, Jpn, Eng, Chn )
    naverUrl = "https://naveropenapi.apigw.ntruss.com/recog/v1/stt?lang=" + lang
    # data = open('음성 파일 경로', 'rb')
    headers = {
        "X-NCP-APIGW-API-KEY-ID": "",
        "X-NCP-APIGW-API-KEY": "",
        "Content-Type": "application/octet-stream"
    }

# FASTAPI
app = FastAPI()

app.add_middleware(
    CORSMiddleware,
    allow_origins=origins,
    allow_credentials=True,
    allow_methods=["*"],
    allow_headers=["*"],
)

def post_process(sentence):
    word_dict = dict()

    before_alpha = 0
    cnt = 1
    new_sentence = str()
    for alpha in sentence:
        new_sentence += alpha
        if before_alpha == alpha:
            cnt += 1
        elif cnt > 1:
            word_dict[before_alpha*cnt] = before_alpha
            cnt = 1
            before_alpha = alpha
    
    for word in word_dict.keys():
        new_sentence.replace(word, word_dict[word])

    return new_sentence

def clova_response(wav_data):
    response = requests.post(ConfigInfo.naverUrl,  data=wav_data, headers=ConfigInfo.headers)
    rescode = response.status_code
    if(rescode == 200):
        sentence = json.loads(response.text)
        return sentence["text"]
    else:
        return "죄송합니다. 오류가 발생했어요."

async def convert_audio(file: UploadFile = File(...)) -> bytes:
    webm_bytes = await file.read()
    audio_segment = AudioSegment.from_file(io.BytesIO(webm_bytes), format='webm')
    wav_bytes = await asyncio.to_thread(audio_segment.export, format='wav')
    return wav_bytes

def spring_post(consulting_no, contentText):
    data = {
    "consultingNo": consulting_no,
    "contentText": contentText,
    "contentSpeaker": False
    }
    requests.post(ConfigInfo.springUrl, json=data)
    print("post: ",data)
    return

@app.on_event("startup")
async def app_startup():
    with open("config.json", "r") as jsonFile:
        json_data = json.load(jsonFile)
    ConfigInfo.springUrl = json_data["spring_url"]
    ConfigInfo.headers["X-NCP-APIGW-API-KEY-ID"] = json_data["client_id"]
    ConfigInfo.headers["X-NCP-APIGW-API-KEY"] = json_data["client_secret"]

@app.get("/")
def read_root():
    return "Hello, This is STT. Please append '/stt/' to this URL"

@app.post("/stt")
async def translate_voice(webm_file: UploadFile = File(...), consulting_no: Annotated[int, Form(),None]=None, naver: Annotated[bool, Form()]=False):
    wav_data = await convert_audio(webm_file)

    if naver:
        new_sentence = clova_response(wav_data)
    else:
        feature = parse_audio(wav_data, del_silence=True)
        input_length = torch.LongTensor([len(feature)])
        model = SttModel.model

        if isinstance(model, nn.DataParallel):
            model = model.module
        model.eval() # 평가모드

        y_hats = model.recognize(feature.unsqueeze(0), input_length)
        sentence = SttModel.vocab.label_to_string(y_hats.cpu().detach().numpy())
        new_sentence = post_process(sentence)

    if consulting_no:
        spring_post(consulting_no = consulting_no, contentText = new_sentence)

    return {"file" : new_sentence}

if __name__ == "__main__":
    uvicorn.run(app, host="0.0.0.0", port=24680)