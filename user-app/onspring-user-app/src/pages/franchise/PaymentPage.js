import PaymentComponent from "../../component/franchise/PaymentComponent";
import { useRoute } from "@react-navigation/native";

const PaymentPage = () => {
    const route = useRoute();
    const {franchiseId} = route.params || {};
    return (
        <PaymentComponent franchiseId={franchiseId}/>
    );
}

export default PaymentPage;