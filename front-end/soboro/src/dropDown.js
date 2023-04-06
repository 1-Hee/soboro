import { useState } from "react";
import './styles/style.css'

function Dropdown(props){
  const [category, setCategory] = useState("--선택--")
  const [open, setOpen] = useState(false)

  function onchange(e){
    if (e.target instanceof HTMLElement && e.target.innerText) {
      setCategory(e.target.innerText)
      props.setCategory(e.target.innerText)
      setOpen(false)
    }
  }

  return (
    <div className="dropdown">
      {
        open ?
        <div className="dropdown_menu">
          <div className="option" onClick={(e) => onchange(e)}>--선택--</div>
          <div className="option" onClick={(e) => onchange(e)}>병원</div>
          <div className="option" onClick={(e) => onchange(e)}>은행</div>
          <div className="option" onClick={(e) => onchange(e)}>도서관</div>
          <div className="option" onClick={(e) => onchange(e)}>도청</div>
          <div className="option" onClick={(e) => onchange(e)}>시청</div>
          <div className="option" onClick={(e) => onchange(e)}>구청</div>
          <div className="option" onClick={(e) => onchange(e)}>동사무소</div>
          <div className="option" onClick={(e) => onchange(e)}>읍사무소</div>
          <div className="option" onClick={(e) => onchange(e)}>면사무소</div>
          <div className="option" onClick={(e) => onchange(e)}>그 외</div>
        </div>
        : 
        <div className="drop_value" onClick={() => setOpen(true)}>{category}</div>
}
</div>
)
}
export default Dropdown;