import { useState, useEffect, useCallback } from 'react'
import './App.css'
import axios from 'axios'
import {useDropzone} from 'react-dropzone'

function App() {

  const [users, setUsers] = useState([])

  const fetchUsers= () =>{
    axios.get("http://localhost:8080/api/v1/user-profiles")
    .then(res =>{
      setUsers(res.data)
    })
  }

  useEffect(() => {
    fetchUsers()
  }, [])

  function MyDropzone({ userProfileId }) {
    const onDrop = useCallback(acceptedFiles => {
      const file = acceptedFiles[0]

      console.log(acceptedFiles)

      const formData = new FormData()
      formData.append("file", file)
      
      axios
        .post(
          `http://localhost:8080/api/v1/user-profiles/${userProfileId}/image/upload`,
          formData,
          {
            headers:{
              "Content-Type":"multipart/form-data"
            }
          })
          .then(()=>{
            console.log("File uploaded successfully")
          })
          .catch(err =>{
            console.log(err)
          });
          

    }, [])
    const {getRootProps, getInputProps, isDragActive} = useDropzone({onDrop})
  
    return (
      <div {...getRootProps()}>
        <input {...getInputProps()} />
        {
          isDragActive ?
            <p>Drop the image here ...</p> :
            <p>Drag 'n' drop profile picture here, or click to select image</p>
        }
      </div>
    )
  }

  return (
    <div className="App">
      {users.map((user, index) =>(
        <div key={index}>
          {user.userProfileId ? (
          <img
            src={`http://localhost:8080/api/v1/user-profiles/${user.userProfileId}/image/download`}/>
            ) : null}
            <h1>{user.username}</h1>
            <p>{user.userProfileId}</p>
            <MyDropzone {...user}/>
            <br/>
        </div>
      ))}
    </div>
  );
}

export default App;
