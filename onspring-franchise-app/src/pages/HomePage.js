import React from 'react';
import { useNavigation } from '@react-navigation/native';
import { View, StyleSheet, TouchableOpacity, Text, Image } from 'react-native';

const HomePage = () => {
    const navigation = useNavigation();

    const moveToPage = (address) => {
        navigation.navigate(address);
    };

    return (
        <View style={styles.container}>
            <View style={styles.grid}>
                {/* 나의 페이지 버튼 */}
                <TouchableOpacity style={[styles.button, {backgroundColor:'#4a90e2'}]} onPress={() => moveToPage('MyPage')}>
                    <Image source={require('../../images/group_4.png')} style={styles.buttonImage} />
                    <Text style={styles.buttonText}>나의 페이지</Text>
                </TouchableOpacity>
                
                {/* 메뉴 등록 버튼 */}
                <TouchableOpacity style={[styles.button, {backgroundColor:'#72a7e8'}]} onPress={() => moveToPage('Menu')}>
                    <Image source={require('../../images/camera.png')} style={styles.buttonImage} />
                    <Text style={styles.buttonText}>메뉴 등록</Text>
                </TouchableOpacity>
                
                {/* 결제 내역 버튼 */}
                <TouchableOpacity style={[styles.button, {backgroundColor:'#9bbfee'}]} onPress={() => moveToPage('Transaction')}>
                    <Image source={require('../../images/file_text.png')} style={styles.buttonImage} />
                    <Text style={styles.buttonText}>결제 내역</Text>
                </TouchableOpacity>
                
                {/* 정산 확인 버튼 */}
                <TouchableOpacity style={[styles.button, {backgroundColor:'#c4d7f4'}]} onPress={() => moveToPage('Billing')}>
                    <Image source={require('../../images/dolloar_sign.png')} style={styles.buttonImage} />
                    <Text style={styles.buttonText}>정산 확인</Text>
                </TouchableOpacity>
            </View>
        </View>
    );
};

const styles = StyleSheet.create({
    container: {
        flex: 1,
        backgroundColor: '#fff',
        alignItems: 'center',
        justifyContent: 'center',
        padding: 20,
    },
    grid: {
        flexDirection: 'row',
        flexWrap: 'wrap',
        justifyContent: 'center',
        gap: 10, // 버튼 사이 간격
    },
    button: {
        width: 150, // 정사각형 크기
        height: 150,
        backgroundColor: '#4a90e2',
        borderRadius: 16, // 모서리 둥글게
        alignItems: 'center',
        justifyContent: 'center',
        margin: 8,
        shadowColor: '#000',
        shadowOffset: { width: 0, height: 2 },
        shadowOpacity: 0.2,
        shadowRadius: 4,
        elevation: 3, // 안드로이드 그림자 효과
        flexDirection: 'column', // 이미지와 텍스트를 수직으로 배치
        textAlign: 'center',
    },
    buttonImage: {
        width: 100,  // 이미지 크기 조정
        height: 100, // 이미지 크기 조정
        resizeMode: 'contain', // 이미지 비율 유지
    },
    buttonText: {
        color: '#fff',
        fontSize: 16,
        fontWeight: '500',
        textAlign: 'center',
        marginTop: 8, // 이미지와 텍스트 간 간격 조정
    }
});

export default HomePage;


// import React from 'react';
// import { useNavigation } from '@react-navigation/native';
// import { View, Text, StyleSheet, TouchableOpacity } from 'react-native';


// const HomePage = () => {
//     const navigation = useNavigation();
//     console.log("home");
//     const moveToPage = (address) => {
//         navigation.navigate(address);
//     };

//     return (
//         <View style={styles.container}>
//             <View style={styles.grid}>
//                 <TouchableOpacity style={[styles.button, {backgroundColor:'#4a90e2'}]} onPress={() => moveToPage('MyPage')}>
//                     <Text style={styles.buttonText}>나의 페이지</Text>
//                 </TouchableOpacity>
                
//                 <TouchableOpacity style={[styles.button, {backgroundColor:'#72a7e8'}]} onPress={() => moveToPage('Menu')}>
//                     <Text style={styles.buttonText}>메뉴등록</Text>
//                 </TouchableOpacity>
                
//                 <TouchableOpacity style={[styles.button, {backgroundColor:'#9bbfee'}]} onPress={() => moveToPage('Transaction')}>
//                     <Text style={styles.buttonText}>결제내역</Text>
//                 </TouchableOpacity>
                
//                 <TouchableOpacity style={[styles.button, {backgroundColor:'#c4d7f4'}]} onPress={() => moveToPage('Billing')}>
//                     <Text style={styles.buttonText}>정산확인</Text>
//                 </TouchableOpacity>
//             </View>
//         </View>
        
//     );
// }

// const styles = StyleSheet.create({
//     container: {
//         flex: 1,
//         backgroundColor: '#fff',
//         alignItems: 'center',
//         justifyContent: 'center',
//         padding: 20,
//     },
//     title: {
//         fontSize: 24,
//         fontWeight: 'bold',
//         marginBottom: 30,
//     },
//     grid: {
//         flexDirection: 'row',
//         flexWrap: 'wrap',
//         justifyContent: 'center',
//         gap: 10, // 버튼 사이 간격
//     },
//     button: {
//         width: 150, // 정사각형 크기
//         height: 150, 
//         backgroundColor: '#4a90e2',
//         borderRadius: 16, // 모서리 둥글게
//         alignItems: 'center',
//         justifyContent: 'center',
//         margin: 8,
//         shadowColor: '#000',
//         shadowOffset: { width: 0, height: 2 },
//         shadowOpacity: 0.2,
//         shadowRadius: 4,
//         elevation: 3, // 안드로이드 그림자 효과
//     },
//     buttonText: {
//         color: '#fff',
//         fontSize: 16,
//         fontWeight: '500',
//         textAlign: 'center',
//     }
// });
// export default HomePage;
