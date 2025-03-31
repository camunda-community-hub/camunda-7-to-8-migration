import React from "react";
import { useBPMN } from "../context/useBPMN";
import Stepper from "../components/Stepper";
import UploadStep from "../components/UploadStep";
import AnalyzingStep from "../components/AnalyzingStep";
import ResultsStep from "../components/ResultsStep";
import PrepareStep from "../components/PrepareStep";    

const BPMNFlow: React.FC = () => {
  const { step } = useBPMN();

  return (
    <div className="flex flex-col items-center p-6 bg-gray-100 min-h-screen">
        {/* Header Section */}
        <h2 className="text-lg font-semibold text-center">
        Seamlessly analyze your BPMN models. Get detailed insights and ready-to-use templates in just a few clicks.
        </h2>

        <div className="p-6 bg-gray-100 min-h-screen">
            <Stepper />
            <div className="bg-white p-6 rounded shadow-md mt-4">
                {step === 1 && <UploadStep />}
                {step === 2 && <AnalyzingStep />}
                {step === 3 && <ResultsStep />}
                {step === 4 && <PrepareStep />}
            </div>
        </div>
    </div>
  );
};

export default BPMNFlow;
