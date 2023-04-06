import './styles/style.css'

function Consultant(props) {
    return (
        <div className="con_chat">
            <img src="/con_icon.png" className="chat_icon" alt="con"/>
            <div className="con_text">{props?.data}</div>        
        </div>
    )
}
export default Consultant;