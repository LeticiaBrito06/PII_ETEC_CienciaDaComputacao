import React from "react";
import { StyleSheet, View } from "react-native";
import { Card, Text, IconButton } from "react-native-paper";

export const MenuCard = ({ title, subtitle, icon, onPress }) => (
  <Card style={styles.card} onPress={onPress}>
    <Card.Content style={styles.content}>
      <IconButton icon={icon} size={40} iconColor="#1A237E" />
      <View>
        <Text variant="titleLarge" style={styles.title}>
          {title}
        </Text>
        <Text variant="bodyMedium" style={styles.subtitle}>
          {subtitle}
        </Text>
      </View>
    </Card.Content>
  </Card>
);

const styles = StyleSheet.create({
  card: {
    marginBottom: 16,
    borderRadius: 16,
    elevation: 2,
  },
  content: {
    flexDirection: "row",
    alignItems: "center",
    paddingVertical: 12,
  },
  title: {
    color: "#1A237E",
    fontWeight: "600",
  },
  subtitle: {
    color: "#666",
    maxWidth: "85%",
  },
});
