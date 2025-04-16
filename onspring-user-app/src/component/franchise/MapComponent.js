import React, { useEffect, useState } from 'react';
import { View, ActivityIndicator } from 'react-native';
import { WebView } from 'react-native-webview';
import { getFranchiseList } from '../../api/franchiseApi';

const MapComponent = () => {
  const [markers, setMarkers] = useState([]);

  useEffect(() => {
    const fetchData = async () => {
      const result = await getFranchiseList(0, 100);
      if (result?.content) {
        const mapped = result.content
          .filter(item => item.latitude && item.longitude)
          .map(item => ({
            name: item.name,
            lat: item.latitude,
            lng: item.longitude,
          }));
        setMarkers(mapped);
      }
    };
    fetchData();
  }, []);

  if (markers.length === 0) {
    return <ActivityIndicator size="large" style={{ flex: 1 }} />;
  }

  const markerDataString = JSON.stringify(markers)
    .replace(/\\/g, '\\\\')
    .replace(/'/g, "\\'");

  const html = `
    <!DOCTYPE html>
    <html>
    <head>
      <meta charset="utf-8" />
      <script src="https://dapi.kakao.com/v2/maps/sdk.js?appkey=c4e57c4314a877758ba64930a66074c7&autoload=false"></script>
      <style>
        html, body, #map { margin: 0; padding: 0; width: 100%; height: 100%; }
      </style>
    </head>
    <body>
      <div id="map"></div>
      <script>
        kakao.maps.load(function () {
          const markerData = JSON.parse('${markerDataString}');
          const container = document.getElementById('map');
          const center = new kakao.maps.LatLng(markerData[0].lat, markerData[0].lng);
          const map = new kakao.maps.Map(container, {
            center: center,
            level: 1  
          });

          markerData.forEach(({ lat, lng, name }) => {
            const position = new kakao.maps.LatLng(lat, lng);
            const marker = new kakao.maps.Marker({ position });
            marker.setMap(map);

            const infowindow = new kakao.maps.InfoWindow({
              content: '<div style="padding:5px;font-size:12px;">' + name + '</div>'
            });
            infowindow.open(map, marker);
          });
        });
      </script>
    </body>
    </html>
  `;

  return (
    <View style={{ flex: 1 }}>
      <WebView
        originWhitelist={['*']}
        source={{ html }}
        javaScriptEnabled={true}
        domStorageEnabled={true}
        allowFileAccess={true}
        allowUniversalAccessFromFileURLs={true}
        mixedContentMode="always"
        style={{ flex: 1 }}
        onError={e => console.error('WebView Error:', e.nativeEvent)}
        onHttpError={e => console.error('WebView HTTP Error:', e.nativeEvent)}
      />
    </View>
  );
};

export default MapComponent;