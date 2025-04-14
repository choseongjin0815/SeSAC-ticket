import { useNavigation } from '@react-navigation/native';
import { useState, useEffect } from 'react';
import React from 'react';
import { View, Text, StyleSheet, TouchableOpacity } from 'react-native';
import { getMyInfo } from '../../api/myPageApi';
import { logoutUser } from '../../auth/loginSlice';
import { useDispatch } from 'react-redux';

const initState = {
    id : 0,
    name : '',
    address : '',
    phone : '',
    ownerName: '',
    businessNumber: '',
    description: ''
}

const MyInfoComponent = () => {
    const [franchise, setFranchise] = useState({...initState});
    
    const navigation = useNavigation();
    const dispatch = useDispatch();

    const handleLogout = async() => {
       await dispatch(logoutUser()).unwrap();
      
    }

    useEffect(() => {
        const fetchData = async () => {
          const info = await getMyInfo();
          setFranchise(info); 
        };
    
        fetchData();
      }, []);

    return (
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
              <Text style={styles.valueText}>{franchise.description}</Text>
          </View>


          <View style={styles.infoRow}>
              <Text style={styles.labelText}>연락처</Text>
              <Text style={styles.valueText}>{franchise.phone}</Text>
          </View>

          {/* 버튼을 가로로 배치하는 View 추가 */}
          <View style={styles.buttonRow}>
              <TouchableOpacity style={styles.button} onPress={() => navigation.navigate('InfoEditPage')}>
              <Text style={styles.buttonText}>수정하기</Text>
              </TouchableOpacity>

              <TouchableOpacity style={styles.button} onPress={handleLogout}>
              <Text style={styles.buttonText}>로그아웃</Text>
              </TouchableOpacity>    
          </View>
        
    </View>
    );
};

const styles = StyleSheet.create({
  container: {
    flex: 1,
    paddingTop: 80,
    paddingHorizontal: 50,
    backgroundColor: '#fff',
  },
  infoRow: {
    marginBottom: 14,
    flexDirection: 'row',
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
  buttonRow: {
    flexDirection: 'row', // 버튼 가로 정렬
    justifyContent: 'center', // 중앙 정렬
    marginTop: 20,
  },
  button: {
    borderWidth: 2,
    borderColor: '#4e82eb',
    borderRadius: 16,
    padding: 8,
    paddingHorizontal: 16,
    alignItems: 'center',
    marginHorizontal: 36, // 버튼 간격 추가
  },
  buttonText: {
    color: '#4e82eb',
    fontSize: 14,
  },
});

export default MyInfoComponent;