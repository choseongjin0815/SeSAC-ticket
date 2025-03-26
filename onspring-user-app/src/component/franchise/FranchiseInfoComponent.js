import { View, Text, Image, StyleSheet } from 'react-native';

const FranchiseInfoComponent = () => {
  return (
    <View style={styles.container}>
      <Text style={styles.title}>압구정샌드위치 녹번점</Text>
      <View style={styles.infoRow}>
        <Image source={require('../../../images/map-pin.png')} style={styles.icon} />
        <Text style={styles.infoText}>
          서울 은평구 은평로 240(응암동, 힐스테이트녹번역) 제상가동 제지2층 제비 307호
        </Text>
      </View>
      <View style={styles.infoRow}>
        <Image source={require('../../../images/call.png')} style={styles.icon} />
        <Text style={styles.infoText}>010-2716-189</Text>
      </View>
    </View>
  );
};

const styles = StyleSheet.create({
  container: {
    paddingLeft: 15, // 왼쪽에서 살짝 떨어지게
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
    marginRight: 8, // 아이콘과 텍스트 간격 조절
  },
  infoText: {
    fontSize: 14,
  },
});

export default FranchiseInfoComponent;