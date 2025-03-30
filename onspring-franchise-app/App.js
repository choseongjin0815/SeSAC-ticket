import React, { Suspense, lazy, useEffect, useState } from 'react';
import { NavigationContainer } from '@react-navigation/native';
import { createStackNavigator } from '@react-navigation/stack';
import { createBottomTabNavigator } from '@react-navigation/bottom-tabs';
import { View, Text, ActivityIndicator, StyleSheet, Image } from 'react-native';
import { getFocusedRouteNameFromRoute } from '@react-navigation/native';
import { Provider, useSelector } from 'react-redux';
import store from './src/store';
import AsyncStorage from '@react-native-async-storage/async-storage';
import { restoreToken } from './src/auth/loginSlice';
import setupInterceptors from './src/auth/interceptor';
import jwtDecode from 'jwt-decode';

// 페이지 & 네비게이터
import BillingStackNavigator from './src/Navigator/BillingStackNavigator';
import MenuStackNavigator from './src/Navigator/MenuStackNavigator';
import MyPageStackNavigator from './src/Navigator/MyPageStackNavigator';
import TransactionStackNavigator from './src/Navigator/TransactionStackNavigator';

const Login = lazy(() => import('./src/pages/login/LoginPage'));
const HomePage = lazy(() => import('./src/pages/HomePage'));

// 로딩 화면
const LoadingScreen = () => (
  <View style={styles.loadingContainer}>
    <ActivityIndicator size="large" color="#4a90e2" />
    <Text style={styles.loadingText}>로딩 중...</Text>
  </View>
); 

// 네비게이터 설정
const Tab = createBottomTabNavigator();
const Stack = createStackNavigator();
const TabStack = createStackNavigator();

const InnerStackNavigator = () => (
  <TabStack.Navigator initialRouteName="Home">
    <TabStack.Screen name="Home" component={HomePage} options={{ headerShown: false }} />
    <TabStack.Screen name="Billing" component={BillingStackNavigator} options={{ headerShown: false }} />
    <TabStack.Screen name="Menu" component={MenuStackNavigator} options={{ headerShown: false }} />
    <TabStack.Screen name="MyPage" component={MyPageStackNavigator} options={{ headerShown: false }} />
    <TabStack.Screen name="Transaction" component={TransactionStackNavigator} options={{ headerShown: false }} />
  </TabStack.Navigator>
);

const TabContainer = () => (
  <Tab.Navigator>
    <Tab.Screen
      name="HomeTab"
      component={InnerStackNavigator}
      options={({ route }) => {
        const routeName = getFocusedRouteNameFromRoute(route) ?? 'Home';
        const tabHiddenRoutes = ['Home', 'Login'];

        return {
          headerShown: false,
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
          tabBarShowLabel: false,
          tabBarStyle: {
            display: tabHiddenRoutes.includes(routeName) ? 'none' : 'flex',
          },
        };
      }}
    />
  </Tab.Navigator>
);

// StackNavigator: accessToken 없어도 refreshToken 있으면 통과
const MainStackNavigator = ({ isReady }) => {
  const accessToken = useSelector((state) => state.login.accessToken);
  const refreshToken = useSelector((state) => state.login.refreshToken);

  if (!isReady) return null;

  return (
    <Stack.Navigator>
      {(accessToken || refreshToken) ? (
        <Stack.Screen name="MainTab" component={TabContainer} options={{ headerShown: false }} />
      ) : (
        <Stack.Screen name="Login" component={Login} options={{ title: "로그인", headerLeft: () => null }} />
      )}
    </Stack.Navigator>
  );
};

// 메인 App 컴포넌트
const App = () => {
  const [isAppReady, setIsAppReady] = useState(false);

  useEffect(() => {
    setupInterceptors(); // axios 인터셉터 연결

    const initApp = async () => {
      try {
        const [[, accessToken], [, refreshToken], [, id], [, tokenExp]] = await AsyncStorage.multiGet([
          'accessToken',
          'refreshToken',
          'id',
          'tokenExp',
        ]);

        const now = Date.now();

        // accessToken이 살아있거나 refreshToken이 있으면 복원
        if (refreshToken && id && (
          (accessToken && tokenExp && now < parseInt(tokenExp, 10)) || !accessToken
        )) {
          store.dispatch(restoreToken({ accessToken, refreshToken, id }));
        } else {
          await AsyncStorage.multiRemove(['accessToken', 'refreshToken', 'tokenExp', 'id']);
        }
      } catch (e) {
        console.log('토큰 복원 실패:', e);
      } finally {
        setIsAppReady(true);
      }
    };

    initApp();
  }, []);

  if (!isAppReady) return <LoadingScreen />;

  return (
    <Provider store={store}>
      <NavigationContainer>
        <Suspense fallback={<LoadingScreen />}>
          <MainStackNavigator isReady={isAppReady} />
        </Suspense>
      </NavigationContainer>
    </Provider>
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