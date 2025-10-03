# 🔐 GUÍA DEFINITIVA: Configurar Secrets en GitHub

## 📋 ¿QUÉ SECRETS NECESITAS?

### Para `ci-cd-complete.yml`:
```
🎉 NO NECESITAS NINGÚN SECRET
✅ Usa GITHUB_TOKEN automáticamente
✅ Solo haz push y funciona
```

### Para `ci-cd-sonarcloud.yml`:
```
⚠️ NECESITAS 3 SECRETS
├── SONAR_TOKEN
├── SONAR_PROJECT_KEY  
└── SONAR_ORGANIZATION
```

---

## 🚀 PASO A PASO: Configurar Secrets en GitHub

### 📍 PASO 1: Ir a tu repositorio en GitHub

```
1. Abre: https://github.com/FaviohuamanVG/Prueba
2. (Tu repositorio actual según la imagen)
```

---

### 📍 PASO 2: Ir a Settings

```
┌─────────────────────────────────────────┐
│  Code  Issues  Pull requests  Actions  │  ← Barra superior
│                                         │
│            Settings  ←  Click aquí     │
└─────────────────────────────────────────┘

📍 Si no ves "Settings", verifica que seas owner/admin del repo
```

---

### 📍 PASO 3: Ir a Secrets and variables

```
Settings (sidebar izquierdo)
├── General
├── Access
├── Code and automation
│   ├── Actions  ← Click aquí
│   │   ├── General
│   │   └── Secrets and variables  ← Luego aquí
│   │       └── Actions  ← Y finalmente aquí
```

**Ruta completa:**
```
Settings → Secrets and variables → Actions
```

---

### 📍 PASO 4: Crear un Secret

```
┌──────────────────────────────────────────┐
│  Actions secrets                         │
├──────────────────────────────────────────┤
│                                          │
│  🟢 New repository secret  ← Click aquí │
│                                          │
└──────────────────────────────────────────┘
```

---

### 📍 PASO 5: Llenar el formulario

```
┌──────────────────────────────────────────┐
│  New secret                              │
├──────────────────────────────────────────┤
│  Name *                                  │
│  ┌────────────────────────────────────┐ │
│  │ SONAR_TOKEN                        │ │  ← Nombre exacto
│  └────────────────────────────────────┘ │
│                                          │
│  Secret *                                │
│  ┌────────────────────────────────────┐ │
│  │ squ_abc123...                      │ │  ← El valor del token
│  └────────────────────────────────────┘ │
│                                          │
│       [Add secret]  ← Click para guardar│
└──────────────────────────────────────────┘
```

---

## 🎯 LOS 3 SECRETS QUE NECESITAS

### 🔑 SECRET 1: SONAR_TOKEN

#### ¿Dónde obtenerlo?

```
1. Ve a: https://sonarcloud.io
2. Click en "Log in" → "Log in with GitHub"
3. Autoriza SonarCloud
4. Click en tu avatar (arriba derecha)
5. "My Account" → "Security"
6. "Generate Tokens"
   - Name: GitHub Actions
   - Type: Global Analysis Token
   - Expires in: No expiration (recomendado)
7. Click "Generate"
8. ⚠️ COPIA EL TOKEN (solo se muestra una vez)
```

#### Ejemplo de valor:
```
squ_1a2b3c4d5e6f7g8h9i0j1k2l3m4n5o6p7q8r9s0t
```

#### En GitHub:
```
Name:   SONAR_TOKEN
Secret: squ_1a2b3c4d5e6f7g8h9i0j1k2l3m4n5o6p7q8r9s0t
```

---

### 🔑 SECRET 2: SONAR_PROJECT_KEY

#### ¿Dónde obtenerlo?

```
1. En SonarCloud, después de importar tu proyecto
2. Ve a tu proyecto "Prueba" (o como lo hayas nombrado)
3. En el sidebar izquierdo: "Project Information"
4. Verás: "Project Key: XXXXXX"
5. Copia ese valor
```

#### Formato típico:
```
FaviohuamanVG_Prueba
[tu-usuario-github]_[nombre-repo]
```

#### En GitHub:
```
Name:   SONAR_PROJECT_KEY
Secret: FaviohuamanVG_Prueba
```

---

### 🔑 SECRET 3: SONAR_ORGANIZATION

#### ¿Dónde obtenerlo?

```
1. En SonarCloud
2. Click en tu avatar → "My Organizations"
3. Verás tu organización con un "Key"
4. Copia ese "Key" (no el "Name")
```

#### Formato típico:
```
faviohuamanvg
[tu-usuario-github en minúsculas]
```

#### En GitHub:
```
Name:   SONAR_ORGANIZATION
Secret: faviohuamanvg
```

---

## ✅ VERIFICAR QUE ESTÉN CONFIGURADOS

Después de crear los 3 secrets, deberías ver:

```
┌──────────────────────────────────────────┐
│  Repository secrets                      │
├──────────────────────────────────────────┤
│  SONAR_TOKEN          Updated 1 min ago │
│  SONAR_PROJECT_KEY    Updated 1 min ago │
│  SONAR_ORGANIZATION   Updated 1 min ago │
└──────────────────────────────────────────┘
```

---

## 🔒 PERMISOS DE WORKFLOW (IMPORTANTE)

### También necesitas habilitar permisos:

```
Settings → Actions → General
Scroll down hasta "Workflow permissions"

Selecciona:
✅ Read and write permissions

✅ Allow GitHub Actions to create and approve pull requests

Click "Save"
```

**Esto permite que:**
- GITHUB_TOKEN pueda publicar en GHCR
- Los workflows puedan hacer commits si es necesario

---

## 🎮 COMANDOS PARA PROBAR

### Después de configurar secrets:

```powershell
# En tu terminal
cd C:\Users\lucio\Music\mc_JaCoCo\ms-catalog

# Instalar dependencia
mvn clean install -DskipTests

# Commit
git add .
git commit -m "🔍 Configure CI/CD pipelines with SonarCloud"

# Push a develop
git push origin develop
```

---

## 👀 VER EL WORKFLOW EJECUTÁNDOSE

```
1. Ve a: https://github.com/FaviohuamanVG/Prueba/actions
2. Verás el workflow ejecutándose
3. Click en el workflow para ver detalles
4. Si falla por secrets, revisa:
   - Que los nombres sean EXACTOS (case-sensitive)
   - Que los valores estén correctos
   - Que estén en "Actions secrets" (no "Dependabot secrets")
```

---

## ❌ ERRORES COMUNES

### Error: "Secret not found"
```
✅ Verifica nombres EXACTOS:
   - SONAR_TOKEN (no sonar_token, ni SonarToken)
   - SONAR_PROJECT_KEY (no SONAR_PROJECT)
   - SONAR_ORGANIZATION (no SONAR_ORG)
```

### Error: "Invalid token"
```
✅ Regenera el token en SonarCloud
✅ Asegúrate de copiar TODO el token (empieza con "squ_")
✅ No dejes espacios al inicio/final
```

### Error: "Project not found"
```
✅ Verifica que el PROJECT_KEY sea exacto
✅ En SonarCloud: Project Information → Project Key
✅ Copia exactamente como aparece
```

---

## 🔄 FLUJO COMPLETO VISUAL

```
┌─────────────────────────────────────────────────────┐
│  1. SonarCloud                                      │
│     ↓                                               │
│     Generar SONAR_TOKEN                             │
│     Obtener SONAR_PROJECT_KEY                       │
│     Obtener SONAR_ORGANIZATION                      │
│                                                     │
├─────────────────────────────────────────────────────┤
│  2. GitHub                                          │
│     ↓                                               │
│     Settings → Secrets → Actions                    │
│     Crear los 3 secrets                             │
│     Configurar workflow permissions                 │
│                                                     │
├─────────────────────────────────────────────────────┤
│  3. Local                                           │
│     ↓                                               │
│     git add .                                       │
│     git commit -m "Configure pipelines"             │
│     git push origin develop                         │
│                                                     │
├─────────────────────────────────────────────────────┤
│  4. GitHub Actions                                  │
│     ↓                                               │
│     Workflow se ejecuta automáticamente             │
│     Tests → SonarCloud → Docker → Security          │
│     ✅ Todo verde = Éxito                           │
└─────────────────────────────────────────────────────┘
```

---

## 📊 COMPARATIVA: Qué workflow usar

### Para AHORA (sin configurar secrets):

```powershell
# Usa ci-cd-complete.yml
git push origin develop

✅ 0 secrets necesarios
✅ Docker automático
✅ Security scan
❌ Sin análisis de calidad
```

### Para DESPUÉS (con secrets configurados):

```powershell
# Usa ci-cd-sonarcloud.yml
# (Primero configura los 3 secrets)
git push origin develop

✅ Todo incluido
✅ SonarCloud
✅ Quality gates
✅ PR decoration
```

---

## 🎯 MI RECOMENDACIÓN PARA TI

### OPCIÓN A: Rápido (5 minutos)

```powershell
# No configures secrets aún
# Solo usa ci-cd-complete.yml

mvn clean install -DskipTests
git add .
git commit -m "🚀 Add complete CI/CD pipeline"
git push origin develop

# En GitHub Actions verás el workflow correr
# Docker se construirá automáticamente
```

### OPCIÓN B: Completo (20 minutos)

```powershell
# 1. Configurar SonarCloud (10 min)
#    Crear cuenta
#    Importar repo
#    Obtener tokens

# 2. Configurar secrets en GitHub (5 min)
#    Crear los 3 secrets

# 3. Push
mvn clean install -DskipTests
git add .
git commit -m "🔍 Add CI/CD with SonarCloud"
git push origin develop
```

---

## 💡 TIP: Probar sin SonarCloud primero

```
1. Primero prueba ci-cd-complete.yml (sin secrets)
2. Verifica que todo funcione
3. Luego configura SonarCloud
4. Cambia a ci-cd-sonarcloud.yml
```

**Ventaja:** Sabes que el problema es solo de SonarCloud si algo falla

---

## 📞 SI ALGO FALLA

### 1. Revisa logs en GitHub Actions
```
Actions → Click en el workflow → Click en el job que falló
Lee el error específico
```

### 2. Errores de Secrets
```
Settings → Secrets → Actions
Verifica que los 3 estén ahí
Borra y recrea si es necesario
```

### 3. Prueba local
```powershell
mvn clean test
# Si falla local, arregla primero
```

---

## ✅ CHECKLIST FINAL

Antes de push, verifica:

- [ ] Instalé dependencia Actuator: `mvn clean install -DskipTests`
- [ ] Decidí qué workflow usar
- [ ] Si uso SonarCloud, configuré los 3 secrets
- [ ] Configuré permisos de workflow (Read and write)
- [ ] Estoy en rama develop (tu caso)
- [ ] Tests pasan local: `mvn test`

---

<div align="center">

# 🎯 RESUMEN PARA TU CASO

**Tu rama:** develop ✅ (ya configurado)

**Tus opciones:**
1. `ci-cd-complete.yml` → 0 secrets (recomendado ahora)
2. `ci-cd-sonarcloud.yml` → 3 secrets (después)

**Siguiente paso:**
```powershell
git push origin develop
```

**URL para configurar secrets:**
```
https://github.com/FaviohuamanVG/Prueba/settings/secrets/actions
```

</div>
