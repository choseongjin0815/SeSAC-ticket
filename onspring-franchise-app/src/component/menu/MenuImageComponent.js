import React, { useState, useEffect } from 'react';
import { View, Text, TouchableOpacity, Image, ScrollView, StyleSheet } from 'react-native';
import { launchImageLibrary } from 'react-native-image-picker';
import { widthPercentageToDP as wp, heightPercentageToDP as hp } from 'react-native-responsive-screen';
import { getMyInfo } from '../../api/myPageApi';
import { postMenuImage } from '../../api/menuApi';
import { API_SERVER_HOST } from '../../api/menuApi';

const MenuImageComponent = () => {
  const [selectedImages, setSelectedImages] = useState([]);
  const [existingImages, setExistingImages] = useState([]);
  const [franchise, setFranchise] = useState(null);

  // 프랜차이즈 정보 가져오기
  useEffect(() => {
    const fetchData = async () => {
      try {
        const info = await getMyInfo(); // 서버에서 프랜차이즈 정보 받아오기
        console.log(info);
        setFranchise(info); // franchise 데이터 저장
        
        // 기존 이미지가 있으면 설정
        if (info.uploadFileNames && info.uploadFileNames.length > 0) {
          setExistingImages(info.uploadFileNames);
        }
      } catch (error) {
        console.error("데이터 불러오기 실패", error);
      }
    };

    fetchData();
  }, []);

  // 이미지 선택 함수
  const selectImages = () => {
    launchImageLibrary(
      {
        mediaType: 'photo',
        quality: 1, // 이미지 품질 설정
        selectionLimit: 5, // 선택할 이미지 개수 설정
      },
      (response) => {
        if (response.didCancel) {
          console.log('User cancelled image picker');
        } else if (response.errorCode) {
          console.log('ImagePicker Error: ', response.errorMessage);
        } else {
          // 선택된 이미지 파일들 처리
          const images = response.assets.map((asset) => asset.uri);
          setSelectedImages(images);
        }
      }
    );
  };

  // 기존 이미지 삭제 처리
  const removeExistingImage = (filename) => {
    setExistingImages(existingImages.filter(name => name !== filename));
  };

  const saveImages = async () => {
    if (!franchise) {
      console.error('Franchise data is not loaded yet');
      alert('프랜차이즈 정보가 로드되지 않았습니다.');
      return;
    }
  
    const formData = new FormData();
  
    // 프랜차이즈 정보 추가
    formData.append('id', franchise.id);
    formData.append('userName', franchise.userName);
    formData.append('name', franchise.name);
    formData.append('ownerName', franchise.ownerName);
    formData.append('businessNumber', franchise.businessNumber);
    formData.append('address', franchise.address);
    formData.append('latitude', franchise.latitude || 0);
    formData.append('longitude', franchise.longitude || 0);
    formData.append('phone', franchise.phone);
    formData.append('description', franchise.description);
    formData.append('isActivated', franchise.isActivated);
    
    // 유지할 기존 이미지 파일명 추가
    existingImages.forEach(filename => {
      formData.append('uploadFileNames', filename);
    });
    
    if (selectedImages.length > 0) {
      selectedImages.forEach((imageUri, index) => {
        const filename = imageUri.split('/').pop();
        const fileType = imageUri.endsWith('.png') ? 'image/png' : 'image/jpeg';
  
        // console.log("imageUri", imageUri, "fileType", fileType, "fileName", filename);
  
        // 파일 추가
        formData.append('files', {
          uri: imageUri,
          type: fileType,
          name: filename,
        });
      });
    } 
  

    for (let pair of formData.entries()) {
      console.log(pair[0]+ ': '+ pair[1]);
    }

    console.log("formData", formData);
  
    try {
      const result = await postMenuImage(formData);
      console.log('Upload successful:', result);
      alert('이미지가 성공적으로 저장되었습니다!');
      
      // 업로드 후 상태 업데이트
      if (selectedImages.length > 0) {
        const newFileNames = selectedImages.map(uri => uri.split('/').pop());
        setExistingImages([...existingImages, ...newFileNames]);
        setSelectedImages([]);
      }
    } catch (error) {
      console.error('Error uploading images:', error);
      alert('이미지 업로드 중 오류가 발생했습니다!');
    }
  };
  return (
    <View style={styles.container}>
      {/* 촬영 및 앨범에서 선택 버튼 */}
      <TouchableOpacity style={styles.button} onPress={selectImages}>
        <Text style={styles.buttonText}>촬영 및 앨범에서 선택</Text>
      </TouchableOpacity>

      <ScrollView contentContainerStyle={styles.scrollContainer}>
        {/* 기존 이미지 표시 */}
        {existingImages.length > 0 && (
          <View style={styles.sectionContainer}>
            <Text style={styles.sectionTitle}>기존 이미지</Text>
            {existingImages.map((filename, index) => (
              <View key={`existing-${index}`} style={styles.imageWrapper}>
                <Image 
                  source={{ uri: `${API_SERVER_HOST}/api/franchise/menu/${filename}` }} 
                  style={styles.image} 
                  resizeMode="contain" 
                />
                <TouchableOpacity 
                  style={styles.deleteButton} 
                  onPress={() => removeExistingImage(filename)}
                >
                  <Text style={styles.deleteText}>X</Text>
                </TouchableOpacity>
              </View>
            ))}
          </View>
        )}

        {/* 새로 선택된 이미지 표시 */}
        {selectedImages.length > 0 && (
          <View style={styles.sectionContainer}>
            <Text style={styles.sectionTitle}>새 이미지</Text>
            {selectedImages.map((imgUri, index) => (
              <View key={`new-${index}`} style={styles.imageWrapper}>
                <Image source={{ uri: imgUri }} style={styles.image} resizeMode="contain" />
                <TouchableOpacity 
                  style={styles.deleteButton} 
                  onPress={() => {
                    setSelectedImages(selectedImages.filter((_, i) => i !== index));
                  }}
                >
                  <Text style={styles.deleteText}>X</Text>
                </TouchableOpacity>
              </View>
            ))}
          </View>
        )}
      </ScrollView>

      {/* 저장 버튼 */}
      <TouchableOpacity style={styles.button} onPress={saveImages}>
        <Text style={styles.buttonText}>저장</Text>
      </TouchableOpacity>
    </View>
  );
};

const styles = StyleSheet.create({
  container: {
    flex: 1,
    backgroundColor: '#fff',
    alignItems: 'center',
    justifyContent: 'space-between',
    paddingVertical: hp('3%'),
  },
  button: {
    width: wp('85%'),
    backgroundColor: '#4a90e2',
    borderRadius: 16,
    padding: 8,
    alignItems: 'center',
    alignSelf: 'center',
    paddingHorizontal: 16,
  },
  buttonText: {
    color: 'white',
    fontWeight: '900',
    fontSize: 18,
  },
  scrollContainer: {
    flexGrow: 1,
    alignItems: 'center',
    paddingVertical: hp('2%'),
    width: '100%',
  },
  sectionContainer: {
    width: '100%',
    marginBottom: hp('2%'),
    alignItems: 'center',
  },
  sectionTitle: {
    fontSize: 18,
    fontWeight: 'bold',
    marginBottom: hp('1%'),
  },
  imageWrapper: {
    marginBottom: hp('2%'),  
    position: 'relative',
  },
  image: {
    width: wp('90%'),  
    height: hp('40%'), 
    maxWidth: 600,  
    maxHeight: 600,
  },
  deleteButton: {
    position: 'absolute',
    top: 10,
    right: 10,
    backgroundColor: 'red',
    borderRadius: 20,
    padding: 5,
  },
  deleteText: {
    color: 'white',
    fontSize: 18,
  },
});

export default MenuImageComponent;