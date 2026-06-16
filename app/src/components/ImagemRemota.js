import React, {
  memo,
  useCallback,
  useEffect,
  useMemo,
  useRef,
  useState,
} from "react";
import { Image, View, StyleSheet, ActivityIndicator, Text } from "react-native";
import api from "../api/cliente";
import { useAuth } from "../context/AuthContext";

const codificarCaminho = (path) =>
  path
    .split("/")
    .map((segment) => {
      try {
        return encodeURIComponent(decodeURIComponent(segment));
      } catch {
        return encodeURIComponent(segment);
      }
    })
    .join("/");

const normalizarCaminhoRelativo = (imagePath) => {
  if (!imagePath) {
    return null;
  }

  const normalizedPath = imagePath.trim().replace(/\\/g, "/");

  const uploadsIndex = normalizedPath.indexOf("/uploads/");

  if (uploadsIndex >= 0) {
    return normalizedPath
      .substring(uploadsIndex + "/uploads/".length)
      .replace(/^\/+/, "");
  }

  if (normalizedPath.startsWith("uploads/")) {
    return normalizedPath.substring("uploads/".length).replace(/^\/+/, "");
  }

  if (
    normalizedPath.startsWith("http://") ||
    normalizedPath.startsWith("https://")
  ) {
    return null;
  }

  const imagensIndex = normalizedPath.indexOf("imagens/");

  if (imagensIndex >= 0) {
    return normalizedPath.substring(imagensIndex).replace(/^\/+/, "");
  }

  return normalizedPath.replace(/^\/+/, "");
};

const montarUrlComBaseAtual = (imagePath, baseUrlAtual) => {
  const baseUrl = (baseUrlAtual || api.defaults.baseURL || "")
    .replace(/\/api\/?$/, "")
    .replace(/\/$/, "");

  const relativePath = imagePath.replace(/^\/+/, "");
  const encodedPath = codificarCaminho(relativePath);

  return `${baseUrl}/uploads/${encodedPath}`;
};

export const montarUrlsImagem = (
  imagePath,
  baseUrlAtual = api.defaults.baseURL,
) => {
  if (!imagePath) {
    return [];
  }

  const normalizedPath = imagePath.trim().replace(/\\/g, "/");

  if (
    normalizedPath.startsWith("http://") ||
    normalizedPath.startsWith("https://")
  ) {
    const relativePath = normalizarCaminhoRelativo(normalizedPath);

    if (relativePath) {
      return [montarUrlComBaseAtual(relativePath, baseUrlAtual)];
    }

    return [normalizedPath];
  }

  let relativePath = normalizedPath;

  if (relativePath.startsWith("uploads/")) {
    relativePath = relativePath.substring("uploads/".length);
  }

  relativePath = relativePath.replace(/^\/+/, "");

  return [montarUrlComBaseAtual(relativePath, baseUrlAtual)];
};

const RemoteImageComponent = ({ imagePath, style, resizeMode = "contain" }) => {
  const { baseUrl } = useAuth();
  const [loading, setLoading] = useState(true);
  const [mostrarSpinner, setMostrarSpinner] = useState(false);
  const [error, setError] = useState(false);
  const [urlIndex, setUrlIndex] = useState(0);

  const timeoutRef = useRef(null);
  const spinnerTimeoutRef = useRef(null);

  const imageUrls = useMemo(
    () => montarUrlsImagem(imagePath, baseUrl),
    [imagePath, baseUrl],
  );

  const imageUrl = imageUrls[urlIndex];

  const limparTimeouts = useCallback(() => {
    if (timeoutRef.current) {
      clearTimeout(timeoutRef.current);
      timeoutRef.current = null;
    }

    if (spinnerTimeoutRef.current) {
      clearTimeout(spinnerTimeoutRef.current);
      spinnerTimeoutRef.current = null;
    }
  }, []);

  const tentarProximaUrl = useCallback(() => {
    limparTimeouts();

    setUrlIndex((currentIndex) => {
      if (currentIndex < imageUrls.length - 1) {
        return currentIndex + 1;
      }

      setError(true);
      setLoading(false);
      setMostrarSpinner(false);

      return currentIndex;
    });
  }, [imageUrls.length, limparTimeouts]);

  useEffect(() => {
    limparTimeouts();

    setUrlIndex(0);
    setError(false);
    setLoading(imageUrls.length > 0);
    setMostrarSpinner(false);

    return limparTimeouts;
  }, [imagePath, imageUrls.length, limparTimeouts]);

  useEffect(() => {
    if (!imageUrl) {
      setLoading(false);
      setMostrarSpinner(false);
      return undefined;
    }

    setLoading(true);
    setError(false);
    setMostrarSpinner(false);

    // Evita o spinner piscando quando a imagem já está em cache.
    spinnerTimeoutRef.current = setTimeout(() => {
      setMostrarSpinner(true);
    }, 300);

    // Não espera 15 segundos para testar a URL alternativa.
    timeoutRef.current = setTimeout(() => {
      tentarProximaUrl();
    }, 5000);

    return limparTimeouts;
  }, [imageUrl, limparTimeouts, tentarProximaUrl]);

  const handleLoad = () => {
    limparTimeouts();
    setLoading(false);
    setMostrarSpinner(false);
    setError(false);
  };

  const handleError = (event) => {
    console.log("Erro ao carregar imagem:", imageUrl, event.nativeEvent?.error);

    tentarProximaUrl();
  };

  if (!imageUrl) {
    return null;
  }

  return (
    <View style={[styles.container, style]}>
      <Image
        source={{
          uri: imageUrl,
          cache: "force-cache",
        }}
        style={styles.image}
        resizeMode={resizeMode}
        onLoad={handleLoad}
        onError={handleError}
      />

      {loading && mostrarSpinner && !error && (
        <View style={styles.loadingOverlay}>
          <ActivityIndicator size="large" color="#1A237E" />
        </View>
      )}

      {error && (
        <View style={styles.errorOverlay}>
          <Text style={styles.errorText}>Erro ao carregar imagem</Text>
        </View>
      )}
    </View>
  );
};

export const RemoteImage = memo(RemoteImageComponent);

const styles = StyleSheet.create({
  container: {
    width: "100%",
    height: 200,
    justifyContent: "center",
    alignItems: "center",
    backgroundColor: "#f5f5f5",
    borderRadius: 10,
    overflow: "hidden",
  },

  image: {
    width: "100%",
    height: "100%",
  },

  loadingOverlay: {
    ...StyleSheet.absoluteFillObject,
    justifyContent: "center",
    alignItems: "center",
    backgroundColor: "rgba(255, 255, 255, 0.65)",
  },

  errorOverlay: {
    ...StyleSheet.absoluteFillObject,
    justifyContent: "center",
    alignItems: "center",
    backgroundColor: "rgba(255, 200, 200, 0.9)",
  },

  errorText: {
    color: "#D32F2F",
    fontSize: 12,
    fontWeight: "bold",
  },
});