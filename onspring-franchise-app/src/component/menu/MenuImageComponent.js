import { API_SERVER_HOST, getMyInfo } from "../../api/myPageApi";
import { useState, useEffect } from "react";
import { View, Image, ScrollView, StyleSheet } from "react-native";
import { widthPercentageToDP as wp, heightPercentageToDP as hp } from 'react-native-responsive-screen';

const MenuImageComponent = () => {
    const [franchise, setFranchise] = useState(null);
    const host = API_SERVER_HOST;

    useEffect(() => {
        getMyInfo().then((data) => {
            setFranchise(data);
            console.log(data.uploadFileNames);
        });
    }, []);

    return (
        <ScrollView contentContainerStyle={styles.scrollContainer}>
            {franchise && franchise.uploadFileNames && franchise.uploadFileNames.map((imgFile, i) => (        
                <View key={i} style={styles.imageWrapper}>
                    <Image 
                        source={{ uri: `${host}/api/franchise/menu/${imgFile}` }} 
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

export default MenuImageComponent;