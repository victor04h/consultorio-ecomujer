import { createTheme } from "@mui/material/styles";

const theme = createTheme({
  palette: {
  primary: {
    main: "#E889A6",
    dark: "#C94F78",
    light: "#F5B4C7",
  },
  secondary: {
    main: "#48C4BE",
    dark: "#268F8B",
    light: "#DDF5F1",
  },
  background: {
    default: "#F8F0F1",
    paper: "#FFFFFF",
  },
  text: {
    primary: "#343638",
    secondary: "#72777A",
    disabled: "#A7AAAC",
  },
  divider: "#E8DDE0",
  success: { main: "#3A9B75" },
  warning: { main: "#D99A36" },
  error: { main: "#C84E5A" },
  info: { main: "#398D9A" },
},
  shape: { borderRadius: 10 },
  typography: {
    fontFamily: '"Roboto", "Segoe UI", Arial, sans-serif',
  },
});

export default theme;
