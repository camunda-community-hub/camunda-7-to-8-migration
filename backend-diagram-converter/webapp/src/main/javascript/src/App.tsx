import React from "react";
import { BPMNProvider } from "./context/BPMNContext";
import BPMNFlow from "./pages/BPMNFlow";

const App: React.FC = () => {
  return (
    <BPMNProvider>
      <div className="min-h-screen bg-gray-100">
        <BPMNFlow />
      </div>
    </BPMNProvider>
  );
};

export default App;
