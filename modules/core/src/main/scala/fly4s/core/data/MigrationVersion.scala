package fly4s.core.data

import org.flywaydb.core.api.{MigrationVersion => JMigrationVersion}

object MigrationVersion {

  val one: MigrationVersion     = JMigrationVersion.fromVersion("1")
  val empty: MigrationVersion   = JMigrationVersion.EMPTY
  val latest: MigrationVersion  = JMigrationVersion.LATEST
  val current: MigrationVersion = JMigrationVersion.CURRENT

  def apply(version: String): MigrationVersion =
    JMigrationVersion.fromVersion(version)
}
