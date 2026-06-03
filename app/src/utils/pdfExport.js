import { Alert, Linking, Platform } from "react-native";
import { File, Paths } from "expo-file-system";
import * as IntentLauncher from "expo-intent-launcher";
import api from "../api/cliente";

const limparNomeArquivo = (nomeArquivo) =>
  (nomeArquivo || "Relatorio.pdf")
    .normalize("NFD")
    .replace(/[\u0300-\u036f]/g, "")
    .replace(/[^a-zA-Z0-9_.-]/g, "_");

export const exportarPdf = async ({ endpoint, nomeArquivo }) => {
  const baseUrl = (api.defaults.baseURL || "").replace(/\/$/, "");
  const arquivoLocal = limparNomeArquivo(nomeArquivo);
  const destino = new File(Paths.cache, arquivoLocal);
  const authorization = api.defaults.headers.common.Authorization;

  try {
    const arquivo = await File.downloadFileAsync(
      `${baseUrl}${endpoint}`,
      destino,
      {
        headers: authorization ? { Authorization: authorization } : {},
        idempotent: true,
      },
    );

    if (Platform.OS === "android") {
      await IntentLauncher.startActivityAsync("android.intent.action.VIEW", {
        data: arquivo.contentUri,
        flags: 1,
        type: "application/pdf",
      });
      return;
    }

    await Linking.openURL(arquivo.uri);
  } catch (erro) {
    console.error("Erro ao abrir PDF:", erro);
    Alert.alert("Erro", "Nao foi possivel abrir o PDF neste dispositivo.");
  }
};
