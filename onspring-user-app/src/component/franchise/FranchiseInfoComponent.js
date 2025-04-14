import React, { useEffect, useState } from 'react';
import { useRoute } from '@react-navigation/native';
import { View, Text, Image, StyleSheet } from 'react-native';
import { getFranchiseInfo } from '../../api/franchiseApi';

const FranchiseInfoComponent = () => {
  const route = useRoute();
  const { franchiseId } = route.params || {};

  const [franchiseInfo, setFranchiseInfo] = useState(null);

  useEffect(() => {
    const fetchData = async () => {
      if (!franchiseId) return;
      try {
        const info = await getFranchiseInfo(franchiseId);
        setFranchiseInfo(info);
      } catch (error) {
        console.error("데이터 불러오기 실패", error);
      }
    };

    fetchData();
  }, [franchiseId]);

  if (!franchiseInfo) {
    return (
      <View style={styles.container}>
        <Text>가맹점 정보를 불러오는 중입니다...</Text>
      </View>
    );
  }

  return (
    <View style={styles.container}>
      <Text style={styles.title}>{franchiseInfo.name || '이름 없음'}</Text>
      <View style={styles.infoRow}>
        <Image source={require('../../../images/map_pin.png')} style={styles.icon} />
        <Text style={styles.infoText}>{franchiseInfo.address || '주소 없음'}</Text>
      </View>
      <View style={styles.infoRow}>
        <Image source={require('../../../images/call.png')} style={styles.icon} />
        <Text style={styles.infoText}>{franchiseInfo.phone || '전화번호 없음'}</Text>
      </View>
    </View>
  );
};

const styles = StyleSheet.create({
  container: {
    paddingLeft: 15,
    paddingVertical: 20,
  },
  title: {
    fontSize: 20,
    fontWeight: 'bold',
    marginBottom: 10,
  },
  infoRow: {
    flexDirection: 'row',
    alignItems: 'center',
    marginBottom: 5,
  },
  icon: {
    width: 18,
    height: 18,
    marginRight: 8,
  },
  infoText: {
    fontSize: 14,
  },
});

export default FranchiseInfoComponent;