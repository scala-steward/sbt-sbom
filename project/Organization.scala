// SPDX-FileCopyrightText: The sbt-sbom team
//
// SPDX-License-Identifier: MIT

import sbt.{ URI, uri }

object Organization {
  val organization: String = "com.github.sbt"
  val organizationName: String = "sbt"
  val organizationHomepage: Option[URI] = Some(uri("https://www.scala-sbt.org/"))
}
