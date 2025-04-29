<details>
<summary><strong>✅ pre-checks.yml</strong> — Run Credential Scanning, Commit validation, License scanning and Upload source code</summary>


### 📄 About

This reusable workflow performs:

- 🔐 Credential scanning (via Gitleaks)  
- ✅ Commit sign-off validation  
- 🧾 license scanning using `license_finder`  
- 📦 Optional artifact upload of the source code for reuse in later stages

### 🔧 Usage

```yaml
jobs:
  pre_checks:
    uses: NavabShariff/shared-library/.github/workflows/pre-checks.yml@main
    with:
      commit_sign_off: true
      commit_message: false
      credential_scan: true
      license_scanning: true
      license_decision_file: 'doc/dependency_decisions.yml'
      upload_artifacts: true
      artifact_name: 'source-code'
```

### 🎛️ Inputs

| Name                  | Type     | Required | Default                        | Description |
|-----------------------|----------|----------|--------------------------------|-------------|
| `commit_sign_off`     | boolean   | ✅ Yes   | true                             | Whether to enforce signed commits (`true` or `false`) |
| `commit_message`      | boolean   | No       | –                              | Commit message validation string or regex (if needed) |
| `license_scanning`    | boolean  | ✅ Yes       | `true`                        | Run `license_finder` to check OSS licenses |
| `license_decision_file` | string | ✅ Yes       | `doc/dependency_decisions.yml` | Path to the ORT/LicenseFinder decisions file |
| `credential_scan` | boolean | ✅ Yes       | `true` | Whether to do credential scanning (`true` or `false`) |
| `upload_artifacts`    | boolean  | No       | `false`                        | Upload source code artifact for later job reuse |
| `artifact_name`       | string   | No       | `source-code`                  | Name of the uploaded artifact |

### 📁 Artifact Upload

If `upload_artifacts` is enabled, the entire source code is zipped and uploaded as an artifact (default name: `source-code`).  
Subsequent jobs can retrieve and reuse it without cloning again:

```yaml
- name: Download artifact
  uses: actions/download-artifact@v4
  with:
    name: source-code
```

</details>

<details>
<summary><strong>🔨 maven-build.yml</strong> — Java/Maven build runner with optional artifact download/upload</summary>

### 📄 About


This reusable workflow compiles Java projects using Maven. It optionally downloads source code artifacts (from earlier stages), performs the Maven command, and can optionally upload the resulting build artifacts.

### 🔧 Usage

```yaml
jobs:
  build:
    uses: NavabShariff/shared-library/.github/workflows/maven-build.yml@main
    with:
      mvn_command: 'clean install'
      java_version: '17'
      checkout: false
      download_artifacts: true
      download_artifact_name: 'source-code'
      upload_artifacts: true
      upload_artifact_name: 'compiled-source-code'
```

### 🎛️ Inputs

| Name                     | Type    | Required | Default               | Description |
|--------------------------|---------|----------|-----------------------|-------------|
| `mvn_command`            | string  | ✅ Yes  | –                     | Maven command to execute (e.g., `clean install`) |
| `java_version`           | string  | ✅ Yes  | –                     | Java version (e.g., `11`, `17`) |
| `checkout`               | boolean | No       | `false`               | Whether to run `actions/checkout` (if code isn't downloaded as artifact) |
| `upload_artifacts`       | boolean | No       | `false`               | Whether to upload the compiled source code |
| `upload_artifact_name`   | string  | No       | `compiled-source-code`| Name of the artifact to upload |
| `download_artifacts`     | boolean | No       | `false`               | Whether to download previously uploaded source code. Enable this if you are not cloning the source code in this stage (i.e., `checkout` is `false`). |
| `download_artifact_name` | string  | No       | –                     | Name of the artifact to download |

### 🧩 Integration Strategy

- ✅ Use `download_artifacts` when consuming source code uploaded in the `pre-checks` stage.
- ✅ Use `upload_artifacts` to pass compiled JARs or other build outputs to downstream jobs (e.g., for BUG analysis, SCA, Or deployment).
- ❗If `checkout` is `true`, repository code is cloned directly; otherwise, assume source is provided via `download_artifacts`.

</details>