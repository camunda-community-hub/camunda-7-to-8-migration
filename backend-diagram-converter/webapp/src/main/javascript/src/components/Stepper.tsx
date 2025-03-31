import React from "react";
import { useBPMN } from "../context/useBPMN";
import { FaCheckCircle } from "react-icons/fa";

const Stepper: React.FC = () => {
  const { step } = useBPMN();
  return (
    <div className="bg-white p-6 rounded-lg shadow-md mt-6 w-full max-w-2xl">
        <div className="flex items-center justify-between mb-4">
            <div className={`font-semibold ${step >= 1 ? "text-blue-600" : "text-gray-400"}`}>
                <FaCheckCircle className="inline mr-2" /> Upload your models
            </div>
            <div className={`font-semibold ${step >= 2 ? "text-blue-600" : "text-gray-400"}`}>
                <FaCheckCircle className="inline mr-2" /> Analyze your models
            </div>
            <div className={`font-semibold ${step === 4 ? "text-blue-600" : "text-gray-400"}`}>
                <FaCheckCircle className="inline mr-2" /> Prepare for XXX
            </div>
        </div>
    </div>
  );
};

export default Stepper;
