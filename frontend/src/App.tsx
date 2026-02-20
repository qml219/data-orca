import { useState } from 'react'
import './App.css'
import { BrowserRouter as Router, Routes, Route } from 'react-router-dom';
import HomePage from '@/components/HomePage/HomePage';
import Login from '@/components/Login/Login';
import AppLayout from './components/AppLayout/AppLayout';
import { ThemeProvider } from '@emotion/react';
import { orcaTheme as theme } from '@/components/Theme/Theme';
import { AuthProvider } from './components/auth/auth.provider';
import { WorkspaceProvider } from './components/contexts/workspace/workspace.provider';
import ProtectedRoute from './components/ProtectedRoute/ProtectedRoute';
import TableUI from './components/TableUI/TablesPage';
import TablesPage from './components/TableUI/TablesPage';
import type { TableMetadata } from './components/TableUI/TableCardsGrid/TableCard/TableCard';
import TableCardsGrid from './components/TableUI/TableCardsGrid/TableCardsGrid';
import ImportTableButton from './components/ImportTableButton/ImportTableButton';

// const proxyTableData: TableMetadata[] = [
//     {
//         tableName: "APQ1",
//         tableDescription: "Account Payable Quarter 1",
//         // tableColumnCount: 4,
//         // tableCount: 1000,
//         // lastModified: new Date()
//     },
//     {
//         tableName: "ARQ1",
//         tableDescription: "Account Receivable Quarter 1",
//         // tableColumnCount: 4,
//         // tableCount: 1000,
//         // lastModified: new Date()
//     },
//     {
//         tableName: "table 1",
//         tableDescription: "table 1",
//         // tableColumnCount: 4,
//         // tableCount: 1000,
//         // lastModified: new Date()
//     },
//     {
//         tableName: "table 1",
//         tableDescription: "table 1",
//         // tableColumnCount: 4,
//         // tableCount: 1000,
//         // lastModified: new Date()
//     },
//     {
//         tableName: "table 1",
//         tableDescription: "table 1",
//         // tableColumnCount: 4,
//         // tableCount: 1000,
//         // lastModified: new Date()
//     },
//     {
//         tableName: "table 1",
//         tableDescription: "table 1",
//         // tableColumnCount: 4,
//         // tableCount: 1000,
//         // lastModified: new Date()
//     }
// ]

function App() {
  // const [count, setCount] = useState(0)

  return (
    <ThemeProvider theme={theme}>
      <Router>
        <AuthProvider>
          <Routes>
            
            <Route path="/" element={<Login/>}/>

            <Route element={<ProtectedRoute/>}> 
              <Route element={<WorkspaceProvider><AppLayout></AppLayout></WorkspaceProvider>}>
                  <Route path="/home" element={<HomePage/>}/>
                  <Route path="/tables" element={
                    <TablesPage
                      headers={<h1>This is the table page header</h1>}
                      controls={
                        // <span>This is the control area</span>
                        <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', border: '1px solid'}}>
                          <span>
                            This is the control area
                          </span>
                          <ImportTableButton></ImportTableButton>
                        </div>
                      }
                    />
                  }/>
              </Route>
            </Route>
          </Routes>
        </AuthProvider>
      </Router>
    </ThemeProvider>
  )
}

export default App
