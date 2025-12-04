import { useState } from 'react'
import { uploadExcel } from '../api/client'
import { useAuth } from '../context/AuthContext'

function ExcelUploadPage() {
  const { isAdmin, user } = useAuth()
  const [file, setFile] = useState(null)
  const [loading, setLoading] = useState(false)
  const [error, setError] = useState('')
  const [result, setResult] = useState(null)

  const handleSubmit = async (event) => {
    event.preventDefault()
    if (!file) {
      setError('Выберите Excel файл для загрузки')
      return
    }
    setError('')
    setResult(null)
    setLoading(true)
    try {
      const response = await uploadExcel(file)
      setResult(response)
    } catch (e) {
      setError(e.message || 'Ошибка загрузки файла')
    } finally {
      setLoading(false)
    }
  }

  return (
    <div>
      <form className="form-card" onSubmit={handleSubmit}>
        <div className="form-title">Загрузка Excel отчёта</div>
        <div className="form-field">
          <label className="form-label">Роль</label>
          <div className="chip-muted">
            <span>{isAdmin ? 'ADMIN ' : 'USER'}</span>
          </div>
        </div>
        <div className="form-field">
          <label className="form-label" htmlFor="file">
            Excel файл подразделения
          </label>
          <input
            id="file"
            type="file"
            className="form-input"
            accept=".xls,.xlsx,.xlsm"
            onChange={(event) => setFile(event.target.files?.[0] || null)}
          />
        </div>
        {error && <div className="form-error">{error}</div>}
        <button
          type="submit"
          className="app-button app-button-primary"
          style={{ width: '100%', marginTop: 10 }}
          disabled={loading}
        >
          {loading ? 'Загрузка...' : 'Upload'}
        </button>
        {result && (
          <div style={{ marginTop: 16, fontSize: 13 }}>
            <div>Результат загрузки:</div>
            <ul style={{ paddingLeft: 18, marginTop: 6 }}>
              <li>
                Всего строк: {result.totalRows != null ? result.totalRows : '—'}
              </li>
              <li>
                Успешно импортировано задач:{' '}
                {result.successCount != null ? result.successCount : '—'}
              </li>
              <li>
                Ошибок при импорте:{' '}
                {result.errorCount != null ? result.errorCount : '—'}
              </li>
            </ul>
            {Array.isArray(result.errors) && result.errors.length > 0 && (
              <div style={{ marginTop: 8 }}>
                <div style={{ fontSize: 12, color: '#fca5a5' }}>
                  Детали ошибок:
                </div>
                <ul style={{ paddingLeft: 18, marginTop: 4 }}>
                  {result.errors.map((err, index) => (
                    <li key={index}>{err}</li>
                  ))}
                </ul>
              </div>
            )}
          </div>
        )}
      </form>
    </div>
  )
}

export default ExcelUploadPage
