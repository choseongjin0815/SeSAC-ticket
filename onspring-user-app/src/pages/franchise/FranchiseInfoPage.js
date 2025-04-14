import FranchiseInfoComponent from "../../component/franchise/FranchiseInfoComponent";
import FranchiseMenuComponent from "../../component/franchise/FranchiseMenuComponent";
import { View, StyleSheet } from "react-native";
import { useRoute } from "@react-navigation/native";

const FranchiseInfoPage = () => {
    const route = useRoute();
    const {franchiseId} = route.params || {};
    return (
        <View style={styles.container}>
            <View style={styles.menuContainer}>
                <FranchiseMenuComponent frachiseId={franchiseId}/>
            </View>
            <View style={styles.infoContainer}>
                <FranchiseInfoComponent franchiseId={franchiseId}/>
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