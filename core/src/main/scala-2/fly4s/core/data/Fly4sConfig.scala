package fly4s.core.data

import cats.data.NonEmptyList
import com.geirolz.macros.fluent.copy.FluentCopy
import fly4s.core.data.Fly4sConfigDefaults.*

import java.nio.charset.Charset

@FluentCopy(collection = true)
case class Fly4sConfig(
  connectRetries: Int                       = defaultConnectRetries,
  initSql: Option[String]                   = defaultInitSql,
  defaultSchemaName: Option[String]         = defaultDefaultSchemaName,
  schemaNames: Option[NonEmptyList[String]] = defaultSchemaNames,
  lockRetryCount: Int                       = defaultLockRetryCount,
  // --- migrations ---
  installedBy: Option[String]                    = defaultInstalledBy,
  locations: List[Location]                      = defaultLocations,
  encoding: Charset                              = defaultEncoding,
  table: String                                  = defaultTable,
  tablespace: Option[String]                     = defaultTablespace,
  targetVersion: MigrationVersion                = defaultTargetVersion,
  baselineVersion: MigrationVersion              = defaultBaselineVersion,
  baselineDescription: String                    = defaultBaselineDescription,
  ignoreMigrationPatterns: List[ValidatePattern] = defaultIgnoreMigrationPatterns,
  // --- placeholders ---
  placeholders: Map[String, String] = defaultPlaceholders,
  placeholderPrefix: String         = defaultPlaceholderPrefix,
  placeholderSuffix: String         = defaultPlaceholderSuffix,
  // --- migrations naming ---
  sqlMigrationPrefix: String           = defaultSqlMigrationPrefix,
  sqlMigrationSuffixes: Seq[String]    = defaultSqlMigrationSuffixes,
  repeatableSqlMigrationPrefix: String = defaultRepeatableSqlMigrationPrefix,
  sqlMigrationSeparator: String        = defaultSqlMigrationSeparator,
  // --- migrations functions ---
  callbacks: List[Callback]                  = defaultCallbacks,
  resolvers: List[MigrationResolver]         = defaultResolvers,
  resourceProvider: Option[ResourceProvider] = defaultResourceProvider,
  // --- flags ---
  group: Boolean                   = defaultGroup,
  mixed: Boolean                   = defaultMixed,
  failOnMissingLocations: Boolean  = defaultFailOnMissingLocations,
  validateMigrationNaming: Boolean = defaultValidateMigrationNaming,
  validateOnMigrate: Boolean       = defaultValidateOnMigrate,
  cleanOnValidationError: Boolean  = defaultCleanOnValidationError,
  cleanDisabled: Boolean           = defaultCleanDisabled,
  createSchemas: Boolean           = defaultCreateSchemas,
  placeholderReplacement: Boolean  = defaultPlaceholderReplacement,
  baselineOnMigrate: Boolean       = defaultBaselineOnMigrate,
  outOfOrder: Boolean              = defaultOutOfOrder,
  skipDefaultCallbacks: Boolean    = defaultSkipDefaultCallbacks,
  skipDefaultResolvers: Boolean    = defaultSkipDefaultResolvers
) extends Fly4sConfigContract
object Fly4sConfig extends Fly4sConfigBuilder
