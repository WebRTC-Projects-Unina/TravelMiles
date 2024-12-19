const config = {
  iceServers: [{ urls: "stun:stun.l.google.com:19302" }]
};
const pc = new RTCPeerConnection(config);

const role = "consulente";
const ws = new WebSocket(`ws://localhost:8080/signaling?role=${role}`);

const chatInput = document.getElementById("chatInput");
const chatOutput = document.getElementById("chatOutput");
const connectButton = document.getElementById("connectButton");
const fileInput = document.getElementById("fileInput");
const sendFileButton = document.getElementById("sendFileButton");
const fileLinks = document.getElementById("fileLinks");

let dataChannel;
let fileBuffer = [];
let receivedFileMetadata = null;
let username = "";


async function getUsernameFromSession() {
  try {
    const response = await fetch('http://localhost:8080/get-username');
    if (!response.ok) {
      throw new Error('Errore durante il recupero dell\'username');
    }
    return await response.text();
  } catch (error) {
    console.error('Errore:', error);
    return null;
  }
}

async function loadUsername() {
  try {
    username = await getUsernameFromSession();
    if (!username) {
      console.log("Username non trovato");
      return;
    } else {
      console.log("Username:", username);
      ws.send(JSON.stringify({ type: "username", username: username }));
    }
  } catch (error) {
    console.error('Errore durante l\'inizializzazione:', error);
  }
}


ws.onmessage = async (event) => {
  const message = JSON.parse(event.data);
  console.log("Messaggio dal WebSocket:", message);

  if (message.type === "info") {
    chatOutput.innerHTML += `<br><em>${message.message}</em>`;
	loadUsername();
	
  } else if (message.type === "offer") {
    await pc.setRemoteDescription(new RTCSessionDescription(message));

    const answer = await pc.createAnswer();
    await pc.setLocalDescription(answer);
    ws.send(JSON.stringify(pc.localDescription));
	
  } else if (message.type === "answer") {
    await pc.setRemoteDescription(new RTCSessionDescription(message));
	
  } else if (message.candidate) {
    await pc.addIceCandidate(new RTCIceCandidate(message));
	//console.log('Io consulente ho ricevuto un message.candidate');
	
  } else if (message.type === "username") {
    
    if (message.username) {
      chatOutput.innerHTML += `<br><em>Utente: ${message.username}</em>`;
    } else {
      console.log('Username del consulente non disponibile');
    }
  }
};


pc.onicecandidate = ({ candidate }) => {
  if (candidate) {
    ws.send(JSON.stringify(candidate));
    //console.log("Nuovo candidato ICE Consulente che sto inviando all'altro peer:", candidate);
  }
};


connectButton.onclick = async () => {
  dataChannel = pc.createDataChannel("chat");
  setupDataChannel(dataChannel);

  const offer = await pc.createOffer();
  await pc.setLocalDescription(offer);
  ws.send(JSON.stringify(pc.localDescription));
};




pc.ondatachannel = (event) => {
  dataChannel = event.channel;
  setupDataChannel(dataChannel);
};


function setupDataChannel(channel) {
  channel.onopen = () => {
    console.log("DataChannel aperto, ora è possibile inviare messaggi");
  };

  channel.onmessage = (e) => {
    const message = JSON.parse(e.data);
    if (message.type === "text") {
      chatOutput.innerHTML += `<br><strong>${message.username}:</strong> ${message.data}`;
    } else if (message.type === "file-metadata") {
      receivedFileMetadata = message;
      fileBuffer = [];
    } else if (message.type === "file-chunk") {
      fileBuffer.push(new Uint8Array(message.data));
      if (fileBuffer.length === Math.ceil(receivedFileMetadata.size / 16000)) {
        const blob = new Blob(fileBuffer);
        const link = document.createElement("a");
        link.href = URL.createObjectURL(blob);
        link.download = receivedFileMetadata.name;
        link.textContent = `Download ${receivedFileMetadata.name}`;
        fileLinks.appendChild(link);
        fileLinks.appendChild(document.createElement("br"));
      }
    }
  };


  chatInput.onkeypress = (e) => {
    if (e.key === "Enter" && channel.readyState === "open") { // Verifico prima che il canale sia aperto
      const message = { type: "text", username: username, data: chatInput.value };
      channel.send(JSON.stringify(message)); 
      chatOutput.innerHTML += `<br><strong>${username}:</strong> ${chatInput.value}`;
      chatInput.value = "";
    }
  };
  
  
  sendFileButton.onclick = () => {
	if (channel.readyState === "open") {
    const file = fileInput.files[0];
    if (file && dataChannel) {
      const reader = new FileReader();
      reader.onload = () => {
        const arrayBuffer = reader.result;
        dataChannel.send(JSON.stringify({ type: "file-metadata", name: file.name, size: file.size }));
        sendFileChunks(arrayBuffer);
      };
      reader.readAsArrayBuffer(file);
    }
	}
  };
}

function sendFileChunks(arrayBuffer) {
  const chunkSize = 16000;
  const uint8Array = new Uint8Array(arrayBuffer);
  for (let i = 0; i < uint8Array.length; i += chunkSize) {
    const chunk = uint8Array.slice(i, i + chunkSize);
    dataChannel.send(JSON.stringify({ type: "file-chunk", data: Array.from(chunk) }));
  }
}
