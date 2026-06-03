import { StatusBar } from "expo-status-bar";
import { Provider as PaperProvider } from "react-native-paper";
import { NavigationContainer } from "@react-navigation/native";
import { AuthProvider } from "./src/context/AuthContext";
import { AppNavigator } from "./src/navigation/AppNavigator";
import { theme } from "./src/theme";

export default function App() {
  return (
    <AuthProvider>
      <PaperProvider theme={theme}>
        <NavigationContainer>
          <AppNavigator />
          <StatusBar style="light" />
        </NavigationContainer>
      </PaperProvider>
    </AuthProvider>
  );
}
