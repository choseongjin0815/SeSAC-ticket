import React, { Suspense, lazy, useEffect, useState } from 'react';
import { NavigationContainer } from '@react-navigation/native';
import { createStackNavigator } from '@react-navigation/stack';
import { createBottomTabNavigator } from '@react-navigation/bottom-tabs';
import { View, Text, ActivityIndicator, StyleSheet, Image } from 'react-native';
import { Provider, useSelector } from 'react-redux';
import AsyncStorage from '@react-native-async-storage/async-storage';
import { restoreToken, refreshToken as refreshTokenAction } from './src/auth/loginSlice';
import store from './src/store';
import setupInterceptors from './src/auth/interceptor';
import TransactionStackNavigator from './src/Navigator/TransactionStackNavigator';
import MyInfoStackNavigator from './src/Navigator/MyInfoStackNavigator';
import FranchiseStackNavigator from './src/Navigator/FranchiseStackNavigator';

const Login = lazy(() => import('./src/pages/login/LoginPage'));
const Map = lazy(() => import('./src/pages/franchise/MapPage'));

const LoadingScreen = () => (
  <View style={styles.loadingContainer}>
    <ActivityIndicator size="large" color="#4a90e2" />
    <Text style={styles.loadingText}>로딩 중...</Text>
  </View>
);

const Tab = createBottomTabNavigator();
const Stack = createStackNavigator();

const TabContainer = () => (
  <Tab.Navigator
    initialRouteName="FranchiseTab"
    screenOptions={{
      tabBarActiveTintColor: '#4CAF50',
      tabBarInactiveTintColor: '#BDBDBD',
    }}
  >
    <Tab.Screen
      name="FranchiseTab"
      component={FranchiseStackNavigator}
      options={{
        tabBarIcon: ({ color, size }) => (
          <Image source={require('./images/group.png')} style={{ width: size * 1.3, height: size * 1.3, tintColor: color, resizeMode: 'contain' }} />
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
          <Image source={require('./images/Transaction.png')} style={{ width: size * 1.3, height: size * 1.3, tintColor: color, resizeMode: 'contain' }} />
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
          <Image source={require('./images/myinfo.png')} style={{ width: size * 1.3, height: size * 1.3, tintColor: color, resizeMode: 'contain' }} />
        ),
        headerShown: false,
        tabBarShowLabel: false,
      }}
    />
  </Tab.Navigator>
);

const MainStackNavigator = ({ isReady }) => {
  const accessToken = useSelector((state) => state.login.accessToken);
  const refreshToken = useSelector((state) => state.login.refreshToken);

  if (!isReady) return null;

  return (
    <Stack.Navigator>
      {(accessToken || refreshToken) ? (
        <Stack.Screen name="MainTab" component={TabContainer} options={{ headerShown: false }} />
      ) : (
        <Stack.Screen name="Login" component={Login} options={{ headerShown: false }} />
      )}
    </Stack.Navigator>
  );
};

const App = () => {
  const [isAppReady, setIsAppReady] = useState(false);

  useEffect(() => {
    setupInterceptors();

    const initApp = async () => {
      try {
        const [[, accessToken], [, refreshTokenValue], [, id], [, tokenExp]] = await AsyncStorage.multiGet([
          'accessToken', 'refreshToken', 'id', 'tokenExp'
        ]);

        const now = Date.now();

        if (refreshTokenValue && id) {
          store.dispatch(restoreToken({ accessToken, refreshToken: refreshTokenValue, id }));

          if (!accessToken || (tokenExp && now >= parseInt(tokenExp, 10))) {
            try {
              console.log('App 시작 시 토큰 만료, 갱신 시도 중...');
              await store.dispatch(refreshTokenAction()).unwrap();
              console.log('토큰 갱신 성공');
            } catch (refreshError) {
              console.log('토큰 갱신 실패:', refreshError);
              await AsyncStorage.multiRemove(['accessToken', 'refreshToken', 'tokenExp', 'id']);
              store.dispatch(restoreToken({ accessToken: null, refreshToken: null, id: null }));
            }
          }
        } else {
          await AsyncStorage.multiRemove(['accessToken', 'refreshToken', 'tokenExp', 'id']);
        }
      } catch (e) {
        console.log('토큰 복원 실패:', e);
        await AsyncStorage.multiRemove(['accessToken', 'refreshToken', 'tokenExp', 'id']);
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
