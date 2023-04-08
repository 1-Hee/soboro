# Tacotron
import os, torch, json
from jamo import hangul_to_jamo
from tacotron_eval import Synthesizer
# MelGan
import soundfile as sf
from os import path
from gan import Generator
from melgan_eval import find_endpoint
# Fast API
from fastapi import FastAPI, HTTPException
import uvicorn, requests, json

os.environ["TF_CPP_MIN_LOG_LEVEL"] = "2"
app = FastAPI()

idx = 0

@app.on_event("startup") 
def startup_event(): 
    print("Startup event") 
    global idx
    idx = 0

def return_idx():
    global idx
    while True:
        idx += 1
        yield idx

@app.get("/tts/{text}")
def tts(text: str, cons_num: int = -1):
    with open("config.json") as f:
        data = f.read()
    config = json.loads(data)
    save_dir = config["save_dir"]
    load_dir = config["load_dir"]
    ckpt = config["ckpt"]
    cons_url = config["cons_url"]
    # parameter
    device = "cuda" if torch.cuda.is_available() else "cpu"
    valid_file_path = save_dir+"/"+text

    # distinct
    if path.exists(valid_file_path):
        with open(valid_file_path, "r") as f:
            filename = f.readline()
        return {"filename": "{}".format(filename)}
    
    # prefix bug
    if len(text) <= 5:
        text += "아"

    # taco-eval
    synth = Synthesizer()
    synth.load(ckpt)
    jamo = "".join(list(hangul_to_jamo(text)))
    mel = synth.synthesize(jamo)
    vocoder = Generator(80)
    ckpt = torch.load(load_dir, map_location=device)
    vocoder.load_state_dict(ckpt["G"])
    mel = mel.unsqueeze(0)

    # melgan-eval
    samplerate = 22050
    audioinit = int(samplerate*0.2)
    audiobasic = samplerate*3
    g_audio = vocoder(mel)
    g_audio = g_audio.squeeze().cpu()
    audio = (g_audio.detach().numpy() * 32768)[audioinit:]
    trim_audio = audio[:find_endpoint(audio)]
    if trim_audio.shape[0] == samplerate*0.4: # minimum length
        audio = audio[:audiobasic]
    else:
        audio = trim_audio
    filename = "generated_{}.wav".format(next(return_idx()))
    filepath = path.join(save_dir, filename)
    response = {
            "filename": "{}".format(filename),
    }
    with open(valid_file_path, "w") as f:
        f.write(filename)
    sf.write(filepath, audio.astype("int16"), 22050)
    os.chmod(filepath, 0o755)
    if cons_num != -1:
        _to_back(cons_url, cons_num, text)
    return response

def _to_back(url: str, cons_num: int, cons_text: str):
    data = {
        "consultingNo": cons_num,
        "contentText": cons_text,
        "contentSpeaker": True
    }
    data = json.dumps(data)
    headers = {"Content-type": "application/json"}
    requests.post(url, data=data, headers=headers)

if __name__ == "__main__":
    uvicorn.run(app, host="0.0.0.0", port=13579)
