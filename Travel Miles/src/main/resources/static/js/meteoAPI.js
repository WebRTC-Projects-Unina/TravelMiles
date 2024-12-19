// API METEO
document.addEventListener('DOMContentLoaded', () => {
    getWeather(cityName); // Aggiungi questa chiamata
    displayComments();    // Mostra i commenti
});


async function getWeather(cityName) {
    try {
        const response = await fetch(`http://localhost:8080/api/weather?city=${cityName}`);
        if (!response.ok) throw new Error('Errore nel recupero dei dati meteo');
        
        const weatherData = await response.json();

        // Temperatura e descrizione
        const temperatureKelvin = weatherData.main.temp;
        const description = weatherData.weather[0]?.description || "Descrizione non disponibile";
        const temperatureCelsius = (temperatureKelvin - 273.15).toFixed(1);
        document.getElementById('temperature').textContent = `Temperatura: ${temperatureCelsius}°C`;
        document.getElementById('weatherDescription').textContent = `Previsione: ${description}`;

        // Alba e Tramonto
        const sunrise = new Date(weatherData.sys.sunrise * 1000).toLocaleTimeString();
        const sunset = new Date(weatherData.sys.sunset * 1000).toLocaleTimeString();
        document.getElementById('sunrise').textContent = sunrise;
        document.getElementById('sunset').textContent = sunset;

        // Precipitazioni
        const precipitation = weatherData.rain?.['1h'] || weatherData.snow?.['1h'] || "Nessuna precipitazione";
        
        if (precipitation === "Nessuna precipitazione") {
            document.getElementById('precipitation').textContent = `${precipitation}`;
        } else {
            document.getElementById('precipitation').textContent = `${precipitation} mm`;
        }

    } catch (error) {
        console.error('Errore:', error);
        document.getElementById('temperature').textContent = 'Errore nel caricamento dei dati';
        document.getElementById('weatherDescription').textContent = '';
        document.getElementById('precipitation').textContent = '';
        document.getElementById('sunrise').textContent = '';
        document.getElementById('sunset').textContent = '';
    }
}
