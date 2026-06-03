import React from "react";
import { View, StyleSheet, ScrollView } from "react-native";
import { Text, IconButton } from "react-native-paper";
import { useAuth } from "../context/AuthContext";
import { MenuCard } from "../components/MenuCard";

export const StudentMenuScreen = ({ navigation }) => {
  const { user, logout } = useAuth();

  return (
    <ScrollView contentContainerStyle={styles.container}>
      <View style={styles.header}>
        <IconButton icon="flask-outline" size={80} iconColor="#1A237E" />
        <Text variant="headlineSmall">Bem-vindo, {user?.nome}</Text>
      </View>

      <View style={styles.menuContainer}>
        <MenuCard
          title="Jogar"
          subtitle="Desafie seus conhecimentos no laboratorio."
          icon="play-circle"
          onPress={() => navigation.navigate("Jogo")}
        />
        <MenuCard
          title="Meu Desempenho"
          subtitle="Veja como voce esta evoluindo."
          icon="chart-bar"
          onPress={() => navigation.navigate("MeuDesempenho")}
        />
        <MenuCard
          title="Perfil"
          subtitle="Visualize seus dados da sessao."
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
