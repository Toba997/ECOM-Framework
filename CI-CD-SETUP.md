# 🏢 Full CI/CD Setup — ecom-app → Jenkins → Pipeline Office

This guide wires a **real** pipeline end-to-end:

```
git push ──► GitHub webhook ──► Jenkins ──► mvn clean test (TestNG) ──► POST /api/webhook
                                                                              │
                                                                              ▼
                                                              Node server + DeepSeek ──► Pipeline Office (SSE)
```

Your machine already has **Java 25, Maven 3.9, Git, Node** installed, and the
ecom-app builds + 10 TestNG tests pass locally (`mvn test`).

The project also includes a runnable Spring Boot storefront with an H2 file
database. Start it with:

```powershell
mvn spring-boot:run
```

Then open `http://localhost:8081/`. Products are seeded automatically and
inventory is preserved across restarts. The JSON API is under
`/api/store/products`, `/api/store/cart`, and `/api/store/checkout`.

---

## Step 1 — Push the code to GitHub

You have a GitHub account with no repo yet. Create one and push:

```bash
# in the ecom-app folder
git init
git add .
git commit -m "e-commerce app with TestNG tests + Jenkinsfile"
git branch -M main
git remote add origin https://github.com/<YOUR_USERNAME>/ecom-app.git
git push -u origin main
```

---

## Step 2 — Install Jenkins

> ⚠️ Jenkins needs **JDK 17 or 21** (not 25). Easiest on Windows: the installer
> bundles its own JRE.

1. Download the Windows installer: https://www.jenkins.io/download/
2. Run the `.msi`, finish setup (it installs as a Windows service).
3. Open `http://localhost:8080` and unlock with the initial admin password
   (shown in the installer / `C:\Program Files\Jenkins\secrets\initialAdminPassword`).
4. Install suggested plugins.

### Install these extra plugins
Manage Jenkins → Plugins → Available:
- **Git** plugin
- **Maven Integration** (optional — we call `mvn` directly, but useful)
- **HTTP Request** plugin ← required for the webhook POST
- **GitHub Integration** plugin ← for the `githubPush()` trigger

---

## Step 3 — Create the Jenkins job

1. New Item → **Pipeline** (or **Multibranch Pipeline**) → name it `ecom-app`.
2. Pipeline definition → **Pipeline script from SCM**:
   - SCM: Git
   - Repository URL: `https://github.com/<YOUR_USERNAME>/ecom-app.git`
   - Branch: `*/main`
   - Script Path: `Jenkinsfile`
3. Save.

> If you want GitHub pushes to auto-trigger: install the GitHub Integration
> plugin, uncomment `triggers { githubPush() }` in `Jenkinsfile`, then add a
> GitHub webhook (Step 4).

---

## Step 4 — Add the GitHub webhook

1. GitHub repo → **Settings → Webhooks → Add webhook**
2. Payload URL: `http://<YOUR_MACHINE_IP>:8080/github-webhook/`
   (for local testing you can use a tunnel like `ngrok http 8080`)
3. Content type: `application/json`
4. Events: **Just the push event**

---

## Step 5 — Connect Jenkins to the Pipeline Office

1. Keep the webhook server running (it's already up):
   ```
   node server.js   (or double-click start.bat)
   ```
   Start the store separately when you want the e-commerce site:
   ```powershell
   mvn spring-boot:run
   ```
2. Make sure these match:
   - `Jenkinsfile` → `WEBHOOK_URL` and `WEBHOOK_TOKEN`
   - `.env` → `JENKINS_WEBHOOK_TOKEN`
3. Run a build. The result is POSTed to the webhook server, DeepSeek writes a
   joke, and the 🏢 Pipeline Office animates it live.

---

## How it maps to the Pipeline Office

| Jenkins result | Webhook status | Office effect |
|----------------|----------------|---------------|
| success        | SUCCESS        | BUILD desk → green, confetti + paper planes, boss banter |
| failure        | FAILURE        | BUILD desk → red, blame arrows, boss banter |
| building       | (STARTED)      | BUILD desk → blue |

The office's own **▶ Start** button is just a simulation — real Jenkins builds
now drive it automatically through SSE.

<!-- webhook smoke test: 2026-08-26 12.04.54 -->

<!-- change: 2026-08-26 18.06.11 -->

<!-- re-verify webhook: 18.09.58 -->

<!-- auto-heal verify: 18.16.29 -->

<!-- push: 18.18.41 -->

<!-- live stage test: 18.28.44 -->

<!-- final verify: 18.29.52 -->

<!-- trigger: 18.49.18 -->
trigger build

<!-- seq flow test: 18.59.09 -->

<!-- incremental test: 19.04.54 -->

<!-- test now: 19.06.51 -->

<!-- final check: 19.08.41 -->

<!-- test: 19.11.07 -->
