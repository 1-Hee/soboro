import React, { useState, useEffect } from 'react';
import QrReader from 'react-web-qr-reader';
import { useNavigate } from 'react-router-dom';
import axios from 'axios'
import './styles/style.css'

function Login() {
    const [showCam, setshowCam] = useState(false);
    const [errors, setErrors] = useState(false);
    const [ishorizon, setIshorizon] = useState(false)
    const navigate = useNavigate();

    const getRoom = async (data) => {
      const url = process.env.REACT_APP_REST_API
      const location = localStorage.getItem("location")
      const category = localStorage.getItem("category")
      try {
        const res = await axios.post(
          url + 'api/consult/save',
          {
            "consultingVisitPlace" : location,
            "consultingVisitClass" : category
          },
          {
            headers: {
              // Authorization token in the header
              Authorization: `Bearer ${data.data}`,
              'Content-Type': 'application/json',
            },
          }
        );
        if("error" in res.data) console.log()
        else{
          navigate(
          '/soboro',
        { state : { log : true, ishor : ishorizon, token : res.data.data }}
        );}
      } catch (err) {
        console.log(err)
      }
    }

    const handleScan = (data) => {
      if (data) {
        const url = process.env.REACT_APP_REST_API  
        axios.get(url + 'api/user', {headers: {
          Authorization : `Bearer ${data.data}`
        }}).then((res) =>
        {
          if('error' in res){setErrors(true)}
          else {
            getRoom(data)
        }
        }).catch(() => setErrors(true))        
      }
    };
  
    const handleError = () => {
    };
  
    const handleClick = () => {
      setshowCam(true);
      timer();
    };
  
    useEffect(() => {
      if (typeof window !== "undefined") {
        const handleResize = () => {
          if (window.innerWidth > 1000) {
            setIshorizon(true)
          }
          else {
            setIshorizon(false)
          }
        }  
        window.addEventListener("resize", handleResize);
        handleResize();
        return () => window.removeEventListener("resize", handleResize)
      }
    }, [])
    
    const timer = () => {
      const timer = setTimeout(() => setshowCam(false), 15000);
      return () => clearTimeout(timer)
    }
    return (
      <>
      {
        showCam ?
        <div className='qr_page' onClick={handleClick}>
          <div className="qrcode" >
            <QrReader
               delay={300}
               onError = {handleError}
               onScan = {handleScan}
               style = {{ width : "420px" }}  />
            </div>
          {
            errors ?
            <div className='qr_err'>앱에서 발급 받은 qr코드만 넣어주세요.</div> :
            <div className='qr_text'>사각형 안에 qr코드를 넣어주세요.</div> 
          }
            <div className="go_main" onClick={() => navigate('/soboro',{ state: {log : false, ishor : ishorizon }})}>
                로그인 없이 이용하기
            </div>
        </div> 
        : 
        <div className="landing_page">
          <QrReader
               delay={300}
               onError = {handleError}
               onScan = {handleScan}
               style = {{ width : 0 }}  />
          <div onClick={handleClick}>
            {
              ishorizon ?
                <div className='login_hori'>
                  <img id='title_hor' src="/soboro_title.png" alt = "rand"/> 
                  <img id='char_hor' src="/soboro_character.png" alt = "rand"/><br /><br/> 
                  <div className='randing_text'>
                    <div className='wave_text'>화면 터치하고 시작하기</div>
                  </div>
                  <img id='copy' src="/soboro_copyright.png" alt = "rand"/> 
                </div>
              :
                <div className='login_vert'>
                  <img id='title_ver' src="/soboro_title.png" alt = "rand"/><br /><br/>
                  <img id='char_ver' src="/soboro_character.png" alt = "rand"/><br /><br/>
                  <div className='randing_text'>
                    <div className='wave_text'>화면 터치하고 시작하기</div>
                  </div><br></br>
                  <img id='copy' src="/soboro_copyright.png" alt = "rand"/> 
                </div>
            }
          </div>
        </div>
      }
      </>
    )
}

export default Login;
