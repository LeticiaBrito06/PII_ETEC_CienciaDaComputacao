import React from "react";
import { View, StyleSheet, ScrollView } from "react-native";
import { Text, IconButton } from "react-native-paper";
import { useAuth } from "../context/AuthContext";
import { MenuCard } from "../components/MenuCard";

export const TeacherMenuScreen = ({ navigation }) => {
  const { user, logout } = useAuth();

  return (
    <ScrollView contentContainerStyle={styles.container}>
      <View style={styles.header}>
        <IconButton icon="flask-round-bottom" size={80} iconColor="#1A237E" />
        <Text variant="headlineSmall">Painel do Professor</Text>
        <Text variant="bodyMedium">{user?.nome}</Text>
      </View>

      <View style={styles.menuContainer}>
        <MenuCard
          title="Gerenciar Questoes"
          subtitle="Adicione ou edite perguntas do jogo."
          icon="file-document-edit"
          onPress={() => navigation.navigate("GerenciarQuestoes")}
        />
        <MenuCard
          title="Novo Aluno"
          subtitle="Cadastre estudantes no sistema."
          icon="account-plus"
          onPress={() => navigation.navigate("NovoAluno")}
        />
        <MenuCard
          title="Relatorios"
          subtitle="Analise o desempenho da turma."
          icon="file-chart"
          onPress={() => navigation.navigate("Relatorios")}
        />
        <MenuCard
          title="Perfil"
          subtitle="Seus dados cadastrais."
          icon="account-circle"
          onPress={() => navigation.navigate("Perfil")}
        />
      </View>

      <View style={styles.footer}>
        <IconButton icon="logout" onPress={logout} iconColor="#B00020" />
        <Text variant="bodySmall" style={{ color: "#B00020" }}>
          Sair
        </Text>
      </View>
    </ScrollView>
  );
};

const styles = StyleSheet.create({
  container: {
    flexGrow: 1,
    backgroundColor: "#E8EAF6",
    padding: 20,
  },
  header: {
    alignItems: "center",
    marginTop: 20,
    marginBottom: 40,
  },
  menuContainer: {
    width: "100%",
  },
  footer: {
    marginTop: "auto",
    alignItems: "center",
    paddingBottom: 20,
  },
});
