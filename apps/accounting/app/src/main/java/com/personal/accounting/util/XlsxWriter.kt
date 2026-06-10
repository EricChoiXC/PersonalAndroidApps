package com.personal.accounting.util

import java.io.OutputStream
import java.util.zip.ZipEntry
import java.util.zip.ZipOutputStream

object XlsxWriter {

    private val COLUMNS = arrayOf(
        "A", "B", "C", "D", "E", "F", "G", "H", "I", "J", "K", "L", "M",
        "N", "O", "P", "Q", "R", "S", "T", "U", "V", "W", "X", "Y", "Z"
    )

    fun write(
        sheetName: String,
        headers: Array<String>,
        rows: List<Array<Any>>,
        output: OutputStream
    ) {
        ZipOutputStream(output).use { zip ->
            writeEntry(zip, "[Content_Types].xml", contentTypesXml())
            writeEntry(zip, "_rels/.rels", rootRelsXml())
            writeEntry(zip, "xl/workbook.xml", workbookXml(sheetName))
            writeEntry(zip, "xl/_rels/workbook.xml.rels", workbookRelsXml())
            writeEntry(zip, "xl/styles.xml", stylesXml())
            writeEntry(zip, "xl/worksheets/sheet1.xml", sheetXml(headers, rows))
        }
    }

    private fun writeEntry(zip: ZipOutputStream, name: String, xml: String) {
        zip.putNextEntry(ZipEntry(name))
        val bytes = xml.toByteArray(Charsets.UTF_8)
        zip.write(bytes)
        zip.closeEntry()
    }

    private fun contentTypesXml(): String = buildString {
        append("<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>")
        append("<Types xmlns=\"http://schemas.openxmlformats.org/package/2006/content-types\">")
        append("<Default Extension=\"rels\" ContentType=\"application/vnd.openxmlformats-package.relationships+xml\"/>")
        append("<Default Extension=\"xml\" ContentType=\"application/xml\"/>")
        append("<Override PartName=\"/xl/workbook.xml\" ContentType=\"application/vnd.openxmlformats-officedocument.spreadsheetml.sheet.main+xml\"/>")
        append("<Override PartName=\"/xl/worksheets/sheet1.xml\" ContentType=\"application/vnd.openxmlformats-officedocument.spreadsheetml.worksheet+xml\"/>")
        append("<Override PartName=\"/xl/styles.xml\" ContentType=\"application/vnd.openxmlformats-officedocument.spreadsheetml.styles+xml\"/>")
        append("</Types>")
    }

    private fun rootRelsXml(): String = buildString {
        append("<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>")
        append("<Relationships xmlns=\"http://schemas.openxmlformats.org/package/2006/relationships\">")
        append("<Relationship Id=\"rId1\" Type=\"http://schemas.openxmlformats.org/officeDocument/2006/relationships/officeDocument\" Target=\"xl/workbook.xml\"/>")
        append("</Relationships>")
    }

    private fun workbookXml(sheetName: String): String = buildString {
        append("<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>")
        append("<workbook xmlns=\"http://schemas.openxmlformats.org/spreadsheetml/2006/main\" xmlns:r=\"http://schemas.openxmlformats.org/officeDocument/2006/relationships\">")
        append("<sheets>")
        append("<sheet name=\"${xmlEscapeAttribute(sheetName)}\" sheetId=\"1\" r:id=\"rId1\"/>")
        append("</sheets>")
        append("</workbook>")
    }

    private fun workbookRelsXml(): String = buildString {
        append("<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>")
        append("<Relationships xmlns=\"http://schemas.openxmlformats.org/package/2006/relationships\">")
        append("<Relationship Id=\"rId1\" Type=\"http://schemas.openxmlformats.org/officeDocument/2006/relationships/worksheet\" Target=\"worksheets/sheet1.xml\"/>")
        append("<Relationship Id=\"rId2\" Type=\"http://schemas.openxmlformats.org/officeDocument/2006/relationships/styles\" Target=\"styles.xml\"/>")
        append("</Relationships>")
    }

    private fun stylesXml(): String = buildString {
        append("<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>")
        append("<styleSheet xmlns=\"http://schemas.openxmlformats.org/spreadsheetml/2006/main\"/>")
    }

    private fun sheetXml(headers: Array<String>, rows: List<Array<Any>>): String = buildString {
        append("<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>")
        append("<worksheet xmlns=\"http://schemas.openxmlformats.org/spreadsheetml/2006/main\">")
        append(buildCols(headers.size))
        append("<sheetData>")
        // Header row
        append("<row r=\"1\">")
        for (i in headers.indices) {
            append("<c r=\"${COLUMNS[i]}1\" t=\"inlineStr\"><is><t>${xmlEscape(headers[i])}</t></is></c>")
        }
        append("</row>")
        // Data rows
        for ((rowIdx, row) in rows.withIndex()) {
            val rowNum = rowIdx + 2
            append("<row r=\"$rowNum\">")
            for ((colIdx, value) in row.withIndex()) {
                val cellRef = "${COLUMNS[colIdx]}$rowNum"
                if (value is Number) {
                    append("<c r=\"$cellRef\"><v>$value</v></c>")
                } else {
                    append("<c r=\"$cellRef\" t=\"inlineStr\"><is><t>${xmlEscape(value.toString())}</t></is></c>")
                }
            }
            append("</row>")
        }
        append("</sheetData>")
        append("</worksheet>")
    }

    private fun buildCols(colCount: Int): String = buildString {
        append("<cols>")
        for (i in 1..colCount) {
            append("<col min=\"$i\" max=\"$i\" width=\"18\" customWidth=\"1\"/>")
        }
        append("</cols>")
    }

    private fun xmlEscape(s: String): String = s
        .replace("&", "&amp;")
        .replace("<", "&lt;")
        .replace(">", "&gt;")

    private fun xmlEscapeAttribute(s: String): String = s
        .replace("&", "&amp;")
        .replace("<", "&lt;")
        .replace(">", "&gt;")
        .replace("\"", "&quot;")
        .replace("'", "&apos;")
}
