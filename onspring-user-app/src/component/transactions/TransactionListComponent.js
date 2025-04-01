import React, { useEffect, useState } from 'react';
import { View, Text, FlatList, StyleSheet, ActivityIndicator } from 'react-native';
import { getTransactionList } from '../../api/transactionApi';

const TransactionListComponent = () => {
  const [data, setData] = useState([]);
  const [page, setPage] = useState(0);
  const [isLast, setIsLast] = useState(false);
  const [loading, setLoading] = useState(false);

  const loadMore = async () => {
    if (loading || isLast) return;

    setLoading(true);
    const result = await getTransactionList(page, 10); // 페이지와 사이즈 전달

    if (result && result.content) {
      const mapped = result.content.map((item) => {
        const date = new Date(item.transactionTime);
        const formattedTime = `${date.getFullYear()}-${(date.getMonth() + 1)
          .toString()
          .padStart(2, '0')}-${date.getDate().toString().padStart(2, '0')} ${date
          .getHours()
          .toString()
          .padStart(2, '0')}:${date.getMinutes().toString().padStart(2, '0')}:${date
          .getSeconds()
          .toString()
          .padStart(2, '0')}`;

        return {
          id: item.id.toString(),
          name: item.franchiseDto.name,
          price: item.amount.toLocaleString(),
          state: item.accepted ? '결제완료' : '결제취소',
          time: formattedTime,
        };
      });

      setData((prev) => [...prev, ...mapped]);
      setIsLast(result.last); // Page 객체의 last 플래그
      setPage((prev) => prev + 1);
    }

    setLoading(false);
  };

  useEffect(() => {
    loadMore(); // 컴포넌트 마운트 시 첫 페이지 로딩
  }, []);

  const renderItem = ({ item }) => (
    <View style={styles.itemContainer}>
      <Text style={styles.name}>{item.name}</Text>
      <Text style={styles.category}>{item.price}</Text>
      <Text style={styles.location}>
        {item.state}
      </Text>
      <Text style={styles.location}>{item.time}</Text>
    </View>
  );

  const renderFooter = () => {
    if (!loading) return null;
    return <ActivityIndicator size="small" style={{ marginVertical: 10 }} />;
  };

  return (
    <View style={styles.container}>
      <FlatList
        data={data}
        keyExtractor={(item) => item.id}
        renderItem={renderItem}
        onEndReached={loadMore}
        onEndReachedThreshold={0.5}
        ListFooterComponent={renderFooter}
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