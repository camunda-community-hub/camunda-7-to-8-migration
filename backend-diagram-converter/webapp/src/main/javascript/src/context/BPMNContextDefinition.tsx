import { createContext } from "react";

type BPMNContextType = {
  step: number;
  setStep: (step: number) => void;
  files: File[];
  setFiles: (files: File[]) => void;
  analysisComplete: boolean;
  setAnalysisComplete: (complete: boolean) => void;
};

export const BPMNContext = createContext<BPMNContextType | undefined>(undefined);
