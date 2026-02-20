import { Button } from "@mui/material";
import Papa from "papaparse";
import { useState } from "react";
export default function CsvUploadStep({ previewRows, onFileSelected }: { previewRows: number, onFileSelected: (f: File, p: string[][]) => void }) {

    const handleFile = (file: File) => {

        const rows: string[][] = [];
        let header: string[] | null = null;


        Papa.parse(file, {
            worker: true,
            skipEmptyLines: true,
            step: (result, parser) => {
                
                const row = result.data as string[];
                let header: string[] | null = null;

                if (!header) {
                    header = row;
                    rows.push(row);
                    return;
                }

                rows.push(row);

                if (rows.length > previewRows) {
                    parser.abort();
                }

            },
            complete: () => {
                onFileSelected(file, rows);
            }
        });
    };

    return (<Button variant="outlined" component="label">
        Choose CSV
        <input
            type="file"
            accept=".csv"
            hidden
            onChange={(e) => { 
                console.log(e.target.files![0]);
                handleFile(e.target.files![0]);
            }}
        />
    </Button>)
}