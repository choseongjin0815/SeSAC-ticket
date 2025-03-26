import { useNavigation } from '@react-navigation/native';
import React from 'react';
import { View, Text, TouchableOpacity, StyleSheet } from 'react-native';

const SettingComponent = () => {
  const navigation = useNavigation();
  const handlePassword = () => {
    navigation.navigate('PasswordChangePage');
  }
  return (
    <View style={styles.container}>
      <View style={styles.header}>
        <Text style={styles.phoneNumber}>01012341234</Text>
        <TouchableOpacity>
          <Text style={styles.loginText}>로그아웃</Text>
        </TouchableOpacity>
      </View>

      <TouchableOpacity style={styles.changePasswordButton} onPress={handlePassword}>
        <Text style={styles.changePasswordText}>비밀번호 변경</Text>
        <Text style={styles.arrowText}>{'>'}</Text>
      </TouchableOpacity>
    </View>
  );
};

const styles = StyleSheet.create({
  container: {
    flex: 1,
    backgroundColor: 'white',
  },
  header: {
    flexDirection: 'row',
    justifyContent: 'space-between',
    alignItems: 'center',
    padding: 16,
  },
  phoneNumber: {
    fontSize: 16,
    color: 'black',
  },
  loginText: {
    paddingHorizontal: 7,
    paddingVertical: 5,
    color: 'green',
    borderRadius: 10,
    borderColor: 'green',
    borderWidth: 1
  },
  changePasswordButton: {
    flexDirection: 'row',
    justifyContent: 'space-between',
    alignItems: 'center',
    paddingHorizontal: 16,
    paddingVertical: 16,
    borderTopWidth: 1,
    borderTopColor: '#f0f0f0',
    borderBottomWidth: 1,
    borderBottomColor: '#f0f0f0',
  },
  changePasswordText: {
    fontSize: 16,
    color: 'black',
  },
  arrowText: {
    color: '#888',
  },
});

export default SettingComponent;