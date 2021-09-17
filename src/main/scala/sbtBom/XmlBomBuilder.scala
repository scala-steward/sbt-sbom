package sbtBom

import org.cyclonedx.CycloneDxSchema
import org.cyclonedx.model.Hash
import org.cyclonedx.util.BomUtils
import sbtBom.licenses.{License, LicensesArchive}
import sbtBom.model.{LicenseId, Module, Modules}

import scala.xml.{Elem, NodeSeq, Text}

class XmlBomBuilder(dependencies: Modules) {
  private val unlicensed = Seq(License(id = Some("Unlicense")))

  def build: Elem =
    <bom xmlns="http://cyclonedx.org/schema/bom/1.0" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" version="1" xsi:schemaLocation="http://cyclonedx.org/schema/bom/1.0 http://cyclonedx.org/schema/bom/1.0">
      {buildComponents}
    </bom>

  private def buildComponents = {
    <components>
      {dependencies.all.map(buildComponent)}
    </components>
  }

  private def buildComponent(d: Module) =
    <component type="library">
      <group>{d.group}</group>
      <name>{d.name}</name>
      <version>{d.version}</version>
      <modified>{d.modified}</modified>
      {buildLicenses(d)}
      {buildHashes(d)}
    </component>

  private def buildLicenses(d: Module) = {
    val licenses: Seq[License] = if (d.licenseIds.nonEmpty) mapLicenses(d.licenseIds) else unlicensed
    <licenses>
      {licenses.map(buildLicense)}
    </licenses>
  }

  private def buildLicense(license: License) =
    <license>
      {license.id xmlMap (<id></id>)}{license.name xmlMap (<name></name>)}
    </license>

  private def buildHashes(d: Module) = {
    import scala.collection.JavaConverters._
    d.file
      .map { f =>
        <hashes>
          {BomUtils.calculateHashes(f, CycloneDxSchema.Version.VERSION_10).asScala.map(buildHash)}
        </hashes>
      }
      .getOrElse(NodeSeq.Empty)
  }

  private def buildHash(hash: Hash) =
    <hash alg={hash.getAlgorithm}>
      {hash.getValue}
    </hash>

  implicit class OptionElem[T](opt: Option[T]) {
    def xmlMap(e: Elem): NodeSeq =
      xmlMap((v) => e.copy(child = new Text(v.toString)))

    def xmlMap(fn: (T) => Elem): NodeSeq =
      opt.map(fn).getOrElse(NodeSeq.Empty)
  }

  private def mapLicenses(licenses: Seq[LicenseId]): Seq[License] =
    licenses.map(mapLicense)

  private def mapLicense(licenseId: LicenseId): License =
    licenseId match {
      case LicenseId(licenseName, licenseUrl) =>
        val licenseId: Option[String] = licenseUrl
          .flatMap { url =>
            LicensesArchive.findByUrl(url)
          }
          .flatMap(_.id)
        License(licenseId, Some(licenseName), Seq())
    }

}