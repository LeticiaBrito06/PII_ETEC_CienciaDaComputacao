import React, { useState, useEffect } from "react";
import { View, StyleSheet, FlatList } from "react-native";
import { Text, Card, ActivityIndicator, List, Button, IconButton } from "react-native-paper";
import api from "../api/cliente";
import { exportarPdf } from "../utils/pdfExport";

export const TeacherPerformanceScreen = () => {
  const [data, setData] = useState([]);
  const [loading, setLoading] = useState(true);
  const [exporting, setExporting] = useState(null);

  useEffect(() => {
    loadAllPerformance();
  }, []);

  const loadAllPerformance = async () => {
    try {
      const response = await api.get("/api/performance");
      setData(response.data);
    } catch (err) {
      console.error(err);
    } finally {
      setLoading(false);
    }
  };

  const handleExportClassPdf = async () => {
    setExporting("turma");
    try {
      await exportarPdf({
        endpoint: "/api/performance/turma/pdf",
        nomeArquivo: "Relatorio_Turma.pdf",
      });
    } finally {
      setExporting(null);
    }
  };

  const handleExportStudentPdf = async (aluno) => {
    setExporting(aluno.id);
    try {
      await exportarPdf({
        endpoint: `/api/performance/${aluno.id}/pdf`,
        nomeArquivo: `Relatorio_${aluno.nome || "Aluno"}.pdf`,
      });
    } finally {
      setExporting(null);
    }
  };

  if (loading)
    return (
      <View style={styles.center}>
        <ActivityIndicator size="large" color="#1A237E" />
      </View>
    );

  return (
    <View style={styles.container}>
      <Button
        mode="contained"
        icon="file-pdf-box"
        onPress={handleExportClassPdf}
        loading={exporting === "turma"}
        disabled={!!exporting}
        style={styles.classPdfButton}
      >
        Baixar PDF da turma
      </Button>
      <FlatList
        data={data}
        keyExtractor={(item) => item.aluno.id.toString()}
        renderItem={({ item }) => (
          <Card style={styles.card}>
            <List.Item
              title={item.aluno.nome}
              description={`Partidas: ${item.totalPartidas} | Acertos: ${item.totalAcertos}`}
              left={(props) => <List.Icon {...props} icon="account" />}
              right={(props) => (
                <View style={styles.right}>
                  <Text style={styles.percent}>
                    {(item.percentualAcerto || 0).toFixed(1)}%
                  </Text>
                  <Text style={styles.label}>Acerto</Text>
                  <IconButton
                    {...props}
                    icon="file-pdf-box"
                    mode="contained-tonal"
                    size={20}
                    onPress={() => handleExportStudentPdf(item.aluno)}
                    disabled={!!exporting}
                    loading={exporting === item.aluno.id}
                  />
                </View>
              )}
            />
          </Card>
        )}
        ItemSeparatorComponent={() => <View style={{ height: 10 }} />}
        ListEmptyComponent={
          <Text style={styles.empty}>
            Nenhum dado de desempenho encontrado.
          </Text>
        }
      />
    </View>
  );
};

const styles = StyleSheet.create({
  container: { flex: 1, backgroundColor: "#E8EAF6", padding: 15 },
  card: { borderRadius: 12, elevation: 2 },
  classPdfButton: { marginBottom: 15, borderRadius: 12 },
  center: { flex: 1, justifyContent: "center", alignItems: "center" },
  right: { alignItems: "center", justifyContent: "center" },
  percent: { fontWeight: "bold", color: "#1A237E", fontSize: 16 },
  label: { fontSize: 10, color: "grey" },
  empty: { textAlign: "center", marginTop: 50, color: "grey" },
});
