from fastapi import FastAPI, File, UploadFile, Form
from pydub import AudioSegment
from fastapi.responses import StreamingResponse
import io
import uvicorn
import asyncio
from torch import Tensor
import torchaudio
import torch
import numpy as np

from kospeech.data.audio.core import load_audio
from kospeech.vocabs.ksponspeech import KsponSpeechVocabulary

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

app = FastAPI()

@app.post("/stt")
async def testStt(file: UploadFile = File(...)):
    webm_bytes = await file.read()

    # webm 파일 읽어서 wav로 리턴
    # from fastapi.responses import StreamingResponse
    # stream = io.BytesIO(webm_bytes)
    # response = StreamingResponse(stream, media_type="audio/wav")
    # return response

    audio_segment = AudioSegment.from_file(io.BytesIO(webm_bytes), format='webm')
    wav_bytes = await asyncio.to_thread(audio_segment.export, format='wav')
    feature = parse_audio(wav_bytes, del_silence=True)
    input_length = torch.LongTensor([len(feature)])
    print(feature)


    return {"ans" : "마바사"}

if __name__ == "__main__":
    uvicorn.run(app="test:app", host="0.0.0.0", port=8000, reload=True)