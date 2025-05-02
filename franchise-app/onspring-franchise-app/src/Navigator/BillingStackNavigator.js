import React, {lazy} from 'react';
import { createStackNavigator } from '@react-navigation/stack';

const Stack = createStackNavigator();


const Billing = lazy(() => import('../pages/billing/BillingPage'));
const BillingDetail = lazy(() => import('../pages/billing/BillingDetailPage'));

const BillingStackNavigator = () => {
    return (
      <Stack.Navigator>
        <Stack.Screen
          name="Billing"
          component={Billing}
          options={{ 
            title: "정산확인",
            headerTitleAlign: "center",
            headerBackTitle: false 
           }}
        />
        <Stack.Screen
          name="BillingDetail"
          component={BillingDetail}
          options={{ 
            title: "정산내역",
            headerTitleAlign: "center",
            headerBackTitle: false 
           }}
        />
      </Stack.Navigator>
    );
  };
  
  export default BillingStackNavigator;