import React, {lazy} from 'react';
import { createStackNavigator } from '@react-navigation/stack';

const Stack = createStackNavigator();

const Transaction = lazy(() => import('../pages/transaction/TransactionPage'));

const TransactionStackNavigator = () => {
    return (
        <Stack.Navigator>
            <Stack.Screen
                name="Transaction"
                component={Transaction}
                options={{ 
                title: "결제내역",
                headerTitleAlign: "center",
                headerBackTitle: false 
                }}
            />    
        </Stack.Navigator>
    );
    
}

export default TransactionStackNavigator;