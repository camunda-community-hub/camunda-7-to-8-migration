import React from "react";
// import { useBPMN } from "../context/useBPMN";
import { FaExternalLinkAlt, FaCheckCircle } from "react-icons/fa";

const PrepareStep: React.FC = () => {
  return (
    <div>
      {/* Success Banner */}
      <div className="bg-green-100 border border-green-400 text-green-700 p-3 rounded-md flex items-center">
        <FaCheckCircle className="mr-2" />
        <span>Analysis complete - Prepare for the next steps</span>
      </div>

      {/* Migration Guide */}
      <div className="mt-6">
        <h3 className="text-lg font-semibold">Migration Guide</h3>
        <p className="text-sm text-gray-600">
          Information about the guide and why it will help.
        </p>
        <a
          href="#"
          className="mt-2 px-4 py-2 border border-blue-600 text-blue-600 rounded flex items-center w-fit hover:bg-blue-100"
        >
          Read migration guide <FaExternalLinkAlt className="ml-2" />
        </a>
      </div>

      {/* AI Tutorial */}
      <div className="mt-6">
        <h3 className="text-lg font-semibold">AI Tutorial</h3>
        <p className="text-sm text-gray-600">
          Information about the AI tutorial and why it will help.
        </p>
        <a
          href="#"
          className="mt-2 px-4 py-2 border border-blue-600 text-blue-600 rounded flex items-center w-fit hover:bg-blue-100"
        >
          Watch AI tutorial <FaExternalLinkAlt className="ml-2" />
        </a>
      </div>
    </div>
  );
};

export default PrepareStep;
