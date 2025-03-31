import React, { useEffect } from "react";
import { useBPMN } from "../context/useBPMN";

const AnalyzingStep: React.FC = () => {
  const { setStep, setAnalysisComplete } = useBPMN();

  useEffect(() => {
    setTimeout(() => {
      setAnalysisComplete(true);
      setStep(3);
    }, 5000); // Simulate analysis process
  }, [setStep, setAnalysisComplete]);

  return (
    <div>
      <h3 className="text-lg font-semibold">Analyzing...</h3>
      <p>Sit tight while we analyze your files.</p>
    </div>
  );
};

export default AnalyzingStep;
