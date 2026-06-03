import { useState } from "react";
import { StyleSheet, ScrollView } from "react-native";
import {
  TextInput,
  Button,
  Text,
  Card,
  IconButton,
  Divider,
} from "react-native-paper";
import { useAuth } from "../context/AuthContext";
import api from "../api/cliente";

export const LoginScreen = () => {
  const { login, updateBaseUrl, baseUrl } = useAuth();
  const [email, setEmail] = useState("");
  const [password, setPassword] = useState("");
  const [url, setUrl] = useState(baseUrl);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState("");

  const handleLogin = async () => {
    setLoading(true);
    setError("");
    updateBaseUrl(url);

    try {
      const response = await api.post("/api/auth/login", {
        email,
        senha: password,
      });
      login(response.data.usuario, response.data.token);
    } catch (err) {
      setError(
        err.response?.data?.message ||
          "Erro ao realizar login. Verifique as credenciais.",
      );
    } finally {
      setLoading(false);
    }
  };

  return (
    <ScrollView contentContainerStyle={styles.container}>
      <Card style={styles.card}>
        <Card.Content style={styles.content}>
          <IconButton
            icon="flask"
            size={64}
            iconColor="#1A237E"
            style={styles.icon}
          />
          <Text variant="headlineLarge" style={styles.title}>
            LabQuest
          </Text>
          <Text variant="bodySmall" style={styles.subtitle}>
            CIENCIA DA COMPUTACAO
          </Text>

          <TextInput
            label="URL da API"
            value={url}
            onChangeText={setUrl}
            mode="outlined"
            style={styles.input}
            placeholder="http://10.0.2.2:8080"
          />

          <TextInput
            label="E-mail"
            value={email}
            onChangeText={setEmail}
            mode="outlined"
            style={styles.input}
            keyboardType="email-address"
            autoCapitalize="none"
          />

          <TextInput
            label="Senha"
            value={password}
            onChangeText={setPassword}
            mode="outlined"
            style={styles.input}
            secureTextEntry
          />

          {error ? <Text style={styles.error}>{error}</Text> : null}

          <Button
            mode="contained"
            onPress={handleLogin}
            loading={loading}
            disabled={loading}
            style={styles.button}
            contentStyle={styles.buttonContent}
          >
            Entrar
          </Button>

          <Text style={styles.hint}>
            Sugestao para Android: http://10.0.2.2:8080
          </Text>

          <Divider style={styles.divider} />

          <Text style={styles.lgpd}>
            Ao entrar, voce concorda com o processamento de seus dados para fins
            pedagogicos, em conformidade com a LGPD.
          </Text>
        </Card.Content>
      </Card>
    </ScrollView>
  );
};

const styles = StyleSheet.create({
  container: {
    flexGrow: 1,
    backgroundColor: "#E8EAF6",
    justifyContent: "center",
    padding: 20,
  },
  card: {
    elevation: 4,
    borderRadius: 20,
  },
  content: {
    alignItems: "center",
    padding: 10,
  },
  icon: {
    margin: 0,
  },
  title: {
    color: "#1A237E",
    fontWeight: "bold",
    fontSize: 42,
  },
  subtitle: {
    letterSpacing: 2,
    fontWeight: "300",
    marginBottom: 20,
  },
  input: {
    width: "100%",
    marginBottom: 12,
  },
  button: {
    width: "100%",
    marginTop: 10,
    borderRadius: 12,
  },
  buttonContent: {
    paddingVertical: 8,
  },
  error: {
    color: "#B00020",
    marginBottom: 10,
    textAlign: "center",
  },
  hint: {
    fontSize: 12,
    color: "grey",
    marginTop: 16,
    textAlign: "center",
  },
  divider: {
    width: "100%",
    marginVertical: 20,
  },
  lgpd: {
    fontSize: 10,
    color: "grey",
    textAlign: "center",
  },
});
