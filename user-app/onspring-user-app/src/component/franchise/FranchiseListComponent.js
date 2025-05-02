import React, { useEffect, useState } from 'react';
import {
  View,
  Text,
  FlatList,
  StyleSheet,
  ActivityIndicator,
  TouchableOpacity,
} from 'react-native';
import { useNavigation } from '@react-navigation/native';
import { getFranchiseList } from '../../api/franchiseApi';

const FranchiseListComponent = () => {
  const navigation = useNavigation();
  const [data, setData] = useState([]);
  const [page, setPage] = useState(0);
  const [isLast, setIsLast] = useState(false);
  const [loading, setLoading] = useState(false);

  const loadMore = async () => {
    if (loading || isLast) return;

    setLoading(true);
    const result = await getFranchiseList(page, 10);
    if (result && result.content) {
      const filtered = result.content.filter((item) => item.activated);
      const mapped = filtered.map((item) => ({
        id: item.id.toString(),
        name: item.name,
        category: item.description || '설명 없음',
        location: item.address,
      }));

      setData((prev) => [...prev, ...mapped]);
      setIsLast(result.last); 
      setPage((prev) => prev + 1);
    }
    setLoading(false);
  };

  useEffect(() => {
    loadMore();
  }, []);

  const renderItem = ({ item }) => (
    <TouchableOpacity
      style={styles.itemContainer}
      onPress={() => navigation.navigate('PaymentPage', { franchiseId: item.id })}
    >
      <Text style={styles.name}>{item.name}</Text>
      <Text style={styles.category}>{item.category}</Text>
      <Text style={styles.location}>{item.location}</Text>
    </TouchableOpacity>
  );

  const renderFooter = () =>
    loading ? <ActivityIndicator size="small" style={{ marginVertical: 10 }} /> : null;

  return (
    <View style={styles.container}>
      <FlatList
        style={{ flex: 1 }}
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

export default FranchiseListComponent;