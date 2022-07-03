import { blue } from "@mui/material/colors";
import { createTheme } from "@mui/system";

export const colors = {
  primary: blue[700],
  secondary: blue[300],
  white: "#fff",
  black: "#000",
};

const theme = createTheme({
  palette: {
    primary: {
      main: colors.primary,
    },
    secondary: {
      main: colors.secondary,
    },
  },
});

export default theme;
