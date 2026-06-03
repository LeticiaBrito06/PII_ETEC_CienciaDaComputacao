import React, { useState, useEffect } from "react";
import { View, StyleSheet, ScrollView, Alert } from "react-native";
import {
  Text,
  Card,
  List,
  IconButton,
  Divider,
  ActivityIndicator,
} from "react-native-paper";
import { useAuth } from "../context/AuthContext";
import { AssociationQuestion as AssociationQuestion } from "../components/AssociationQuestion";
import { RemoteImage as ImagemRemota } from "../components/ImagemRemota";
import api from "../api/cliente";

export const GameScreen = ({ navigation }) => {
  const { user } = useAuth();
  const [questions, setQuestions] = useState([]);
  const [index, setIndex] = useState(0);
  const [loading, setLoading] = useState(true);
  const [gameId, setGameId] = useState(null);
  const [score, setScore] = useState(0);
  const [answers, setAnswers] = useState([]);
  const [selectedAlt, setSelectedAlt] = useState(null);
  const [showFeedback, setShowFeedback] = useState(false);

  // Ajudas
  const [fiftyUsed, setFiftyUsed] = useState(false);
  const [extraChanceUsed, setExtraChanceUsed] = useState(false);
  const [skipUsed, setSkipUsed] = useState(false);
  const [extraChanceActive, setExtraChanceActive] = useState(false);
  const [removedIds, setRemovedIds] = useState([]);
  const [associationHintActive, setAssociationHintActive] = useState(false);

  // Dificuldade progressiva
  const [consecutiveHits, setConsecutiveHits] = useState(0);
  const [consecutiveMisses, setConsecutiveMisses] = useState(0);
  const [currentLevel, setCurrentLevel] = useState("FACIL");

  useEffect(() => {
    startGame();
  }, []);

  const startGame = async () => {
    try {
      const questionsResp = await api.get("/api/questions");
      const gameResp = await api.post("/api/games", {
        studentId: user.id,
        difficultyLevel: "FACIL",
      });
      setQuestions(questionsResp.data);
      setGameId(gameResp.data.id);
    } catch (err) {
      Alert.alert("error", "Nao foi possivel iniciar o jogo.");
      navigation.goBack();
    } finally {
      setLoading(false);
    }
  };

  const handleAnswer = (correta, idAlt = null) => {
    if (showFeedback) return;

    if (!correta && extraChanceActive) {
      setExtraChanceActive(false);
      if (idAlt) setRemovedIds([...removedIds, idAlt]);
      Alert.alert(
        "Chance Extra!",
        "Voce errou, mas sua ajuda esta ativa. Tente novamente!",
      );
      return;
    }

    const currentQuestion = questions[index];
    setSelectedAlt(idAlt);
    setShowFeedback(true);

    const pontos =
      currentQuestion.nivelDificuldade === "FACIL"
        ? 10
        : currentQuestion.nivelDificuldade === "MEDIO"
          ? 20
          : 30;
    const nextScore = correta ? score + pontos : score;

    if (correta) {
      setScore(nextScore);
      const novosAcertos = consecutiveHits + 1;
      setConsecutiveHits(novosAcertos);
      setConsecutiveMisses(0);
      if (novosAcertos >= 3) {
        setConsecutiveHits(0);
        levelUp();
      }
    } else {
      const novosErros = consecutiveMisses + 1;
      setConsecutiveMisses(novosErros);
      setConsecutiveHits(0);
      if (novosErros >= 3) {
        setConsecutiveMisses(0);
        baixarNivel();
      }
    }

    const nextAnswers = [
      ...answers,
      {
        questaoId: currentQuestion.id,
        alternativaId: idAlt,
        correta: correta,
        tempoResposta: 0,
      },
    ];
    setAnswers(nextAnswers);

    setTimeout(() => {
      setShowFeedback(false);
      setSelectedAlt(null);
      proximoPasso(nextAnswers, nextScore);
    }, 1500);
  };

  const levelUp = () => {
    if (currentLevel === "FACIL") setCurrentLevel("MEDIO");
    else if (currentLevel === "MEDIO") setCurrentLevel("DIFICIL");
  };

  const baixarNivel = () => {
    if (currentLevel === "DIFICIL") setCurrentLevel("MEDIO");
    else if (currentLevel === "MEDIO") setCurrentLevel("FACIL");
  };

  const usarCinquentaCinquenta = () => {
    if (fiftyUsed) return;
    const currentQuestion = questions[index];

    if (currentQuestion.tipo === "MULTIPLA_ESCOLHA") {
      setFiftyUsed(true);
      const incorretas = currentQuestion.alternativas.filter((a) => !a.correta);
      const embaralhadas = incorretas.sort(() => 0.5 - Math.random());
      setRemovedIds(embaralhadas.slice(0, 2).map((a) => a.id));
    } else if (currentQuestion.tipo === "ASSOCIACAO") {
      setFiftyUsed(true);
      setAssociationHintActive(true);
      Alert.alert("Ajuda!", "Dois pares foram resolvidos para voce!");
    }
  };

  const usarChanceExtra = () => {
    if (extraChanceUsed) return;
    setExtraChanceUsed(true);
    setExtraChanceActive(true);
    Alert.alert("Ajuda Ativada", "Sua próxima response errada sera ignorada!");
  };

  const usarPular = () => {
    if (skipUsed) return;
    setSkipUsed(true);
    proximoPasso();
  };

  const proximoPasso = (respostasAtuais = answers, pontuacaoAtual = score) => {
    setRemovedIds([]);
    setExtraChanceActive(false);
    setAssociationHintActive(false);
    if (index < questions.length - 1) {
      setIndex(index + 1);
    } else {
      finalizarJogo(respostasAtuais, pontuacaoAtual);
    }
  };

  const finalizarJogo = async (
    respostasAtuais = answers,
    pontuacaoAtual = score,
  ) => {
    try {
      setLoading(true);

      // Mapear answers para o formato esperado pelo backend
      const respostas = respostasAtuais.map((answer) => ({
        questionId: answer.questaoId,
        alternativaId: answer.alternativaId,
        correta: answer.correta,
        tempoResposta: answer.tempoResposta,
      }));

      const finishResp = await api.put(`/api/games/${gameId}/finish`, {
        studentId: user.id,
        respostas: respostas,
        pontuacao: pontuacaoAtual,
        difficultyLevel: currentLevel,
      });
      Alert.alert(
        "Fim de Jogo",
        `Voce marcou ${finishResp.data.pontuacao} pontos!`,
        [{ text: "OK", onPress: () => navigation.navigate("MenuAluno") }],
      );
    } catch (err) {
      console.error("Erro ao finalizar jogo:", err);
      Alert.alert("error", "Nao foi possivel salvar seu desempenho.");
      navigation.navigate("MenuAluno");
    } finally {
      setLoading(false);
    }
  };

  if (loading)
    return (
      <View style={styles.center}>
        <ActivityIndicator size="large" color="#1A237E" />
      </View>
    );

  const currentQuestion = questions[index];
  if (!currentQuestion) return null;

  return (
    <ScrollView contentContainerStyle={styles.container}>
      <Card style={styles.card}>
        <Card.Content>
          <View style={styles.header}>
            <Text variant="labelLarge" style={styles.score}>
              Pontos: {score}
            </Text>
            <Text variant="labelLarge" style={styles.progress}>
              Questao {index + 1} de {questions.length}
            </Text>
          </View>

          <Divider style={styles.divider} />

          <Text variant="titleMedium" style={styles.enunciado}>
            {currentQuestion.enunciado}
          </Text>

          {currentQuestion.imagemUrl && (
            <ImagemRemota
              imagePath={currentQuestion.imagemUrl}
              style={styles.mainImage}
            />
          )}

          <View style={styles.aids}>
            <View style={styles.aidItem}>
              <IconButton
                icon="dice-multiple"
                mode="contained-tonal"
                disabled={fiftyUsed}
                onPress={usarCinquentaCinquenta}
              />
              <Text variant="labelSmall">50/50</Text>
            </View>
            <View style={styles.aidItem}>
              <IconButton
                icon="cached"
                mode="contained-tonal"
                disabled={extraChanceUsed}
                onPress={usarChanceExtra}
              />
              <Text variant="labelSmall">Chance +</Text>
            </View>
            <View style={styles.aidItem}>
              <IconButton
                icon="skip-next"
                mode="contained-tonal"
                disabled={skipUsed}
                onPress={usarPular}
              />
              <Text variant="labelSmall">Pular</Text>
            </View>
          </View>

          <Divider style={styles.divider} />

          {currentQuestion.tipo === "MULTIPLA_ESCOLHA" ? (
            currentQuestion.alternativas &&
            Array.isArray(currentQuestion.alternativas) ? (
              currentQuestion.alternativas
                .filter((a) => !removedIds.includes(a.id))
                .map((alt) => {
                  const estaSelecionada = selectedAlt === alt.id;
                  const ehCorreta = alt.correta;
                  let corFundo = "#fff";
                  if (showFeedback) {
                    if (ehCorreta)
                      corFundo = "#C8E6C9"; // Verde claro
                    else if (estaSelecionada && !ehCorreta)
                      corFundo = "#FFCDD2"; // Vermelho claro
                  }

                  return (
                    <List.Item
                      key={alt.id}
                      title={alt.texto}
                      onPress={() => handleAnswer(alt.correta, alt.id)}
                      style={[styles.altItem, { backgroundColor: corFundo }]}
                      left={(props) =>
                        alt.imagemUrl ? (
                          <ImagemRemota
                            imagePath={alt.imagemUrl}
                            style={styles.altThumb}
                            resizeMode="cover"
                          />
                        ) : null
                      }
                    />
                  );
                })
            ) : (
              <Text style={styles.error}>
                Erro: Alternativas não carregaram
              </Text>
            )
          ) : (
            <AssociationQuestion
              alternatives={currentQuestion.alternativas}
              onFinish={(correta) => handleAnswer(correta)}
              hintActive={associationHintActive}
              extraChanceActive={extraChanceActive}
            />
          )}
        </Card.Content>
      </Card>
    </ScrollView>
  );
};

const styles = StyleSheet.create({
  container: { padding: 15, backgroundColor: "#E8EAF6", flexGrow: 1 },
  card: { borderRadius: 15, elevation: 4 },
  center: { flex: 1, justifyContent: "center", alignItems: "center" },
  header: {
    flexDirection: "row",
    justifyContent: "space-between",
    marginBottom: 10,
  },
  score: { color: "#1A237E", fontWeight: "bold" },
  progress: { color: "#666" },
  divider: { marginVertical: 15 },
  enunciado: { marginBottom: 20, lineHeight: 24, textAlign: "center" },
  mainImage: { width: "100%", height: 200, borderRadius: 10, marginBottom: 20 },
  aids: {
    flexDirection: "row",
    justifyContent: "space-around",
    marginBottom: 10,
  },
  aidItem: { alignItems: "center" },
  altItem: {
    borderWidth: 1,
    borderColor: "#E0E0E0",
    borderRadius: 10,
    marginBottom: 10,
    paddingVertical: 5,
  },
  altThumb: { width: 50, height: 50, borderRadius: 5, marginRight: 10 },
  error: { color: "#D32F2F", padding: 10, textAlign: "center" },
});
