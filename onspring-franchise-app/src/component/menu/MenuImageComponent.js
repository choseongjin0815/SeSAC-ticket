import React, { useState, useEffect } from 'react';
import { View, TouchableOpacity, Image, ScrollView, StyleSheet, Text } from 'react-native';
import { launchImageLibrary, launchCamera } from 'react-native-image-picker'; 
import { widthPercentageToDP as wp, heightPercentageToDP as hp } from 'react-native-responsive-screen';
import { getMyInfo } from '../../api/myPageApi';
import { postMenuImage } from '../../api/menuApi';
import { API_SERVER_HOST } from '../../api/menuApi';

const MenuImageComponent = () => {
  const [selectedImages, setSelectedImages] = useState([]); // 새로 선택된 이미지들
  const [existingImages, setExistingImages] = useState([]); // 기존에 있는 이미지들
  const [franchise, setFranchise] = useState(null);

  // 프랜차이즈 정보 가져오기
  useEffect(() => {
    const fetchData = async () => {
      try {
        const info = await getMyInfo(); // 서버에서 프랜차이즈 정보 받아오기
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

  // 이미지 선택 함수 (앨범 또는 카메라 선택)
  const selectImageSource = () => {
    const options = {
      mediaType: 'photo',
      quality: 1, // 이미지 품질 설정
      selectionLimit: 5, // 선택할 이미지 개수 설정
    };

    launchImageLibrary(options, (response) => {
      handleImageResponse(response);
    });
  };

  // 카메라로 사진 촬영
  const takePhoto = () => {
    const options = {
      mediaType: 'photo',
      quality: 1, // 이미지 품질 설정
    };

    launchCamera(options, (response) => {
      handleImageResponse(response);
    });
  };

  const handleImageResponse = (response) => {
    if (response.didCancel) {
      console.log('User cancelled image picker');
    } else if (response.errorCode) {
      console.log('ImagePicker Error: ', response.errorMessage);
    } else {
      const images = response.assets.map((asset) => asset.uri);
      console.log('Selected images : ', images);
      setSelectedImages(prev => [...prev, ...images]);
    }
  };
  

  // 기존 이미지 삭제 처리
  const removeImage = (filename) => {
    setExistingImages((prevImages) => {
      // 기존 이미지에서 삭제된 파일을 제외한 새로운 배열 반환
      const updatedImages = prevImages.filter((name) => name !== filename);
      return updatedImages;
    });
  };

  // 이미지 저장 함수
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
    
    // 기존 이미지 파일명 추가 (삭제된 것은 제외)
    existingImages.forEach(filename => {
      formData.append('uploadFileNames', filename);
    });

    // 새로 선택된 이미지들 추가
    if (selectedImages.length > 0) {
      selectedImages.forEach((imageUri) => {
        const filename = imageUri.split('/').pop();
        const fileType = imageUri.endsWith('.png') ? 'image/png' : 'image/jpeg';

        formData.append('files', {
          uri: imageUri,
          type: fileType,
          name: filename,
        });
      });
    }

    try {
      const result = await postMenuImage(formData);
      console.log('Upload successful:', result);
      alert('이미지가 성공적으로 저장되었습니다!');
      
      // 업로드 후 상태 업데이트
      if (result.uploadFileNames) {
        // API 응답에서 업데이트된 파일명 목록을 사용
        setExistingImages(result.uploadFileNames);
        setSelectedImages([]); // 선택한 이미지는 초기화
      } else if (selectedImages.length > 0) {
        // API가 업데이트된 파일명을 반환하지 않는 경우의 대체 로직
        const newFileNames = selectedImages.map(uri => uri.split('/').pop());
        setExistingImages([...existingImages, ...newFileNames]);
        setSelectedImages([]); // 선택한 이미지는 초기화
      }

      // 서버에서 최신 이미지 목록을 다시 가져오기
       const info = await getMyInfo(); // 서버에서 프랜차이즈 정보 받아오기
       setFranchise(info); // franchise 데이터 업데이트
       setExistingImages(info.uploadFileNames); // 업로드된 새로운 이미지 목록을 갱신
    } catch (error) {
      console.error('Error uploading images:', error);
      alert('이미지 업로드 중 오류가 발생했습니다!');
    }
  };

  return (
    <View style={styles.container}>
      {/* 앨범 선택 및 카메라 촬영 버튼을 가로로 붙이기 */}
      <View style={styles.buttonRow}>
        <TouchableOpacity style={styles.button} onPress={selectImageSource}>
          <Text style={styles.buttonText}>앨범 선택</Text>
        </TouchableOpacity>

        <TouchableOpacity style={styles.button} onPress={takePhoto}>
          <Text style={styles.buttonText}>카메라 촬영</Text>
        </TouchableOpacity>
      </View>

      {/* 기존 이미지 표시 */}
      <ScrollView contentContainerStyle={styles.scrollContainer}>
      {existingImages.length > 0 && (
        <View style={styles.sectionContainer}>
          {existingImages.map((filename, index) => (
            <View key={`existing-${index}`} style={styles.imageWrapper}>
              <TouchableOpacity 
                onPress={() => removeImage(filename)} // 이미지 눌렀을 때 삭제
              >
                <Image 
                  source={{ uri: `${API_SERVER_HOST}/api/franchise/menu/${filename}` }} 
                  style={styles.image} 
                  resizeMode="contain" 
                />
              </TouchableOpacity>
            </View>
          ))}
        </View>
      )}
        {/* {existingImages.length > 0 && (
          <View style={styles.sectionContainer}>
            {existingImages.map((filename, index) => (
              <View key={`existing-${index}`} style={styles.imageWrapper}>
                <Image 
                  source={{ uri: `${API_SERVER_HOST}/api/franchise/menu/${filename}` }} 
                  style={styles.image} 
                  resizeMode="contain" 
                />
                <TouchableOpacity 
                  style={styles.deleteButton} 
                  onPress={() => removeImage(filename)}
                >
                  <Text style={styles.deleteText}>X</Text>
                </TouchableOpacity>
              </View>
            ))}
          </View>
        )} */}

        {/* 새로 선택된 이미지 표시 */}
        {selectedImages.length > 0 && (
          <View style={styles.sectionContainer}>
            {selectedImages.map((imgUri, index) => (
              <View key={`new-${index}`} style={styles.imageWrapper}>
                <TouchableOpacity 
                  onPress={() => {
                    setSelectedImages(selectedImages.filter((_, i) => i !== index));
                  }}
                >
                  <Image source={{ uri: imgUri }} style={styles.image} resizeMode="contain" />
                </TouchableOpacity>
              </View>
            ))}
          </View>
        )}
        {/* 새로 선택된 이미지 표시 */}
        {/* {selectedImages.length > 0 && (
          <View style={styles.sectionContainer}>
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
        )} */}
      </ScrollView>

      {/* 저장 버튼 */}
      <TouchableOpacity style={[styles.button, { marginTop: 2 }]} onPress={saveImages}>
        <Text style={styles.buttonText}>저장</Text>
      </TouchableOpacity>
    </View>
  );
};

const styles = StyleSheet.create({
  container: {
    flex: 1,
    // backgroundColor: 'red',
    backgroundColor: '#fff',
    alignItems: 'center',
    justifyContent: 'space-between',
    paddingVertical: hp('3%'),
  },
  buttonRow: {
    flexDirection: 'row',
    justifyContent: 'space-between',
    width: '85%',
    marginBottom: hp('2%'),
  },
  button: {
    width: '48%',
    // backgroundColor: 'red',
    backgroundColor: '#4a90e2',
    borderRadius: 16,
    padding: 8,
    alignItems: 'center',
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
    right: 55,
    padding: 5,
  },
  deleteText: {
    color: 'black',
    fontSize: 18,
  },
});

export default MenuImageComponent;