import React from "react";
import { createStackNavigator } from "@react-navigation/stack";
import { useAuth } from "../context/AuthContext";
import { LoginScreen as LoginScreen } from "../screens/LoginScreen";
import { StudentMenuScreen as StudentMenuScreen } from "../screens/StudentMenuScreen";
import { TeacherMenuScreen as TeacherMenuScreen } from "../screens/TeacherMenuScreen";
import { GameScreen as GameScreen } from "../screens/GameScreen";
import { PerformanceScreen as PerformanceScreen } from "../screens/PerformanceScreen";
import { TeacherPerformanceScreen as TeacherPerformanceScreen } from "../screens/TeacherPerformanceScreen";
import { QuestionManagementScreen as QuestionManagementScreen } from "../screens/QuestionManagementScreen";
import { StudentRegistrationScreen as StudentRegistrationScreen } from "../screens/StudentRegistrationScreen";
import { ProfileScreen as ProfileScreen } from "../screens/ProfileScreen";

const Stack = createStackNavigator();

export const AppNavigator = () => {
  const { user } = useAuth();

  return (
    <Stack.Navigator
      screenOptions={{
        headerStyle: { backgroundColor: "#1A237E" },
        headerTintColor: "#fff",
        headerTitleAlign: "center",
      }}
    >
      {!user ? (
        <Stack.Screen
          name="Login"
          component={LoginScreen}
          options={{ headerShown: false }}
        />
      ) : user.tipo === "PROFESSOR" ? (
        <>
          <Stack.Screen
            name="MenuProfessor"
            component={TeacherMenuScreen}
            options={{ title: "Menu do Professor" }}
          />
          <Stack.Screen
            name="GerenciarQuestoes"
            component={QuestionManagementScreen}
            options={{ title: "Gerenciar Perguntas" }}
          />
          <Stack.Screen
            name="NovoAluno"
            component={StudentRegistrationScreen}
            options={{ title: "Cadastro de Aluno" }}
          />
          <Stack.Screen
            name="Relatorios"
            component={TeacherPerformanceScreen}
            options={{ title: "Desempenho da Turma" }}
          />
          <Stack.Screen
            name="Perfil"
            component={ProfileScreen}
            options={{ title: "Perfil" }}
          />
        </>
      ) : (
        <>
          <Stack.Screen
            name="MenuAluno"
            component={StudentMenuScreen}
            options={{ title: "Menu do Aluno" }}
          />
          <Stack.Screen
            name="Jogo"
            component={GameScreen}
            options={{ title: "LabQuest Jogo" }}
          />
          <Stack.Screen
            name="MeuDesempenho"
            component={PerformanceScreen}
            options={{ title: "Meu Desempenho" }}
          />
          <Stack.Screen
            name="Perfil"
            component={ProfileScreen}
            options={{ title: "Perfil" }}
          />
        </>
      )}
    </Stack.Navigator>
  );
};
