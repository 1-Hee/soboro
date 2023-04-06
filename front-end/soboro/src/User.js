import './styles/style.css'

function User(props) {
    return (
        <div className="user_chat">
            <div className="user_text">{props?.data}</div>        
            <img src="/user-icon.png" className="chat_icon" alt="user"/>
        </div>
    )
}
export default User;