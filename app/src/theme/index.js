import { DefaultTheme } from 'react-native-paper';

export const theme = {
  ...DefaultTheme,
  colors: {
    ...DefaultTheme.colors,
    primary: '#1A237E',
    secondary: '#303F9F',
    background: '#E8EAF6',
    surface: '#FFFFFF',
    error: '#B00020',
    text: '#000000',
    onPrimary: '#FFFFFF',
    onSecondary: '#FFFFFF',
    onSurface: '#000000',
  },
  roundness: 12,
};
