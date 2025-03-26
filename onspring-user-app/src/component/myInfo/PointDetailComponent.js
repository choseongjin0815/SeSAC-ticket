import React from 'react';
import { View, Text, StyleSheet, SafeAreaView, ScrollView } from 'react-native';

const PointDetailComponent = ({ card }) => (
  <View style={styles.cardContainer}>
    <Text style={styles.cardTitle}>{card.name}</Text>
    <View style={styles.divider} />
    <View style={styles.detailRow}>
      <Text style={styles.labelText}>가용포인트</Text>
      <Text style={styles.valueText}>{card.availablePoints.toLocaleString()}</Text>
    </View>
    <View style={styles.detailRow}>
      <Text style={styles.labelText}>충전포인트</Text>
      <Text style={styles.valueText}>{card.chargedPoints.toLocaleString()}</Text>
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
    <View style={styles.detailRow}>
      <Text style={styles.labelText}>지급일</Text>
      <Text style={styles.valueText}>{card.issueDate}</Text>
    </View>
  </View>
);

const PointCardScreen = () => {
  const cardData = [
    {
      name: '종식',
      availablePoints: 144300,
      chargedPoints: 200000,
      expirationDate: '2025.02.28',
      limitPerUse: 10000,
      availableDays: '월, 수, 금',
      availableTime: '11:00 - 14:00',
      issueDate: '2025.02.01 00:41'
    },
    {
      name: '러닝메이트',
      availablePoints: 300000,
      chargedPoints: 600000,
      expirationDate: '2025.02.28',
      limitPerUse: 15000,
      availableDays: '월, 화, 수, 목, 금',
      availableTime: '13:00 - 21:00',
      issueDate: '2025.02.01 00:41'
    }
  ];

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