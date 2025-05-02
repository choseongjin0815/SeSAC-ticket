import React, { useState } from 'react';
import { useNavigation } from '@react-navigation/native';
import { useDispatch } from 'react-redux';
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
import { loginUser } from '../../auth/loginSlice';

const LoginPage = () => {
  const [userName, setUserName] = useState('');
  const [password, setPassword] = useState('');
  
  const dispatch = useDispatch();

  const handleLogin = async () => {
    console.log('Login button pressed'); // 버튼 클릭 로그
    console.log('Username:', userName);
    console.log('Password:', password);
    try {
     
      const result = await dispatch(loginUser({
        credentials: {
          userName: userName,
          password: password
        }
      })).unwrap();

      console.log(result);
      // navigation.navigate('MainTab');
    } catch (error) {
      Alert.alert( 
        error.message || '아이디 또는 패스워드가 틀렸습니다.'
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

          {/* 아이디(사용자명) 입력 필드 */}
          <TextInput
            style={styles.input}
            placeholder="아이디를 입력해주세요"
            value={userName}
            onChangeText={setUserName}
            autoCapitalize="none"
          />
          
          {/* 비밀번호 입력 필드 */}
          <TextInput
            style={styles.input}
            placeholder="비밀번호를 입력해주세요"
            value={password}
            onChangeText={setPassword}
            secureTextEntry
          />
          
          {/* 로그인 버튼 */}
          <TouchableOpacity 
            style={styles.loginButton} 
            onPress={handleLogin}
          >
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
  formContainer: {
    padding: 20,
  },
  pageTitle: {
    fontSize: 24,
    fontWeight: 'bold',
    textAlign: 'center',
    marginBottom: 30,
    color: '#2B579A',
  },
  input: {
    borderWidth: 1,
    borderColor: '#DDDDDD',
    borderRadius: 5,
    padding: 15,
    marginVertical: 10,
    backgroundColor: 'white',
    fontSize: 16,
  },
  loginButton: {
    backgroundColor: '#2B579A',
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
});

export default LoginPage;
