<details>
<summary><strong>✅ pre-checks</strong> — Composite GitHub Action to run Credential Scan, Commit Validation, and License Scanning</summary>

### 📄 About

This composite action performs:

- 🔐 Credential scanning (via Gitleaks)  
- ✅ Commit sign-off validation using `commit-check-action`  
- 🧾 License scanning using [`license_finder`](https://github.com/pivotal/LicenseFinder)

It is meant to be shared across multiple repositories using a centralized shared action in `.github/actions/pre-checks`.

---

### 🔧 Usage

```yaml
jobs:
  pre_checks:
    runs-on: ubuntu-latest
    steps:
      - name: Checkout code
        uses: actions/checkout@v4
        with:
          path: .

      - name: Run Pre-checks
        uses: NavabShariff/shared-library/.github/actions/pre-checks@main
        with:
          commit_sign_off: true
          commit_message: false
          credential_scan: true
          license_scanning: true
          license_decision_file: 'dependency_decisions.yml'
```

---

### 🎛️ Inputs

| Name                    | Type     | Required | Default                        | Description |
|-------------------------|----------|----------|--------------------------------|-------------|
| `commit_sign_off`       | boolean  | ✅ Yes   | `true`                         | Whether to enforce signed commits |
| `commit_message`        | boolean  | ❌ No    | `false`                        | Whether to validate commit message (used by commit-check-action) |
| `credential_scan`       | boolean  | ✅ Yes   | `true`                         | Run Gitleaks credential scanning |
| `license_scanning`      | boolean  | ✅ Yes   | `true`                         | Run `license_finder` to check OSS licenses |
| `license_decision_file` | string   | ✅ Yes   | `doc/dependency_decisions.yml` | Path to the LicenseFinder decisions file |

---

### ⚙️ How It Works

- **Credential Scan**: Executes Gitleaks to find secrets in the codebase.
- **Commit Validation**: Verifies commits for proper sign-off or message format.
- **License Scan**: Uses `license_finder` to verify all dependencies are approved based on a provided decisions file.



### 🧼 Cleanup Step (Post License Scan)

As part of the `pre-checks` action, a cleanup step has been added to reduce artifact size and avoid uploading unnecessary files. This step runs automatically **after license scanning** and removes the following directories:

- `.git/` – Git history, which can be large
- `venv/` – Python virtual environment

This helps keep the uploaded artifacts small and clean.

</details>

<details>
<summary><strong>⚙️ Java Maven Reusable</strong> — Composite GitHub Action to run Maven commands with specified Java version</summary>

### 📄 About

This composite GitHub Action allows you to run any [Maven](https://maven.apache.org/) command (`compile`, `package`, `test`, `bug analysis`, `dependency checks`etc.) using a specified Java version. It is useful for standardizing Maven builds across multiple repositories by centralizing this logic in a shared GitHub Action.


### 🔧 Usage

```yaml
jobs:
  maven_build:
    runs-on: ubuntu-latest
    steps:
      - name: Checkout code
        uses: actions/checkout@v4

      - name: Run Maven Build
        uses: NavabShariff/shared-library/.github/actions/java-maven@main
        with:
          java_version: '17'
          mvn_command: clean compile
```


### 🎛️ Inputs

| Name           | Type   | Required | Default         | Description                                |
|----------------|--------|----------|------------------|--------------------------------------------|
| `java_version` | string | ✅ Yes   | `17`             | Java version to use (e.g., `11`, `17`)     |
| `mvn_command`  | string | ✅ Yes   | `clean compile`  | Maven command to run (e.g., `clean install`, `compile`, `test`) |


### ⚙️ How It Works

1. **Java Setup**: Uses [`actions/setup-java`](https://github.com/actions/setup-java) to configure the Java environment with the given version.
2. **Maven Execution**: Runs the specified Maven command using the provided input (`mvn_command`).


### 🐞 Bug-analysis:

To make this workflow function properly to run bug-analysis , your `pom.xml` must include the **SpotBugs Maven plugin** as shown below:

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

> `check out the official documentation`:
[SpotBugs Maven Plugin Documentation](https://spotbugs.readthedocs.io/en/latest/maven.html)

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

> `check out the official documentation`:
[SpotBugs Maven Plugin Documentation](https://spotbugs.readthedocs.io/en/latest/maven.html)

### 🧩 Integration Strategy

- ✅ Use this workflow after a successful Maven build stage (`maven-build.yml`) where compiled source is uploaded.
- ✅ Pass in the same artifact name used during upload in the build stage.
- ✅ Use the uploaded report artifact in downstream workflows like audit or security review.

</details>


<details>
<summary><strong>🛠️ dependency-check.yml</strong> — Dependency check using OWASP Dependency Check</summary>

### 📄 About

This reusable workflow performs a dependency check using the `OWASP Dependency Check` Maven plugin to scan for vulnerabilities in your project's dependencies. It optionally downloads source code artifacts, executes the Maven command, and uploads the resulting dependency check report.

### 🔧 Usage

```yaml
jobs:
  dependency-check:
    uses: NavabShariff/shared-library/.github/workflows/dependency-check.yml@main
    with:
      mvn_command: 'clean verify'
      java_version: '17'
      download_artifacts: true
      download_artifact_name: 'source-code'
      dependency_report_name: 'dependency-check-report'
```

### 🎛️ Inputs

| Name                     | Type    | Required | Default               | Description |
|--------------------------|---------|----------|-----------------------|-------------|
| `mvn_command`            | string  | ✅ Yes  | –                     | Maven command to execute (e.g., `clean verify`) |
| `java_version`           | string  | ✅ Yes  | –                     | Java version (e.g., `11`, `17`) |
| `download_artifacts`     | boolean | ✅ Yes  | –                     | Whether to download previously uploaded source code artifacts (from earlier stages) |
| `download_artifact_name` | string  | ✅ Yes  | –                     | Name of the artifact to download |
| `dependency_report_name` | string  | No  | –                     | Name to use for the uploaded dependency check report artifact (e.g., `dependency-check-report`) |

### 🧩 Integration Strategy

- ✅ Use `download_artifacts` when consuming source code uploaded in the `pre-checks` or build stage.
- ✅ Use `dependency_report_name` to upload the OWASP Dependency Check report for visibility and further actions.
- ✅ No need to build or compile code for this stage; plain source code is sufficient. Therefore, you can run this stage in parallel with the build stage to reduce pipeline execution time.

### ⚙️ Maven Plugin Configuration

To run the OWASP Dependency Check in your Maven project, you need to add the following plugin to your `pom.xml`:

```xml
<plugin>
    <groupId>org.owasp</groupId>
    <artifactId>dependency-check-maven</artifactId>
    <version>12.1.0</version>
    <executions>
        <execution>
            <goals>
                <goal>check</goal>
            </goals>
        </execution>
    </executions>
    <configuration>
        <formats>
        <format>HTML</format>
        </formats>
        <outputDirectory>${project.basedir}</outputDirectory>
    </configuration>
</plugin>
```

> `check out the official documentation`:  
[OWASP Dependency Check Maven Plugin Documentation](https://jeremylong.github.io/DependencyCheck/dependency-check-maven/index.html)

</details>

<details>
<summary><strong>🔍 sonar-scanning-java.yml</strong> — SonarQube Static Code Analysis for Java Projects</summary>

### 📄 About

This reusable GitHub Actions workflow performs static code analysis on Java projects using SonarQube. It optionally downloads compiled source code, SpotBugs and OWASP dependency-check reports and runs the scan using the SonarQube Scanner CLI. 

### 🔧 Usage

```yaml
jobs:
  sca:
    needs: [build, bug_analysis, dependency_check]
    uses: NavabShariff/shared-library/.github/workflows/sonar-scanning-java.yml@main
    secrets:
      SONAR_HOST_URL: ${{ secrets.SONAR_HOST_URL }}
      SONAR_TOKEN: ${{ secrets.SONAR_TOKEN }}
    with: 
      download_artifacts: true
      download_artifact_name: ${{ github.event.repository.name }}-compile-code
      download_bug_report: true
      bug_report_name: ${{ github.event.repository.name }}-bug-report
      download_dependency_check_report: false
      dependency_check_report_name: ${{ github.event.repository.name }}-dependency-check-report
      qualitygate: false
```

### 🎛️ Inputs

| Name                            | Type    | Required | Default | Description |
|---------------------------------|---------|----------|---------|-------------|
| `download_artifacts`            | boolean | ✅ Yes   | `true` | Whether to download compiled source code artifacts |
| `download_artifact_name`        | string  | No       | –       | Name of the compiled code artifact to download |
| `download_bug_report`           | boolean | No       | `false` | Whether to download the SpotBugs bug report |
| `bug_report_name`               | string  | No       | –       | Name of the SpotBugs bug report artifact (Suggestion: use predefined GitHub action vars like `${{ github.event.repository.name }}-bug-report`) |
| `download_dependency_check_report` | boolean | No       | `false` | Whether to download the OWASP dependency-check report |
| `dependency_check_report_name`  | string  | No       | –       | Name of the dependency-check artifact |
| `qualitygate`                   | string  | ✅ Yes    | –       | Whether to wait for SonarQube quality gate result (`true`/`false`) |

### 🔐 Secrets

| Name              | Required | Description |
|-------------------|----------|-------------|
| `SONAR_HOST_URL`  | ✅ Yes | URL of your SonarQube server |
| `SONAR_TOKEN`     | ✅ Yes | Authentication token for SonarQube |

### 🧩 Integration Strategy

- ✅ Use in combination with SpotBugs and dependency-check workflows for complete static and SCA (Software Composition Analysis).
- ✅ Recommended to use previously compiled source code artifact to avoid repeated builds.
- ✅ Run this as a downstream job after build, bug analysis, and dependency check.
- ❗SonarQube Scanner CLI runs inside a Docker container (`sonarsource/sonar-scanner-cli:latest`), so ensure network access to SonarQube.
- ✅ Use consistent artifact names using GitHub context variables for reusability.

</details>

<details>
<summary><strong>🐳 docker-login-build-push-ecr.yml</strong> — Docker Build and Push to Amazon ECR</summary>

### 📄 About

This reusable GitHub Actions workflow builds a Docker image and pushes it to Amazon ECR. Optionally, it can download a pre-built source code artifact and save the Docker image as a `.tar.gz` artifact for later use.

### 🔧 Usage

```yaml
jobs:
  docker_build_push:
    uses: NavabShariff/shared-library/.github/workflows/docker-login-build-push-ecr.yml@main
    with:
      ecr_repo: 'salary-api'
      aws_region: 'ap-south-1'
      download_artifacts: true
      download_artifact_name: ${{ github.event.repository.name }}
      save_docker_image: true
    secrets:
      AWS_IAM_ROLE_ATHENTICATION: ${{ secrets.AWS_IAM_ROLE_ATHENTICATION }}
```

### 🎛️ Inputs

| Name                     | Type    | Required | Default | Description |
|--------------------------|---------|----------|---------|-------------|
| `ecr_repo`               | string  | ✅ Yes   | –       | ECR repository name where image should be pushed |
| `aws_region`             | string  | ✅ Yes   | –       | AWS region where the ECR repo exists |
| `download_artifacts`     | boolean | ✅ Yes   | `false` | Whether to download the compiled source code artifact |
| `download_artifact_name` | string  | ✅ Yes   |    –    | Name of the source artifact to download eg:- `${{ github.event.repository.name }}` |
| `save_docker_image`      | boolean | No       | `false` | If `true`, saves the image as a `.tar.gz` file and uploads it as an artifact |

### 🔐 Secrets

| Name                        | Required | Description |
|-----------------------------|----------|-------------|
| `AWS_IAM_ROLE_ATHENTICATION` | ✅ Yes | IAM Role ARN to assume for ECR authentication |

### 📤 Outputs

| Name        | Description                             |
|-------------|-----------------------------------------|
| `image_tag` | Generated Docker image tag (e.g., `branchname-<sha>`) |
| `image_name`| Full Docker image path with tag         |

### 🧩 Integration Strategy

- ✅ Optionally saves Docker image for air-gapped/on-prem deployments or further promotion pipelines.
- ✅ Uses short SHA with branch name for image tagging.
- 🔐 Requires `AWS I AM ROLE` to authenticate to AWS ECR.

</details>


<details>
<summary><strong>📦 docker-login-build-push-ghcr.yml</strong> — Docker Build and Push to GitHub Container Registry (GHCR)</summary>

### 📄 About

This reusable GitHub Actions workflow builds a Docker image and pushes it to GitHub Container Registry (GHCR). It supports downloading previously built source code as an artifact.

### 🔧 Usage

```yaml
jobs:
  docker_build_ghcr:
    uses: NavabShariff/shared-library/.github/workflows/docker-login-build-push-ghcr.yml@main
    with:
      download_artifacts: true
      artifact_name: ${{ github.event.repository.name }}
```

### 🎛️ Inputs

| Name              | Type    | Required | Default | Description |
|-------------------|---------|----------|---------|-------------|
| `download_artifacts` | boolean | ✅ Yes   | `false` | Whether to download the built artifact (source) before building the image |
| `artifact_name`      | string  | ✅ Yes   | –       | Name of the artifact to download |

### 🔐 Secrets

| Name              | Required | Description                     |
|-------------------|----------|---------------------------------|
| `GITHUB_TOKEN`     | ✅ Yes   | GitHub-provided token for authentication with GHCR (automatically available in Actions) |


### 🧩 Integration Strategy

- ✅ Meant to be used in CI pipelines where artifacts (e.g., built binaries, code) are uploaded and later used to build images.
- ✅ Useful for private GitHub-hosted images via GHCR.
- 🔄 Automatically constructs image name and tags based on repo and commit data.
- 🔐 Leverages `GITHUB_TOKEN` for secure push without needing extra secrets.

</details>


<details>
<summary><strong>🛡️ ecr-image-scan.yml</strong> — Amazon ECR Image Vulnerability Scan</summary>

### 📄 About

This reusable GitHub Actions workflow scans a Docker image in Amazon ECR for vulnerabilities after it's pushed.

### 🔧 Usage

```yaml
jobs:
  docker_image_scan:
    uses: your-org/shared-library/.github/workflows/ecr-image-scan.yml@main
    with:
      ecr_repo: 'salary-api'
      aws_region: 'ap-south-1'
      critical_threshold: 3
    secrets:
      AWS_IAM_ROLE_ATHENTICATION: ${{ secrets.AWS_IAM_ROLE_ATHENTICATION }}
```

#### ✅ **How It Works**
- Authenticates to AWS using an IAM role.
- Waits for the ECR scan results.
- Parses scan output to check for critical vulnerabilities.
- Fails the pipeline if the number of critical issues exceeds the configured threshold.

---

### 📥 **Inputs**

| Name               | Type     | Required | Default               | Description                                                                 |
|--------------------|----------|----------|------------------------|-----------------------------------------------------------------------------|
| `ecr_repo`         | string   | ✅       | –                      | Name of the ECR repository to scan.                                        |
| `aws_region`       | string   | ✅       | –                      | AWS region where the ECR repo is hosted.                                   |
| `critical_threshold` | number | ❌       | `5`                    | Max allowed number of `CRITICAL` vulnerabilities before the scan fails.    |

---

### 🔐 **Secrets**

| Name                        | Description                                            |
|-----------------------------|--------------------------------------------------------|
| `AWS_IAM_ROLE_ATHENTICATION` | The IAM role to assume for AWS CLI access.             |

---

</details>

<details>
<summary><strong>🛡️ dast.yml</strong> — DAST with OWASP ZAP</summary>

### 📄 About


This reusable GitHub Actions workflow performs Dynamic Application Security Testing (DAST) using OWASP ZAP on a Dockerized application.

---

### 🧠 **What It Does**

1. **Downloads the compiled source code** (usually to retrieve `docker-compose.yml`).
2. **Downloads the Docker image artifact** that was built and saved in a previous job.
3. **Loads and starts the application** using Docker Compose.
4. **Runs a full ZAP scan** against the local app on `http://localhost:8080`.
5. **Validates the scan report**, and fails the job if high-risk issues are found.


### 🔧 Usage

```yaml
jobs:
  dast:
    needs: [docker_build_push, docker_image_scan]
    uses: NavabShariff/shared-library/.github/workflows/dast.yml@main
    with:
      download_artifacts: true
      download_artifact_name: ${{ github.event.repository.name }}
      image_name: ${{ needs.docker_build_push.outputs.image_name }}
      image_tag: ${{ needs.docker_build_push.outputs.image_tag }}
    secrets:
      GH_TOKEN: ${{ secrets.GH_TOKEN }}
```

### 🧠 **Inputs**

| Name                      | Type    | Required | Description                                                                                                                                     |
|---------------------------|---------|----------|-------------------------------------------------------------------------------------------------------------------------------------------------|
| `download_artifacts`      | boolean | ✅       | Whether to download the source code artifact (commonly includes `docker-compose.yml`) needed to spin up the containerized app for testing.     |
| `download_artifact_name`  | string  | ✅       | Name of the uploaded source code artifact to be downloaded.                                                                                     |
| `image_name`              | string  | ✅       | The Docker image name to be tested. Typically passed from the `docker_build_push` stage output using `${{ needs.docker_build_push.outputs.image_name }}`. |
| `image_tag`               | string  | ✅       | The tag of the Docker image to be tested. Typically passed from the `docker_build_push` stage output using `${{ needs.docker_build_push.outputs.image_tag }}`. |


### 🔐 **Secrets**

| Name        | Description                                |
|-------------|--------------------------------------------|
| `GH_TOKEN`  | GitHub token to authenticate ZAP scan logs.|


### ⚙️ **ZAP Scan Behavior Explained**

The workflow uses the [zaproxy/action-full-scan](https://github.com/zaproxy/action-full-scan) GitHub Action to perform a full DAST scan. These key settings are used:

```yaml
cmd_options: '-J report_json.json -z "-config urls.file=/zap/wrk/urls.txt"'
fail_action: false
```

- **`cmd_options`**:  
  - `-J report_json.json`: Generates a full scan report in JSON format (used for later validation).
  - `-z "-config urls.file=/zap/wrk/urls.txt"`: Instructs ZAP to scan URLs listed in a custom file (`urls.txt`), if provided.

- **`fail_action: false`**:  
  By default, ZAP fails the workflow if it encounters *any* warnings, errors, or alerts — even low-risk ones — returning an exit code `2`.  
  To avoid false positives or premature workflow failures, we set `fail_action: false`. Instead, the scan result is manually parsed in the **"Validate ZAP Report for High Risk Issues"** step, which fails the job **only if High risk issues are found**.

</details>

<details>
<summary><strong>🛡️ gitops-update-source-truth.yml</strong> — GitOps Deploy Trigger Workflow</summary>

### 📄 About


This workflow is used to update the GitOps repository with the latest image tag based on the branch from which the workflow was triggered. It aligns with a GitOps strategy where **Argo CD** watches the GitOps repo and applies changes to appropriate environments based on updates to `kustomization.yaml`.


### 🔧 Usage

```yaml
trigger_cd:
  needs: [dast]
  uses: NavabShariff/shared-library/.github/workflows/gitops-update-source-truth.yml@main
  with:
    gitops_repo: "NavabShariff/gitops-source"
  secrets:
    GH_TOKEN: ${{ secrets.GH_TOKEN }}
```

### 🧾 Inputs

| Name          | Type   | Required | Description                                                                 |
|---------------|--------|----------|-----------------------------------------------------------------------------|
| `gitops_repo` | string | ✅       | The GitHub repository where your GitOps manifests (e.g., Kustomize configs) are stored. |

### 🔐 Secrets

| Name       | Required | Description                                           |
|------------|----------|-------------------------------------------------------|
| `GH_TOKEN` | ✅       | GitHub token with permissions to push to the GitOps repo. |

---

### 🌿 Branch-to-Environment Mapping Strategy

This workflow assumes a **three-tier GitOps environment model**:

| Branch Pattern     | Target Environment | GitOps Directory Path Format                  |
|--------------------|--------------------|-----------------------------------------------|
| `main` or `master` | Production         | `<app-name>/overlays/prod/kustomization.yaml` |
| `release-*`        | Staging            | `<app-name>/overlays/staging/kustomization.yaml` |
| others (e.g., dev) | Development/QA     | `<app-name>/overlays/dev/kustomization.yaml`  |

- The `<app-name>` is automatically derived from the current repository name.
- The image tag format used is:  
  ```
  <branch-name>-<first-8-chars-of-commit-sha>
  ```
  Example: `dev-9fbc3d1a`

---

### 🔄 How It Works

1. **Checkout GitOps Repository**  
   Clones the repo defined in `gitops_repo` so the manifest files can be modified.

2. **Determine Target Environment Folder**  
   Sets the environment folder path (`$ENV_FOLDER`) based on the triggering branch.

3. **Update Image Tag**  
   Locates the corresponding `kustomization.yaml` and updates the `newTag:` field to match the new image version.

4. **Commit and Push**  
   Commits the updated file and pushes the change to the GitOps repo.  
   Argo CD (or your GitOps controller) will then automatically detect this change and sync the target environment accordingly.


### 💡 Notes

- Ensure your GitOps repo uses Kustomize with an environment structure like:
  ```
  apps/
    └── my-service/
         └── overlays/
              ├── dev/
              ├── staging/
              └── prod/
  ```
- The Argo CD application should point to these environment paths.
- No changes are committed if the tag value has not changed (`git commit` is skipped with a message).

</details>


<details>
<summary><strong>📢 slack-alert.yml</strong> — Send Slack notifications on workflow success or failure</summary>

### 📄 About

This reusable GitHub Actions workflow sends formatted Slack notifications when a workflow run succeeds or fails. It is designed to be used as a `workflow_call` in downstream pipelines, providing visibility into CI/CD pipeline results via Slack using an [incoming webhook](https://api.slack.com/messaging/webhooks).

### 🔧 Usage

```yaml
jobs:
  notify:
    uses: NavabShariff/shared-library/.github/workflows/slack-alert.yml@main
    with:
      commit_author_name: ${{ github.event.pusher.name }}
      commit_message: ${{ github.event.head_commit.message }}
      commit_id: ${{ github.sha }}
      run_id: ${{ github.run_id }}
    secrets:
      SLACK_WEBHOOK_URL: ${{ secrets.SLACK_WEBHOOK_URL }}
```

### 🎛️ Inputs

| Name                 | Type   | Required | Description |
|----------------------|--------|----------|-------------|
| `commit_author_name` | string | ✅ Yes   | Name of the commit author. Use `${{ github.event.pusher.name }}` to fetch dynamically. |
| `commit_message`     | string | ✅ Yes   | Commit message. Use `${{ github.event.head_commit.message }}` to fetch dynamically. |
| `commit_id`          | string | ✅ Yes   | Commit SHA. Use `${{ github.sha }}` to fetch dynamically. |
| `run_id`             | string | ✅ Yes   | GitHub Actions run ID. Use `${{ github.run_id }}` to fetch dynamically. |

### 🔐 Secrets

| Name                | Required | Description |
|---------------------|----------|-------------|
| `SLACK_WEBHOOK_URL` | ✅ Yes   | Slack Incoming Webhook URL to post messages |

### 📤 Behavior

- ✅ Sends a formatted Slack message on **success** of the parent workflow, including author, branch, commit, and message details.
- ❌ Sends a different message on **failure** of the parent workflow with the same commit context.
- 🔗 Includes a clickable button linking directly to the GitHub Actions job run.

</details>