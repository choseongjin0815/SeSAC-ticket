import React, { useState } from 'react';
import { useNavigation } from '@react-navigation/native';
import {
  SafeAreaView,
  View,
  Text,
  TextInput,
  TouchableOpacity,
  StyleSheet,
  KeyboardAvoidingView,
  Platform,
  Alert
} from 'react-native';
import { useDispatch } from 'react-redux';
import { loginUser } from '../../auth/loginSlice';


const LoginPage = () => {
  const [phone, setPhone] = useState('');
  const [password, setPassword] = useState('');
    

  const dispatch = useDispatch();

  const handleLogin = async () => {
    console.log('Login button pressed'); // 버튼 클릭 로그
    console.log('phone:', phone);
    console.log('Password:', password);
    try {
     
      const result = await dispatch(loginUser({
        credentials: {
          phone: phone,
          password: password
        }
      })).unwrap();

      console.log(result);
      // navigation.navigate('MainTab');
    } catch (error) {
      Alert.alert(
        error.message || '아이디 또는 비밀번호가 틀렸습니다.'
      );
    }
  };

  return (
    <SafeAreaView style={styles.container}>
      <KeyboardAvoidingView
        behavior={Platform.OS === 'ios' ? 'padding' : 'height'}
        style={styles.keyboardView}
      >

        <View style={styles.formContainer}>
          {/* 입력 필드 */}
          <TextInput
            style={styles.input}
            placeholder="핸드폰 번호를 입력해주세요"
            value={phone}
            onChangeText={setPhone}
            keyboardType="default"
            // keyboardType="phone-pad"
          />
          
          <TextInput
            style={styles.input}
            placeholder="비밀번호를 입력해주세요"
            value={password}
            onChangeText={setPassword}
            secureTextEntry
          />         

          {/* 로그인 버튼 */}
          <TouchableOpacity style={styles.loginButton} onPress={() => handleLogin('MainTab')}>
            <Text style={styles.loginButtonText}>로그인</Text>
          </TouchableOpacity>
        </View>
      </KeyboardAvoidingView>

    </SafeAreaView>
  );
};

const styles = StyleSheet.create({
  container: {
    flex: 1,
    backgroundColor: 'white',
  },
  keyboardView: {
    flex: 1,
    justifyContent: 'center',
  },
  header: {
    flexDirection: 'row',
    alignItems: 'center',
    justifyContent: 'space-between',
    paddingVertical: 15,
    borderBottomWidth: 1,
    borderBottomColor: '#EEEEEE',
  },
  backButton: {
    paddingHorizontal: 15,
  },
  backButtonText: {
    fontSize: 24,
    fontWeight: '300',
  },
  headerTitle: {
    fontSize: 18,
    fontWeight: 'bold',
  },
  placeholder: {
    width: 40,
  },
  formContainer: {
    padding: 20,
  },
  input: {
    borderWidth: 1,
    borderColor: '#DDDDDD',
    borderRadius: 5,
    padding: 15,
    marginVertical: 10,
    backgroundColor: 'white',
  },
  checkboxContainer: {
    flexDirection: 'row',
    alignItems: 'center',
    marginVertical: 10,
  },
  checkbox: {
    width: 22,
    height: 22,
    borderRadius: 22,
    borderWidth: 2,
    borderColor: '#2B579A',
    alignItems: 'center',
    justifyContent: 'center',
    marginRight: 8,
  },
  checkmark: {
    width: 14,
    height: 14,
    borderRadius: 14,
    backgroundColor: '#2B579A',
    alignItems: 'center',
    justifyContent: 'center',
  },
  checkmarkText: {
    color: 'white',
    fontSize: 12,
  },
  checkboxLabel: {
    fontSize: 14,
    color: '#333',
  },
  loginButton: {
    backgroundColor: '#4CAF50',
    borderRadius: 5,
    padding: 15,
    alignItems: 'center',
    marginTop: 20,
  },
  loginButtonText: {
    color: 'white',
    fontSize: 16,
    fontWeight: 'bold',
  },
  footer: {
    alignItems: 'center',
    justifyContent: 'center',
    paddingBottom: 30,
  },
  signUpText: {
    fontSize: 14,
    color: '#666',
  },
});

export default LoginPage;