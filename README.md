<details>
<summary><strong>✅ pre-checks.yml</strong> — Run Credential Scanning, commit validation, license scanning and upload source code</summary>

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