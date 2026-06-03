import React, { useState, useEffect } from "react";
import { View, StyleSheet } from "react-native";
import { Text, Button, Card } from "react-native-paper";
import { RemoteImage as ImagemRemota } from "./ImagemRemota";

export const AssociationQuestion = ({
  alternatives,
  onFinish,
  hintActive,
  extraChanceActive,
}) => {
  const [rotulosEmbaralhados, setShuffledLabels] = useState([]);
  const [selecoes, setSelections] = useState({});
  const [resolvidosPelaDica, setSolvedByHint] = useState([]);
  const [paresMantidos, setParesMantidos] = useState([]);
  const [feedback, setFeedback] = useState(null); // 'correct' ou 'incorrect'

  useEffect(() => {
    if (!alternatives || !Array.isArray(alternatives)) {
      setShuffledLabels([]);
      setSelections({});
      setSolvedByHint([]);
      setParesMantidos([]);
      setFeedback(null);
      return;
    }
    const rotulos = alternatives
      .map((a) => a.texto)
      .sort(() => 0.5 - Math.random());
    setShuffledLabels(rotulos);
    setSelections({});
    setSolvedByHint([]);
    setParesMantidos([]);
    setFeedback(null);
  }, [alternatives]);

  useEffect(() => {
    if (
      hintActive &&
      Object.keys(selecoes).length === 0 &&
      alternatives &&
      Array.isArray(alternatives)
    ) {
      // Resolve 2 pares aleatorios
      const novasSelecoes = { ...selecoes };
      const indices = alternatives.map((_, i) => i);
      const indicesEmbaralhados = indices.sort(() => 0.5 - Math.random());
      const paraResolver = indicesEmbaralhados.slice(0, 2);

      paraResolver.forEach((idx) => {
        novasSelecoes[idx] = alternatives[idx].texto;
      });
      setSelections(novasSelecoes);
      setSolvedByHint(paraResolver);
    }
  }, [hintActive, alternatives]);

  const lidarComEnvio = () => {
    if (feedback) return;
    if (!alternatives || !Array.isArray(alternatives)) {
      alert("Erro: alternativas não carregaram corretamente.");
      return;
    }
    if (Object.keys(selecoes).length < alternatives.length) {
      alert("Por favor, associe todos os itens.");
      return;
    }

    const paresCorretos = [];
    let correto = true;
    alternatives.forEach((alt, index) => {
      const selecionado = (selecoes[index] || "").trim().toLowerCase();
      const esperado = (alt.texto || "").trim().toLowerCase();
      if (selecionado === esperado) {
        paresCorretos.push(index);
      } else {
        correto = false;
      }
    });

    if (!correto && extraChanceActive) {
      const selecoesCorretas = {};
      paresCorretos.forEach((index) => {
        selecoesCorretas[index] = alternatives[index].texto;
      });
      setParesMantidos(paresCorretos);
      setSelections(selecoesCorretas);
      onFinish(false);
      return;
    }

    setFeedback(correto ? "correct" : "incorrect");
    setTimeout(() => {
      onFinish(correto);
    }, 1500);
  };

  return (
    <View>
      {alternatives &&
      Array.isArray(alternatives) &&
      alternatives.length > 0 ? (
        alternatives.map((alt, index) => {
          const foiResolvido = resolvidosPelaDica.includes(index);
          const foiMantido = paresMantidos.includes(index);
          const estaBloqueado = foiResolvido || foiMantido;
          let corCard = "#fff";
          if (feedback) {
            const selecionado = (selecoes[index] || "").trim().toLowerCase();
            const esperado = (alt.texto || "").trim().toLowerCase();
            const ehPar = selecionado === esperado;
            corCard = ehPar ? "#C8E6C9" : "#FFCDD2";
          } else if (estaBloqueado) {
            corCard = "#C8E6C9";
          }

          return (
            <Card
              key={index}
              style={[styles.card, { backgroundColor: corCard }]}
            >
              <Card.Content style={styles.linha}>
                {alt.imagemUrl && (
                  <ImagemRemota
                    imagePath={alt.imagemUrl}
                    style={styles.miniatura}
                    resizeMode="cover"
                  />
                )}
                <View style={styles.containerSelecao}>
                  <Text
                    variant="labelMedium"
                    style={{ fontWeight: estaBloqueado ? "bold" : "normal" }}
                  >
                    Item {index + 1}{" "}
                    {foiResolvido ? "(Resolvido pela Dica)" : ""}
                  </Text>
                  <View style={styles.opcoes}>
                    {rotulosEmbaralhados &&
                    Array.isArray(rotulosEmbaralhados) &&
                    rotulosEmbaralhados.length > 0 ? (
                      rotulosEmbaralhados.map((rotulo, rIdx) => (
                        <Button
                          key={rIdx}
                          mode={
                            selecoes[index] === rotulo
                              ? "contained"
                              : "outlined"
                          }
                          onPress={() =>
                            !feedback &&
                            !estaBloqueado &&
                            setSelections({ ...selecoes, [index]: rotulo })
                          }
                          compact
                          style={styles.botaoOpcao}
                          labelStyle={{ fontSize: 10 }}
                          disabled={estaBloqueado && selecoes[index] !== rotulo}
                        >
                          {rotulo}
                        </Button>
                      ))
                    ) : (
                      <Text style={styles.loadingText}>
                        Carregando opcoes...
                      </Text>
                    )}
                  </View>
                </View>
              </Card.Content>
            </Card>
          );
        })
      ) : (
        <Text style={styles.loadingText}>
          Carregando questao de associacao...
        </Text>
      )}
      <Button
        mode="contained"
        onPress={lidarComEnvio}
        style={styles.enviar}
        disabled={!!feedback}
      >
        {feedback ? "Verificando..." : "Confirmar Associacao"}
      </Button>
    </View>
  );
};

const styles = StyleSheet.create({
  card: { marginBottom: 10, borderRadius: 12, elevation: 2 },
  linha: { flexDirection: "row", alignItems: "center" },
  miniatura: { width: 60, height: 60, borderRadius: 8, marginRight: 15 },
  containerSelecao: { flex: 1 },
  opcoes: { flexDirection: "row", flexWrap: "wrap", marginTop: 5 },
  botaoOpcao: { margin: 2, borderRadius: 8 },
  enviar: { marginTop: 10, borderRadius: 10, paddingVertical: 5 },
  loadingText: {
    color: "#999",
    padding: 10,
    textAlign: "center",
    fontStyle: "italic",
  },
});
