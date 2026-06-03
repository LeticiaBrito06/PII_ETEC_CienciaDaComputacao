import { useState, useEffect } from "react";
import { View, StyleSheet, ScrollView } from "react-native";
import { Text, Card, ActivityIndicator, Divider, Button } from "react-native-paper";
import { useAuth } from "../context/AuthContext";
import api from "../api/cliente";
import { exportarPdf } from "../utils/pdfExport";

export const PerformanceScreen = () => {
  const { user } = useAuth();
  const [performance, setPerformance] = useState(null);
  const [loading, setLoading] = useState(true);
  const [exporting, setExporting] = useState(false);

  useEffect(() => {
    loadPerformance();
  }, []);

  const loadPerformance = async () => {
    try {
      const response = await api.get(`/api/performance/${user.id}`);
      setPerformance(response.data);
    } catch (err) {
      console.error(err);
    } finally {
      setLoading(false);
    }
  };

  const handleExportPdf = async () => {
    setExporting(true);
    try {
      await exportarPdf({
        endpoint: `/api/performance/${user.id}/pdf`,
        nomeArquivo: `Relatorio_${user.nome || "Aluno"}.pdf`,
      });
    } finally {
      setExporting(false);
    }
  };

  if (loading)
    return (
      <View style={styles.center}>
        <ActivityIndicator size="large" color="#1A237E" />
      </View>
    );

  return (
    <ScrollView contentContainerStyle={styles.container}>
      <Card style={styles.card}>
        <Card.Title title="Estatisticas de Jogo" subtitle={user.nome} />
        <Divider />
        <Card.Content style={styles.content}>
          <View style={styles.statBox}>
            <Text variant="headlineMedium" style={styles.statValue}>
              {performance?.totalPartidas || 0}
            </Text>
            <Text variant="labelMedium">Partidas Jogadas</Text>
          </View>
          <View style={styles.row}>
            <View style={[styles.statBox, { flex: 1 }]}>
              <Text
                variant="headlineSmall"
                style={[styles.statValue, { color: "green" }]}
              >
                {performance?.totalAcertos || 0}
              </Text>
              <Text variant="labelSmall">Acertos</Text>
            </View>
            <View style={[styles.statBox, { flex: 1 }]}>
              <Text
                variant="headlineSmall"
                style={[styles.statValue, { color: "red" }]}
              >
                {performance?.totalErros || 0}
              </Text>
              <Text variant="labelSmall">Erros</Text>
            </View>
          </View>
          <Divider style={styles.divider} />
          <View style={styles.statBox}>
            <Text variant="headlineSmall" style={styles.statValue}>
              {(performance?.percentualAcerto || 0).toFixed(1)}%
            </Text>
            <Text variant="labelMedium">Aproveitamento Geral</Text>
          </View>
          <View style={styles.statBox}>
            <Text variant="headlineSmall" style={styles.statValue}>
              {performance?.nivelMedio || "FACIL"}
            </Text>
            <Text variant="labelMedium">Nivel Medio de Dificuldade</Text>
          </View>
          <Button
            mode="contained"
            icon="file-pdf-box"
            onPress={handleExportPdf}
            loading={exporting}
            disabled={exporting}
            style={styles.pdfButton}
          >
            Baixar PDF
          </Button>
        </Card.Content>
      </Card>
    </ScrollView>
  );
};

const styles = StyleSheet.create({
  container: { flexGrow: 1, backgroundColor: "#E8EAF6", padding: 20 },
  card: { borderRadius: 20, elevation: 4 },
  center: { flex: 1, justifyContent: "center", alignItems: "center" },
  content: { paddingVertical: 20 },
  statBox: { alignItems: "center", marginVertical: 15 },
  statValue: { fontWeight: "bold", color: "#1A237E" },
  row: { flexDirection: "row", justifyContent: "space-around" },
  divider: { marginVertical: 10 },
  pdfButton: { marginTop: 10, borderRadius: 12 },
});
