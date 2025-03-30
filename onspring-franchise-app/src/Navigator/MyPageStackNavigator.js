import React, {lazy} from 'react';
import { createStackNavigator } from '@react-navigation/stack';

const Stack = createStackNavigator();

const MyPage = lazy(() => import('../pages/myPage/MyPage'));
const InfoEditPage = lazy(() => import('../pages/myPage/MyInfoEditPage'));
const ChangePasswordPage = lazy(() => import('../pages/myPage/ChangePasswordPage'));

const MyPageStackNavigator = () => {
    return (
        <Stack.Navigator>
          <Stack.Screen
            name="MyPage"
            component={MyPage}
            options={{ 
              title: "나의 페이지",
              headerTitleAlign: "center",
              headerBackTitle: false 
             }}
          />
          <Stack.Screen
            name="InfoEditPage"
            component={InfoEditPage}
            options={{ 
              title: "나의 페이지",
              headerTitleAlign: "center",
              headerBackTitle: false 
             }}
          />
          <Stack.Screen
            name="ChangePasswordPage"
            component={ChangePasswordPage}
            options={{ 
              title: "비밀번호 변경",
              headerTitleAlign: "center",
              headerBackTitle: false 
             }}
          />
        
        </Stack.Navigator>
      );
}

export default MyPageStackNavigator;