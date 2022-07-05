import React from 'react';
import { BrowserRouter, Routes, Route } from 'react-router-dom';
import { Box } from '@mui/material';

import Appbar from 'components/Appbar';

import Home from 'pages/Home';

function App() {
  return (
    <BrowserRouter>
      <Appbar />
      <Box bgcolor="secondary.main">
        <Routes>
          <Route path="/" element={<Home />} />
        </Routes>
      </Box>
    </BrowserRouter>
  );
}

export default App;
