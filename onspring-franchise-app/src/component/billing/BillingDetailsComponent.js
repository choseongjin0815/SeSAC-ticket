import React, { useState, useEffect, useRef } from "react";
import {
  View, Text, TouchableOpacity, StyleSheet, Image, Modal, ScrollView, ActivityIndicator
} from "react-native";
import { Calendar } from "react-native-calendars";
import { format } from "date-fns";
import { getTransactionList } from "../../api/transactionApi";
import { getSettlementList } from "../../api/transactionApi";

const initState = {
  settlementDto: [],
};

const BillingDetailsComponent = ({ month }) => {
  const [year, monthValue] = month.split(".");
  const [settlementData, setSettlementData] = useState({ ...initState });
  const [startDate, setStartDate] = useState(new Date(year, parseInt(monthValue) - 1, 1));
  const [endDate, setEndDate] = useState(new Date(year, parseInt(monthValue), 0));
  const [period, setPeriod] = useState();
  const [loading, setLoading] = useState(false);

  const [showStartCalendar, setShowStartCalendar] = useState(false);
  const [showEndCalendar, setShowEndCalendar] = useState(false);
  const scrollRef = useRef(null);

  useEffect(() => {
    fetchTransactions();
  }, []);

  useEffect(() => {
    if (period !== undefined) {
      fetchTransactions(true);
    }
  }, [period, startDate, endDate]);

  const fetchTransactions = async (scrollToTop = false) => {
    setLoading(true);
    try {
      const response = await getSettlementList({
     
        period,
        startDate: formatDateForCalendar(startDate),
        endDate: formatDateForCalendar(endDate),
      });
      setSettlementData({ settlementDto: response });
      if (scrollToTop && scrollRef.current) {
        setTimeout(() => {
          scrollRef.current.scrollTo({ y: 0, animated: false });
        }, 100);
      }
    } catch (e) {
      console.error("정산 내역 조회 실패:", e);
    } finally {
      setLoading(false);
    }
  };

  const handlePeriodSelection = (selectedPeriod) => {
    setPeriod(selectedPeriod);
  };

  const handleStartDateSelect = (day) => {
    const selectedDate = new Date(day.dateString);
    setStartDate(selectedDate);
    setShowStartCalendar(false);
  };

  const handleEndDateSelect = (day) => {
    const selectedDate = new Date(day.dateString);
    setEndDate(selectedDate);
    setShowEndCalendar(false);
  };

  const formatDateForCalendar = (date) => format(date, "yyyy.MM.dd");
  const formatDateForDisplay = (date) => format(date, "yyyy.MM.dd");

  return (
    <View style={styles.container}>
      <View style={styles.dateContainer}>
        <Text style={styles.label}>조회 기간</Text>
        <View style={styles.datesRowContainer}>
          <View style={styles.dateInputContainer}>
            <TouchableOpacity onPress={() => setShowStartCalendar(true)} style={styles.dateInput}>
              <Text style={styles.dateText}>{formatDateForDisplay(startDate)}</Text>
            </TouchableOpacity>
            <TouchableOpacity onPress={() => setShowStartCalendar(true)} style={styles.calendarIconContainer}>
              <Image source={require("../../../images/calendar.png")} style={styles.calendarIcon} />
            </TouchableOpacity>
          </View>

          <Text style={styles.tildeText}>    ~</Text>

          <View style={styles.dateInputContainer}>
            <TouchableOpacity onPress={() => setShowEndCalendar(true)} style={styles.dateInput}>
              <Text style={styles.dateText}>{formatDateForDisplay(endDate)}</Text>
            </TouchableOpacity>
            <TouchableOpacity onPress={() => setShowEndCalendar(true)} style={styles.calendarIconContainer}>
              <Image source={require("../../../images/calendar.png")} style={styles.calendarIcon} />
            </TouchableOpacity>
          </View>
        </View>
      </View>

      {/* 캘린더 모달 */}
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
          </View>
        </View>
      </Modal>

      <View style={styles.buttonContainer}>
        {["오늘", "최근1주", "최근2주", "최근3주"].map((label) => (
          <TouchableOpacity key={label} style={styles.filterButton} onPress={() => handlePeriodSelection(label)}>
            <Text style={styles.buttonText}>{label}</Text>
          </TouchableOpacity>
        ))}
      </View>

      <TouchableOpacity style={styles.searchButton} onPress={() => fetchTransactions(true)}>
        <Text style={styles.searchButtonText}>조회</Text>
      </TouchableOpacity>

      {loading && <ActivityIndicator size="large" color="#3366CC" style={{ marginVertical: 10 }} />}

      <View style={styles.tableContainer}>
        <View style={styles.tableHeader}>
          <Text style={[styles.tableHeaderText, styles.dateColumn]}>번호</Text>
          <Text style={[styles.tableHeaderText, styles.dateColumn]}>일시</Text>
          <Text style={[styles.tableHeaderText, styles.dateColumn]}>아이디</Text>
          <Text style={[styles.tableHeaderText, styles.dateColumn]}>금액</Text>
        </View>

        <ScrollView ref={scrollRef} style={styles.tableBody}>
          {settlementData.settlementDto?.map((item, index) => (
            <View key={index} style={styles.tableRow}>
              <Text style={[styles.cellText, styles.dateColumn]}>{item.id}</Text>
              <Text style={[styles.cellText, styles.dateColumn]}>
                {new Date(item.transactionTime).toLocaleString('ko-KR')}
              </Text>
              <Text style={[styles.cellText, styles.dateColumn]}>{item.endUserDto.phone}</Text>
              <Text style={[styles.cellText, styles.dateColumn]}>{item.amount.toLocaleString()}</Text>
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
  dateColumn: {
    flex: 1.5,
    textAlign: "center",
  },
});

export default BillingDetailsComponent;