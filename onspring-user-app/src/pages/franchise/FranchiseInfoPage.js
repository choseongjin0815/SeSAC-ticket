import FranchiseInfoComponent from "../../component/franchise/FranchiseInfoComponent";
import FranchiseMenuComponent from "../../component/franchise/FranchiseMenuComponent";
import { View, StyleSheet } from "react-native";
const FranchiseInfoPage = () => {

    return (
        <View style={styles.container}>
            <View style={styles.menuContainer}>
                <FranchiseMenuComponent/>
            </View>
            <View style={styles.infoContainer}>
                <FranchiseInfoComponent/>
            </View>
        </View>
    );    
}
const styles = StyleSheet.create({
    container: {
      flex: 1,
      backgroundColor: 'white',
    },
    menuContainer: {
      flex: 8.2, 
    },
    infoContainer: {
      flex: 1.8, 
    },
  });


export default FranchiseInfoPage;