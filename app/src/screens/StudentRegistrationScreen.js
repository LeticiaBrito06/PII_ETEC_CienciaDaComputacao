import React, { useState } from "react";
import { View, StyleSheet, ScrollView, Alert } from "react-native";
import { TextInput, Button, Text, Card, Divider } from "react-native-paper";
import api from "../api/cliente";

export const StudentRegistrationScreen = () => {
  const [nome, setNome] = useState("");
  const [email, setEmail] = useState("");
  const [password, setPassword] = useState("");
  const [loading, setLoading] = useState(false);

  const handleRegister = async () => {
    const nomeAluno = nome.trim();
    const emailAluno = email.trim().toLowerCase();
    const senhaAluno = password.trim();

    if (!nomeAluno || !emailAluno || !senhaAluno) {
      Alert.alert("Erro", "Preencha todos os campos.");
      return;
    }

    if (!/^\d{11}$/.test(senhaAluno)) {
      Alert.alert("Erro", "A senha deve conter exatamente 11 numeros.");
      return;
    }

    setLoading(true);
    try {
      await api.post("/api/users", {
        nome: nomeAluno,
        email: emailAluno,
        senha: senhaAluno,
        tipo: "ALUNO",
      });
      Alert.alert("Sucesso", "Aluno cadastrado com sucesso!");
      setNome("");
      setEmail("");
      setPassword("");
    } catch (err) {
      Alert.alert(
        "Erro",
        err.response?.data?.message || "Nao foi possivel cadastrar o aluno.",
      );
    } finally {
      setLoading(false);
    }
  };

  return (
    <ScrollView contentContainerStyle={styles.container}>
      <Card style={styles.card}>
        <Card.Content>
          <Text variant="titleMedium" style={styles.info}>
            Cadastre um novo aluno para que ele possa acessar o sistema mobile.
          </Text>
          <Divider style={styles.divider} />

          <TextInput
            label="Nome Completo"
            value={nome}
            onChangeText={setNome}
            mode="outlined"
            style={styles.input}
          />

          <TextInput
            label="E-mail Institucional"
            value={email}
            onChangeText={setEmail}
            mode="outlined"
            style={styles.input}
            keyboardType="email-address"
            autoCapitalize="none"
          />

          <TextInput
            label="Senha Numerica (11 digitos)"
            value={password}
            onChangeText={setPassword}
            mode="outlined"
            style={styles.input}
            secureTextEntry
          />

          <Button
            mode="contained"
            onPress={handleRegister}
            loading={loading}
            disabled={loading}
            style={styles.button}
          >
            Cadastrar Aluno
          </Button>
        </Card.Content>
      </Card>
    </ScrollView>
  );
};

const styles = StyleSheet.create({
  container: { flexGrow: 1, backgroundColor: "#E8EAF6", padding: 20 },
  card: { borderRadius: 20, elevation: 4 },
  info: { color: "#666", textAlign: "center", marginBottom: 10 },
  divider: { marginVertical: 15 },
  input: { marginBottom: 15 },
  button: { marginTop: 10, borderRadius: 12, paddingVertical: 8 },
});
