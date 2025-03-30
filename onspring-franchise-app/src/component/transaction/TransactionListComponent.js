import React, { useState, useEffect } from "react";
import { View, Text, TouchableOpacity, StyleSheet, Image, Modal } from "react-native";
import { Calendar } from 'react-native-calendars'; 
import { format } from "date-fns";
import { cancelTransaction, getTransactionList } from "../../api/transactionApi"; 
import { ScrollView } from "react-native-gesture-handler";

const initState = {
  transactionDto: [],
};
const TransactionListComponent = () => {
    const [trigger, setTrigger] = useState(false);
    const [transactionData, setTransactionData] = useState({ ...initState });
    const [startDate, setStartDate] = useState(new Date());
    const [endDate, setEndDate] = useState(new Date());
    const [period, setPeriod] = useState("오늘");

    const [showStartCalendar, setShowStartCalendar] = useState(false);
    const [showEndCalendar, setShowEndCalendar] = useState(false);

  const handlePeriodSelection = (selectedPeriod) => {
    setPeriod(selectedPeriod);
  };

  const handleCancelTransaction = (transactionId) =>{
    console.log(transactionId);
    cancelTransaction(transactionId).then(() =>{
      setTrigger((prev) => !prev);
    })
  }

  useEffect(() => {
    if (period) {
      fetchTransactions();
      console.log("transaction: ", transactionData);
    }
  }, [period, trigger]);

  const fetchTransactions = async () => {
    // period, startDate, endDate를 API에 전달하여 트랜잭션 목록을 가져옵니다.
    const response = await getTransactionList({
      period,
      startDate: formatDateForCalendar(startDate),
      endDate: formatDateForCalendar(endDate),
    });
  
    setTransactionData({
      transactionDto: response,
    });
  };

  // 날짜 형식 변환 함수
  const formatDateForCalendar = (date) => {
    return format(date, 'yyyy.MM.dd');
  };

  const formatDateForDisplay = (date) => {
    return format(date, 'yyyy.MM.dd');
  };

  // 캘린더에서 날짜 선택 시 처리
  const handleStartDateSelect = (day) => {
    const selectedDate = new Date(day.dateString);
    setStartDate(selectedDate);
    setShowStartCalendar(false);
    setPeriod("");
  };

  const handleEndDateSelect = (day) => {
    const selectedDate = new Date(day.dateString);
    setEndDate(selectedDate);
    setShowEndCalendar(false);
  };

  return (
    <View style={styles.container}>
      {/* 조회 기간 설정 */}
      <View style={styles.dateContainer}>
        <Text style={styles.label}>조회 기간</Text>
        <View style={styles.datesRowContainer}>
          <View style={styles.dateInputContainer}>
            <TouchableOpacity onPress={() => setShowStartCalendar(true)} style={styles.dateInput}>
              <Text style={styles.dateText}>{formatDateForDisplay(startDate)}</Text>
            </TouchableOpacity>
            <TouchableOpacity onPress={() => setShowStartCalendar(true)} style={styles.calendarIconContainer}>
              <Image source={require('../../../images/calendar.png')} style={styles.calendarIcon} />
            </TouchableOpacity>
          </View>

          <Text style={styles.tildeText}>     ~</Text>

          <View style={styles.dateInputContainer}>
            <TouchableOpacity onPress={() => setShowEndCalendar(true)} style={styles.dateInput}>
              <Text style={styles.dateText}>{formatDateForDisplay(endDate)}</Text>
            </TouchableOpacity>
            <TouchableOpacity onPress={() => setShowEndCalendar(true)} style={styles.calendarIconContainer}>
              <Image source={require('../../../images/calendar.png')} style={styles.calendarIcon} />
            </TouchableOpacity>
          </View>
        </View>
      </View>

      {/* 시작 날짜 캘린더 모달 */}
      <Modal animationType="fade" transparent={true} visible={showStartCalendar} onRequestClose={() => setShowStartCalendar(false)}>
        <View style={styles.modalContainer}>
          <View style={styles.calendarContainer}>
            <View style={styles.calendarHeader}>
              <Text style={styles.calendarTitle}>시작 날짜 선택</Text>
              <TouchableOpacity onPress={() => setShowStartCalendar(false)}>
                <Text style={styles.closeButton}>X</Text>
              </TouchableOpacity>
            </View>
            <Calendar
              current={formatDateForCalendar(startDate)}
              onDayPress={handleStartDateSelect}
              markedDates={{
                [formatDateForCalendar(startDate)]: { selected: true, selectedColor: '#3366CC' },
              }}
              theme={{
                todayTextColor: '#3366CC',
                selectedDayBackgroundColor: '#3366CC',
                selectedDayTextColor: '#ffffff',
              }}
            />
          </View>
        </View>
      </Modal>

      {/* 종료 날짜 캘린더 모달 */}
      <Modal animationType="fade" transparent={true} visible={showEndCalendar} onRequestClose={() => setShowEndCalendar(false)}>
        <View style={styles.modalContainer}>
          <View style={styles.calendarContainer}>
            <View style={styles.calendarHeader}>
              <Text style={styles.calendarTitle}>종료 날짜 선택</Text>
              <TouchableOpacity onPress={() => setShowEndCalendar(false)}>
                <Text style={styles.closeButton}>X</Text>
              </TouchableOpacity>
            </View>
            <Calendar
              current={formatDateForCalendar(endDate)}
              onDayPress={handleEndDateSelect}
              markedDates={{
                [formatDateForCalendar(endDate)]: { selected: true, selectedColor: '#3366CC' },
              }}
              theme={{
                todayTextColor: '#3366CC',
                selectedDayBackgroundColor: '#3366CC',
                selectedDayTextColor: '#ffffff',
              }}
            />
          </View>
        </View>
      </Modal>

      

      {/* 버튼 */}
      <View style={styles.buttonContainer}>
        <TouchableOpacity style={styles.filterButton} onPress={() => handlePeriodSelection('오늘')}>
          <Text style={styles.buttonText}>오늘</Text>
        </TouchableOpacity>
        <TouchableOpacity style={styles.filterButton} onPress={() => handlePeriodSelection('최근1주')}>
          <Text style={styles.buttonText}>최근 1주</Text>
        </TouchableOpacity>
        <TouchableOpacity style={styles.filterButton} onPress={() => handlePeriodSelection('최근2주')}>
          <Text style={styles.buttonText}>최근 2주</Text>
        </TouchableOpacity>
        <TouchableOpacity style={styles.filterButton} onPress={() => handlePeriodSelection('최근3주')}>
          <Text style={styles.buttonText}>최근 3주</Text>
        </TouchableOpacity>
      </View>

      <TouchableOpacity style={styles.searchButton} onPress={fetchTransactions}>
        <Text style={styles.searchButtonText}>조회</Text>
      </TouchableOpacity>

      <View style={styles.tableContainer}>
        {/* 테이블 헤더 */}
        <View style={styles.tableHeader}>
          <Text style={[styles.tableHeaderText, styles.dateColumn]}>일시</Text>
          <Text style={[styles.tableHeaderText, styles.dateColumn]}>아이디</Text>
          <Text style={[styles.tableHeaderText, styles.dateColumn]}>금액</Text>
          <Text style={[styles.tableHeaderText, styles.dateColumn]}>결제상태</Text>
          <Text style={[styles.tableHeaderText, styles.dateColumn]}>결제취소</Text>
        </View>
        
        {/* 테이블 바디 */}
        <ScrollView style={styles.tableBody}>
          {transactionData.transactionDto && transactionData.transactionDto.map((item, index) => (
            <View key={index} style={styles.tableRow}>
              <Text style={[styles.cellText, styles.dateColumn]}>
                {new Date(item.transactionTime).toLocaleString()}
              </Text>
              <Text style={[styles.cellText, styles.dateColumn]}>{item.endUserDto.phone}</Text>
              <Text style={[styles.cellText, styles.dateColumn]}>{item.amount.toLocaleString()}</Text>
              {item.accepted ? 
                <Text style={[styles.cellText, styles.dateColumn]}>완료</Text>
                :
                <Text style={[styles.cellText, styles.dateColumn]}>취소</Text>
              }
              <View style={[styles.buttonCell, styles.dateColumn]}>
               
               {item.accepted && !item.closed &&
                <TouchableOpacity style={styles.detailButton} onPress={() => handleCancelTransaction(item.id)}>
                  <Text style={styles.detailButtonText}>취소</Text>
                </TouchableOpacity> 
               } 
                
              </View>      
            </View>
          ))}
        </ScrollView>
      </View>
    </View>
  );
};


const styles = StyleSheet.create({
  container: {
    padding: 16,
    backgroundColor: "#fff",
    flex: 1,
  },
  dateContainer: {
    marginBottom: 20,
    flexDirection: "row", // 텍스트와 캘린더가 가로로 배치되도록 설정
    alignItems: "center",
  },
  label: {
    fontSize: 16,
    fontWeight: "600",
    marginRight: 10, // 텍스트와 캘린더 사이의 간격을 조정
  },
  datesRowContainer: {
    flexDirection: "row",
    alignItems: "center",
    justifyContent: "center",
    marginTop: 10,
  },
  tildeText: {
    fontSize: 18,
    fontWeight: "800",
    marginHorizontal: 15,
  },
  dateInputContainer: {
    flexDirection: "row",
    alignItems: "center",
    position: "relative",
  },
  dateInput: {
    padding: 12,
    borderWidth: 1,
    borderColor: "#3366CC",
    borderRadius: 5,
    width: 120,
    alignItems: "center",
  },
  dateText: {
    color: "#000",
    fontSize: 14,
  },
  calendarIconContainer: {
    position: "absolute",
    right: -30, // 아이콘을 박스 밖으로 이동
    padding: 5,
  },
  calendarIcon: {
    width: 20,
    height: 20,
  },
  modalContainer: {
    flex: 1,
    justifyContent: "center",
    alignItems: "center",
    backgroundColor: "rgba(0, 0, 0, 0.5)",
  },
  calendarContainer: {
    width: "80%",
    backgroundColor: "white",
    borderRadius: 10,
    padding: 20,
    shadowColor: "#000",
    shadowOffset: {
      width: 0,
      height: 2,
    },
    shadowOpacity: 0.25,
    shadowRadius: 3.84,
    elevation: 5,
  },
  calendarHeader: {
    flexDirection: "row",
    justifyContent: "space-between",
    marginBottom: 10,
  },
  calendarTitle: {
    fontSize: 18,
    fontWeight: "bold",
  },
  closeButton: {
    fontSize: 18,
    fontWeight: "bold",
    color: "#666",
  },
  buttonContainer: {
    flexDirection: "row",
    justifyContent: "space-between",
    marginVertical: 10,
  },
  filterButton: {
    padding: 12,
    backgroundColor: "#fff",
    borderWidth: 1,
    borderColor: "#3366CC",
    borderRadius: 5,
    flex: 1,
    marginHorizontal: 3,
    alignItems: "center",
  },
  buttonText: {
    color: "#3366CC",
    fontSize: 14,
  },
  searchButton: {
    backgroundColor: "#3366CC",
    padding: 15,
    alignItems: "center",
    borderRadius: 5,
    marginVertical: 15,
  },
  searchButtonText: {
    color: "#fff",
    fontSize: 16,
    fontWeight: "600",
  },
  tableContainer: {
    flex: 1,
    borderWidth: 1,
    borderColor: "#ddd",
    borderRadius: 5,
  },
  tableHeader: {
    flexDirection: "row",
    // backgroundColor: "#f2f2f2",
    borderBottomWidth: 1,
    borderBottomColor: "#ddd",
    paddingVertical: 12,
  },
  tableHeaderText: {
    fontWeight: "bold",
    textAlign: "center",
  },
  tableBody: {
    flex: 1,
  },
  tableRow: {
    flexDirection: "row", // 가로로 배치
    paddingVertical: 15,
    borderBottomWidth: 1,
    borderBottomColor: "#eee",
    alignItems: "center", // 세로로 중앙 정렬
  },
  cellText: {
    textAlign: "center",
    verticalAlign: "middle",
    fontSize: 14,
  },
  buttonCell: {
    alignItems: "center",
    justifyContent: "center",
  },
  detailButton: {
    backgroundColor: "#ff4d4d",
    paddingHorizontal: 10,
    paddingVertical: 5,
    borderRadius: 5,
  },
  detailButtonText: {
    color: "#fff",
    fontSize: 12,
  },
  // 컬럼별 너비 설정
  numberColumn: {
    flex: 0.5, // 번호 컬럼은 좁게
    textAlign: "center",
  },
  dateColumn: {
    flex: 1.5, // 일시 컬럼은 넓게
    textAlign: "center",
  },
  idColumn: {
    flex: 1,
    textAlign: "center",
  },
  amountColumn: {
    flex: 1,
    textAlign: "right",
    paddingRight: 10,
  },
  statusColumn: {
    flex: 0.8,
    textAlign: "center",
  },
  cancelColumn: {
    flex: 0.8,
    textAlign: "center",
  },
});

export default TransactionListComponent;