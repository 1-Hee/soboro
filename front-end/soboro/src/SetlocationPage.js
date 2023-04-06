import { useState, useEffect } from "react";
import { useNavigate } from "react-router-dom";
import Dropdown from "./dropDown"
import './styles/style.css'

function SetLocation() {

  const [ishorizon, setIshorizon] = useState(false)
  const [location, setLocation] = useState(undefined)
  const [category, setCategory] = useState("--선택--")
  let navigate = useNavigate();

  function checklocation() {
    if (category === "--선택--" ){
      alert("카테고리 입력은 필수입니다.")
    }
    if (location === "") {
      alert("위치 입력은 필수 입니다.")
    }

    if (category !== "--선택--" && location !== ""){
      localStorage.setItem('location', location)
      localStorage.setItem('category', category)
      navigate('/login')
    }
  }

  useEffect(() => {
    if(localStorage.getItem('location')) navigate('/login')

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
  }, [ishorizon, navigate])


  return(
    <div className="set_loc">
    {
      ishorizon ?
        <div className="set_hor">
          <Dropdown setCategory={setCategory}/>
          <input
            placeholder="ex) 국민은행 대전봉명점"
            onChange={(e) => setLocation(e.target.value)} 
            value={location}
            style={{
              marginLeft: 50,
              marginRight : 50
            }}
            />
          <div className="set_btn" onClick={checklocation}>등록</div>
        </div>
      :
        <div className="set_ver">
          <div style={{width : 430, padding: 0}}>
            <Dropdown setCategory={setCategory}/>
          </div>
          <input
            placeholder="ex) 국민은행 대전봉명점"
            onChange={(e) => setLocation(e.target.value)} 
            value={location}
            style={{
              marginTop: 50,
              marginBottom : 50
            }}
            
            />
          <div className="set_btn" onClick={checklocation}>등록</div>
        </div>
    }
    </div>
  )
}

export default SetLocation;
