import React from 'react';
import { View, Text, FlatList, StyleSheet } from 'react-native';
import { useNavigation } from '@react-navigation/native';
import { TouchableOpacity } from 'react-native';

const sampleData = [
    { id: '1', name: '맛을찾아마라탕', category: '마라탕 맛집', location: '서울 은평구' },
    { id: '2', name: '빵커피 꿈친2호점', category: '따뜻한 빵집 베이커리 "꿈친"', location: '서울 은평구' },
    { id: '3', name: '압구정샌드위치 녹번점', category: '녹번동 찐맛집 압구정샌드위치입니다.', location: '서울 은평구' },
    { id: '4', name: '은평고기국수', category: '고기 국수 맛집', location: '서울 은평구' },
    { id: '5', name: '응일한정식 생고기', category: '푸짐하고 깔끔한 백반 맛집', location: '서울 은평구' },
    { id: '6', name: '프랭크버거 녹번역점', category: '갓성비와 존맛의 수제버거', location: '서울 은평구' },
    { id: '7', name: '김호권의 청년어부 녹번역점', category: '초밥 맛집', location: '서울 은평구' },
  ];
  const renderItem = ({ item, navigation }) => {
      return (
          <TouchableOpacity
              style={styles.itemContainer}
              onPress={() => navigation.navigate('PaymentPage')} // 클릭 시 PaymentPage로 이동
          >
              <Text style={styles.name}>{item.name}</Text>
              <Text style={styles.category}>{item.category}</Text>
              <Text style={styles.location}>{item.location}</Text>
          </TouchableOpacity>
      );
  };
  
  const FranchiseListComponent = () => {
      const navigation = useNavigation(); // useNavigation 훅을 여기서 사용
  
      return (
          <View style={styles.container}>
              <FlatList
                  data={sampleData} // 나중에 실제 데이터와 연결 가능
                  keyExtractor={(item) => item.id}
                  renderItem={(props) => renderItem({ ...props, navigation })} // navigation을 props로 전달
              />
          </View>
      );
  };
  
const styles = StyleSheet.create({
    container: {
      flex: 1,
      backgroundColor: '#fff',
      paddingHorizontal: 15,
      paddingTop: 10,
    },
    itemContainer: {
      paddingVertical: 15,
      borderBottomWidth: 1,
      borderBottomColor: '#ddd',
    },
    name: {
      fontSize: 16,
      fontWeight: 'bold',
      color: '#333',
    },
    category: {
      fontSize: 14,
      color: '#666',
      marginVertical: 3,
    },
    location: {
      fontSize: 12,
      color: '#999',
    },
  });

export default FranchiseListComponent;