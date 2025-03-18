import React, { useState } from "react";
import { BPMNContext } from "./BPMNContextDefinition";

// Provider Component
export const BPMNProvider: React.FC<{ children: React.ReactNode }> = ({ children }) => {
  const [step, setStep] = useState(1);
  const [files, setFiles] = useState<File[]>([]);
  const [analysisComplete, setAnalysisComplete] = useState(false);

  return (
    <BPMNContext.Provider value={{ step, setStep, files, setFiles, analysisComplete, setAnalysisComplete }}>
      {children}
    </BPMNContext.Provider>
  );
};
