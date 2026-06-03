import React from "react";
import { View, StyleSheet } from "react-native";
import { Text, Card, Avatar, Divider, Button } from "react-native-paper";
import { useAuth } from "../context/AuthContext";

export const ProfileScreen = () => {
  const { user, baseUrl, logout } = useAuth();

  return (
    <View style={styles.container}>
      <Card style={styles.card}>
        <View style={styles.header}>
          <Avatar.Icon size={80} icon="account" backgroundColor="#1A237E" />
          <Text variant="headlineSmall" style={styles.name}>
            {user?.nome}
          </Text>
          <Text variant="bodyMedium" style={styles.email}>
            {user?.email}
          </Text>
        </View>
        <Divider />
        <Card.Content style={styles.info}>
          <View style={styles.infoRow}>
            <Text variant="labelLarge">Tipo de Conta:</Text>
            <Text variant="bodyLarge">
              {user?.tipo === "PROFESSOR" ? "Professor" : "Aluno"}
            </Text>
          </View>
          <View style={styles.infoRow}>
            <Text variant="labelLarge">Servidor API:</Text>
            <Text variant="bodyMedium" style={{ color: "grey" }}>
              {baseUrl}
            </Text>
          </View>
        </Card.Content>
        <Divider />
        <Card.Actions style={styles.actions}>
          <Button
            icon="logout"
            mode="outlined"
            onPress={logout}
            color="#B00020"
          >
            Sair da Conta
          </Button>
        </Card.Actions>
      </Card>
    </View>
  );
};

const styles = StyleSheet.create({
  container: {
    flex: 1,
    backgroundColor: "#E8EAF6",
    padding: 20,
    justifyContent: "center",
  },
  card: { borderRadius: 20, elevation: 4 },
  header: { alignItems: "center", padding: 20 },
  name: { marginTop: 10, fontWeight: "bold", color: "#1A237E" },
  email: { color: "grey" },
  info: { paddingVertical: 20 },
  infoRow: {
    flexDirection: "row",
    justifyContent: "space-between",
    marginBottom: 15,
  },
  actions: { justifyContent: "center", paddingBottom: 20 },
});
