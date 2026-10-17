import { useState } from "react";
import { useNavigate } from "react-router-dom";
import Alert from "@mui/material/Alert";
import Box from "@mui/material/Box";
import Button from "@mui/material/Button";
import Paper from "@mui/material/Paper";
import logo from "../assets/logo_EcoMujer.png";
import TextField from "@mui/material/TextField";
import Typography from "@mui/material/Typography";
import CircularProgress from "@mui/material/CircularProgress";
import { useAuth } from "../context/AuthContext";
import { mensajeError } from "../api/client";

export default function LoginPage() {
  const { login } = useAuth();
  const navigate = useNavigate();
  const [correo, setCorreo] = useState("");
  const [contrasena, setContrasena] = useState("");
  const [error, setError] = useState("");
  const [cargando, setCargando] = useState(false);

  const manejarEnvio = async (evento) => {
    evento.preventDefault();
    setError("");
    setCargando(true);
    try {
      await login(correo, contrasena);
      navigate("/", { replace: true });
    } catch (err) {
      setError(mensajeError(err, "Correo o contraseña incorrectos"));
    } finally {
      setCargando(false);
    }
  };

  return (
    <Box
      sx={{
        minHeight: "100vh",
        display: "flex",
        alignItems: "center",
        justifyContent: "center",
        background: "linear-gradient(135deg, #F8F0F1 0%, #E889A6 100%)",
        p: 2,
      }}
    >
      <Paper elevation={6} sx={{ p: 4, width: "100%", maxWidth: 400, borderRadius: 3 }}>
        <Box sx={{ display: "flex", flexDirection: "column", alignItems: "center", mb: 3 }}>
          <img src={logo} alt="Eco Mujer" style={{ width: 100, height: 100, marginBottom: 16 }} />
          <Typography variant="h5" fontWeight={700} sx={{ mt: 1 }}>
            Eco Mujer
          </Typography>
          <Typography variant="body2" color="text.secondary">
            Panel administrativo
          </Typography>
        </Box>

        {error && (
          <Alert severity="error" sx={{ mb: 2 }}>
            {error}
          </Alert>
        )}

        <Box component="form" onSubmit={manejarEnvio} sx={{ display: "flex", flexDirection: "column", gap: 2 }}>
          <TextField
            label="Correo electrónico"
            type="email"
            value={correo}
            onChange={(e) => setCorreo(e.target.value)}
            required
            fullWidth
          />
          <TextField
            label="Contraseña"
            type="password"
            value={contrasena}
            onChange={(e) => setContrasena(e.target.value)}
            required
            fullWidth
          />
          <Button type="submit" variant="contained" size="large" disabled={cargando}>
            {cargando ? <CircularProgress size={24} color="inherit" /> : "Iniciar sesión"}
          </Button>
        </Box>
      </Paper>
    </Box>
  );
}
