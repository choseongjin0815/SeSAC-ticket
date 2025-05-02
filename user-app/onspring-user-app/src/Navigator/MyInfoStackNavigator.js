import React, { lazy } from 'react';
import { createStackNavigator } from '@react-navigation/stack';
import { Image, TouchableOpacity } from 'react-native';

const Stack = createStackNavigator();

const MyPage = lazy(() => import('../pages/myInfo/MyPage'));
const PointDetailPage = lazy(() => import('../pages/myInfo/PointDetailPage'));
const SettingPage = lazy(() => import('../pages/myInfo/SettingPage'));
const PasswordChangePage = lazy(() => import('../pages/myInfo/PasswordChangePage'));

const MyInfoStackNavigator = () => {
  return (
    <Stack.Navigator>
      <Stack.Screen
        name="MyPage"
        component={MyPage}
        options={({ navigation }) => ({
          title: "My 새싹",
          headerTitleAlign: "center",
          headerLeft: () => null,
          headerRight: () => (
            <TouchableOpacity onPress={() => navigation.navigate('SettingPage')}>
              <Image
                source={require('../../images/setting.png')} 
                style={{ width: 25, height: 25, marginRight: 10 }} 
              />
            </TouchableOpacity>
          ),
        })}
      />
      <Stack.Screen
        name="PointDetailPage"
        component={PointDetailPage}
        options={{
          title: "포인트 상세보기",
          headerTitleAlign: "center",
          headerBackTitle: false,
          headerStyle: { backgroundColor: '#4CAF50' },
          headerTintColor: '#FFFFFF',
        }}
      />
      <Stack.Screen
        name="SettingPage"
        component={SettingPage}
        options={{
          title: "환경설정",
          headerTitleAlign: "center",
          headerBackTitle: false,
          headerStyle: { backgroundColor: '#4CAF50' },
          headerTintColor: '#FFFFFF',
        }}
      />
      <Stack.Screen
        name="PasswordChangePage"
        component={PasswordChangePage}
        options={{
          title: "비밀번호 변경",
          headerTitleAlign: "center",
          headerBackTitle: false,
          headerStyle: { backgroundColor: '#4CAF50' },
          headerTintColor: '#FFFFFF',
        }}
      />
      
    </Stack.Navigator>
  );
};

export default MyInfoStackNavigator;