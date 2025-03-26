import { useNavigation } from '@react-navigation/native';
import React from 'react';
import { 
  View, 
  Text, 
  StyleSheet, 
  SafeAreaView, 
  TouchableOpacity, 
  Image 
} from 'react-native';

const MyInfoComponent = () => {
    const navigation = useNavigation();
  const productDetails = [
    { name: '런치', price: 144300 },
    { name: '러닝메이트', price: 300000 }
  ];
  const handlePointDetail = () => {
    navigation.navigate('PointDetailPage');
  }
  return (
    <SafeAreaView style={styles.container}>
      <View style={styles.headerContainer}>
        <Text style={styles.headerTitle}>홍길동</Text>
        <Text style={styles.subTitle}>은명</Text>
        <Text style={styles.description}>학생</Text>
        <Text style={styles.description}>신입 개발자를 위한 자바 백엔드 심화 과정</Text>
      </View>

      <View style={styles.productContainer}>
        {productDetails.map((product, index) => (
          <View key={index} style={styles.productItem}>
            <Text style={styles.productName}>{product.name}</Text>
            <View style={styles.productPriceContainer}>
              <Text style={styles.productPrice}>{product.price.toLocaleString()}P</Text>
              {index === 0 ? (
                <TouchableOpacity onPress={handlePointDetail}
                >
                  <Image 
                    source={require('../../../images/Vector.png')} 
                    style={styles.chevronIcon} 
                  />
                </TouchableOpacity>
              ): <View style={styles.chevronIcon}/>}
            </View>
          </View>
        ))}
      </View>

      <TouchableOpacity style={styles.footerContainer}>
        <Text style={styles.footerText}>공지사항</Text>
      </TouchableOpacity>
    </SafeAreaView>
  );
};

const styles = StyleSheet.create({
  container: {
    flex: 1,
    backgroundColor: 'white',
  },
  headerContainer: {
    padding: 20,
    borderBottomWidth: 1,
    borderBottomColor: '#f0f0f0'
  },
  headerTitle: {
    fontSize: 22,
    fontWeight: 'bold',
    marginBottom: 10
  },
  subTitle: {
    color: '#666',
    marginBottom: 5
  },
  description: {
    color: '#888',
    marginBottom: 5
  },
  productContainer: {
    backgroundColor: 'white',
    borderWidth: 1,
    borderColor: '#e0e0e0',
    margin: 20,
    borderRadius: 10,
  },
  productItem: {
    flexDirection: 'row',
    justifyContent: 'space-between',
    alignItems: 'center',
    padding: 15,
    borderBottomWidth: 1,
    borderBottomColor: '#f0f0f0'
  },
  productName: {
    fontSize: 16,
  },
  productPriceContainer: {
    flexDirection: 'row',
    alignItems: 'center'
  },
  productPrice: {
    fontSize: 16,
    fontWeight: 'bold',
    marginRight: 10
  },
  chevronIcon: {
    width: 8,
    height: 8,
    tintColor: '#888'
  },
  footerContainer: {
    padding: 20,
  },
  footerText: {
    color: '#888',
  }
});

export default MyInfoComponent;