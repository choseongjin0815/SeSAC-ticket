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
  Modal,
} from 'react-native';
import { loginUser } from '../../auth/loginSlice';

const LoginPage = () => {
  const [userName, setUserName] = useState('');
  const [password, setPassword] = useState('');
  const [modalVisible, setModalVisible] = useState(false);
  const [modalMessage, setModalMessage] = useState('');

  const dispatch = useDispatch();
  const navigation = useNavigation();

  const showModal = (message) => {
    setModalMessage(message);
    setModalVisible(true);
  };

  const handleLogin = async () => {
    if (!userName.trim()) {
      showModal('아이디를 입력해주세요.');
      return;
    }

    if (!password.trim()) {
      showModal('비밀번호를 입력해주세요.');
      return;
    }

    console.log('Login button pressed');
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
      showModal(error.message || '아이디 또는 패스워드가 틀렸습니다.');
    }
  };

  return (
    <SafeAreaView style={styles.container}>
      <KeyboardAvoidingView
        behavior={Platform.OS === 'ios' ? 'padding' : 'height'}
        style={styles.keyboardView}
      >
        <View style={styles.formContainer}>
          <TextInput
            style={styles.input}
            placeholder="아이디를 입력해주세요"
            value={userName}
            onChangeText={setUserName}
            autoCapitalize="none"
          />

          <TextInput
            style={styles.input}
            placeholder="비밀번호를 입력해주세요"
            value={password}
            onChangeText={setPassword}
            secureTextEntry
          />

          <TouchableOpacity 
            style={styles.loginButton} 
            onPress={handleLogin}
          >
            <Text style={styles.loginButtonText}>로그인</Text>
          </TouchableOpacity>
        </View>
      </KeyboardAvoidingView>

      {/* 모달 */}
      <Modal transparent visible={modalVisible} animationType="fade">
        <View style={styles.overlay}>
          <View style={styles.modalContainer}>
            <Text style={styles.modalMessage}>{modalMessage}</Text>
            <TouchableOpacity
              style={styles.modalButton}
              onPress={() => setModalVisible(false)}
            >
              <Text style={styles.modalButtonText}>확인</Text>
            </TouchableOpacity>
          </View>
        </View>
      </Modal>
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
  // 모달 스타일
  overlay: {
    flex: 1,
    backgroundColor: 'rgba(0,0,0,0.3)',
    justifyContent: 'center',
    alignItems: 'center',
  },
  modalContainer: {
    backgroundColor: '#fff',
    padding: 24,
    borderRadius: 8,
    minWidth: '70%',
    alignItems: 'center',
  },
  modalMessage: {
    fontSize: 16,
    color: '#333',
    marginBottom: 20,
    textAlign: 'center',
  },
  modalButton: {
    backgroundColor: '#2B579A',
    paddingVertical: 10,
    paddingHorizontal: 20,
    borderRadius: 6,
  },
  modalButtonText: {
    color: '#fff',
    fontWeight: 'bold',
  },
});

export default LoginPage;