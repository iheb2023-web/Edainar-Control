# Cloude Code ToolBox — MCP & Skills awareness

_Generated: 2026-09-19T13:13:08.855Z_

## How to use this report

- **Saved copy:** This file is **`.claude/cloude-code-toolbox-mcp-skills-awareness.md`** — refreshed whenever the toolbox runs an MCP & Skills scan (including on workspace open when auto-scan is enabled). It is meant for **Claude Code workspace context** together with `CLAUDE.md` (which gets a shorter replaceable summary when auto-merge is on).
- **MCP:** Lists **configured** servers from Claude Code config (`~/.claude.json` for user scope, `.mcp.json` for project scope). Use `/mcp` in the Claude Code panel to connect servers for your session.
- **Skills:** **On-disk** folders with `SKILL.md`. Claude Code does not auto-load them; attach `SKILL.md` or paths in chat when useful.
- **Task routing:** When the user’s request matches a server’s purpose (e.g. Confluence → Confluence/Atlassian MCP), prefer that **server id** from the tables below.

---

## MCP — workspace

Workspace `mcp.json` _(folder: control-backend)_

- **d:\Projets personnele\D17_Movements\control-backend\.mcp.json** — _File missing_

_No active workspace servers in mcp.json._

## MCP — user profile

- **C:\Users\SSTECH\.claude.json** — _File missing_

_No active user-scoped servers in mcp.json._

## Skills (local `SKILL.md` folders)

### Project-scoped

_None found (or no workspace open)._

### User-scoped

- **accidental-data-loss-prevention** — `C:\Users\SSTECH\.copilot\skills\accidental-data-loss-prevention`
  - |

- **bigquery-ai-ml** — `C:\Users\SSTECH\.copilot\skills\bigquery-ai-ml`
  - Leverages BigQuery's built-in machine learning and GenAI capabilities

- **bigquery-bigframes** — `C:\Users\SSTECH\.copilot\skills\bigquery-bigframes`
  - Generates Python code using BigQuery DataFrames (BigFrames), the pandas/scikit-learn-style\

- **bigquery-data-transfer-service** — `C:\Users\SSTECH\.copilot\skills\bigquery-data-transfer-service`
  - Discovers and inspects BigQuery Data Transfer Service (DTS) configurations.

- **bigquery-graph** — `C:\Users\SSTECH\.copilot\skills\bigquery-graph`
  - Provides guidelines and best practices for querying and defining property

- **bigquery-sql** — `C:\Users\SSTECH\.copilot\skills\bigquery-sql`
  - Provides BigQuery SQL query optimization techniques, execution best practices,

- **bigtable-basics** — `C:\Users\SSTECH\.copilot\skills\bigtable-basics`
  - Assists in provisioning instances/tables, designing performant schemas,

- **building-data-apps** — `C:\Users\SSTECH\.copilot\skills\building-data-apps`
  - |

- **data-autocleaning** — `C:\Users\SSTECH\.copilot\skills\data-autocleaning`
  - Automated data quality and transformation capabilities for Dataform/dbt/BigQuery

- **dataform-bigquery** — `C:\Users\SSTECH\.copilot\skills\dataform-bigquery`
  - Expertise in generating clean, correct, and efficient Dataform pipeline

- **dbt-bigquery** — `C:\Users\SSTECH\.copilot\skills\dbt-bigquery`
  - Expert guidance for creating, modifying, and optimizing dbt pipelines

- **discovering-gcp-data-assets** — `C:\Users\SSTECH\.copilot\skills\discovering-gcp-data-assets`
  - |

- **enforcing-resource-attribution** — `C:\Users\SSTECH\.copilot\skills\enforcing-resource-attribution`
  - Enforces resource attribution for CLI commands. Use this skill whenever

- **federate-lakehouse-catalog** — `C:\Users\SSTECH\.copilot\skills\federate-lakehouse-catalog`
  - Sets up Google Cloud Lakehouse federated catalogs to remote Iceberg

- **gcp-composer-troubleshooting** — `C:\Users\SSTECH\.copilot\skills\gcp-composer-troubleshooting`
  - Provides expert guidance for troubleshooting Cloud Composer (Apache

- **gcp-data-pipelines** — `C:\Users\SSTECH\.copilot\skills\gcp-data-pipelines`
  - Primary entry point for building, managing, and orchestrating data pipelines

- **gcp-dataflow** — `C:\Users\SSTECH\.copilot\skills\gcp-dataflow`
  - |

- **gcp-managed-airflow-dag-authoring** — `C:\Users\SSTECH\.copilot\skills\gcp-managed-airflow-dag-authoring`
  - Guides the authoring and validation of Apache Airflow DAGs for Managed

- **gcp-managed-airflow-migrations** — `C:\Users\SSTECH\.copilot\skills\gcp-managed-airflow-migrations`
  - Provides guidance for migrating Apache Airflow DAGs in Managed Service

- **gcp-managed-airflow-recommendations** — `C:\Users\SSTECH\.copilot\skills\gcp-managed-airflow-recommendations`
  - Provides recommendations and best practices for creating, configuring,

- **gcp-pipeline-orchestration** — `C:\Users\SSTECH\.copilot\skills\gcp-pipeline-orchestration`
  - This skill helps the agent generate or update orchestration pipeline

- **gcp-pipeline-resource-provisioning** — `C:\Users\SSTECH\.copilot\skills\gcp-pipeline-resource-provisioning`
  - |

- **gcp-spark** — `C:\Users\SSTECH\.copilot\skills\gcp-spark`
  - |

- **gcs-security-assessment** — `C:\Users\SSTECH\.copilot\skills\gcs-security-assessment`
  - Assesses the security posture of Google Cloud Storage (GCS) buckets\

- **google-cloud-auth-verification** — `C:\Users\SSTECH\.copilot\skills\google-cloud-auth-verification`
  - Mandatory Step 0 pre-flight execution order and authentication verification

- **google-cloud-storage-basics** — `C:\Users\SSTECH\.copilot\skills\google-cloud-storage-basics`
  - Stores, retrieves, and manages data as objects in Cloud Storage (Google\

- **google-cloud-storage-bucket-architect** — `C:\Users\SSTECH\.copilot\skills\google-cloud-storage-bucket-architect`
  - Creates Cloud Storage (Google Cloud Storage, or GCS) buckets. Analyzes\

- **google-cloud-storage-fuse** — `C:\Users\SSTECH\.copilot\skills\google-cloud-storage-fuse`
  - Mounts Cloud Storage buckets as a POSIX file system with Cloud Storage\

- **managing-python-dependencies** — `C:\Users\SSTECH\.copilot\skills\managing-python-dependencies`
  - |

- **ml-best-practices** — `C:\Users\SSTECH\.copilot\skills\ml-best-practices`
  - |

- **notebook-guidance** — `C:\Users\SSTECH\.copilot\skills\notebook-guidance`
  - |-

- **schema-mapping** — `C:\Users\SSTECH\.copilot\skills\schema-mapping`
  - Guides the process of analyzing, mapping, and documenting transformations

- **skill-repair** — `C:\Users\SSTECH\.copilot\skills\skill-repair`
  - |

---

## Suggested next steps

- **MCP:** Use this extension’s hub **MCP** tab, or `claude mcp list` in the terminal. In Claude Code, use `/mcp` to connect servers for the session.
- **Edit config:** Open `~/.claude.json` (user MCP) or `<workspace>/.mcp.json` (project MCP) via the extension commands.
- **Refresh this report:** run **Intelligence — scan MCP & Skills awareness** again after changing MCP config or adding skills.

_Report from Cloude Code ToolBox extension._
