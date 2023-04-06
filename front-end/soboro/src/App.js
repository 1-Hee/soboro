import { BrowserRouter as Router, Routes, Route } from "react-router-dom"
import Location from './SetlocationPage'
import Login from './LoginPage'
import Main from './MainPage'

const App = () => {
  return (
    <div className="App">
        <Router>
          <div>
            <Routes>
              <Route path ='/' element={<Location />} />
              <Route path ='/login' element={<Login />} />
              <Route expect path ='/soboro' element={<Main />} />
            </Routes>
          </div>
        </Router>
    </div>
  )
}
export default App