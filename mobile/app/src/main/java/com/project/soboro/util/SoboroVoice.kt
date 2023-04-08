package com.project.soboro.util

import android.media.MediaPlayer
import android.media.MediaRecorder

class SoboroVoice {

    companion object {
        //recorder
        private var recorder: MediaRecorder? = null

        //녹음 할 수 있는 상태 만들기
        fun startRecording(path:String) {
//            recorder = MediaRecorder().apply {
//                setAudioSource(MediaRecorder.AudioSource.MIC)
//                setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP)
//                setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB)
//                setOutputFile(path);
//                prepare()
//            }
//            recorder?.start()

            recorder = MediaRecorder().apply {
                setAudioSource(MediaRecorder.AudioSource.MIC)
                setVideoSource(MediaRecorder.VideoSource.SURFACE)
                setOutputFormat(MediaRecorder.OutputFormat.WEBM)
                setVideoEncoder(MediaRecorder.VideoEncoder.VP8)
                setAudioEncoder(MediaRecorder.AudioEncoder.OPUS)
                setOutputFile(path)
                prepare()
                start()
            }
        }
//
        fun stopRecording() {
            recorder?.run {
                stop()
                release()
            }
            recorder = null
        }

        // player
        var player: MediaPlayer? = null;
        // 플레이어 재생
        fun startPlaying(path:String) {
            player = MediaPlayer().apply {
                setDataSource(path)
                prepare()
            }
            player?.start()
        }

        // 플레이어
        fun stopPlaying() {
            player?.release()
            player = null
        }
    }
}