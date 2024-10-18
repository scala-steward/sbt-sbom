package io.github.siculo.sbtbom.model

import org.cyclonedx.model.Component.{ Scope, Type }

case class Module(
    group: String,
    name: String,
    version: String,
    modified: Boolean,
    componentType: Type,
    componentScope: Scope,
    licenses: Seq[License]
)
