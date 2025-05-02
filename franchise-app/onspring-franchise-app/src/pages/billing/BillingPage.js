import { useNavigation } from "@react-navigation/native";
import React, {useState, useEffect} from "react";
import { View, Text, ScrollView, TouchableOpacity, StyleSheet } from "react-native";
import { widthPercentageToDP as wp, heightPercentageToDP as hp } from "react-native-responsive-screen";
import { getSettlements } from "../../api/settlementApi";

const initState = {
    settlementSummaryDto : [],
}


const BillingPage = () => {

    const navigation = useNavigation();

    const [summaryData, setSummaryData] = useState({...initState}); 

    const handleDetail = (month) => {
        console.log(month);
        navigation.navigate('BillingDetail', {month});
    }

    useEffect(() => {
            getSettlements().then(data => {
                setSummaryData({
                    settlementSummaryDto: data
                });
            })
    }, []);
        
    

    return (
        <View style={styles.container}>
            {/* 정산 내역 테이블 */}
            <View style={styles.tableContainer}>
                <View style={styles.tableHeader}>
                    <Text style={styles.columnHeader}>정산월</Text>
                    <Text style={styles.columnHeader}>건수</Text>
                    <Text style={styles.columnHeader}>금액</Text>
                    <Text style={styles.columnHeader}>정산내역</Text>
                </View>

                {/* 컴포넌트화 해야함 */}
                <ScrollView style={styles.tableBody}>
                    {summaryData.settlementSummaryDto && summaryData.settlementSummaryDto.map((item, index) => (
                    
                        <View key={index} style={styles.tableRow}>
                            <Text style={styles.cellText}>
                                {item.year}-{item.month < 10 ? `0${item.month}` : item.month}
                            </Text>
                            <Text style={styles.cellText}>{item.totalTransactions}</Text>
                            <Text style={styles.cellText}>{item.totalAmount.toLocaleString()}</Text>
                            <View style={styles.buttonCell}>
                                <TouchableOpacity style={styles.detailButton} onPress={() => handleDetail(`${item.year}.${item.month < 10 ? `0${item.month}` : item.month}`)}>
                                    <Text style={styles.detailButtonText}>상세확인</Text>
                                </TouchableOpacity>
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
        flex: 1,
        backgroundColor: "#fff",
    },
    tableContainer: {
        flex: 1,
    },
    tableHeader: {
        flexDirection: "row",
        borderBottomWidth: 1,
        borderBottomColor: "#e0e0e0",
        paddingVertical: hp("1.5%"),
    },
    columnHeader: {
        flex: 1,
        textAlign: "center",
        fontWeight: "bold",
        fontSize: 16,
    },
    tableBody: {
        flex: 1,
    },
    tableRow: {
        flexDirection: "row",
        paddingVertical: hp("1.5%"),
        borderBottomWidth: 1,
        borderBottomColor: "#e0e0e0",
    },
    cellText: {
        flex: 1,
        textAlign: "center",
        fontSize: 16,
        alignSelf: "center",
    },
    buttonCell: {
        flex: 1,
        alignItems: "center",
        justifyContent: "center",
    },
    detailButton: {
        backgroundColor: "#f0f0f0",
        paddingVertical: 6,
        paddingHorizontal: 12,
        borderRadius: 4,
        borderWidth: 1,
        borderColor: "#ddd",
    },
    detailButtonText: {
        color: "#333",
        fontSize: 14,
    },
});

export default BillingPage;