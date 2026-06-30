import { BrowserRouter, Route, Routes } from 'react-router-dom'
import WorkspacesPage from './pages/WorkspacesPage'
import FormsPage from './pages/FormsPage'
import FormBuilderPage from './pages/FormBuilderPage'
import SubmissionsPage from './pages/SubmissionsPage'

export default function App() {
  return (
    <BrowserRouter>
      <Routes>
        <Route path="/" element={<WorkspacesPage />} />
        <Route path="/workspaces/:workspaceId/forms" element={<FormsPage />} />
        <Route path="/forms/:formId/builder" element={<FormBuilderPage />} />
        <Route path="/forms/:formId/submissions" element={<SubmissionsPage />} />
      </Routes>
    </BrowserRouter>
  )
}