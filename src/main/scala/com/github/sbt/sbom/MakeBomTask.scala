// SPDX-FileCopyrightText: The sbt-sbom team
//
// SPDX-License-Identifier: MIT

package com.github.sbt.sbom

import sbt.*

import java.nio.charset.StandardCharsets
import java.nio.file.Files

class MakeBomTask(properties: BomTaskProperties, bomFile: File) extends BomTask[File](properties) {

  override def execute: File = {
    log.info(s"Creating bom file ${bomFile.getAbsolutePath}")
    val bomText = getBomText
    Files.write(bomFile.toPath, bomText.getBytes(StandardCharsets.UTF_8))
    validateBomFile(bomFile)
    log.info(s"Bom file ${bomFile.getAbsolutePath} created")
    bomFile
  }
}
