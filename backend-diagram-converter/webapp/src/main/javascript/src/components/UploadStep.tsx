import React, { useState, DragEvent } from "react";
import { useBPMN } from "../context/useBPMN";
import { FaFileAlt, FaUpload } from "react-icons/fa";

const UploadStep: React.FC = () => {
  const { files, setFiles, setStep } = useBPMN();
  const [isDragging, setIsDragging] = useState(false);

  const createFormData = async () => {
    const formData = new FormData();
    if (files.length === 0) {
      alert("Please select a .bpmn file");
      return null;
    }
    console.log(files[0])
    formData.append("file", files[0]);
    // formData.append("appendDocumentation", appendDocumentation.checked);
    // formData.append("defaultJobTypeEnabled", defaultJobTypeEnabled.checked)
    return formData;
  };

  const handleFileChange = (event: React.ChangeEvent<HTMLInputElement>) => {
    if (event.target.files) {
      setFiles(Array.from(event.target.files));
    }
  };


  // Handle drag-over event
  const handleDragOver = (event: DragEvent<HTMLDivElement>) => {
    event.preventDefault();
    setIsDragging(true);
  };

  // Handle drag leave
  const handleDragLeave = () => {
    setIsDragging(false);
  };

  // Handle file drop
  const handleDrop = (event: DragEvent<HTMLDivElement>) => {
    event.preventDefault();
    setIsDragging(false);

    if (event.dataTransfer.files) {
      setFiles([...files, ...Array.from(event.dataTransfer.files)]);
    }
  };

  return (
    <div>
      <div>
        <h3 className="text-lg font-semibold">Upload your BPMN models</h3>

        {/* Drag and Drop Area */}
        <div
          className={`border-2 border-dashed rounded-md p-6 mt-4 text-center transition-all ${isDragging ? "border-blue-500 bg-blue-50" : "border-gray-400 bg-gray-50"
            }`}
          onDragOver={handleDragOver}
          onDragLeave={handleDragLeave}
          onDrop={handleDrop}
        >
          <label className="cursor-pointer flex flex-col items-center">
            <FaUpload className="text-gray-500 text-3xl mx-auto mb-2" />
            <p className="text-black-600 font-semibold">Click or drag files here to upload</p>
            <p className="text-gray-400">Upload BPMN or zip files</p>
            <input type="file" multiple onChange={handleFileChange} className="hidden" />
          </label>
        </div>

        {/* Uploaded Files List */}
        {files.length > 0 && (
          <div className="mt-4">
            {files.map((file, index) => (
              <div key={index} className="flex items-center space-x-2 text-gray-700">
                <FaFileAlt className="text-blue-500" />
                <span className="text-sm">{file.name}</span>
              </div>
            ))}
          </div>
        )}
        <h3 className="text-lg font-semibold text-gray-800">Instructions:</h3>
        <p className="text-gray-600 mt-2">
          Upload your BPMN models.
        </p>
        <p className="text-gray-600 mt-2">
          Here are some FAQs about the product.
        </p>
        <p className="text-gray-600 mt-2">
          Data privacy information for users.
        </p>

        <button className="mt-6 bg-blue-600 text-white px-6 py-2 rounded-md shadow-md hover:bg-blue-700"
          onClick={async () => {
            const formData = await createFormData();
            if (!formData) {
              return;
            }
            console.log(formData) 
            // Accept application/bpmn+xml
            const response = await fetch(`/convert`, {
              body: formData,
              method: "POST",
              headers: {
                Accept: "application/bpmn+xml",
              },
              mode: "no-cors"
            })
            console.log(response)
            const json = await response.text()
            console.log(json)
            setStep(2)
          }}
        >
          Analyze and convert
        </button>
      </div>
    </div>
  );
};

export default UploadStep;
