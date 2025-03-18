import React from "react";
import { useBPMN } from "../context/useBPMN";
import { FaCheckCircle, FaDownload } from "react-icons/fa";

const ResultsStep: React.FC = () => {
  const { setStep } = useBPMN();

  return (
    <div>
      {/* Success Banner */}
      <div className="bg-green-100 border border-green-400 text-green-700 p-3 rounded-md flex items-center">
        <FaCheckCircle className="mr-2" />
        <span>Analysis complete - See your analyzed files and take the relevant actions</span>
      </div>

      {/* Download Button */}
      <div className="mt-6">
        <h3 className="text-lg font-semibold">Analyze results</h3>
        <p className="text-sm text-gray-600">
          Once your files are analyzed, you can download the template to see the results.
        </p>
        <button className="mt-4 px-6 py-2 bg-blue-600 text-white rounded shadow hover:bg-blue-700 flex items-center">
          <FaDownload className="mr-2" /> Download XLS template
        </button>
      </div>

      {/* Next Step Button */}
      <button
        className="mt-6 px-6 py-2 bg-gray-700 text-white rounded shadow hover:bg-gray-900"
        onClick={() => setStep(4)}
      >
        Proceed to Next Steps
      </button>
    </div>
  );
};

export default ResultsStep;
