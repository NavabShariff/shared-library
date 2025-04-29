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
- ❗If `checkout` is `true`, repository code is cloned directly; otherwise, assume source code is provided via `download_artifacts`.

</details>

<details>
<summary><strong>🐞 bug-analysis.yml</strong> — Java static bug analysis using SpotBugs</summary>

### 📄 About

This reusable workflow performs Bug Analysis analysis using [SpotBugs](https://spotbugs.github.io/) on a Maven project. It supports downloading previously compiled code artifacts, executing the SpotBugs analysis, and uploading the resulting report file for further review or integration in later CI/CD stages.

### 🔧 Usage

```yaml
jobs:
  bug-analysis:
    uses: NavabShariff/shared-library/.github/workflows/bug-analysis.yml@main
    with:
      download_artifacts: true
      download_artifact_name: 'compiled-source-code'
      mvn_command: 'spotbugs:spotbugs'
      java_version: '17'
      bug_report_name: 'spotbugs-report'
```

### 🎛️ Inputs

| Name                     | Type    | Required | Default | Description |
|--------------------------|---------|----------|---------|-------------|
| `download_artifacts`     | boolean | ✅ Yes  | –       | Whether to download previously uploaded source code artifact |
| `download_artifact_name` | string  | ✅ Yes  | –       | Name of the artifact to download |
| `mvn_command`            | string  | ✅ Yes  | –       | Maven command to execute (e.g., `spotbugs:spotbugs`) |
| `java_version`           | string  | ✅ Yes  | –       | Java version to set up before executing Maven |
| `bug_report_name`        | string  | ✅ Yes  | –       | Name to use for the uploaded bug report artifact. 💡 Suggestion: use predefined GitHub Action variables (e.g., `${{ github.event.repository.name }}-bug-report`) to avoid hardcoding this value per project. |

### 📦 Maven Plugin Requirement

To make this workflow function properly, your `pom.xml` must include the **SpotBugs Maven plugin** as shown below:

```xml
<plugin>
  <groupId>com.github.spotbugs</groupId>
  <artifactId>spotbugs-maven-plugin</artifactId>
  <version>4.7.3.0</version>
  <configuration>
    <effort>Max</effort>
    <failOnError>false</failOnError>
    <threshold>Low</threshold>
    <xmlOutput>true</xmlOutput>
    <outputDirectory>${project.build.directory}</outputDirectory>
  </configuration>
</plugin>
```

> 🔔 Without this plugin, the Maven `spotbugs:spotbugs` goal will not run correctly.

### 🧩 Integration Strategy

- ✅ Use this workflow after a successful Maven build stage (`maven-build.yml`) where compiled source is uploaded.
- ✅ Pass in the same artifact name used during upload in the build stage.
- ✅ Use the uploaded report artifact in downstream workflows like audit or security review.

</details>