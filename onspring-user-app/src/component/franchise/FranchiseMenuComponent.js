import { useState, useEffect } from "react";
import { useRoute } from "@react-navigation/native";
import { View, Image, ScrollView, StyleSheet } from "react-native";
import { widthPercentageToDP as wp, heightPercentageToDP as hp } from 'react-native-responsive-screen';
import { API_SERVER_HOST, getFranchiseInfo } from "../../api/franchiseApi";

const FranchiseMenuComponent = () => {
    const route = useRoute();
    const { franchiseId } = route.params || {};
    const host = API_SERVER_HOST;

    const [franchise, setFranchise] = useState(null);

    useEffect(() => {
        const fetchData = async () => {
          if (!franchiseId) return;
          try {
            const info = await getFranchiseInfo(franchiseId);
            setFranchise(info);
          } catch (error) {
            console.error("데이터 불러오기 실패", error);
          }
        };
    
        fetchData();
    }, [franchiseId]);

    return (
        <ScrollView contentContainerStyle={styles.scrollContainer}>
            {franchise && franchise.uploadFileNames && franchise.uploadFileNames.map((imgFile, i) => (        
                <View key={i} style={styles.imageWrapper}>
                    <Image 
                        source={{ uri: `${host}/api/user/menu/${imgFile}` }} 
                        style={styles.image}
                        resizeMode="contain"
                    />
                </View>
            ))}
        </ScrollView>
    );
}

const styles = StyleSheet.create({
    scrollContainer: {
        flexGrow: 1,
        alignItems: 'center',
        paddingVertical: hp('2%'),
    },
    imageWrapper: {
        marginBottom: hp('2%'),  // 이미지 간격 조절
    },
    image: {
        width: wp('90%'),  // 화면 너비의 80%
        height: hp('80%'), // 화면 높이의 30%
        maxWidth: 600,  
        maxHeight: 600,
    }
});

export default FranchiseMenuComponent;