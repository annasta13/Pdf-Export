package com.habileducation.pdfexport.pdfService

import android.os.Environment
import com.habileducation.pdfexport.data.User
import com.itextpdf.text.*
import com.itextpdf.text.pdf.PdfPCell
import com.itextpdf.text.pdf.PdfPTable
import com.itextpdf.text.pdf.PdfWriter
import java.io.File
import java.io.FileOutputStream

/**
 * Created by Annas Surdyanto on 05/02/22.
 *
 */
class PdfService {
    val TITLE_FONT = Font(Font.FontFamily.TIMES_ROMAN, 16f, Font.BOLD)
    val BODY_FONT = Font(Font.FontFamily.TIMES_ROMAN, 12f, Font.NORMAL)
    private lateinit var pdf: PdfWriter

    private fun createFile(): File {
        //Prepare file
        val title = "Pdf to export.pdf"
        val path = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
        val file = File(path, title)
        if (!file.exists()) file.createNewFile()
        return file
    }

    private fun createDocument(): Document {
        //Create Document
        val document = Document()
        document.setMargins(24f, 24f, 32f, 32f)
        document.pageSize = PageSize.A4
        return document
    }

    private fun setupPdfWriter(document: Document, file: File) {
        pdf = PdfWriter.getInstance(document, FileOutputStream(file))
        pdf.setFullCompression()
        //Open the document
        document.open()
    }

    private fun createTable(): PdfPTable {
        val table = PdfPTable(4)
        table.widthPercentage = 100f
        table.setWidths(floatArrayOf(0.2f, 1f, 1f, 1f))
        table.headerRows = 1
        table.defaultCell.verticalAlignment = Element.ALIGN_CENTER
        table.defaultCell.horizontalAlignment = Element.ALIGN_CENTER
        return table
    }

    private fun createCell(content: String): PdfPCell {
        val cell = PdfPCell(Phrase(content))
        cell.horizontalAlignment = Element.ALIGN_CENTER
        cell.verticalAlignment = Element.ALIGN_MIDDLE
        //setup padding
        cell.setPadding(8f)
        cell.isUseAscender = true
        cell.paddingLeft = 4f
        cell.paddingRight = 4f
        cell.paddingTop = 8f
        cell.paddingBottom = 8f
        return cell
    }

    private fun addLineSpace(document: Document, number: Int) {
        for (i in 0 until number) {
            document.add(Paragraph(" "))
        }
    }

    private fun createParagraph(content: String): Paragraph{
        val paragraph = Paragraph(content, BODY_FONT)
        paragraph.firstLineIndent = 25f
        paragraph.alignment = Element.ALIGN_JUSTIFIED
        return paragraph
    }

    fun createUserTable(
        data: List<User>,
        paragraphList: List<String>,
        onFinish: (file: File) -> Unit,
        onError: (Exception) -> Unit
    ) {
        //Define the document
        val file = createFile()
        val document = createDocument()

        //Setup PDF Writer
        setupPdfWriter(document, file)

        //Add Title
        document.add(Paragraph("Paragraph Title", TITLE_FONT))
        //Add Empty Line as necessary
        addLineSpace(document, 1)

        //Add paragraph
        paragraphList.forEach {content->
            val paragraph = createParagraph(content)
            document.add(paragraph)
        }
        addLineSpace(document, 1)
        //Add Empty Line as necessary

        //Add table title
        document.add(Paragraph("User Data", TITLE_FONT))
        addLineSpace(document, 1)

        //Define Table
        val table = createTable()
        //Table header (first row)
        val tableHeaderContent = listOf("No", "First Name", " Middle Name", " Last Name")
        //write table header into table
        tableHeaderContent.forEach {
            //define a cell
            val cell = createCell(it)
            //add our cell into our table
            table.addCell(cell)
        }
        //write user data into table
        data.forEach {
            //Write Each User Id
            val idCell = createCell(it.id.toString())
            table.addCell(idCell)
            //Write Each First Name
            val firstNameCell = createCell(it.firstName)
            table.addCell(firstNameCell)
            //Write Each Middle Name
            val middleNameCell = createCell(it.middleName)
            table.addCell(middleNameCell)
            //Write Each Last Name
            val lastNameCell = createCell(it.lastName)
            table.addCell(lastNameCell)
        }
        document.add(table)
        document.close()

        try {
            pdf.close()
        } catch (ex: Exception) {
            onError(ex)
        } finally {
            onFinish(file)
        }
    }
}