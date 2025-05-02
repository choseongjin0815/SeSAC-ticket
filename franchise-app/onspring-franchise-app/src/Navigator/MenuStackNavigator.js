import React, {lazy} from 'react';
import { createStackNavigator } from '@react-navigation/stack';

const Stack = createStackNavigator();

const Menu = lazy(() => import('../pages/menu/MenuPage'));

const MenuStackNavigator = () => {
    return (
        <Stack.Navigator>
            <Stack.Screen
                name="Menu"
                component={Menu}
                options={{ 
                title: "메뉴등록",
                headerBackTitle: false ,
                headerTitleAlign: "center",
                }}
            />    
        </Stack.Navigator>
    );
}

export default MenuStackNavigator;