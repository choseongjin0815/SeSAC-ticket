import React, { Suspense, lazy } from 'react';
import { NavigationContainer, useNavigation } from '@react-navigation/native';
import { createStackNavigator } from '@react-navigation/stack';
import { createBottomTabNavigator } from '@react-navigation/bottom-tabs';
import { createMaterialTopTabNavigator } from '@react-navigation/material-top-tabs'; // 상단 탭 추가
import { View, Text, ActivityIndicator, StyleSheet, Image } from 'react-native';
import { TouchableOpacity } from 'react-native';
import TransactionStackNavigator from './src/Navigator/TransactionStackNavigator';
import MyInfoStackNavigator from './src/Navigator/MyInfoStackNavigator';
import FranchiseStackNavigator from './src/Navigator/FranchiseStackNavigator';


const Login = lazy(() => import('./src/pages/login/LoginPage'));
const Map = lazy(() => import('./src/pages/franchise/MapPage'));

// 로딩 화면 컴포넌트
const LoadingScreen = () => (
  <View style={styles.loadingContainer}>
    <ActivityIndicator size="large" color="#4a90e2" />
    <Text style={styles.loadingText}>로딩 중...</Text>
  </View>
);

// 탭 네비게이터
const Tab = createBottomTabNavigator();

// 상단 탭 네비게이터
const TopTab = createMaterialTopTabNavigator();

// 스택 네비게이터
const Stack = createStackNavigator();

// // 상단 탭 컨테이너
// const TopTabContainer = () => {
//   return (
//     <TopTab.Navigator initialRouteName="FranchiseTab">
//       <TopTab.Screen
//         name="FranchiseTab"
//         component={FranchiseStackNavigator}
//         options={{
//           tabBarLabel: '맛집',
//         }}
//       />
//       <TopTab.Screen
//         name="MapTab"
//         component={Map}  // 지도 화면
//         options={{
//           tabBarLabel: '지도',
//         }}
//       />
//     </TopTab.Navigator>
//   );
// };

// 탭 컨테이너
const TabContainer = () => {
  return (
    <Tab.Navigator 
      initialRouteName="FranchiseTab"
      screenOptions={{
        tabBarActiveTintColor: '#4CAF50',  // 활성화된 탭 아이콘 색상
        tabBarInactiveTintColor: '#BDBDBD',  // 비활성화된 탭 아이콘 색상
      }}
    >
      <Tab.Screen
        name="FranchiseTab"
        component={FranchiseStackNavigator}  // 상단 탭 네비게이터 사용
        options={{
          tabBarIcon: ({ color, size }) => (
            <Image
              source={require('./images/group.png')}
              style={{
                width: size * 1.3,
                height: size * 1.3,
                tintColor: color,
                resizeMode: 'contain',
              }}
            />
          ),
          headerShown: false,
          tabBarShowLabel: false,
        }}
      />
      <Tab.Screen
        name="TransactionTab"
        component={TransactionStackNavigator}
        options={{
          tabBarIcon: ({ color, size }) => (
            <Image
              source={require('./images/Transaction.png')}
              style={{
                width: size * 1.3,
                height: size * 1.3,
                tintColor: color,
                resizeMode: 'contain',
              }}
            />
          ),
          headerShown: false,
          tabBarShowLabel: false,
        }}
      />
      <Tab.Screen
        name="MyInfoTab"
        component={MyInfoStackNavigator}
        options={{
          tabBarIcon: ({ color, size }) => (
            <Image
              source={require('./images/myinfo.png')}
              style={{
                width: size * 1.3,
                height: size * 1.3,
                tintColor: color,
                resizeMode: 'contain',
              }}
            />
          ),
          headerShown: false,
          tabBarShowLabel: false,
        }}
      />
    </Tab.Navigator>
  );
};

// 메인 스택 네비게이터 (탭 네비게이터를 포함)
const MainStackNavigator = () => {
  const isLoggedIn = false; // 로그인 여부 담을 변수
  return (
    <Stack.Navigator initialRouteName={isLoggedIn ? "MainTab" : "Login"}>
      <Stack.Screen
        name="Login"
        component={Login}
        options={{ headerShown: false }}
      />
      <Stack.Screen
        name="MainTab"
        component={TabContainer}
        options={{ headerShown: false }}
      />
    </Stack.Navigator>
  );
};

// 메인 앱 컴포넌트
const App = () => {
  return (
    <NavigationContainer>
      <Suspense fallback={<LoadingScreen />}>
        <MainStackNavigator />
      </Suspense>
    </NavigationContainer>
  );
};

const styles = StyleSheet.create({
  loadingContainer: {
    flex: 1,
    justifyContent: 'center',
    alignItems: 'center',
    backgroundColor: '#fff',
  },
  loadingText: {
    marginTop: 10,
    fontSize: 16,
    color: '#666',
  },
});

export default App;