import { useContext } from "react";
import { BPMNContext } from "./BPMNContextDefinition";

// Custom Hook to use the BPMN Context
export const useBPMN = () => {
  const context = useContext(BPMNContext);
  if (!context) {
    throw new Error("useBPMN must be used within BPMNProvider");
  }
  return context;
};
