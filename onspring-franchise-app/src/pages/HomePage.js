import React from 'react';
import { useNavigation } from '@react-navigation/native';
import { View, Text, StyleSheet, TouchableOpacity } from 'react-native';


const HomePage = () => {
    const navigation = useNavigation();
    console.log("home");
    const moveToPage = (address) => {
        navigation.navigate(address);
    };

    return (
        <View style={styles.container}>
            <View style={styles.grid}>
                <TouchableOpacity style={[styles.button, {backgroundColor:'#4a90e2'}]} onPress={() => moveToPage('MyPage')}>
                    <Text style={styles.buttonText}>나의 페이지</Text>
                </TouchableOpacity>
                
                <TouchableOpacity style={[styles.button, {backgroundColor:'#72a7e8'}]} onPress={() => moveToPage('Menu')}>
                    <Text style={styles.buttonText}>메뉴등록</Text>
                </TouchableOpacity>
                
                <TouchableOpacity style={[styles.button, {backgroundColor:'#9bbfee'}]} onPress={() => moveToPage('Transaction')}>
                    <Text style={styles.buttonText}>결제내역</Text>
                </TouchableOpacity>
                
                <TouchableOpacity style={[styles.button, {backgroundColor:'#c4d7f4'}]} onPress={() => moveToPage('Billing')}>
                    <Text style={styles.buttonText}>정산확인</Text>
                </TouchableOpacity>
            </View>
        </View>
        
    );
}

const styles = StyleSheet.create({
    container: {
        flex: 1,
        backgroundColor: '#fff',
        alignItems: 'center',
        justifyContent: 'center',
        padding: 20,
    },
    title: {
        fontSize: 24,
        fontWeight: 'bold',
        marginBottom: 30,
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
    },
    buttonText: {
        color: '#fff',
        fontSize: 16,
        fontWeight: '500',
        textAlign: 'center',
    }
});
export default HomePage;
// Stack Navigator 설정
// const Stack = createStackNavigator();

// const AppNavigator = () => (
    
//         <Stack.Navigator 
//             initialRouteName="Home" // 이후 로그인 되었는지 여부에 따라 로그인 페이지 먼저 노출되게끔
//         >
//             <Stack.Screen 
//                 name="Home" 
//                 component={HomePage} 
//                 options={{ headerShown: false, headerBackTitleVisible: false, headerTitle: '' }}
//             />
//             <Stack.Screen name="Billing" component={Billing} options={{title: "정산확인"}}/>
//             <Stack.Screen name="Menu" component={Menu} options={{title: "메뉴등록"}}/>
//             <Stack.Screen name="MyPage" component={MyPage} options={{title: "나의 페이지"}}/>
//             <Stack.Screen name="Transaction" component={Transaction} options={{title: "결제내역"}}/>
//         </Stack.Navigator>
    
// );

// export default AppNavigator;