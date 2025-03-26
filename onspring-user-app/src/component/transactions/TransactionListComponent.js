import React from 'react';
import { View, Text, FlatList, StyleSheet } from 'react-native';
import { useNavigation } from '@react-navigation/native';
import { TouchableOpacity } from 'react-native';

const sampleData = [
    { id: '1', name: '맛을찾아마라탕', price: '8800', state: '서울 은평구', time: '2025-03-25' },
    { id: '2', name: '빵커피 꿈친2호점', price: '7200', state: '서울 은평구', time: '2025-03-24' },
   
  ];
  const renderItem = ({ item}) => {
      return (
          <View
              style={styles.itemContainer}
              
          >
              <Text style={styles.name}>{item.name}</Text>
              <Text style={styles.category}>{item.price}</Text>
              <Text style={styles.location}>{item.state}</Text>
              <Text style={styles.location}>{item.time}</Text>
          </View>
      );
  };
  
  const TransactionListComponent = () => {
     
  
      return (
          <View style={styles.container}>
              <FlatList
                  data={sampleData} // 나중에 실제 데이터와 연결 가능
                  keyExtractor={(item) => item.id}
                  renderItem={(props) => renderItem({ ...props })} // navigation을 props로 전달
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

export default TransactionListComponent;