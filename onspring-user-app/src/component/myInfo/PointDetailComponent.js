import React, { useEffect, useState } from 'react';
import { View, Text, StyleSheet, SafeAreaView, ScrollView, ActivityIndicator } from 'react-native';
import { getMyPoints } from '../../api/myInfoApi'; // 실제 경로에 맞게 조정

const PointDetailComponent = ({ card }) => (
  <View style={styles.cardContainer}>
    <Text style={styles.cardTitle}>{card.name}</Text>
    <View style={styles.divider} />
    <View style={styles.detailRow}>
      <Text style={styles.labelText}>가용포인트</Text>
      <Text style={styles.valueText}>{card.availablePoints}</Text>
    </View>
    <View style={styles.detailRow}>
      <Text style={styles.labelText}>충전포인트</Text>
      <Text style={styles.valueText}>{card.chargedPoints}</Text>
    </View>
    <View style={styles.detailRow}>
      <Text style={styles.labelText}>사용기한</Text>
      <Text style={styles.valueText}>{card.expirationDate}</Text>
    </View>
    <View style={styles.detailRow}>
      <Text style={styles.labelText}>1회 한도</Text>
      <Text style={styles.valueText}>{card.limitPerUse.toLocaleString()}</Text>
    </View>
    <View style={styles.detailRow}>
      <Text style={styles.labelText}>사용가능 요일</Text>
      <Text style={styles.valueText}>{card.availableDays}</Text>
    </View>
    <View style={styles.detailRow}>
      <Text style={styles.labelText}>사용가능 시간</Text>
      <Text style={styles.valueText}>{card.availableTime}</Text>
    </View>
    {/* <View style={styles.detailRow}>
      <Text style={styles.labelText}>지급일</Text>
      <Text style={styles.valueText}>{card.issueDate}</Text>
    </View> */}
  </View>
);

const PointCardScreen = () => {
  const [cardData, setCardData] = useState([]);
  const [loading, setLoading] = useState(true);

  // 요일 문자열 변환
  const convertDays = (dto) => {
    const days = [
      dto.monday && '월',
      dto.tuesday && '화',
      dto.wednesday && '수',
      dto.thursday && '목',
      dto.friday && '금',
      dto.saturday && '토',
      dto.sunday && '일',
    ].filter(Boolean);
    return days.join(', ');
  };

  // 날짜 포맷
  const formatDate = (dateStr) => {
    if (!dateStr) return '';
    const date = new Date(dateStr);
    return `${date.getFullYear()}.${(date.getMonth() + 1)
      .toString()
      .padStart(2, '0')}.${date.getDate().toString().padStart(2, '0')}`;
  };

  // 시간 포맷 (HH:mm 형식 그대로 사용)
  const formatTimeRange = (start, end) => {
    return `${start} - ${end}`;
  };

  useEffect(() => {
    const fetchPoints = async () => {
      const result = await getMyPoints();
      if (result) {
        const filtered = result.filter((item) => item.activated); 
  
        const mapped = filtered.map((item) => ({
          name: item.partyName,
          availablePoints: item.availableAmount,
          chargedPoints: item.chargedAmount,
          expirationDate: formatDate(item.validThru),
          limitPerUse: Number(item.maximumAmount),
          availableDays: convertDays(item),
          availableTime: formatTimeRange(item.allowedTimeStart, item.allowedTimeEnd),
          issueDate: formatDate(item.createdTime),
          activated: item.activated
        }));
  
        setCardData(mapped);
      }
      setLoading(false);
    };
  
    fetchPoints();
  }, []);

  if (loading) {
    return (
      <SafeAreaView style={styles.container}>
        <ActivityIndicator size="large" color="#000" style={{ marginTop: 30 }} />
      </SafeAreaView>
    );
  }

  return (
    <SafeAreaView style={styles.container}>
      <ScrollView>
        {cardData.map((card, index) => (
          <PointDetailComponent key={index} card={card} />
        ))}
      </ScrollView>
    </SafeAreaView>
  );
};

const styles = StyleSheet.create({
  container: {
    flex: 1,
    backgroundColor: '#ffffff'
  },
  cardContainer: {
    backgroundColor: 'white',
    borderWidth: 1,
    borderColor: '#E0E0E0',
    borderRadius: 10,
    padding: 15,
    margin: 10,
    shadowColor: '#000',
    shadowOffset: { width: 0, height: 2 },
    shadowOpacity: 0.1,
    shadowRadius: 4,
    elevation: 3
  },
  cardTitle: {
    fontSize: 18,
    fontWeight: 'bold',
    marginBottom: 10
  },
  divider: {
    height: 1,
    backgroundColor: '#E0E0E0',
    marginBottom: 10
  },
  detailRow: {
    flexDirection: 'row',
    justifyContent: 'space-between',
    alignItems: 'center',
    marginBottom: 5
  },
  labelText: {
    fontSize: 14,
    color: '#666666'
  },
  valueText: {
    fontSize: 14,
    fontWeight: 'bold',
    color: '#000000'
  }
});

export default PointCardScreen;