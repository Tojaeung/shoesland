import React from "react";
import { BrowserRouter, Routes, Route } from "react-router-dom";
import Appbar from "components/Appbar";

import Home from "pages/Home";

function App() {
  return (
    <BrowserRouter>
      <Appbar />
      <Routes>
        <Route path="/" element={<Home />} />
      </Routes>
    </BrowserRouter>
  );
}

export default App;
