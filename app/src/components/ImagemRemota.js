import React, { useEffect, useRef, useState } from "react";
import { Image, View, StyleSheet, ActivityIndicator, Text } from "react-native";
import api from "../api/cliente";

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
    return normalizedPath.substring(uploadsIndex + "/uploads/".length).replace(/^\/+/, "");
  }

  if (normalizedPath.startsWith("uploads/")) {
    return normalizedPath.substring("uploads/".length).replace(/^\/+/, "");
  }

  const imagensIndex = normalizedPath.indexOf("imagens/");
  if (imagensIndex >= 0) {
    return normalizedPath.substring(imagensIndex).replace(/^\/+/, "");
  }

  if (normalizedPath.startsWith("http://") || normalizedPath.startsWith("https://")) {
    return null;
  }

  return normalizedPath.replace(/^\/+/, "");
};

const montarUrlsImagem = (imagePath) => {
  if (!imagePath) {
    return [];
  }

  const normalizedPath = imagePath.trim().replace(/\\/g, "/");
  const baseUrl = (api.defaults.baseURL || "").replace(/\/$/, "");
  const relativePath = normalizarCaminhoRelativo(normalizedPath);

  if (!relativePath) {
    return [normalizedPath];
  }

  const encodedPath = codificarCaminho(relativePath);
  const filename = relativePath.split("/").pop();

  return [
    `${baseUrl}/api/public/images/${codificarCaminho(filename)}`,
    `${baseUrl}/uploads/${encodedPath}`,
  ];
};

export const RemoteImage = ({ imagePath, style, resizeMode = "contain" }) => {
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(false);
  const [urlIndex, setUrlIndex] = useState(0);
  const timeoutRef = useRef(null);
  const imageUrls = montarUrlsImagem(imagePath);
  const imageUrl = imageUrls[urlIndex];

  const limparTimeout = () => {
    if (timeoutRef.current) {
      clearTimeout(timeoutRef.current);
      timeoutRef.current = null;
    }
  };

  const tentarProximaUrl = () => {
    limparTimeout();

    if (urlIndex < imageUrls.length - 1) {
      setUrlIndex((currentIndex) => currentIndex + 1);
    } else {
      setError(true);
      setLoading(false);
    }
  };

  const iniciarTimeout = () => {
    limparTimeout();
    timeoutRef.current = setTimeout(tentarProximaUrl, 15000);
  };

  useEffect(() => {
    setUrlIndex(0);
  }, [imagePath]);

  useEffect(() => {
    setError(false);
    setLoading(!!imageUrl);

    if (imageUrl) {
      iniciarTimeout();
    } else {
      limparTimeout();
    }

    return limparTimeout;
  }, [imageUrl]);

  const handleLoadStart = () => {
    setLoading(true);
    setError(false);
    iniciarTimeout();
  };

  const handleLoadEnd = () => {
    limparTimeout();
    setLoading(false);
  };

  const handleError = (e) => {
    console.log("Erro ao carregar imagem:", imageUrl, e.nativeEvent?.error);
    tentarProximaUrl();
  };

  if (!imageUrl) {
    return null;
  }

  return (
    <View style={[styles.container, style]}>
      <Image
        source={{ uri: imageUrl }}
        style={[styles.image, style]}
        resizeMode={resizeMode}
        onLoadStart={handleLoadStart}
        onLoadEnd={handleLoadEnd}
        onError={handleError}
      />
      {loading && (
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
    position: "absolute",
    width: "100%",
    height: "100%",
    justifyContent: "center",
    alignItems: "center",
    backgroundColor: "rgba(255, 255, 255, 0.8)",
  },
  errorOverlay: {
    position: "absolute",
    width: "100%",
    height: "100%",
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
