import { useState, useEffect, useRef } from "react";
import { View, StyleSheet, FlatList, Alert, ScrollView } from "react-native";
import {
  Text,
  Card,
  FAB,
  ActivityIndicator,
  IconButton,
  Portal,
  Modal,
  TextInput,
  Button,
  SegmentedButtons,
  Divider,
} from "react-native-paper";
import * as ImagePicker from "expo-image-picker";
import { RemoteImage as ImagemRemota } from "../components/ImagemRemota";
import api from "../api/cliente";

const CATEGORIA_PADRAO = "Materiais de laboratorio";

const criarAlternativasPadrao = () => [
  { texto: "", correta: true, imagemUrl: "" },
  { texto: "", correta: false, imagemUrl: "" },
  { texto: "", correta: false, imagemUrl: "" },
  { texto: "", correta: false, imagemUrl: "" },
];

const normalizarAlternativas = (alternativas) =>
  Array.isArray(alternativas) && alternativas.length > 0
    ? alternativas
    : criarAlternativasPadrao();

const formatarErroApi = (err) => {
  const dados = err.response?.data;
  if (dados?.fields) {
    return Object.entries(dados.fields)
      .map(([campo, mensagem]) => `${campo}: ${mensagem}`)
      .join("\n");
  }

  return dados?.message || "Nao foi possivel salvar a questao.";
};

const CampoTextoEstavel = ({
  valor,
  aoAlterar,
  aoRascunhar,
  chaveCampo,
  ...props
}) => {
  const [rascunho, setRascunho] = useState(valor || "");

  useEffect(() => {
    setRascunho(valor || "");
  }, [chaveCampo, valor]);

  const confirmarAlteracao = () => {
    aoAlterar(rascunho);
  };

  const lidarComMudanca = (texto) => {
    setRascunho(texto);
    if (aoRascunhar) {
      aoRascunhar(texto);
    }
  };

  return (
    <TextInput
      {...props}
      value={rascunho}
      onChangeText={lidarComMudanca}
      onBlur={confirmarAlteracao}
      onEndEditing={confirmarAlteracao}
    />
  );
};

export const QuestionManagementScreen = () => {
  const [questions, setQuestions] = useState([]);
  const [loading, setLoading] = useState(true);
  const [enviandoImagem, setUploading] = useState(false);
  const [modalVisivel, setModalVisible] = useState(false);
  const [questaoSendoEditada, setEditingQuestion] = useState(null);

  // Estados do formulario
  const [enunciado, setEnunciado] = useState("");
  const [tipo, setTipo] = useState("MULTIPLA_ESCOLHA");
  const [nivel, setNivel] = useState("FACIL");
  const [imagemUrl, setImagemUrl] = useState("");
  const [alternativas, setAlternativas] = useState(criarAlternativasPadrao);
  const enunciadoRef = useRef("");
  const alternativasRef = useRef(criarAlternativasPadrao());

  useEffect(() => {
    carregarQuestoes();
  }, []);

  const carregarQuestoes = async () => {
    setLoading(true);
    try {
      const response = await api.get("/api/questions");
      setQuestions(response.data);
    } catch (err) {
      console.error(err);
    } finally {
      setLoading(false);
    }
  };

  const selecionarImagem = async (callback) => {
    const resultado = await ImagePicker.launchImageLibraryAsync({
      mediaTypes: ImagePicker.MediaTypeOptions.Images,
      allowsEditing: true,
      aspect: [4, 3],
      quality: 1,
    });

    if (!resultado.canceled) {
      fazerUploadImagem(resultado.assets[0].uri, callback);
    }
  };

  const fazerUploadImagem = async (uri, callback) => {
    setUploading(true);
    const dadosForm = new FormData();
    const partesUri = uri.split(".");
    const tipoArquivo = partesUri[partesUri.length - 1];

    dadosForm.append("file", {
      uri,
      name: `foto.${tipoArquivo}`,
      type: `image/${tipoArquivo}`,
    });

    try {
      const response = await api.post("/api/uploads/images", dadosForm, {
        headers: { "Content-Type": "multipart/form-data" },
      });
      callback(response.data.relativePath || response.data.url);
    } catch (err) {
      Alert.alert("error", "Nao foi possivel enviar a imagem.");
    } finally {
      setUploading(false);
    }
  };

  const lidarComEdicao = (q) => {
    const alternativasNormalizadas = normalizarAlternativas(q.alternativas);

    setEditingQuestion(q);
    enunciadoRef.current = q.enunciado || "";
    alternativasRef.current = alternativasNormalizadas;
    setEnunciado(q.enunciado || "");
    setTipo(q.tipo || "MULTIPLA_ESCOLHA");
    setNivel(q.nivelDificuldade || "FACIL");
    setImagemUrl(q.imagemUrl || "");
    setAlternativas(alternativasNormalizadas);
    setModalVisible(true);
  };

  const lidarComExclusao = (id) => {
    Alert.alert("Excluir", "Deseja realmente excluir esta questao?", [
      { text: "Cancelar", style: "cancel" },
      {
        text: "Excluir",
        style: "destructive",
        onPress: async () => {
          try {
            await api.delete(`/api/questions/${id}`);
            carregarQuestoes();
          } catch (err) {
            Alert.alert("error", "Nao foi possivel excluir a questao.");
          }
        },
      },
    ]);
  };

  const salvarQuestao = async () => {
    const enunciadoTratado = enunciadoRef.current.trim();
    const alternativasTratadas = normalizarAlternativas(
      alternativasRef.current,
    ).map((alt) => ({
        ...alt,
        texto: (alt.texto || "").trim(),
        imagemUrl: alt.imagemUrl || "",
      }));

    if (!enunciadoTratado) {
      Alert.alert("error", "O enunciado e obrigatorio.");
      return;
    }

    if (alternativasTratadas.some((alt) => !alt.texto)) {
      Alert.alert(
        "error",
        tipo === "MULTIPLA_ESCOLHA"
          ? "Preencha o texto de todas as alternativas."
          : "Preencha o texto de todos os pares de associacao.",
      );
      return;
    }

    if (
      tipo === "MULTIPLA_ESCOLHA" &&
      alternativasTratadas.filter((alt) => alt.correta).length !== 1
    ) {
      Alert.alert("error", "Selecione exatamente uma alternativa correta.");
      return;
    }

    const dadosQuestao = {
      enunciado: enunciadoTratado,
      tipo,
      nivelDificuldade: nivel,
      imagemUrl,
      categoria: CATEGORIA_PADRAO,
      alternativas: alternativasTratadas,
    };

    try {
      if (questaoSendoEditada) {
        await api.put(`/api/questions/${questaoSendoEditada.id}`, dadosQuestao);
      } else {
        await api.post("/api/questions", dadosQuestao);
      }
      setModalVisible(false);
      carregarQuestoes();
      limparFormulario();
    } catch (err) {
      console.error("Erro ao salvar questao:", err.response?.data || err);
      Alert.alert("error", formatarErroApi(err));
    }
  };

  const limparFormulario = () => {
    const alternativasPadrao = criarAlternativasPadrao();

    setEditingQuestion(null);
    enunciadoRef.current = "";
    alternativasRef.current = alternativasPadrao;
    setEnunciado("");
    setTipo("MULTIPLA_ESCOLHA");
    setNivel("FACIL");
    setImagemUrl("");
    setAlternativas(alternativasPadrao);
  };

  const atualizarAlternativa = (index, campos) => {
    const novasAlts = [...normalizarAlternativas(alternativasRef.current)];
    novasAlts[index] = { ...novasAlts[index], ...campos };
    alternativasRef.current = novasAlts;
    setAlternativas(novasAlts);
  };

  const rascunharTextoAlternativa = (index, texto) => {
    const novasAlts = [...normalizarAlternativas(alternativasRef.current)];
    novasAlts[index] = { ...novasAlts[index], texto };
    alternativasRef.current = novasAlts;
  };

  if (loading && questions.length === 0) {
    return (
      <View style={styles.centro}>
        <ActivityIndicator size="large" color="#1A237E" />
      </View>
    );
  }

  return (
    <View style={styles.container}>
      <FlatList
        data={questions}
        keyExtractor={(item) => item.id.toString()}
        renderItem={({ item }) => (
          <Card style={styles.cardQuestao}>
            <Card.Title
              title={item.enunciado}
              titleNumberOfLines={2}
              subtitle={`${item.tipo} - ${item.nivelDificuldade}`}
              left={(props) => (
                <IconButton {...props} icon="help-circle-outline" />
              )}
            />
            <Card.Actions>
              <Button onPress={() => lidarComEdicao(item)}>Editar</Button>
              <Button
                textColor="#B00020"
                onPress={() => lidarComExclusao(item.id)}
              >
                Excluir
              </Button>
            </Card.Actions>
          </Card>
        )}
        onRefresh={carregarQuestoes}
        refreshing={loading}
      />

      <FAB
        icon="plus"
        style={styles.fab}
        onPress={() => {
          limparFormulario();
          setModalVisible(true);
        }}
      />

      <Portal>
        <Modal
          visible={modalVisivel}
          onDismiss={() => setModalVisible(false)}
          contentContainerStyle={styles.modal}
        >
          <ScrollView>
            <Text variant="headlineSmall" style={styles.tituloModal}>
              {questaoSendoEditada ? "Editar Questao" : "Nova Questao"}
            </Text>

            <CampoTextoEstavel
              label="Enunciado"
              valor={enunciado}
              chaveCampo={`enunciado-${questaoSendoEditada?.id || "nova"}`}
              aoRascunhar={(texto) => {
                enunciadoRef.current = texto;
              }}
              aoAlterar={(texto) => {
                enunciadoRef.current = texto;
                setEnunciado(texto);
              }}
              mode="outlined"
              multiline
              numberOfLines={3}
              style={styles.input}
            />

            <Text variant="labelLarge" style={styles.label}>
              Dificuldade
            </Text>
            <SegmentedButtons
              value={nivel}
              onValueChange={setNivel}
              buttons={[
                { value: "FACIL", label: "Facil" },
                { value: "MEDIO", label: "Medio" },
                { value: "DIFICIL", label: "Dificil" },
              ]}
              style={styles.segmento}
            />

            <Text variant="labelLarge" style={styles.label}>
              Tipo de Questao
            </Text>
            <SegmentedButtons
              value={tipo}
              onValueChange={setTipo}
              buttons={[
                { value: "MULTIPLA_ESCOLHA", label: "Multipla Escolha" },
                { value: "ASSOCIACAO", label: "Associacao" },
              ]}
              style={styles.segmento}
            />

            <Button
              mode="outlined"
              onPress={() => selecionarImagem(setImagemUrl)}
              loading={enviandoImagem}
              icon="image"
              style={styles.botaoImagem}
            >
              {imagemUrl ? "Alterar Imagem" : "Adicionar Imagem"}
            </Button>

            {imagemUrl ? (
              <ImagemRemota imagePath={imagemUrl} style={styles.preview} />
            ) : null}

            <Divider style={styles.divisor} />

            <Text variant="titleMedium" style={styles.label}>
              {tipo === "MULTIPLA_ESCOLHA"
                ? "Alternativas"
                : "Pares de Associacao"}
            </Text>

            {normalizarAlternativas(alternativas).map((alt, index) => (
              <Card key={index} style={styles.cardAlt}>
                <Card.Content>
                  <View style={styles.linhaAlt}>
                    <CampoTextoEstavel
                      label={
                        tipo === "MULTIPLA_ESCOLHA"
                          ? `Opcao ${index + 1}`
                          : `Item ${index + 1}`
                      }
                      valor={alt.texto}
                      chaveCampo={`alternativa-${questaoSendoEditada?.id || "nova"}-${alt.id || index}`}
                      aoRascunhar={(t) => rascunharTextoAlternativa(index, t)}
                      aoAlterar={(t) =>
                        atualizarAlternativa(index, { texto: t })
                      }
                      mode="outlined"
                      style={{ flex: 1 }}
                    />
                    {tipo === "MULTIPLA_ESCOLHA" && (
                      <IconButton
                        icon={alt.correta ? "check-circle" : "circle-outline"}
                        iconColor={alt.correta ? "#4CAF50" : "#666"}
                        onPress={() => {
                          const novas = normalizarAlternativas(
                            alternativasRef.current,
                          ).map((a, i) => ({
                            ...a,
                            correta: i === index,
                          }));
                          alternativasRef.current = novas;
                          setAlternativas(novas);
                        }}
                      />
                    )}
                  </View>
                  <Button
                    mode="text"
                    compact
                    onPress={() =>
                      selecionarImagem((url) =>
                        atualizarAlternativa(index, { imagemUrl: url }),
                      )
                    }
                    icon="image-plus"
                  >
                    {alt.imagemUrl ? "Alterar Foto" : "Add Foto"}
                  </Button>
                </Card.Content>
              </Card>
            ))}

            <Button
              mode="contained"
              onPress={salvarQuestao}
              style={styles.botaoSalvar}
            >
              Salvar Questao
            </Button>
            <Button onPress={() => setModalVisible(false)}>Cancelar</Button>
          </ScrollView>
        </Modal>
      </Portal>
    </View>
  );
};

const styles = StyleSheet.create({
  container: { flex: 1, backgroundColor: "#F5F5F5", padding: 10 },
  centro: { flex: 1, justifyContent: "center", alignItems: "center" },
  cardQuestao: { marginBottom: 10, borderRadius: 12 },
  fab: {
    position: "absolute",
    margin: 16,
    right: 0,
    bottom: 0,
    backgroundColor: "#1A237E",
  },
  modal: {
    backgroundColor: "white",
    padding: 20,
    margin: 10,
    borderRadius: 15,
    maxHeight: "90%",
  },
  tituloModal: { marginBottom: 20, fontWeight: "bold", color: "#1A237E" },
  input: { marginBottom: 15 },
  label: { marginBottom: 8, marginTop: 10, fontWeight: "bold" },
  segmento: { marginBottom: 15 },
  botaoImagem: { marginBottom: 10 },
  preview: { width: "100%", height: 150, borderRadius: 8, marginBottom: 15 },
  divisor: { marginVertical: 15 },
  cardAlt: { marginBottom: 10, backgroundColor: "#FAFAFA" },
  linhaAlt: { flexDirection: "row", alignItems: "center" },
  botaoSalvar: { marginTop: 20, marginBottom: 10, backgroundColor: "#1A237E" },
});
