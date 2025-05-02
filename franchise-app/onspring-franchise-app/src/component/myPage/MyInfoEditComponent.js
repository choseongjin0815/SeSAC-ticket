import { useNavigation } from '@react-navigation/native';
import React, { useState, useEffect } from 'react';
import { getMyInfo, updateInfo } from '../../api/myPageApi';
import { 
  View, 
  Text, 
  TextInput, 
  StyleSheet, 
  TouchableOpacity, 
  KeyboardAvoidingView,
  Platform,
  ScrollView,
  SafeAreaView
} from 'react-native';

const MyInfoEditComponent = () => {
    const navigation = useNavigation();

    // 초기 상태 설정 (프랜차이즈 정보가 담긴 객체)
    const [franchise, setFranchise] = useState({
        name: '',
        phone: '',
        address: '',
        ownerName: '',
        businessNumber: '',
        description: ''
    });

    // 데이터 가져오기
    useEffect(() => {
        const fetchData = async () => {
            const info = await getMyInfo(); 
            setFranchise(info); 
            console.log(info);
        };

        fetchData();
    }, []);

    const handleSave = (phoneInfo, descriptionInfo) => {
        const franchiseObj = {
            phone: phoneInfo,
            description: descriptionInfo
        }
        updateInfo(franchiseObj).then(() => {
            navigation.navigate('MyPage', {refresh: true});
        });
    };

    const hanleCancel = () => {
        navigation.goBack();
    }

    // 연락처 수정 핸들러
    const handlePhoneChange = (text) => {
        setFranchise((prevState) => ({
            ...prevState,
            phone: text,
        }));
    };

    // 설명 수정 핸들러
    const handleDescriptionChange = (text) => {
        console.log(text);
        setFranchise((prevState) => ({
            ...prevState,
            description: text,
        }));
    };

    return (
        <SafeAreaView style={styles.safeArea}>
            <KeyboardAvoidingView
                behavior={Platform.OS === "ios" ? "padding" : "height"}
                style={styles.keyboardAvoid}
            >
                <ScrollView contentContainerStyle={styles.scrollContainer}>
                    <View style={styles.container}>
                        <View style={styles.infoRow}>
                            <Text style={styles.labelText}>가맹점</Text>
                            <Text style={[styles.valueText, { fontWeight: 'bold' }]}>{franchise.name}</Text>
                        </View>

                        <View style={styles.infoRow}>
                            <Text style={styles.labelText}>사업자번호</Text>
                            <Text style={styles.valueText}>{franchise.businessNumber}</Text>
                        </View>

                        <View style={styles.infoRow}>
                            <Text style={styles.labelText}>대표자</Text>
                            <Text style={styles.valueText}>{franchise.ownerName}</Text>
                        </View>

                        <View style={styles.infoRow}>
                            <Text style={styles.labelText}>주소</Text>
                            <Text style={styles.addressText}>
                                {franchise.address}
                            </Text>
                        </View>

                        <View style={styles.infoRow}>
                            <Text style={styles.labelText}>가게 설명</Text>
                            <TextInput
                                style={styles.input}
                                value={franchise.description || ''} 
                                onChangeText={handleDescriptionChange} 
                                placeholder="가게 설명 입력"
                                multiline={true}
                            />
                        </View>

                        <View style={styles.infoRow}>
                            <Text style={styles.labelText}>연락처</Text>
                            <TextInput
                                style={styles.input}
                                value={franchise.phone} 
                                onChangeText={handlePhoneChange} 
                                keyboardType="phone-pad"
                                placeholder="연락처 입력"
                            />
                        </View>

                        {/* 버튼을 가로로 배치하는 View 추가 */}
                        <View style={styles.buttonRow}>
                            <TouchableOpacity style={styles.button} onPress={hanleCancel}>
                                <Text style={styles.buttonText}>취소</Text>
                            </TouchableOpacity>

                            <TouchableOpacity style={styles.button} onPress={() => handleSave(franchise.phone, franchise.description)}>
                                <Text style={styles.buttonText}>확인</Text>
                            </TouchableOpacity>
                        </View>
                        
                        <View style={styles.passwordButtonContainer}>
                            <TouchableOpacity 
                                style={styles.passwordButton} 
                                onPress={() => navigation.navigate('ChangePasswordPage')}
                            >
                                <Text style={styles.buttonText}>비밀번호 변경하기</Text>
                            </TouchableOpacity>
                        </View>
                    </View>
                </ScrollView>
            </KeyboardAvoidingView>
        </SafeAreaView>
    );
};

const styles = StyleSheet.create({
    safeArea: {
        flex: 1,
        backgroundColor: '#fff',
    },
    keyboardAvoid: {
        flex: 1,
    },
    scrollContainer: {
        flexGrow: 1,
    },
    container: {
        flex: 1,
        paddingTop: 80,
        paddingHorizontal: 50,
        paddingBottom: 30,
        backgroundColor: '#fff',
    },
    infoRow: {
        marginBottom: 25,
        flexDirection: 'row',
        alignItems: 'center',
    },
    labelText: {
        width: 80,
        fontSize: 14,
        color: '#888',
    },
    valueText: {
        fontSize: 14,
        color: '#333',
        flex: 1,
    },
    addressText: {
        fontSize: 12,
        color: '#333',
        flex: 1,
        lineHeight: 20,
    },
    input: {
        flex: 1,
        fontSize: 14,
        color: '#333',
        borderBottomWidth: 1,
        borderBottomColor: '#888',
        paddingVertical: 4,
    },
    buttonRow: {
        flexDirection: 'row',
        justifyContent: 'center',
        marginTop: 20,
        marginBottom: 40,
    },
    button: {
        borderWidth: 2,
        borderColor: '#4e82eb',
        borderRadius: 16,
        padding: 8,
        paddingHorizontal: 16,
        alignItems: 'center',
        marginHorizontal: 36,
    },
    buttonText: {
        color: '#4e82eb',
        fontSize: 14,
    },
    passwordButtonContainer: {
        justifyContent: 'center',
        alignItems: 'center',
        marginTop: 70,
    },
    passwordButton: {
        borderWidth: 2,
        borderColor: '#4e82eb',
        borderRadius: 16,
        padding: 12,
        paddingHorizontal: 20,
        alignItems: 'center',
        width: '60%',
    }
});

export default MyInfoEditComponent;
// import { useNavigation } from '@react-navigation/native';
// import React, { useState, useEffect } from 'react';
// import { getMyInfo } from '../../api/myPageApi';
// import { updateInfo } from '../../api/myPageApi';
// import { View, Text, TextInput, StyleSheet, TouchableOpacity, Alert } from 'react-native';


// const MyInfoEditComponent = () => {
//     const navigation = useNavigation();

//     // 초기 상태 설정 (프랜차이즈 정보가 담긴 객체)
//     const [franchise, setFranchise] = useState({
//         name: '',
//         phone: '',
//         address: '',
//         ownerName: '',
//         businessNumber: '',
//         description: ''
//     });

//     // 데이터 가져오기
//     useEffect(() => {
//         const fetchData = async () => {
//             const info = await getMyInfo(); 
//             setFranchise(info); 
//             console.log(info);
//         };

//         fetchData();
//     }, []);


//     const handleSave = (phoneInfo, descriptionInfo) => {
//         const franchiseObj = {
//             phone : phoneInfo,
//             description: descriptionInfo
//         }
//         updateInfo(franchiseObj).then(() => {
//             navigation.navigate('MyPage',{refresh: true});
//         });
        
//     };

//     const hanleCancel = () => {
//         navigation.goBack();
//     }

//     // 연락처 수정 핸들러
//     const handlePhoneChange = (text) => {
//         setFranchise((prevState) => ({
//             ...prevState,
//             phone: text, // 연락처 변경 시 업데이트
//         }));
//     };

//     // 설명 수정 핸들러
//     const handleDescriptionChange = (text) => {
//         console.log(text);
//         setFranchise((prevState) => ({
//             ...prevState,
//             description: text, // 연락처 변경 시 업데이트
//         }));
//     };


//     return (
//         <View style={styles.container}>
//             <View style={styles.infoRow}>
//                 <Text style={styles.labelText}>가맹점</Text>
//                 <Text style={[styles.valueText, { fontWeight: 'bold' }]}>{franchise.name}</Text>
//             </View>

//             <View style={styles.infoRow}>
//                 <Text style={styles.labelText}>사업자번호</Text>
//                 <Text style={styles.valueText}>{franchise.businessNumber}</Text>
//             </View>

//             <View style={styles.infoRow}>
//                 <Text style={styles.labelText}>대표자</Text>
//                 <Text style={styles.valueText}>{franchise.ownerName}</Text>
//             </View>

//             <View style={styles.infoRow}>
//                 <Text style={styles.labelText}>주소</Text>
//                 <Text style={styles.addressText}>
//                     {franchise.address}
//                 </Text>
//             </View>

//             <View style={styles.infoRow}>
//                 <Text style={styles.labelText}>가게 설명</Text>
//                 <TextInput
//                     style={styles.input}
//                     value={franchise.description || ''} // 초기값 franchise.description
//                     onChangeText={handleDescriptionChange} // 값 변경 시 업데이트
//                     keyboardType="phone-pad"
//                     placeholder="가게 설명 입력"
//                 />
//             </View>

//             <View style={styles.infoRow}>
//                 <Text style={styles.labelText}>연락처</Text>
//                 <TextInput
//                     style={styles.input}
//                     value={franchise.phone} // 초기값 franchise.phone
//                     onChangeText={handlePhoneChange} // 값 변경 시 업데이트
//                     keyboardType="phone-pad"
//                     placeholder="연락처 입력"
//                 />
//             </View>

//             {/* 버튼을 가로로 배치하는 View 추가 */}
//             <View style={styles.buttonRow}>
//                 <TouchableOpacity style={styles.button} onPress={hanleCancel}>
//                     <Text style={styles.buttonText}>취소</Text>
//                 </TouchableOpacity>

//                 <TouchableOpacity style={styles.button} onPress={() => handleSave(franchise.phone, franchise.description)}>
//                     <Text style={styles.buttonText}>확인</Text>
//                 </TouchableOpacity>
//             </View>

//             <TouchableOpacity 
//                 style={[styles.button, styles.passwordButton]} 
//                 onPress={() => navigation.navigate('ChangePasswordPage')}
//             >
//                 <Text style={styles.buttonText}>비밀번호 변경하기</Text>
//             </TouchableOpacity>
//         </View>
//     );
// };

// const styles = StyleSheet.create({
//     container: {
//         flex: 1,
//         paddingTop: 80,
//         paddingHorizontal: 50,
//         backgroundColor: '#fff',
//     },
//     infoRow: {
//         marginBottom: 25,
//         flexDirection: 'row',
//         alignItems: 'center',
//     },
//     labelText: {
//         width: 80,
//         fontSize: 14,
//         color: '#888',
//     },
//     valueText: {
//         fontSize: 14,
//         color: '#333',
//         flex: 1,
//     },
//     addressText: {
//         fontSize: 12,
//         color: '#333',
//         flex: 1,
//         lineHeight: 20,
//     },
//     input: {
//         flex: 1,
//         fontSize: 14,
//         color: '#333',
//         borderBottomWidth: 1,
//         borderBottomColor: '#888',
//         paddingVertical: 4,
//     },
//     buttonRow: {
//         flexDirection: 'row',
//         justifyContent: 'center',
//         marginTop: 20,
//     },
//     button: {
//         borderWidth: 2,
//         borderColor: '#4e82eb',
//         borderRadius: 16,
//         padding: 8,
//         paddingHorizontal: 16,
//         alignItems: 'center',
//         marginHorizontal: 36,
//     },
//     buttonText: {
//         color: '#4e82eb',
//         fontSize: 14,
//     },
//     passwordButton: {
//         position: 'absolute',
//         bottom: 70,
//         alignSelf: 'center',
//         width: '60%',
//         paddingVertical: 12,
//     }
// });

// export default MyInfoEditComponent;