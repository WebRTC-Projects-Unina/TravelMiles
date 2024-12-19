const config = {
  iceServers: [{ urls: "stun:stun.l.google.com:19302" }]
};

const pc = new RTCPeerConnection(config);

const role = "utente";
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
      throw new Error("Errore durante il recupero dell'username");
    }
    return await response.text();
  } catch (error) {
    console.error("Errore:", error);
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
    console.error("Errore durante l'inizializzazione:", error);
  }
}



ws.onmessage = async (event) => {
  const message = JSON.parse(event.data);
  console.log("Messaggio dal WebSocket:", message);

  if (message.type === "info") {
    chatOutput.innerHTML += `<br><em>${message.message}</em>`;
	loadUsername();
	
  } else if (message.type === "offer") {
    await pc.setRemoteDescription(new RTCSessionDescription(message))

    const answer = await pc.createAnswer();
    await pc.setLocalDescription(answer);
    ws.send(JSON.stringify(pc.localDescription));
	
  } else if (message.type === "answer") {
    await pc.setRemoteDescription(new RTCSessionDescription(message));
	
  } else if (message.candidate) {
    await pc.addIceCandidate(new RTCIceCandidate(message));
	//console.log('Io utente ho ricevuto un message.candidate');
	
  } else if (message.type === "username") {
    if (message.username) {
      chatOutput.innerHTML += `<br><em>Consulente: ${message.username}</em>`;
    } else {
      console.log('Username del consulente non disponibile');
    }
  }
};


pc.onicecandidate = ({ candidate }) => {
  if (candidate) {
    ws.send(JSON.stringify(candidate));
	//console.log("Nuovo candidato ICE Utente che sto inviando all'altro peer:", candidate);
  }
};


connectButton.onclick = async () => {
  dataChannel = pc.createDataChannel("chat");
  setupDataChannel(dataChannel);

  const offer = await pc.createOffer();
  await pc.setLocalDescription(offer);
  ws.send(JSON.stringify(pc.localDescription));
};

//Aggiorno il datachannel
pc.ondatachannel = (event) => {
  dataChannel = event.channel; 
  setupDataChannel(dataChannel);
};


function setupDataChannel(channel) {
  channel.onopen = () => {
    console.log("DataChannel aperto, ora è possibile inviare messaggi e file");
  };

  channel.onmessage = (e) => {
    const message = JSON.parse(e.data);
    if (message.type === "text") {
      chatOutput.innerHTML += `<br><strong>${message.username}:</strong> ${message.data}`;
    } else if (message.type === "file-metadata") {
      receivedFileMetadata = message;  //salvo i metadati del file
      fileBuffer = []; //creo un buffer dove inserire i dati del mio file
    } else if (message.type === "file-chunk") {
      fileBuffer.push(new Uint8Array(message.data)); //inserisco i dati all'interno del fileBuffer (ovviamente di tipo Uint8Array come la funzione sendFileChunks)
      if (fileBuffer.length === Math.ceil(receivedFileMetadata.size / 16000)) {   //controllo se ho ricevuto tutti i chunk
        const blob = new Blob(fileBuffer);  //quando ho ottenuto tutti i chunk e ho il mio file intero, creo un binary large object 
        const link = document.createElement("a"); //creo un link per poter scaricare il file lato ricezione
        link.href = URL.createObjectURL(blob);
        link.download = receivedFileMetadata.name;
        link.textContent = `Download ${receivedFileMetadata.name}`;
        fileLinks.appendChild(link);
        fileLinks.appendChild(document.createElement("br"));
      }
    }
  };

  
  chatInput.onkeypress = (e) => {
    if (e.key === "Enter" && channel.readyState === "open") {
      const message = { type: "text", username: username, data: chatInput.value };
      channel.send(JSON.stringify(message));
      chatOutput.innerHTML += `<br><strong>${username}:</strong> ${chatInput.value}`;
      chatInput.value = "";
    }
  };

  sendFileButton.onclick = () => {
    if (channel.readyState === "open") {
      const file = fileInput.files[0]; //Prelevo il file in input
      if (file) { //controllo se è vuoto
        const reader = new FileReader(); //se non è vuoto creo un oggetto FileReader
        reader.onload = () => { //quando viene letto il file (async)
          const arrayBuffer = reader.result; //inserisco in arrayBuffer
          channel.send(JSON.stringify({ type: "file-metadata", name: file.name, size: file.size })); //invio informazioni in json in modo tale da poterlo ricostruire
          sendFileChunks(arrayBuffer); //invio il file in tanti chunk
        };
        reader.readAsArrayBuffer(file); //leggo il file come array binario, ciò avviene prima dell'onload
      }
    } else {
      console.warn("DataChannel non disponibile o non aperto. Impossibile inviare file.");
    }
  };
}


function sendFileChunks(arrayBuffer) {
  const chunkSize = 16000; //definisco la dimensione massima del chunk
  const uint8Array = new Uint8Array(arrayBuffer); //converto arrayBuffer in un array di byte
  for (let i = 0; i < uint8Array.length; i += chunkSize) { //divido l'array in tanti blocchi di dimensione chunkSize
    const chunk = uint8Array.slice(i, i + chunkSize);
    dataChannel.send(JSON.stringify({ type: "file-chunk", data: Array.from(chunk) })); //converto in json per inviare la porzione di file
  }
}
