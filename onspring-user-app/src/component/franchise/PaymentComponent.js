import React, { useState, useEffect, useRef } from 'react';
import {
  View,
  Text,
  TextInput,
  TouchableOpacity,
  StyleSheet,
  SafeAreaView,
  Modal,
  ScrollView,
  KeyboardAvoidingView,
  Platform,
  Keyboard,
  findNodeHandle,
} from 'react-native';
import { useNavigation, useRoute } from '@react-navigation/native';
import { getFranchiseInfo } from '../../api/franchiseApi';
import { getMyPoints } from '../../api/myInfoApi';
import { postTransaction } from '../../api/transactionApi';

const PaymentComponent = () => {
  const navigation = useNavigation();
  const route = useRoute();
  const { franchiseId } = route.params || {};
  const totalAmountInputRef = useRef(null);

  const [selectedItem, setSelectedItem] = useState(null);
  const [totalAmount, setTotalAmount] = useState('');

  const [isFirstModalVisible, setIsFirstModalVisible] = useState(false);
  const [isSecondModalVisible, setIsSecondModalVisible] = useState(false);
  const [isThirdModalVisible, setIsThirdModalVisible] = useState(false);

  const [isErrorModalVisible, setIsErrorModalVisible] = useState(false);
  const [errorMessage, setErrorMessage] = useState('');

  const [storeName, setStoreName] = useState('');
  const [items, setItems] = useState([]);
  const [keyboardIsVisible, setKeyboardIsVisible] = useState(false);

  useEffect(() => {
    const fetchData = async () => {
      if (franchiseId) {
        const store = await getFranchiseInfo(franchiseId);
        setStoreName(store?.name || '가맹점 이름 불러오기 실패');
      }

      const result = await getMyPoints();
      const now = new Date();
      const currentDay = now.getDay();
      const currentTime = `${now.getHours().toString().padStart(2, '0')}:${now.getMinutes().toString().padStart(2, '0')}`;

      const mapped = result.map((item) => {
        const expiration = new Date(item.validThru);
        const startTime = item.allowedTimeStart;
        const endTime = item.allowedTimeEnd;
        const availableDays = [
          item.sunday,
          item.monday,
          item.tuesday,
          item.wednesday,
          item.thursday,
          item.friday,
          item.saturday,
        ];

        const isValid =
          item.activated &&
          now <= expiration &&
          availableDays[currentDay] &&
          currentTime >= startTime &&
          currentTime <= endTime;

        return {
          name: item.partyName,
          price: item.availableAmount,
          maximumAmount: item.maximumAmount,
          pointId: item.pointId,
          partyId: item.partyId,
          disabled: !isValid,
        };
      });

      setItems(mapped);
    };
    fetchData();

    // Set up keyboard listeners
    const keyboardDidShowListener = Keyboard.addListener(
      'keyboardDidShow',
      () => setKeyboardIsVisible(true)
    );
    const keyboardDidHideListener = Keyboard.addListener(
      'keyboardDidHide',
      () => setKeyboardIsVisible(false)
    );

    // Clean up keyboard listeners
    return () => {
      keyboardDidShowListener.remove();
      keyboardDidHideListener.remove();
    };
  }, [franchiseId]);

  const focusAmountInput = () => {
    if (totalAmountInputRef.current) {
      if (Platform.OS === 'android') {
        setTimeout(() => {
          totalAmountInputRef.current.focus();
        }, 100);
      } else {
        totalAmountInputRef.current.focus();
      }
    }
  };

  const handleInfo = () => navigation.navigate('FranchiseInfoPage', { franchiseId });
  
  const toggleRadioButton = (itemName) => {
    setSelectedItem(itemName === selectedItem ? null : itemName);
    // Focus on the amount input after selecting an item
    focusAmountInput();
  };
  
  const closeModal = (setter) => () => setter(false);
  
  const handleTotalAmountChange = (text) => setTotalAmount(text.replace(/[^0-9]/g, '') || '0');

  const handlePayment = () => {
    // Dismiss keyboard
    Keyboard.dismiss();
    
    const selected = items.find((item) => item.name === selectedItem);
    const amount = parseFloat(totalAmount);

    if (!selected) {
      setErrorMessage('포인트를 선택해주세요.');
      setIsErrorModalVisible(true);
      return;
    }

    if (amount > selected.price) {
      setErrorMessage('가용 포인트보다 결제 금액이 큽니다.');
      setIsErrorModalVisible(true);
      return;
    }

    if (amount > selected.maximumAmount) {
      setErrorMessage('1회 최대 사용금액을 초과했습니다.');
      setIsErrorModalVisible(true);
      return;
    }

    setIsFirstModalVisible(true);
  };

  const handleFirstModalConfirm = () => {
    setIsFirstModalVisible(false);
    setIsSecondModalVisible(true);
  };

  const handleSecondModalConfirm = async () => {
    const selected = items.find((item) => item.name === selectedItem);
    const transactionDto = { amount: parseFloat(totalAmount) };

    try {
      await postTransaction(franchiseId, selected.pointId, transactionDto, selected.partyId);
      setIsSecondModalVisible(false);
      setIsThirdModalVisible(true);
    } catch (error) {
      setIsSecondModalVisible(false);
      setErrorMessage(error);
      setIsErrorModalVisible(true);
    }
  };

  const handleThirdModalClose = () => {
    setIsThirdModalVisible(false);
    navigation.navigate('TransactionTab', {refresh: true});
  };

  const renderTotalAmountInput = () => (
    <TouchableOpacity 
      activeOpacity={1} 
      style={styles.totalAmountContainer}
      onPress={focusAmountInput}
    >
      <Text style={{ fontSize: 16 }}>합계금액</Text>
      
        <TextInput
          ref={totalAmountInputRef}
          style={styles.totalAmountInput}
          value={totalAmount.toString()}
          onChangeText={handleTotalAmountChange}
          keyboardType="numeric"
          returnKeyType="done"
          textAlign="right"
          contextMenuHidden={true}
          selectTextOnFocus={true}
          onSubmitEditing={Keyboard.dismiss}
        />
     
      <Text style={{ fontSize: 20 }}>원</Text>
    </TouchableOpacity>
  );

  return (
    <SafeAreaView style={styles.container}>
      <KeyboardAvoidingView 
        style={{ flex: 1 }} 
        behavior={Platform.OS === 'ios' ? 'padding' : 'height'}
        keyboardVerticalOffset={Platform.OS === 'ios' ? 0 : 20}
        enabled
      >
        <ScrollView 
          contentContainerStyle={{ paddingBottom: 100 }}
          keyboardShouldPersistTaps="handled"
        >
          <View style={styles.headerContainer}>
            <Text style={styles.headerLocation}>{storeName}</Text>
            <TouchableOpacity style={styles.infoButton} onPress={handleInfo}>
              <Text style={{ color: 'grey' }}>메뉴 및 상세 정보</Text>
            </TouchableOpacity>
          </View>

          <View style={styles.voucherContainer}>
            <Text style={styles.voucherTitle}>SeSAC 전자식권</Text>
            {renderTotalAmountInput()}
          </View>

          {items.map((item) => (
            <TouchableOpacity
              key={item.name}
              style={[styles.itemContainer, item.disabled && { opacity: 0.4 }]}
              onPress={() => !item.disabled && toggleRadioButton(item.name)}
              disabled={item.disabled}
            >
              <View style={[styles.radioButton, selectedItem === item.name && styles.radioButtonSelected]} />
              <Text style={styles.itemName}>{item.name}</Text>
              <Text style={styles.itemPrice}>{item.price.toLocaleString()}P</Text>
            </TouchableOpacity>
          ))}
        </ScrollView>

        <TouchableOpacity style={styles.paymentButton} onPress={handlePayment}>
          <Text style={styles.paymentButtonText}>결제</Text>
        </TouchableOpacity>

        {/* 모달들 아래 생략 없이 전부 유지됨 */}
        {/* 첫 번째 모달 */}
        <Modal transparent animationType="fade" visible={isFirstModalVisible}>
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
                <TouchableOpacity style={styles.firstModalCancelButton} onPress={closeModal(setIsFirstModalVisible)}>
                  <Text style={styles.firstModalCancelButtonText}>결제취소</Text>
                </TouchableOpacity>
              </View>
            </View>
          </View>
        </Modal>

        {/* 두 번째 모달 */}
        <Modal transparent animationType="fade" visible={isSecondModalVisible}>
          <View style={styles.modalOverlay}>
            <View style={styles.secondModalContainer}>
              <Text style={styles.secondModalTitle}>결제하기</Text>
              <TouchableOpacity style={styles.secondModalAmountButton} onPress={handleSecondModalConfirm}>
                <Text style={styles.secondModalAmountText}>{parseInt(totalAmount).toLocaleString()}</Text>
                <Text style={styles.secondModalAmountSubtext}>사장님 눌러주세요!</Text>
              </TouchableOpacity>
              <Text style={styles.secondModalStoreName}>{storeName}</Text>
              <Text style={styles.secondModalItem}>{selectedItem}</Text>
              <TouchableOpacity style={styles.secondModalCancelButton} onPress={closeModal(setIsSecondModalVisible)}>
                <Text style={styles.secondModalCancelButtonText}>취소</Text>
              </TouchableOpacity>
            </View>
          </View>
        </Modal>

        {/* 세 번째 모달 */}
        <Modal transparent animationType="fade" visible={isThirdModalVisible}>
          <View style={styles.modalOverlay}>
            <View style={styles.thirdModalContainer}>
              <Text style={styles.thirdModalTitle}>결제완료</Text>
              <Text style={styles.thirdModalAmount}>{parseInt(totalAmount).toLocaleString()}</Text>
              <Text style={styles.thirdModalStoreName}>{storeName}</Text>
              <Text style={styles.thirdModalItem}>{selectedItem}</Text>
              <TouchableOpacity style={styles.thirdModalCloseButton} onPress={handleThirdModalClose}>
                <Text style={styles.thirdModalCloseButtonText}>닫기</Text>
              </TouchableOpacity>
            </View>
          </View>
        </Modal>

        {/* 에러 모달 */}
        <Modal transparent animationType="fade" visible={isErrorModalVisible}>
          <View style={styles.modalOverlay}>
            <View style={[styles.firstModalContainer, { paddingVertical: 30 }]}>
              <Text style={{ fontSize: 16, fontWeight: 'bold', marginBottom: 15, textAlign: 'center' }}>{errorMessage}</Text>
              <TouchableOpacity style={styles.errorModalButton} onPress={() => setIsErrorModalVisible(false)}>
                <Text style={styles.errorModalButtonText}>확인</Text>
              </TouchableOpacity>
            </View>
          </View>
        </Modal>
      </KeyboardAvoidingView>
    </SafeAreaView>
  );
};

const styles = StyleSheet.create({
  container: { flex: 1, backgroundColor: 'white' },
  headerContainer: { flexDirection: 'row', justifyContent: 'space-between', padding: 15 },
  headerLocation: { fontWeight: 'bold' },
  infoButton: {
    paddingHorizontal: 10, paddingVertical: 4,
    borderWidth: 1, borderColor: 'grey', borderRadius: 5,
  },
  voucherContainer: {
    margin: 15, padding: 25, borderWidth: 2, borderColor: 'green', borderRadius: 15,
  },
  voucherTitle: {
    color: 'green', fontWeight: 'bold', marginBottom: 15, fontSize: 20, textAlign: 'center',
  },
  totalAmountContainer: { flexDirection: 'row', alignItems: 'center', justifyContent: 'space-between' },
  totalAmountInput: {
    borderBottomWidth: 3, borderBottomColor: 'gray',
    textAlign: 'right', fontSize: 20, width: '60%',
    paddingVertical: 5,
  },
  itemContainer: {
    flexDirection: 'row', alignItems: 'center',
    padding: 15, borderBottomWidth: 1, borderBottomColor: '#eee',
  },
  itemName: { marginLeft: 10, flex: 1 },
  itemPrice: { fontWeight: 'bold' },
  radioButton: { width: 20, height: 20, borderRadius: 10, borderWidth: 2, borderColor: 'gray' },
  radioButtonSelected: { borderColor: 'green', backgroundColor: 'green' },
  paymentButton: {
    backgroundColor: '#4CAF50', paddingVertical: 15,
    alignItems: 'center', justifyContent: 'center',
    position: 'absolute', bottom: 0, width: '100%',
  },
  paymentButtonText: { color: 'white', fontSize: 18, fontWeight: 'bold' },
  modalOverlay: {
    flex: 1, justifyContent: 'center', alignItems: 'center',
    backgroundColor: 'rgba(0,0,0,0.5)',
  },
  firstModalContainer: {
    width: '90%', backgroundColor: 'white', borderRadius: 10,
    paddingVertical: 20, paddingHorizontal: 15, alignItems: 'center',
  },
  firstModalMessage: { fontSize: 16, fontWeight: 'bold', marginBottom: 15 },
  firstModalStoreName: { fontSize: 14, marginBottom: 5 },
  firstModalAmount: { fontSize: 14, marginBottom: 5 },
  firstModalItem: { fontSize: 14, marginBottom: 15 },
  firstModalButtonContainer: { flexDirection: 'row', justifyContent: 'space-between', width: '100%' },
  firstModalConfirmButton: {
    backgroundColor: '#4CAF50', flex: 1, marginRight: 10,
    paddingVertical: 12, borderRadius: 5, alignItems: 'center',
  },
  firstModalCancelButton: {
    backgroundColor: 'lightgrey', flex: 1,
    paddingVertical: 12, borderRadius: 5, alignItems: 'center',
  },
  firstModalConfirmButtonText: { color: 'white', fontWeight: 'bold' },
  firstModalCancelButtonText: { color: 'white', fontWeight: 'bold' },
  secondModalContainer: {
    width: '90%', backgroundColor: 'white', borderRadius: 10,
    paddingVertical: 20, paddingHorizontal: 15, alignItems: 'center',
  },
  secondModalTitle: { fontSize: 16, fontWeight: 'bold', marginBottom: 15 },
  secondModalAmountButton: {
    backgroundColor: '#4CAF50', width: '100%', paddingVertical: 20,
    borderRadius: 5, alignItems: 'center', marginBottom: 15,
  },
  secondModalAmountText: { color: 'white', fontSize: 20, fontWeight: 'bold' },
  secondModalAmountSubtext: { color: 'white', marginTop: 5 },
  secondModalStoreName: { fontSize: 14, marginBottom: 5 },
  secondModalItem: { fontSize: 14, marginBottom: 15 },
  secondModalCancelButton: {
    backgroundColor: 'lightgrey', width: '100%',
    paddingVertical: 12, borderRadius: 5, alignItems: 'center',
  },
  secondModalCancelButtonText: { color: 'white', fontWeight: 'bold' },
  thirdModalContainer: {
    width: '90%', backgroundColor: 'white', borderRadius: 10,
    paddingVertical: 20, paddingHorizontal: 15, alignItems: 'center',
  },
  thirdModalTitle: { fontSize: 16, fontWeight: 'bold', marginBottom: 15 },
  thirdModalAmount: {
    backgroundColor: '#4CAF50', color: 'white', fontSize: 24, fontWeight: 'bold',
    paddingVertical: 15, paddingHorizontal: 100, borderRadius: 5, marginBottom: 15,
  },
  thirdModalStoreName: { fontSize: 14, marginBottom: 5 },
  thirdModalItem: { fontSize: 14, marginBottom: 15 },
  thirdModalCloseButton: {
    backgroundColor: 'lightgrey', width: '100%',
    paddingVertical: 12, borderRadius: 5, alignItems: 'center',
  },
  thirdModalCloseButtonText: { color: 'white', fontWeight: 'bold' },
  errorModalButton: {
    backgroundColor: '#4CAF50', paddingVertical: 18,
    paddingHorizontal: 30, borderRadius: 8, alignItems: 'center', alignSelf: 'center',
  },
  errorModalButtonText: { color: 'white', fontWeight: 'bold', fontSize: 16 },
});

export default PaymentComponent;