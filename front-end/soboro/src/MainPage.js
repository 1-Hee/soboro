import { drawConnectors, drawLandmarks } from "@mediapipe/drawing_utils";
import { Holistic, HAND_CONNECTIONS } from "@mediapipe/holistic";
import { Camera } from "@mediapipe/camera_utils";
import { useRef, useEffect, useState } from 'react';
import { io }  from 'socket.io-client'
import { useLocation, useNavigate } from "react-router-dom";
import ReactAudioPlayer from "react-audio-player";
import Consultant from './Consultant';
import axios from "axios";
import User from './User';
import './styles/style.css';
import { useCallback } from "react";

function Main() {
  const canvasRef = useRef(null);
  const videoRef = useRef(null);
  const navigate = useNavigate();

  const [ishorizon, setIshorizon] =  useState(true);
  const [islogin, setIslogin] =  useState(false);
  const [chatdata, setChat] = useState([]);
  const [num, setNum] = useState(null);
  const [error, setError] = useState("한 단어씩 말씀해 주세요.")

  // 음성 녹음 부분
  const [audioStream, setAudioStream] = useState(null); 
  const [mediaRecorder, setMediaRecorder] = useState(null);
  const [recording, setRecording] = useState(false);
  const [audio, setAudio] = useState("")
  let chunks = [] // 오디오 정보를 넣는 리스트
  let formData = new FormData()

  var socket = io(process.env.REACT_APP_FLASK_URL, {
    withCredentials : true
  });
  const url = process.env.REACT_APP_REST_API
  const stt_url = process.env.REACT_APP_STT_URL
  const tts_url = process.env.REACT_APP_TTS_URL
  const location = useLocation(); 

    
  //소켓 연결합니다.
  let isconnect = false
  socket.on("connect", () => isconnect = true)


  // 오디오 녹음 시작하는 부분
  const startRecording = async () => {
    try {
      const stream = await navigator.mediaDevices.getUserMedia({ audio: true });
      setAudioStream(stream);
      const options = {
        audioBitsPerSecond: 16000, // set the desired audio rate here
      };

      const mediaRecorder = new MediaRecorder(stream, options);
      setMediaRecorder(mediaRecorder);
      
      mediaRecorder.addEventListener('dataavailable', event => {
        chunks.push(event.data);
      });

      // 오디오 녹음이 종료가 되었을 때 호출되는 함수
      // useState를 사용했더니 axios 보다 늦어 바로 해당 데이터를 formData에 넣어주니 오류 해결
      // 발생한 오류 : data가 null 값이라 422에러 발생
      mediaRecorder.addEventListener('stop',async () => {
        const audioBlob = new Blob(chunks, { type: 'webm' });
        formData = new FormData()
        if(islogin) formData.append('consulting_no', num)
        formData.append('webm_file', audioBlob)
        formData.append('naver', true)      
        setRecording(false);
        handleUpload()
      });
      mediaRecorder.start();
      setRecording(true);
    } catch (error) {
      // console.error(error);
    }
  };

  const stopRecording = () => {
    if (mediaRecorder) {
      mediaRecorder.stop();
      audioStream.getTracks().forEach(track => track.stop());
    }
  };

  const handleUpload = async () => {   
    await axios.post(stt_url, formData)
    .then((res) => {
      if(res.data.file) {
        const data = {"con" : res.data.file}
        setChat([...chatdata, data])
      }
    })
  };

  socket.on('results', function(data){
    const user = {"user" : data}
    const headers = {'Content-Type' : 'application/json' }
    let number
    if(location.state.log) number = location.state?.token
    else number = -1
    axios.get(tts_url + `${data}?cons_num=${number}` , headers)
    .then((res)=>{
      setChat([...chatdata, user])
      setAudio(url + "/api/ai/tts?address=" + res.data.filename)
      })
  });

  socket.on('response_back', function(data){
    if(data === "failed")
    {
      setError("다시 말씀해주세요.")
    }
    else
    {
      setError(data)
    }
  });

  // 손의 정보를 가져 오는 부분
  // eslint-disable-next-line 
    function onResults(results) { 
    const canvasElement = canvasRef.current;
    const canvasCtx = canvasElement?.getContext('2d')
    canvasCtx?.save();
    canvasCtx?.clearRect(0, 0, canvasElement.width, canvasElement.height);
    canvasCtx?.drawImage(results.image, 0, 0, canvasElement.width, canvasElement.height);
    
    // 손 인식이 되어야 소켓과 통신하고
    if(canvasRef.current && (results.leftHandLandmarks || results.rightHandLandmarks)){
      const dataUrl = canvasRef.current.toDataURL('image/jpeg', 0.5)
      socket.emit('image', dataUrl);
    }
    // 유저가 현재 손이 제대로 인식이 되고 있는지를 확인 해야 하기 때문에 부담스럽지 않은
    // 색상으로 유저의 손을 캔버스에 그려줌.
    drawConnectors(canvasCtx, results.leftHandLandmarks, HAND_CONNECTIONS,
      {color: '#FDFDFD', lineWidth: 1});
    drawLandmarks(canvasCtx, results.leftHandLandmarks,
      {color: '#FDFDFD', lineWidth: 1, radius: 1});
    drawConnectors(canvasCtx, results.rightHandLandmarks, HAND_CONNECTIONS,
      {color: '#FDFDFD', lineWidth: 1});
    drawLandmarks(canvasCtx, results.rightHandLandmarks,
      {color: '#FDFDFD', lineWidth: 1, radius:1});
      canvasCtx.restore();
  }

  useEffect(() => {
    // 해당 링크에 로그인 상태와 가로모드 세로모드의 정보가 담겨 있는지를 확인하는 부분
    if (!location.state) return;
    // 만약 로그인이 된 상태이면 해당 토큰을 서버로 보내서 생성된 컨설팅 룸 번호를 받아오는 부분
    if (location.state.log === true && location.state.token){
      setIslogin(true)
      setNum(location.state.token)
    }
    else setIslogin(false)
    // 현재 가로모드인지 세로 모드인지를 설정하는 부분
    if (location.state.ishor === true) setIshorizon(true)
    else setIshorizon(false)
  }, [location.state])


  useCallback(() => {
        // 오디오, 비디오 사용 허가 받는 코드
        const getUserMedia = async () => {
          try {
            const stream = await navigator.mediaDevices.getUserMedia({video: true, audio : true});
            const videoR = videoRef.current;
            videoR.srcObject = stream;
          } catch (e) {
            // console.log(e)
          }
        };
        getUserMedia();
  }, [])

  // eslint-disable-next-line
  useEffect(() => {
    // mediapipe 라이브러리 사용 부분
    const holistic = new Holistic({locateFile: (file) => {
      return `https://cdn.jsdelivr.net/npm/@mediapipe/holistic/${file}`;
    }});
    
    // 카메라 켭니다.
    const camera = new Camera(videoRef.current, {
      onFrame: async () => {
        if (videoRef.current !== null) await holistic.send({image: videoRef.current});
      },
    });
    camera.start();

    // 미디어파이프 라이브러리를 사용하기 위해서 설정해주는 부분
    holistic.setOptions({
      modelComplexity: 1,
      smoothLandmarks: true,
      enableSegmentation: true,
      smoothSegmentation: true,
      refineFaceLandmarks: true,
      minDetectionConfidence: 0.5,
      minTrackingConfidence: 0.5
    });
    holistic.onResults(onResults);

  }, []);
  
  
  return (
    <div className="soboro_page">
      <div className="main_top"><img src="/logo.png" className="main_logo" alt='logo' /></div>
      <video ref={videoRef} className="input_video" style={{display:"none"}}></video>
      <div className="mainpage">
      {
        ishorizon ?
        // 가로모드
        <div className="main_hor">
          <div className="canvasbox_hor">
            <canvas ref={canvasRef} className="canvas_hor"></canvas> 
            <div className="error_msg_hor">{error}</div>  
            <div className="bottom_hor">
            <img src="/end_btn.png" onClick={() => {navigate('/'); socket.disconnect();
            }} className="end_btn" alt="end" />
            </div>
          </div>
          
          <div>
            <div className="chatbox_hor">
              {
                chatdata?.map((data, index) => {
                  if("con" in data) return <Consultant id={index} data={data?.con}/>
                  else return <User id={index} data={data?.user}/>
                })
              }
            </div>
            <div className="chat_bottom_hor">
              {recording ? (
              <img src="/record_end_btn.png" onClick={stopRecording} alt="off" className="end_btn"/>
              ) : (
              <img src="/record_on_btn.png" onClick={startRecording} alt="on" className="end_btn"/>
              )}
            </div>
          </div>
        </div>
        :
        // 세로모드
        <div className="main_ver">
          <div className="canvasbox_ver">
            <canvas ref={canvasRef} className="canvas_ver"></canvas> 
              <div className="error_msg_ver">{error}</div>    
            <div className="bottom_ver">
            <img src="/end_btn.png" className="end_btn" alt="end" />
            </div>
            <div>
              <div className="chatbox_ver">
              {
                chatdata?.map((data, index) => {
                  if("con" in data) return <Consultant id={index} data={data?.con}/>
                  else return <User id={index} data={data?.user}/>
                })
              }
              </div>
            <div className="bottom_ver">
              {recording ? (
                <img src="/record_end_btn.png" onClick={stopRecording} alt="end" className="end_btn"/>
                ) : (
                <img src="/record_on_btn.png" onClick={startRecording} alt="on" className="end_btn"/>
              )}
            </div>
          </div>
          </div> 
        </div>
      }
      </div>
      <ReactAudioPlayer
        src={audio}
        autoPlay
        controls style={{display : "None"}} />
    </div>
  )
}
export default Main;