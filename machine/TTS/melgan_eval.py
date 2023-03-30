import numpy as np

def find_endpoint(wav, threshold_db=-40, min_silence_sec=0.8):
    window_length = int(22050 * min_silence_sec)
    hop_length = window_length // 4
    threshold = np.power(10.0, threshold_db * 0.05) * 32768
    for x in range(hop_length, len(wav) - window_length, hop_length):
        if np.max(wav[x : x + window_length]) < threshold:
            return x + hop_length
    return len(wav)
