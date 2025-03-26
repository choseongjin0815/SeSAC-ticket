import { useNavigation } from '@react-navigation/native';
import React, { useState } from 'react';
import { 
  View, 
  Text, 
  TextInput, 
  TouchableOpacity, 
  StyleSheet, 
  SafeAreaView, 
  Modal
} from 'react-native';

const PaymentComponent = () => {
  const [selectedItem, setSelectedItem] = useState(null);
  const [totalAmount, setTotalAmount] = useState('');
  const [isFirstModalVisible, setIsFirstModalVisible] = useState(false);
  const [isSecondModalVisible, setIsSecondModalVisible] = useState(false);
  const [isThirdModalVisible, setIsThirdModalVisible] = useState(false);
  const [storeName, setStoreName] = useState('압구정샌드위치 녹번점');

  const navigation = useNavigation();

  const items = [
    { name: '런치', price: 144300 },
    { name: '러닝 메이트', price: 300000 }
  ];

  const handleInfo = () => {
    navigation.navigate('FranchiseInfoPage');
  };

  const toggleRadioButton = (itemName, price) => {
    const newSelectedItem = itemName === selectedItem ? null : itemName;
    setSelectedItem(newSelectedItem);
  };

  const handlePayment = () => {
    setIsFirstModalVisible(true);
  };

  const handleFirstModalConfirm = () => {
    setIsFirstModalVisible(false);
    setIsSecondModalVisible(true);
  };

  const handleSecondModalConfirm = () => {
    setIsSecondModalVisible(false);
    setIsThirdModalVisible(true);
  };

  const closeFirstModal = () => {
    setIsFirstModalVisible(false);
  };

  const closeSecondModal = () => {
    setIsSecondModalVisible(false);
  };

  const closeThirdModal = () => {
    setIsThirdModalVisible(false);
  };

  const handleTotalAmountChange = (text) => {
    const numericValue = text.replace(/[^0-9]/g, '');
    setTotalAmount(numericValue ? numericValue : '0');
  };

  return (
    <SafeAreaView style={styles.container}>
      <View style={styles.headerContainer}>
        <Text style={styles.headerLocation}>
          {storeName}
        </Text>
        <View style={styles.headerRight}>
          <TouchableOpacity style={styles.infoButton} onPress={handleInfo}>
            <Text style={{color: 'grey'}}>메뉴 및 상세 정보</Text>
          </TouchableOpacity>
        </View>
      </View>

      <View style={styles.voucherContainer}>
        <Text style={styles.voucherTitle}>SeSAC 전자식권</Text>
        <View style={styles.totalAmountContainer}>
          <Text style={{fontSize: 16}}>합계금액</Text>
          <TextInput 
            style={styles.totalAmountInput}
            value={totalAmount.toString()}
            onChangeText={handleTotalAmountChange}
            editable={true}
          /><Text style={{fontSize: 20}}>원</Text>
        </View>
      </View>

      {items.map((item) => (
        <TouchableOpacity 
          key={item.name} 
          style={styles.itemContainer}
          onPress={() => toggleRadioButton(item.name, item.price)}
        >
          <View 
            style={[
              styles.radioButton, 
              selectedItem === item.name && styles.radioButtonSelected
            ]}
          />
          <Text style={styles.itemName}>{item.name}</Text>
          <Text style={styles.itemPrice}>{item.price.toLocaleString()}P</Text>
        </TouchableOpacity>
      ))}

      <TouchableOpacity 
        style={styles.paymentButton}
        onPress={handlePayment} 
      >
        <Text style={styles.paymentButtonText}>결제</Text>
      </TouchableOpacity>

      {/* 첫 번째 모달 - 결제 확인 */}
      <Modal
        transparent={true}
        animationType="fade"
        visible={isFirstModalVisible}
        onRequestClose={closeFirstModal}
      >
        <View style={styles.modalOverlay}>
          <View style={styles.firstModalContainer}>
            <Text style={styles.firstModalMessage}>다시 한번 확인해주세요</Text>
            <Text style={styles.firstModalStoreName}>{storeName}</Text>
            <Text style={styles.firstModalAmount}>{parseInt(totalAmount).toLocaleString()}원</Text>
            <Text style={styles.firstModalItem}>{selectedItem}</Text>

            <View style={styles.firstModalButtonContainer}>
              <TouchableOpacity style={styles.firstModalConfirmButton} onPress={handleFirstModalConfirm}>
                <Text style={styles.firstModalConfirmButtonText}>결제하기</Text>
              </TouchableOpacity>
              <TouchableOpacity style={styles.firstModalCancelButton} onPress={closeFirstModal}>
                <Text style={styles.firstModalCancelButtonText}>결제취소</Text>
              </TouchableOpacity>
            </View>
          </View>
        </View>
      </Modal>

      {/* 두 번째 모달 - 결제 진행 */}
      <Modal
        transparent={true}
        animationType="fade"
        visible={isSecondModalVisible}
        onRequestClose={closeSecondModal}
      >
        <View style={styles.modalOverlay}>
          <View style={styles.secondModalContainer}>
            <Text style={styles.secondModalTitle}>결제하기</Text>
            <TouchableOpacity 
              style={styles.secondModalAmountButton} 
              onPress={handleSecondModalConfirm}
            >
              <Text style={styles.secondModalAmountText}>{parseInt(totalAmount).toLocaleString()}</Text>
              <Text style={styles.secondModalAmountSubtext}>사장님 눌러주세요!</Text>
            </TouchableOpacity>
            <Text style={styles.secondModalStoreName}>{storeName}</Text>
            <Text style={styles.secondModalItem}>{selectedItem}</Text>
            <TouchableOpacity style={styles.secondModalCancelButton} onPress={closeSecondModal}>
              <Text style={styles.secondModalCancelButtonText}>취소</Text>
            </TouchableOpacity>
          </View>
        </View>
      </Modal>

      {/* 세 번째 모달 - 결제 완료 */}
      <Modal
        transparent={true}
        animationType="fade"
        visible={isThirdModalVisible}
        onRequestClose={closeThirdModal}
      >
        <View style={styles.modalOverlay}>
          <View style={styles.thirdModalContainer}>
            <Text style={styles.thirdModalTitle}>결제완료</Text>
            <Text style={styles.thirdModalAmount}>{parseInt(totalAmount).toLocaleString()}</Text>
            <Text style={styles.thirdModalStoreName}>{storeName}</Text>
            <Text style={styles.thirdModalItem}>{selectedItem}</Text>
            <TouchableOpacity style={styles.thirdModalCloseButton} onPress={closeThirdModal}>
              <Text style={styles.thirdModalCloseButtonText}>닫기</Text>
            </TouchableOpacity>
          </View>
        </View>
      </Modal>
    </SafeAreaView>
  );
};

const styles = StyleSheet.create({
  container: {
    flex: 1,
    backgroundColor: 'white',
  },
  headerContainer: {
    flexDirection: 'row', 
    justifyContent: 'space-between', 
    padding: 15,
    borderBottomWidth: 1,
    borderBottomColor: '#eee'
  },
  headerLocation: {
    fontWeight: 'bold'
  },
  headerRight: {
    color: 'gray'
  },
  infoButton: {
    paddingHorizontal: 10,
    paddingVertical: 4,
    borderWidth: 1,
    borderColor: 'grey',
    borderRadius: 5
  },
  voucherContainer: {
    margin: 15,
    padding: 25,
    borderWidth: 2,
    borderColor: 'green',
    borderRadius: 15,
  },
  voucherTitle: {
    color: 'green',
    fontWeight: 'bold',
    marginBottom: 15,
    fontSize: 20,
    textAlign: 'center',
  },
  totalAmountContainer: {
    flexDirection: 'row',
    alignItems: 'center',
    justifyContent: 'space-between',
    marginTop: 10,
  },
  totalAmountInput: {
    borderBottomWidth: 3,
    borderBottomColor: 'gray',
    textAlign: 'right',
    fontSize: 20,
    width: '60%',
  },
  itemContainer: {
    flexDirection: 'row', 
    alignItems: 'center', 
    padding: 15,
    borderBottomWidth: 1,
    borderBottomColor: '#eee'
  },
  itemName: {
    marginLeft: 10,
    flex: 1
  },
  itemPrice: {
    fontWeight: 'bold'
  },
  radioButton: {
    width: 20,
    height: 20,
    borderRadius: 10,
    borderWidth: 2,
    borderColor: 'gray',
  },
  radioButtonSelected: {
    borderColor: 'green',
    backgroundColor: 'green'
  },
  paymentButton: {
    backgroundColor: '#4CAF50',
    paddingVertical: 15,
    alignItems: 'center',
    justifyContent: 'center',
    marginTop: 'auto',
  },
  paymentButtonText: {
    color: 'white',
    fontSize: 18,
    fontWeight: 'bold'
  },
  modalOverlay: {
    flex: 1,
    justifyContent: 'center',
    alignItems: 'center',
    backgroundColor: 'rgba(0, 0, 0, 0.5)',
  },
  
  // First Modal Styles
  firstModalContainer: {
    width: '90%',
    backgroundColor: 'white',
    borderRadius: 10,
    paddingVertical: 20,
    paddingHorizontal: 15,
    alignItems: 'center',
  },
  firstModalMessage: {
    fontSize: 16,
    fontWeight: 'bold',
    marginBottom: 15,
  },
  firstModalStoreName: {
    fontSize: 14,
    marginBottom: 5,
  },
  firstModalAmount: {
    fontSize: 14,
    marginBottom: 5,
  },
  firstModalItem: {
    fontSize: 14,
    marginBottom: 15,
  },
  firstModalButtonContainer: {
    flexDirection: 'row',
    justifyContent: 'space-between',
    width: '100%',
  },
  firstModalConfirmButton: {
    backgroundColor: '#4CAF50',
    flex: 1,
    marginRight: 10,
    paddingVertical: 12,
    borderRadius: 5,
    alignItems: 'center',
  },
  firstModalConfirmButtonText: {
    color: 'white',
    fontWeight: 'bold',
  },
  firstModalCancelButton: {
    backgroundColor: 'lightgrey',
    flex: 1,
    paddingVertical: 12,
    borderRadius: 5,
    alignItems: 'center',
  },
  firstModalCancelButtonText: {
    color: 'white',
    fontWeight: 'bold',
  },

  // Second Modal Styles
  secondModalContainer: {
    width: '90%',
    backgroundColor: 'white',
    borderRadius: 10,
    paddingVertical: 20,
    paddingHorizontal: 15,
    alignItems: 'center',
  },
  secondModalTitle: {
    fontSize: 16,
    fontWeight: 'bold',
    marginBottom: 15,
  },
  secondModalAmountButton: {
    backgroundColor: '#4CAF50',
    width: '100%',
    paddingVertical: 20,
    borderRadius: 5,
    alignItems: 'center',
    marginBottom: 15,
  },
  secondModalAmountText: {
    color: 'white',
    fontSize: 20,
    fontWeight: 'bold',
  },
  secondModalAmountSubtext: {
    color: 'white',
    marginTop: 5,
  },
  secondModalStoreName: {
    fontSize: 14,
    marginBottom: 5,
  },
  secondModalItem: {
    fontSize: 14,
    marginBottom: 15,
  },
  secondModalCancelButton: {
    backgroundColor: 'lightgrey',
    width: '100%',
    paddingVertical: 12,
    borderRadius: 5,
    alignItems: 'center',
  },
  secondModalCancelButtonText: {
    color: 'white',
    fontWeight: 'bold',
  },

  // Third Modal Styles
  thirdModalContainer: {
    width: '90%',
    backgroundColor: 'white',
    borderRadius: 10,
    paddingVertical: 20,
    paddingHorizontal: 15,
    alignItems: 'center',
  },
  thirdModalTitle: {
    fontSize: 16,
    fontWeight: 'bold',
    marginBottom: 15,
  },
  thirdModalAmount: {
    backgroundColor: '#4CAF50',
    color: 'white',
    fontSize: 24,
    fontWeight: 'bold',
    paddingVertical: 15,
    paddingHorizontal: 50,
    borderRadius: 5,
    marginBottom: 15,
  },
  thirdModalStoreName: {
    fontSize: 14,
    marginBottom: 5,
  },
  thirdModalItem: {
    fontSize: 14,
    marginBottom: 15,
  },
  thirdModalCloseButton: {
    backgroundColor: 'lightgrey',
    width: '100%',
    paddingVertical: 12,
    borderRadius: 5,
    alignItems: 'center',
  },
  thirdModalCloseButtonText: {
    color: 'white',
    fontWeight: 'bold',
  },
});

export default PaymentComponent;