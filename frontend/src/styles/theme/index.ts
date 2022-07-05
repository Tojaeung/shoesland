import { blue } from '@mui/material/colors';
import { createTheme } from '@mui/material/styles';

const theme = createTheme({
  palette: {
    primary: {
      main: blue[700],
    },
    secondary: {
      main: blue[200],
    },
  },

  components: {
    MuiLink: {
      defaultProps: {
        underline: 'none',
        color: 'inherit',
      },
    },
  },
});

export default theme;
