import { useNavigation } from '@react-navigation/native';
import React, { useState, useEffect } from 'react';
import { getMyInfo, getMyPartyInfo, getMyPoints } from '../../api/myInfoApi';
import { 
  View, 
  Text, 
  StyleSheet, 
  SafeAreaView, 
  TouchableOpacity, 
  Image,
  ActivityIndicator
} from 'react-native';

const initState = {
  id : 0,
  partyId : 0,
  name : '',
  phone: '',
  partyDto: [],
  currentPoint: ''
}

const MyInfoComponent = () => {
  const navigation = useNavigation();
  const [user, setUser] = useState({...initState});
  const [points, setPoints] = useState([]);
  const [party, setParty] = useState('');
  const [isLoading, setIsLoading] = useState(true);

  const handlePointDetail = () => {
    navigation.navigate('PointDetailPage');
  }

  useEffect(() => {
    const fetchData = async () => {
      try {
        const info = await getMyInfo();
        const partyInfo = await getMyPartyInfo();
        const pointsData = await getMyPoints();
        
        setUser(info);
        setPoints(pointsData.map(item => ({
          ...item,
          availableAmount: Number(item.availableAmount),
          chargedAmount: Number(item.chargedAmount)
        })));
        setParty(partyInfo);
      } catch (error) {
        console.error("데이터 불러오기 실패:", error);
      } finally {
        setIsLoading(false);
      }
    };

    fetchData();
  }, []);

  if (isLoading) {
    return (
      <SafeAreaView style={styles.container}>
        <ActivityIndicator size="large" color="#0000ff" />
      </SafeAreaView>
    );
  }

  return (
    <SafeAreaView style={styles.container}>
      <View style={styles.headerContainer}>
        <Text style={styles.headerTitle}>{user.name}</Text>
        {/* <Text style={styles.subTitle}>은명</Text>
        <Text style={styles.description}>학생</Text> */}
        <Text style={styles.description}>{party.name}</Text>
      </View>

      <View style={styles.productContainer}>
        {points.filter((point) => point.activated).map((point, index) => (
          <View key={index} style={styles.productItem}>
            <Text style={styles.productName}>{point.partyName}</Text>
            <View style={styles.productPriceContainer}>
              <Text style={styles.productPrice}>
                {point.availableAmount.toLocaleString()}P
              </Text>
              {index === 0 && (
                <TouchableOpacity onPress={handlePointDetail}>
                  <Image 
                    source={require('../../../images/vector.png')} 
                    style={styles.chevronIcon} 
                  />
                </TouchableOpacity>
              )}
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

// 기존 스타일 객체는 변경 없이 그대로 유지
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
