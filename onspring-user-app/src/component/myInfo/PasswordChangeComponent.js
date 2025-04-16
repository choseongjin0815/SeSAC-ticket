import React, { useState } from 'react';
import {
  View,
  Text,
  TextInput,
  TouchableOpacity,
  StyleSheet,
  Modal,
} from 'react-native';
import { updatePassword } from '../../api/myInfoApi';
import { useNavigation } from '@react-navigation/native';

const PasswordChangeComponent = () => {
  const navigation = useNavigation();

  const [form, setForm] = useState({
    currentPassword: '',
    newPassword: '',
    confirmPassword: '',
  });

  const [modalVisible, setModalVisible] = useState(false);
  const [modalMessage, setModalMessage] = useState('');

  const handleChange = (field, value) => {
    setForm(prev => ({ ...prev, [field]: value }));
  };

  const showModal = (message) => {
    setModalMessage(message);
    setModalVisible(true);
  };

  const handleSave = async () => {
    if (!form.currentPassword || !form.newPassword || !form.confirmPassword) {
      showModal('모든 비밀번호 항목을 입력해주세요.');
      return;
    }

    if (form.newPassword !== form.confirmPassword) {
      showModal('새 비밀번호가 일치하지 않습니다.');
      return;
    }

    try {
      const message = await updatePassword({
        oldPassword: form.currentPassword,
        newPassword: form.newPassword,
      });

      setForm({ currentPassword: '', newPassword: '', confirmPassword: '' });
      showModal(message);
      setTimeout(() => {
        setModalVisible(false);
        navigation.navigate('MyPage');
      }, 1500);
    } catch (errMessage) {
      showModal(errMessage.toString());
    }
  };

  return (
    <View style={styles.container}>
      <View style={styles.inputRow}>
        <Text style={styles.label}>현재 비밀번호</Text>
        <TextInput
          style={styles.input}
          secureTextEntry
          value={form.currentPassword}
          onChangeText={value => handleChange('currentPassword', value)}
        />
      </View>

      <View style={styles.inputRow}>
        <Text style={styles.label}>새 비밀번호</Text>
        <TextInput
          style={styles.input}
          secureTextEntry
          value={form.newPassword}
          onChangeText={value => handleChange('newPassword', value)}
        />
      </View>

      <View style={styles.inputRow}>
        <Text style={styles.label}>새 비밀번호 확인</Text>
        <TextInput
          style={styles.input}
          secureTextEntry
          value={form.confirmPassword}
          onChangeText={value => handleChange('confirmPassword', value)}
        />
      </View>

      <TouchableOpacity style={styles.button} onPress={handleSave}>
        <Text style={styles.buttonText}>비밀번호 변경</Text>
      </TouchableOpacity>

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
    </View>
  );
};

const styles = StyleSheet.create({
  container: {
    flex: 1,
    padding: 40,
    backgroundColor: '#fff',
  },
  inputRow: {
    marginBottom: 16,
  },
  label: {
    fontSize: 14,
    color: '#333',
    marginBottom: 4,
  },
  input: {
    borderWidth: 1,
    borderColor: '#aaa',
    borderRadius: 4,
    paddingHorizontal: 10,
    height: 40,
  },
  button: {
    marginTop: 20,
    backgroundColor: '#4CAF50',
    paddingVertical: 12,
    alignItems: 'center',
    borderRadius: 4,
  },
  buttonText: {
    color: '#fff',
    fontSize: 16,
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
    backgroundColor: '#1E4EBE',
    paddingVertical: 10,
    paddingHorizontal: 20,
    borderRadius: 6,
  },
  modalButtonText: {
    color: '#fff',
    fontWeight: 'bold',
  },
});

export default PasswordChangeComponent;