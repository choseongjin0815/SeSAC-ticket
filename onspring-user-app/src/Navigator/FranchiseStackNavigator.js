import React, { lazy } from 'react';
import { createStackNavigator } from '@react-navigation/stack';
import { createMaterialTopTabNavigator } from '@react-navigation/material-top-tabs';  // 상단 탭 추가

const Stack = createStackNavigator();
const TopTab = createMaterialTopTabNavigator(); // 상단 탭 네비게이터

// lazy 로 페이지 불러오기
const HomePage = lazy(() => import("../pages/franchise/HomePage"));
const PaymentPage = lazy(() => import("../pages/franchise/PaymentPage"));
const FranchiseInfoPage = lazy(() => import("../pages/franchise/FranchiseInfoPage"));
const MapPage = lazy(() => import("../pages/franchise/MapPage"));  // 새로 추가한 Map 페이지

// 상단탭을 적용할 페이지들만 포함한 탭 네비게이터
const TopTabNavigator = () => {
    return (
        <TopTab.Navigator  
            initialRouteName="HomePage"
            screenOptions={{
                tabBarStyle: {
                    flexDirection: 'row',
                },
                tabBarItemStyle: {
                    width: 'auto', 
                },
                tabBarLabelStyle: {
                    fontSize: 18, 
                    fontWeight: 500,
                    textTransform: 'none', 
                    writingMode: 'horizontal-tb',
                },
                tabBarIndicatorStyle: {
                    backgroundColor: 'black', 
                },
        }}
    >
            <TopTab.Screen
                name="HomePage"
                component={HomePage}
                options={{ 
                    title: "맛집",
                }}
            />
            <TopTab.Screen
                name="MapPage"
                component={MapPage}
                options={{ title: "지도" }}
            />
        </TopTab.Navigator>
    );
};

// FranchiseStackNavigator에서 HomePage에만 탭을 적용
const FranchiseStackNavigator = () => {
    return (
        <Stack.Navigator initialRouteName='HomePage'>
            {/* HomePage에서만 상단 탭 적용 */}
            <Stack.Screen
                name="HomePage"
                component={TopTabNavigator} // TopTabNavigator를 적용
                options={{ 
                    headerTitleAlign: "center",
                    headerShown: false
                }}
            />    
            <Stack.Screen
                name="PaymentPage"
                component={PaymentPage}
                options={{ 
                    title: "포인트 결제",
                    headerTitleAlign: "center",
                    headerBackTitle: false,
                    headerStyle: {backgroundColor: '#4CAF50'},
                    headerTintColor: '#FFFFFF'
                }}
            />
            <Stack.Screen
                name="FranchiseInfoPage"
                component={FranchiseInfoPage}
                options={{ 
                    title: "가맹점 정보",
                    headerTitleAlign: "center",
                    headerBackTitle: false,
                    headerStyle: {backgroundColor: '#4CAF50'},
                    headerTintColor: '#FFFFFF'
                }}
            />
        </Stack.Navigator>
    );
}

export default FranchiseStackNavigator;