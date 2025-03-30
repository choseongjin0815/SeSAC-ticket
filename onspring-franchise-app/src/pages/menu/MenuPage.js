import { View, Text, TouchableOpacity, Image } from "react-native";
import { StyleSheet } from "react-native";
import { widthPercentageToDP as wp, heightPercentageToDP as hp } from 'react-native-responsive-screen';
import MenuImageComponent from "../../component/menu/MenuImageComponent";

const MenuPage = () => {
    return (
        <View style={[styles.container]}>
            <TouchableOpacity style={[styles.button]}>
                <Text 
                    style={styles.buttonText}
                    // onPress={handleLogout} 실제 api 호출 시 추가
                >촬영 및 앨범에서 선택
                </Text>
            </TouchableOpacity>
            
            <MenuImageComponent/>
            {/* <View style={styles.imageContainer}>
                <Image
                    source={require('../../../images/menu.jpg')} // 실제 이미지 호출하는 api 주소로 변경 필요
                    style={styles.image}
                    resizeMode="contain"
                />
            </View> */}
            
            <TouchableOpacity style={styles.button}>
                <Text 
                    style={styles.buttonText}
                    // onPress={handleLogout} 실제 api 호출 시 추가
                >저장
                </Text>
            </TouchableOpacity>
            
        </View>
        
    );
}

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
  imageContainer: {
    flex: 1,
    width: wp('90%'),
    justifyContent: 'center',
    alignItems: 'center',
    marginVertical: hp('2%'),
  },
  image: {
    width: '100%',
    height: '100%',
  }
});

export default MenuPage;