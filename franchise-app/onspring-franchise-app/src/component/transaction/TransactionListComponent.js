import React, { useState, useEffect, useRef } from "react";
import {
  View, Text, TouchableOpacity, StyleSheet, Image, Modal, ActivityIndicator, ScrollView
} from "react-native";
import { Calendar } from "react-native-calendars";
import { format } from "date-fns";
import { cancelTransaction, getTransactionList } from "../../api/transactionApi";

const initState = {
  transactionDto: [],
};

const TransactionListComponent = () => {
  const [trigger, setTrigger] = useState(false);
  const [transactionData, setTransactionData] = useState({ ...initState });
  const [startDate, setStartDate] = useState(new Date());
  const [endDate, setEndDate] = useState(new Date());
  const [period, setPeriod] = useState("오늘");
  const [loading, setLoading] = useState(false);
  const [showStartCalendar, setShowStartCalendar] = useState(false);
  const [showEndCalendar, setShowEndCalendar] = useState(false);

  const [showCancelModal, setShowCancelModal] = useState(false); // 취소 모달 상태 추가
  const [selectedTransactionId, setSelectedTransactionId] = useState(null); // 선택된 거래 ID 저장

  const scrollRef = useRef(null); 

  const handlePeriodSelection = (selectedPeriod) => {
    setPeriod(selectedPeriod);
  };

  const handleCancelTransaction = (transactionId) => {
    setSelectedTransactionId(transactionId);
    setShowCancelModal(true); // 취소 모달 열기
  };

  const confirmCancelTransaction = () => {
    cancelTransaction(selectedTransactionId).then(() => {
      setShowCancelModal(false);
      setTrigger((prev) => !prev); // 취소 후 데이터 새로 고침
    }).catch((error) => {
      console.error("거래 취소 실패:", error);
      setShowCancelModal(false);
    });
  };

  useEffect(() => {
    if (period) {
      fetchTransactions();
    }
  }, [period, trigger]);

  const fetchTransactions = async () => {
    setLoading(true);
    try {
      const response = await getTransactionList({
        period,
        startDate: formatDateForCalendar(startDate),
        endDate: formatDateForCalendar(endDate),
      });
      setTransactionData({ transactionDto: response });

      setTimeout(() => {
        if (scrollRef.current) {
          scrollRef.current.scrollTo({ y: 0, animated: false });
        }
      }, 100);
    } catch (e) {
      console.error("거래내역 조회 실패:", e);
    } finally {
      setLoading(false);
    }
  };

  const formatDateForCalendar = (date) => format(date, "yyyy.MM.dd");
  const formatDateForDisplay = (date) => format(date, "yyyy.MM.dd");

  const handleStartDateSelect = (day) => {
    setStartDate(new Date(day.dateString));
    setShowStartCalendar(false);
    setPeriod("");
  };

  const handleEndDateSelect = (day) => {
    setEndDate(new Date(day.dateString));
    setShowEndCalendar(false);
  };

  return (
    <View style={styles.container}>
      {/* 조회 기간 선택 UI */}
      <View style={styles.dateContainer}>
        <Text style={styles.label}>    </Text>
        <View style={styles.datesRowContainer}>
          {/* 시작 날짜 */}
          <View style={styles.dateInputContainer}>
            <TouchableOpacity onPress={() => setShowStartCalendar(true)} style={styles.dateInput}>
              <Text style={styles.dateText}>{formatDateForDisplay(startDate)}</Text>
            </TouchableOpacity>
            <TouchableOpacity onPress={() => setShowStartCalendar(true)} style={styles.calendarIconContainer}>
              <Image source={require('../../../images/calendar.png')} style={styles.calendarIcon} />
            </TouchableOpacity>
          </View>

          <Text style={styles.tildeText}>    ~</Text>

          {/* 종료 날짜 */}
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

      {/* 캘린더 모달들 */}
      <Modal transparent visible={showStartCalendar} animationType="fade">
        <View style={styles.modalContainer}>
          <View style={styles.calendarContainer}>
            <View style={styles.calendarHeader}>
              <Text style={styles.calendarTitle}>시작 날짜 선택</Text>
              <TouchableOpacity onPress={() => setShowStartCalendar(false)}>
                <Text style={styles.closeButton}>X</Text>
              </TouchableOpacity>
            </View>
            <Calendar
              current={format(startDate, 'yyyy-MM-dd')}
              onDayPress={handleStartDateSelect}
              markedDates={{
                [format(startDate, 'yyyy-MM-dd')]: { selected: true, selectedColor: '#3366CC' },
              }}
              theme={{ todayTextColor: '#3366CC' }}
            />
            {/* <Calendar
              current={format(startDate,)}
              onDayPress={handleStartDateSelect}
              markedDates={{
                [formatDateForCalendar(startDate)]: { selected: true, selectedColor: '#3366CC' },
              }}
              theme={{ todayTextColor: '#3366CC' }}
            /> */}
          </View>
        </View>
      </Modal>

      <Modal transparent visible={showEndCalendar} animationType="fade">
        <View style={styles.modalContainer}>
          <View style={styles.calendarContainer}>
            <View style={styles.calendarHeader}>
              <Text style={styles.calendarTitle}>종료 날짜 선택</Text>
              <TouchableOpacity onPress={() => setShowEndCalendar(false)}>
                <Text style={styles.closeButton}>X</Text>
              </TouchableOpacity>
            </View>
            <Calendar
              current={format(endDate, 'yyyy-MM-dd')}
              onDayPress={handleEndDateSelect}
              markedDates={{
                [format(endDate, 'yyyy-MM-dd')]: { selected: true, selectedColor: '#3366CC' },
              }}
              theme={{ todayTextColor: '#3366CC' }}
            />
            {/* <Calendar
              current={formatDateForCalendar(endDate)}
              onDayPress={handleEndDateSelect}
              markedDates={{
                [formatDateForCalendar(endDate)]: { selected: true, selectedColor: '#3366CC' },
              }}
              theme={{ todayTextColor: '#3366CC' }}
            /> */}
          </View>
        </View>
      </Modal>

      {/* 결제 취소 모달 */}
      <Modal transparent visible={showCancelModal} animationType="fade">
        <View style={styles.modalContainer}>
          <View style={styles.cancelModalContainer}>
            <Text style={styles.cancelModalTitle}>정말 결제 취소를 하시겠습니까?</Text>
            <View style={styles.cancelModalButtons}>
              <TouchableOpacity onPress={() => setShowCancelModal(false)} style={styles.cancelButton}>
                <Text style={styles.cancelButtonText}>취소</Text>
              </TouchableOpacity>
              <TouchableOpacity onPress={confirmCancelTransaction} style={styles.confirmButton}>
                <Text style={styles.confirmButtonText}>확인</Text>
              </TouchableOpacity>
            </View>
          </View>
        </View>
      </Modal>

      {/* 필터 버튼 */}
      <View style={styles.buttonContainer}>
        {["오늘", "최근1주", "최근2주", "최근3주"].map((label) => (
          <TouchableOpacity key={label} style={styles.filterButton} onPress={() => handlePeriodSelection(label)}>
            <Text style={styles.buttonText}>{label}</Text>
          </TouchableOpacity>
        ))}
      </View>

      <TouchableOpacity style={styles.searchButton} onPress={fetchTransactions}>
        <Text style={styles.searchButtonText}>조회</Text>
      </TouchableOpacity>

      {/* 로딩 인디케이터 */}
      {loading && <ActivityIndicator size="large" color="#3366CC" style={{ marginVertical: 10 }} />}

      {/* 거래내역 테이블 */}
      <View style={styles.tableContainer}>
        <View style={styles.tableHeader}>
          <Text style={[styles.tableHeaderText, styles.dateColumn]}>일시</Text>
          <Text style={[styles.tableHeaderText, styles.dateColumn]}>아이디</Text>
          <Text style={[styles.tableHeaderText, styles.dateColumn]}>금액</Text>
          <Text style={[styles.tableHeaderText, styles.dateColumn]}>결제상태</Text>
          <Text style={[styles.tableHeaderText, styles.dateColumn]}>결제취소</Text>
        </View>

        <ScrollView ref={scrollRef} style={styles.tableBody}>
          {transactionData.transactionDto?.map((item, index) => (
            <View key={index} style={styles.tableRow}>
              <Text style={[styles.cellText, styles.dateColumn]}>
                {new Date(item.transactionTime).toLocaleString('ko-KR')}
              </Text>
              <Text style={[styles.cellText, styles.dateColumn]}>{item.endUserDto.phone}</Text>
              <Text style={[styles.cellText, styles.dateColumn]}>{item.amount.toLocaleString()}</Text>
              <Text style={[styles.cellText, styles.dateColumn]}>{item.accepted ? "완료" : "취소"}</Text>
              <View style={[styles.buttonCell, styles.dateColumn]}>
                {item.accepted && !item.closed && (
                  <TouchableOpacity style={styles.detailButton} onPress={() => handleCancelTransaction(item.id)}>
                    <Text style={styles.detailButtonText}>취소</Text>
                  </TouchableOpacity>
                )}
              </View>
            </View>
          ))}
        </ScrollView>
      </View>
    </View>
  );
};

// 스타일 추가
const styles = StyleSheet.create({
  container: {
    padding: 16,
    backgroundColor: "#fff",
    flex: 1,
  },
  dateContainer: {
    marginBottom: 20,
    flexDirection: "row",
    alignItems: "center",
  },
  label: {
    fontSize: 16,
    fontWeight: "600",
    marginRight: 10,
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
    right: -30,
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
    shadowOffset: { width: 0, height: 2 },
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
    flexDirection: "row",
    paddingVertical: 15,
    borderBottomWidth: 1,
    borderBottomColor: "#eee",
    alignItems: "center",
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
  numberColumn: {
    flex: 0.5,
    textAlign: "center",
  },
  dateColumn: {
    flex: 1.5,
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
  cancelModalContainer: {
    width: "80%",
    backgroundColor: "white",
    borderRadius: 10,
    padding: 20,
    alignItems: "center",
  },
  cancelModalTitle: {
    fontSize: 18,
    fontWeight: "bold",
    marginBottom: 20,
  },
  cancelModalButtons: {
    flexDirection: "row",
    justifyContent: "space-between",
    width: "100%",
  },
  cancelButton: {
    backgroundColor: "#ccc",
    padding: 10,
    borderRadius: 5,
    width: "45%",
    alignItems: "center",
  },
  cancelButtonText: {
    color: "#fff",
    fontSize: 16,
  },
  confirmButton: {
    backgroundColor: "#ff4d4d",
    padding: 10,
    borderRadius: 5,
    width: "45%",
    alignItems: "center",
  },
  confirmButtonText: {
    color: "#fff",
    fontSize: 16,
  },
});

export default TransactionListComponent;
// import React, { useState, useEffect, useRef } from "react";
// import {
//   View, Text, TouchableOpacity, StyleSheet, Image, Modal, ActivityIndicator, ScrollView
// } from "react-native";
// import { Calendar } from "react-native-calendars";
// import { format } from "date-fns";
// import { cancelTransaction, getTransactionList } from "../../api/transactionApi";

// const initState = {
//   transactionDto: [],
// };

// const TransactionListComponent = () => {
//   const [trigger, setTrigger] = useState(false);
//   const [transactionData, setTransactionData] = useState({ ...initState });
//   const [startDate, setStartDate] = useState(new Date());
//   const [endDate, setEndDate] = useState(new Date());
//   const [period, setPeriod] = useState("오늘");
//   const [loading, setLoading] = useState(false);

//   const [showStartCalendar, setShowStartCalendar] = useState(false);
//   const [showEndCalendar, setShowEndCalendar] = useState(false);

//   const scrollRef = useRef(null); 

//   const handlePeriodSelection = (selectedPeriod) => {
//     setPeriod(selectedPeriod);
//   };

//   const handleCancelTransaction = (transactionId) => {
//     cancelTransaction(transactionId).then(() => {
//       setTrigger((prev) => !prev);
//     });
//   };

//   useEffect(() => {
//     if (period) {
//       fetchTransactions();
//     }
//   }, [period, trigger]);

//   const fetchTransactions = async () => {
//     setLoading(true);
//     try {
//       const response = await getTransactionList({
//         period,
//         startDate: formatDateForCalendar(startDate),
//         endDate: formatDateForCalendar(endDate),
//       });
//       setTransactionData({ transactionDto: response });

//       setTimeout(() => {
//         if (scrollRef.current) {
//           scrollRef.current.scrollTo({ y: 0, animated: false});
//         }
//       }, 100);
//     } catch (e) {
//       console.error("거래내역 조회 실패:", e);
//     } finally {
//       setLoading(false);
//     }
//   };

//   const formatDateForCalendar = (date) => format(date, "yyyy.MM.dd");
//   const formatDateForDisplay = (date) => format(date, "yyyy.MM.dd");

//   const handleStartDateSelect = (day) => {
//     setStartDate(new Date(day.dateString));
//     setShowStartCalendar(false);
//     setPeriod("");
//   };

//   const handleEndDateSelect = (day) => {
//     setEndDate(new Date(day.dateString));
//     setShowEndCalendar(false);
//   };

//   return (
//     <View style={styles.container}>
//       {/* 조회 기간 선택 UI */}
//       <View style={styles.dateContainer}>
//         <Text style={styles.label}>조회 기간</Text>
//         <View style={styles.datesRowContainer}>
//           {/* 시작 날짜 */}
//           <View style={styles.dateInputContainer}>
//             <TouchableOpacity onPress={() => setShowStartCalendar(true)} style={styles.dateInput}>
//               <Text style={styles.dateText}>{formatDateForDisplay(startDate)}</Text>
//             </TouchableOpacity>
//             <TouchableOpacity onPress={() => setShowStartCalendar(true)} style={styles.calendarIconContainer}>
//               <Image source={require('../../../images/calendar.png')} style={styles.calendarIcon} />
//             </TouchableOpacity>
//           </View>

//           <Text style={styles.tildeText}>    ~</Text>

//           {/* 종료 날짜 */}
//           <View style={styles.dateInputContainer}>
//             <TouchableOpacity onPress={() => setShowEndCalendar(true)} style={styles.dateInput}>
//               <Text style={styles.dateText}>{formatDateForDisplay(endDate)}</Text>
//             </TouchableOpacity>
//             <TouchableOpacity onPress={() => setShowEndCalendar(true)} style={styles.calendarIconContainer}>
//               <Image source={require('../../../images/calendar.png')} style={styles.calendarIcon} />
//             </TouchableOpacity>
//           </View>
//         </View>
//       </View>

//       {/* 캘린더 모달들 */}
//       <Modal transparent visible={showStartCalendar} animationType="fade">
//         <View style={styles.modalContainer}>
//           <View style={styles.calendarContainer}>
//             <View style={styles.calendarHeader}>
//               <Text style={styles.calendarTitle}>시작 날짜 선택</Text>
//               <TouchableOpacity onPress={() => setShowStartCalendar(false)}>
//                 <Text style={styles.closeButton}>X</Text>
//               </TouchableOpacity>
//             </View>
//             <Calendar
//               current={formatDateForCalendar(startDate)}
//               onDayPress={handleStartDateSelect}
//               markedDates={{
//                 [formatDateForCalendar(startDate)]: { selected: true, selectedColor: '#3366CC' },
//               }}
//               theme={{ todayTextColor: '#3366CC' }}
//             />
//           </View>
//         </View>
//       </Modal>

//       <Modal transparent visible={showEndCalendar} animationType="fade">
//         <View style={styles.modalContainer}>
//           <View style={styles.calendarContainer}>
//             <View style={styles.calendarHeader}>
//               <Text style={styles.calendarTitle}>종료 날짜 선택</Text>
//               <TouchableOpacity onPress={() => setShowEndCalendar(false)}>
//                 <Text style={styles.closeButton}>X</Text>
//               </TouchableOpacity>
//             </View>
//             <Calendar
//               current={formatDateForCalendar(endDate)}
//               onDayPress={handleEndDateSelect}
//               markedDates={{
//                 [formatDateForCalendar(endDate)]: { selected: true, selectedColor: '#3366CC' },
//               }}
//               theme={{ todayTextColor: '#3366CC' }}
//             />
//           </View>
//         </View>
//       </Modal>

//       {/* 필터 버튼 */}
//       <View style={styles.buttonContainer}>
//         {["오늘", "최근1주", "최근2주", "최근3주"].map((label) => (
//           <TouchableOpacity key={label} style={styles.filterButton} onPress={() => handlePeriodSelection(label)}>
//             <Text style={styles.buttonText}>{label}</Text>
//           </TouchableOpacity>
//         ))}
//       </View>

//       <TouchableOpacity style={styles.searchButton} onPress={fetchTransactions}>
//         <Text style={styles.searchButtonText}>조회</Text>
//       </TouchableOpacity>

//       {/* 로딩 인디케이터 */}
//       {loading && <ActivityIndicator size="large" color="#3366CC" style={{ marginVertical: 10 }} />}

//       {/* 거래내역 테이블 */}
//       <View style={styles.tableContainer}>
//         <View style={styles.tableHeader}>
//           <Text style={[styles.tableHeaderText, styles.dateColumn]}>일시</Text>
//           <Text style={[styles.tableHeaderText, styles.dateColumn]}>아이디</Text>
//           <Text style={[styles.tableHeaderText, styles.dateColumn]}>금액</Text>
//           <Text style={[styles.tableHeaderText, styles.dateColumn]}>결제상태</Text>
//           <Text style={[styles.tableHeaderText, styles.dateColumn]}>결제취소</Text>
//         </View>

//         <ScrollView ref={scrollRef} style={styles.tableBody}>
//           {transactionData.transactionDto?.map((item, index) => (
//             <View key={index} style={styles.tableRow}>
//               <Text style={[styles.cellText, styles.dateColumn]}>
//                 {new Date(item.transactionTime).toLocaleString('ko-KR')}
//               </Text>
//               <Text style={[styles.cellText, styles.dateColumn]}>{item.endUserDto.phone}</Text>
//               <Text style={[styles.cellText, styles.dateColumn]}>{item.amount.toLocaleString()}</Text>
//               <Text style={[styles.cellText, styles.dateColumn]}>{item.accepted ? "완료" : "취소"}</Text>
//               <View style={[styles.buttonCell, styles.dateColumn]}>
//                 {item.accepted && !item.closed && (
//                   <TouchableOpacity style={styles.detailButton} onPress={() => handleCancelTransaction(item.id)}>
//                     <Text style={styles.detailButtonText}>취소</Text>
//                   </TouchableOpacity>
//                 )}
//               </View>
//             </View>
//           ))}
//         </ScrollView>
//       </View>
//     </View>
//   );
// };

// const styles = StyleSheet.create({
//   container: {
//     padding: 16,
//     backgroundColor: "#fff",
//     flex: 1,
//   },
//   dateContainer: {
//     marginBottom: 20,
//     flexDirection: "row",
//     alignItems: "center",
//   },
//   label: {
//     fontSize: 16,
//     fontWeight: "600",
//     marginRight: 10,
//   },
//   datesRowContainer: {
//     flexDirection: "row",
//     alignItems: "center",
//     justifyContent: "center",
//     marginTop: 10,
//   },
//   tildeText: {
//     fontSize: 18,
//     fontWeight: "800",
//     marginHorizontal: 15,
//   },
//   dateInputContainer: {
//     flexDirection: "row",
//     alignItems: "center",
//     position: "relative",
//   },
//   dateInput: {
//     padding: 12,
//     borderWidth: 1,
//     borderColor: "#3366CC",
//     borderRadius: 5,
//     width: 120,
//     alignItems: "center",
//   },
//   dateText: {
//     color: "#000",
//     fontSize: 14,
//   },
//   calendarIconContainer: {
//     position: "absolute",
//     right: -30,
//     padding: 5,
//   },
//   calendarIcon: {
//     width: 20,
//     height: 20,
//   },
//   modalContainer: {
//     flex: 1,
//     justifyContent: "center",
//     alignItems: "center",
//     backgroundColor: "rgba(0, 0, 0, 0.5)",
//   },
//   calendarContainer: {
//     width: "80%",
//     backgroundColor: "white",
//     borderRadius: 10,
//     padding: 20,
//     shadowColor: "#000",
//     shadowOffset: { width: 0, height: 2 },
//     shadowOpacity: 0.25,
//     shadowRadius: 3.84,
//     elevation: 5,
//   },
//   calendarHeader: {
//     flexDirection: "row",
//     justifyContent: "space-between",
//     marginBottom: 10,
//   },
//   calendarTitle: {
//     fontSize: 18,
//     fontWeight: "bold",
//   },
//   closeButton: {
//     fontSize: 18,
//     fontWeight: "bold",
//     color: "#666",
//   },
//   buttonContainer: {
//     flexDirection: "row",
//     justifyContent: "space-between",
//     marginVertical: 10,
//   },
//   filterButton: {
//     padding: 12,
//     backgroundColor: "#fff",
//     borderWidth: 1,
//     borderColor: "#3366CC",
//     borderRadius: 5,
//     flex: 1,
//     marginHorizontal: 3,
//     alignItems: "center",
//   },
//   buttonText: {
//     color: "#3366CC",
//     fontSize: 14,
//   },
//   searchButton: {
//     backgroundColor: "#3366CC",
//     padding: 15,
//     alignItems: "center",
//     borderRadius: 5,
//     marginVertical: 15,
//   },
//   searchButtonText: {
//     color: "#fff",
//     fontSize: 16,
//     fontWeight: "600",
//   },
//   tableContainer: {
//     flex: 1,
//     borderWidth: 1,
//     borderColor: "#ddd",
//     borderRadius: 5,
//   },
//   tableHeader: {
//     flexDirection: "row",
//     borderBottomWidth: 1,
//     borderBottomColor: "#ddd",
//     paddingVertical: 12,
//   },
//   tableHeaderText: {
//     fontWeight: "bold",
//     textAlign: "center",
//   },
//   tableBody: {
//     flex: 1,
//   },
//   tableRow: {
//     flexDirection: "row",
//     paddingVertical: 15,
//     borderBottomWidth: 1,
//     borderBottomColor: "#eee",
//     alignItems: "center",
//   },
//   cellText: {
//     textAlign: "center",
//     verticalAlign: "middle",
//     fontSize: 14,
//   },
//   buttonCell: {
//     alignItems: "center",
//     justifyContent: "center",
//   },
//   detailButton: {
//     backgroundColor: "#ff4d4d",
//     paddingHorizontal: 10,
//     paddingVertical: 5,
//     borderRadius: 5,
//   },
//   detailButtonText: {
//     color: "#fff",
//     fontSize: 12,
//   },
//   numberColumn: {
//     flex: 0.5,
//     textAlign: "center",
//   },
//   dateColumn: {
//     flex: 1.5,
//     textAlign: "center",
//   },
//   idColumn: {
//     flex: 1,
//     textAlign: "center",
//   },
//   amountColumn: {
//     flex: 1,
//     textAlign: "right",
//     paddingRight: 10,
//   },
//   statusColumn: {
//     flex: 0.8,
//     textAlign: "center",
//   },
//   cancelColumn: {
//     flex: 0.8,
//     textAlign: "center",
//   },
// });

// export default TransactionListComponent;
